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

data class WorkflowJobFilter(val branches: Filter?, val tags: Filter?)

class WorkflowJobFilterSpec<in C>(internal val body: WorkflowJobFilterBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowJobFilterBuilder<out C> {
    val context: C

    fun branches(body: FilterBuilder<C>.() -> Unit)

    fun branches(spec: FilterSpec<C>)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)

    fun tags(body: FilterBuilder<C>.() -> Unit)

    fun tags(spec: FilterSpec<C>)

    fun tags(ref: FilterRef)

    fun tags(value: Filter)

    fun include(body: WorkflowJobFilterBuilder<C>.() -> Unit)

    fun include(spec: WorkflowJobFilterSpec<C>)

    fun <C2> include(context: C2, body: WorkflowJobFilterBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowJobFilterSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobFilterBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobFilterSpec<C2>)
}

class WorkflowJobFilterRef(internal val key: String)

internal data class WorkflowJobFilterShell(var branches: Scaffold<Filter>? = null, var tags: Scaffold<Filter>? = null) : Scaffold<WorkflowJobFilter> {
    override suspend fun resolve(registry: Registry): WorkflowJobFilter {
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
            tags?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJobFilter(
            branches?.let{ it.resolve(registry) },
            tags?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowJobFilterShellBuilder<out C>(override val context: C, internal var shell: WorkflowJobFilterShell = WorkflowJobFilterShell()) : WorkflowJobFilterBuilder<C> {
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

    override fun tags(body: FilterBuilder<C>.() -> Unit) {
        shell = shell.copy(tags = FilterScaffolder<C>(FilterSpec<C>(body)).createScaffold(context))
    }

    override fun tags(spec: FilterSpec<C>) {
        shell = shell.copy(tags = FilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun tags(ref: FilterRef) {
        shell = shell.copy(tags = Deferred(ref.key))
    }

    override fun tags(value: Filter) {
        shell = shell.copy(tags = Wrapper(value))
    }

    override fun include(body: WorkflowJobFilterBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowJobFilterSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowJobFilterBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowJobFilterSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobFilterBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobFilterSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowJobFilterShellBuilder<C2> = WorkflowJobFilterShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowJobFilterShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowJobFilterScaffolder<in C>(internal val spec: WorkflowJobFilterSpec<C>) : Scaffolder<C, WorkflowJobFilter> {
    override fun createScaffold(context: C): Scaffold<WorkflowJobFilter> {
        val builder = WorkflowJobFilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
