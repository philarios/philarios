package io.philarios.circleci

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

data class MachineExecutor(val enabled: Boolean?, val image: String?)

class MachineExecutorSpec<in C>(internal val body: MachineExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface MachineExecutorBuilder<out C> {
    val context: C

    fun enabled(value: Boolean)

    fun image(value: String)

    fun include(body: MachineExecutorBuilder<C>.() -> Unit)

    fun include(spec: MachineExecutorSpec<C>)

    fun <C2> include(context: C2, body: MachineExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MachineExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MachineExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MachineExecutorSpec<C2>)
}

class MachineExecutorRef(internal val key: String)

internal data class MachineExecutorShell(var enabled: Scaffold<Boolean>? = null, var image: Scaffold<String>? = null) : Scaffold<MachineExecutor> {
    override suspend fun resolve(registry: Registry): MachineExecutor {
        val value = MachineExecutor(
            enabled?.let{ it.resolve(registry) },
            image?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class MachineExecutorShellBuilder<out C>(override val context: C, internal var shell: MachineExecutorShell = MachineExecutorShell()) : MachineExecutorBuilder<C> {
    override fun enabled(value: Boolean) {
        shell = shell.copy(enabled = Wrapper(value))
    }

    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun include(body: MachineExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: MachineExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: MachineExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: MachineExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: MachineExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: MachineExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MachineExecutorShellBuilder<C2> = MachineExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: MachineExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class MachineExecutorScaffolder<in C>(internal val spec: MachineExecutorSpec<C>) : Scaffolder<C, MachineExecutor> {
    override fun createScaffold(context: C): Scaffold<MachineExecutor> {
        val builder = MachineExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
