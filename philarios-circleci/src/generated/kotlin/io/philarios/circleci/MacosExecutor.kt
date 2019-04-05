package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class MacosExecutor(val xcode: String?)

class MacosExecutorSpec<in C>(internal val body: MacosExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface MacosExecutorBuilder<out C> {
    val context: C

    fun xcode(value: String)

    fun include(body: MacosExecutorBuilder<C>.() -> Unit)

    fun include(spec: MacosExecutorSpec<C>)

    fun <C2> include(context: C2, body: MacosExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MacosExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MacosExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MacosExecutorSpec<C2>)
}

class MacosExecutorRef(internal val key: String)

internal data class MacosExecutorShell(var xcode: Scaffold<String>? = null) : Scaffold<MacosExecutor> {
    override suspend fun resolve(registry: Registry): MacosExecutor {
        val value = MacosExecutor(
            xcode?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class MacosExecutorShellBuilder<out C>(override val context: C, internal var shell: MacosExecutorShell = MacosExecutorShell()) : MacosExecutorBuilder<C> {
    override fun xcode(value: String) {
        shell = shell.copy(xcode = Wrapper(value))
    }

    override fun include(body: MacosExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: MacosExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: MacosExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: MacosExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: MacosExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: MacosExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MacosExecutorShellBuilder<C2> = MacosExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: MacosExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class MacosExecutorScaffolder<in C>(internal val spec: MacosExecutorSpec<C>) : Scaffolder<C, MacosExecutor> {
    override fun createScaffold(context: C): Scaffold<MacosExecutor> {
        val builder = MacosExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
