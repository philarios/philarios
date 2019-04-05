package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class WorkflowTrigger(val schedule: WorkflowTriggerSchedule?)

class WorkflowTriggerSpec<in C>(internal val body: WorkflowTriggerBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerBuilder<out C> {
    val context: C

    fun schedule(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun schedule(spec: WorkflowTriggerScheduleSpec<C>)

    fun schedule(ref: WorkflowTriggerScheduleRef)

    fun schedule(value: WorkflowTriggerSchedule)

    fun include(body: WorkflowTriggerBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerSpec<C2>)
}

class WorkflowTriggerRef(internal val key: String)

internal data class WorkflowTriggerShell(var schedule: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTrigger> {
    override suspend fun resolve(registry: Registry): WorkflowTrigger {
        coroutineScope {
            schedule?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTrigger(
            schedule?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowTriggerShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerShell = WorkflowTriggerShell()) : WorkflowTriggerBuilder<C> {
    override fun schedule(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder<C>(WorkflowTriggerScheduleSpec<C>(body)).createScaffold(context))
    }

    override fun schedule(spec: WorkflowTriggerScheduleSpec<C>) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder<C>(spec).createScaffold(context))
    }

    override fun schedule(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(schedule = Deferred(ref.key))
    }

    override fun schedule(value: WorkflowTriggerSchedule) {
        shell = shell.copy(schedule = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerShellBuilder<C2> = WorkflowTriggerShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowTriggerScaffolder<in C>(internal val spec: WorkflowTriggerSpec<C>) : Scaffolder<C, WorkflowTrigger> {
    override fun createScaffold(context: C): Scaffold<WorkflowTrigger> {
        val builder = WorkflowTriggerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
