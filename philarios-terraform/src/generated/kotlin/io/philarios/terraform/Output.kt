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

data class Output(val name: String, val value: Any)

class OutputSpec<in C>(internal val body: OutputBuilder<C>.() -> Unit)

@DslBuilder
interface OutputBuilder<out C> {
    val context: C

    fun name(value: String)

    fun value(value: Any)

    fun include(body: OutputBuilder<C>.() -> Unit)

    fun include(spec: OutputSpec<C>)

    fun <C2> include(context: C2, body: OutputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: OutputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: OutputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: OutputSpec<C2>)
}

class OutputRef(internal val key: String)

internal data class OutputShell(var name: Scaffold<String>? = null, var value: Scaffold<Any>? = null) : Scaffold<Output> {
    override suspend fun resolve(registry: Registry): Output {
        checkNotNull(name) { "Output is missing the name property" }
        checkNotNull(value) { "Output is missing the value property" }
        val value = Output(
            name!!.let{ it.resolve(registry) },
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class OutputShellBuilder<out C>(override val context: C, internal var shell: OutputShell = OutputShell()) : OutputBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun value(value: Any) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: OutputBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: OutputSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: OutputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: OutputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: OutputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: OutputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): OutputShellBuilder<C2> = OutputShellBuilder(context, shell)

    private fun <C2> merge(other: OutputShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class OutputScaffolder<in C>(internal val spec: OutputSpec<C>) : Scaffolder<C, Output> {
    override fun createScaffold(context: C): Scaffold<Output> {
        val builder = OutputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
