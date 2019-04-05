package io.philarios.terraform

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Variable(
        val name: String,
        val type: String,
        val default: Any
)

class VariableSpec<in C>(internal val body: VariableBuilder<C>.() -> Unit)

@DslBuilder
interface VariableBuilder<out C> {
    val context: C

    fun name(value: String)

    fun type(value: String)

    fun default(value: Any)

    fun include(body: VariableBuilder<C>.() -> Unit)

    fun include(spec: VariableSpec<C>)

    fun <C2> include(context: C2, body: VariableBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: VariableSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: VariableBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: VariableSpec<C2>)
}

class VariableRef(internal val key: String)

internal data class VariableShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var default: Scaffold<Any>? = null
) : Scaffold<Variable> {
    override suspend fun resolve(registry: Registry): Variable {
        checkNotNull(name) { "Variable is missing the name property" }
        checkNotNull(type) { "Variable is missing the type property" }
        checkNotNull(default) { "Variable is missing the default property" }
        val value = Variable(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            default!!.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class VariableShellBuilder<out C>(override val context: C, internal var shell: VariableShell = VariableShell()) : VariableBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun default(value: Any) {
        shell = shell.copy(default = Wrapper(value))
    }

    override fun include(body: VariableBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: VariableSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: VariableBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: VariableSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: VariableBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: VariableSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): VariableShellBuilder<C2> = VariableShellBuilder(context, shell)

    private fun <C2> merge(other: VariableShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class VariableScaffolder<in C>(internal val spec: VariableSpec<C>) : Scaffolder<C, Variable> {
    override fun createScaffold(context: C): Scaffold<Variable> {
        val builder = VariableShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
