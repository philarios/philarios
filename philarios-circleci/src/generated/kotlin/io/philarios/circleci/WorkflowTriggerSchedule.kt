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

data class WorkflowTriggerSchedule(val cron: String?, val filters: WorkflowTriggerSchedule?)

class WorkflowTriggerScheduleSpec<in C>(internal val body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerScheduleBuilder<out C> {
    val context: C

    fun cron(value: String)

    fun filters(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun filters(spec: WorkflowTriggerScheduleSpec<C>)

    fun filters(ref: WorkflowTriggerScheduleRef)

    fun filters(value: WorkflowTriggerSchedule)

    fun include(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerScheduleSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerScheduleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleSpec<C2>)
}

class WorkflowTriggerScheduleRef(internal val key: String)

internal data class WorkflowTriggerScheduleShell(var cron: Scaffold<String>? = null, var filters: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTriggerSchedule> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerSchedule {
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerSchedule(
            cron?.let{ it.resolve(registry) },
            filters?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerScheduleShell = WorkflowTriggerScheduleShell()) : WorkflowTriggerScheduleBuilder<C> {
    override fun cron(value: String) {
        shell = shell.copy(cron = Wrapper(value))
    }

    override fun filters(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder<C>(WorkflowTriggerScheduleSpec<C>(body)).createScaffold(context))
    }

    override fun filters(spec: WorkflowTriggerScheduleSpec<C>) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder<C>(spec).createScaffold(context))
    }

    override fun filters(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(filters = Deferred(ref.key))
    }

    override fun filters(value: WorkflowTriggerSchedule) {
        shell = shell.copy(filters = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerScheduleSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerScheduleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerScheduleShellBuilder<C2> = WorkflowTriggerScheduleShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerScheduleShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowTriggerScheduleScaffolder<in C>(internal val spec: WorkflowTriggerScheduleSpec<C>) : Scaffolder<C, WorkflowTriggerSchedule> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerSchedule> {
        val builder = WorkflowTriggerScheduleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
