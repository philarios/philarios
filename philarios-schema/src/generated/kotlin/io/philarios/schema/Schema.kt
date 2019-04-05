package io.philarios.schema

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Schema(
        val pkg: String,
        val name: String,
        val types: List<Type>
)

class SchemaSpec<in C>(internal val body: SchemaBuilder<C>.() -> Unit)

@DslBuilder
interface SchemaBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: SchemaBuilder<C>.() -> Unit)

    fun include(spec: SchemaSpec<C>)

    fun <C2> include(context: C2, body: SchemaBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SchemaSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>)
}

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.RegistryRef(io.philarios.schema.Schema::class, key)

internal data class SchemaShell(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var types: List<Scaffold<Type>>? = null
) : Scaffold<Schema> {
    override suspend fun resolve(registry: Registry): Schema {
        checkNotNull(pkg) { "Schema is missing the pkg property" }
        checkNotNull(name) { "Schema is missing the name property" }
        coroutineScope {
            types?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Schema(
            pkg!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            types.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Schema::class, value.name, value)
        return value
    }
}

@DslBuilder
internal class SchemaShellBuilder<out C>(override val context: C, internal var shell: SchemaShell = SchemaShell()) : SchemaBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(types = shell.types.orEmpty() + TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(value))
    }

    override fun include(body: SchemaBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SchemaSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SchemaBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SchemaSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SchemaShellBuilder<C2> = SchemaShellBuilder(context, shell)

    private fun <C2> merge(other: SchemaShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class SchemaScaffolder<in C>(internal val spec: SchemaSpec<C>) : Scaffolder<C, Schema> {
    override fun createScaffold(context: C): Scaffold<Schema> {
        val builder = SchemaShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
