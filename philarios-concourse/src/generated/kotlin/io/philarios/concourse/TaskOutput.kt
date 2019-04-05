package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class TaskOutput(val name: String, val path: String?)

class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit)

@DslBuilder
interface TaskOutputBuilder<out C> {
    val context: C

    fun name(value: String)

    fun path(value: String)

    fun include(body: TaskOutputBuilder<C>.() -> Unit)

    fun include(spec: TaskOutputSpec<C>)

    fun <C2> include(context: C2, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskOutputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskOutputSpec<C2>)
}

class TaskOutputRef(internal val key: String)

internal data class TaskOutputShell(var name: Scaffold<String>? = null, var path: Scaffold<String>? = null) : Scaffold<TaskOutput> {
    override suspend fun resolve(registry: Registry): TaskOutput {
        checkNotNull(name) { "TaskOutput is missing the name property" }
        val value = TaskOutput(
            name!!.let{ it.resolve(registry) },
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class TaskOutputShellBuilder<out C>(override val context: C, internal var shell: TaskOutputShell = TaskOutputShell()) : TaskOutputBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: TaskOutputBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskOutputSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskOutputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskOutputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskOutputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskOutputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskOutputShellBuilder<C2> = TaskOutputShellBuilder(context, shell)

    private fun <C2> merge(other: TaskOutputShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskOutputScaffolder<in C>(internal val spec: TaskOutputSpec<C>) : Scaffolder<C, TaskOutput> {
    override fun createScaffold(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
