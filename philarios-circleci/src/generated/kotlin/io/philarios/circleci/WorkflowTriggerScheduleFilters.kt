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

data class WorkflowTriggerScheduleFilters(val branches: Filter?)

class WorkflowTriggerScheduleFiltersSpec<in C>(internal val body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowTriggerScheduleFiltersBuilder<out C> {
    val context: C

    fun branches(body: FilterBuilder<C>.() -> Unit)

    fun branches(spec: FilterSpec<C>)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)

    fun include(body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit)

    fun include(spec: WorkflowTriggerScheduleFiltersSpec<C>)

    fun <C2> include(context: C2, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowTriggerScheduleFiltersSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleFiltersSpec<C2>)
}

class WorkflowTriggerScheduleFiltersRef(internal val key: String)

internal data class WorkflowTriggerScheduleFiltersShell(var branches: Scaffold<Filter>? = null) : Scaffold<WorkflowTriggerScheduleFilters> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerScheduleFilters {
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerScheduleFilters(
            branches?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleFiltersShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerScheduleFiltersShell = WorkflowTriggerScheduleFiltersShell()) : WorkflowTriggerScheduleFiltersBuilder<C> {
    override fun branches(body: FilterBuilder<C>.() -> Unit) {
        shell = shell.copy(branches = FilterScaffolder<C>(FilterSpec<C>(body)).createScaffold(context))
    }

    override fun branches(spec: FilterSpec<C>) {
        shell = shell.copy(branches = FilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun branches(ref: FilterRef) {
        shell = shell.copy(branches = Deferred(ref.key))
    }

    override fun branches(value: Filter) {
        shell = shell.copy(branches = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerScheduleFiltersSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerScheduleFiltersSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleFiltersSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerScheduleFiltersShellBuilder<C2> = WorkflowTriggerScheduleFiltersShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerScheduleFiltersShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowTriggerScheduleFiltersScaffolder<in C>(internal val spec: WorkflowTriggerScheduleFiltersSpec<C>) : Scaffolder<C, WorkflowTriggerScheduleFilters> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerScheduleFilters> {
        val builder = WorkflowTriggerScheduleFiltersShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
