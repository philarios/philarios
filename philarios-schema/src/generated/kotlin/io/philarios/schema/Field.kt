package io.philarios.schema

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Field(
        val name: String,
        val key: Boolean?,
        val singularName: String?,
        val type: Type
)

class FieldSpec<in C>(internal val body: FieldBuilder<C>.() -> Unit)

@DslBuilder
interface FieldBuilder<out C> {
    val context: C

    fun name(value: String)

    fun key(value: Boolean)

    fun singularName(value: String)

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: FieldBuilder<C>.() -> Unit)

    fun include(spec: FieldSpec<C>)

    fun <C2> include(context: C2, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FieldSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>)
}

class FieldRef(internal val key: String)

internal data class FieldShell(
        var name: Scaffold<String>? = null,
        var key: Scaffold<Boolean>? = null,
        var singularName: Scaffold<String>? = null,
        var type: Scaffold<Type>? = null
) : Scaffold<Field> {
    override suspend fun resolve(registry: Registry): Field {
        checkNotNull(name) { "Field is missing the name property" }
        checkNotNull(type) { "Field is missing the type property" }
        coroutineScope {
            type?.let{ launch { it.resolve(registry) } }
        }
        val value = Field(
            name!!.let{ it.resolve(registry) },
            key?.let{ it.resolve(registry) },
            singularName?.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class FieldShellBuilder<out C>(override val context: C, internal var shell: FieldShell = FieldShell()) : FieldBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun key(value: Boolean) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun singularName(value: String) {
        shell = shell.copy(singularName = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(type = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun include(body: FieldBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FieldSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FieldBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FieldSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FieldShellBuilder<C2> = FieldShellBuilder(context, shell)

    private fun <C2> merge(other: FieldShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class FieldScaffolder<in C>(internal val spec: FieldSpec<C>) : Scaffolder<C, Field> {
    override fun createScaffold(context: C): Scaffold<Field> {
        val builder = FieldShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
