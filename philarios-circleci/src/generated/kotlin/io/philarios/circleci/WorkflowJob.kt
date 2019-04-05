package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class WorkflowJob(
        val requires: List<String>?,
        val context: String?,
        val type: WorkflowJobType?,
        val filters: WorkflowJobFilter?
)

class WorkflowJobSpec<in C>(internal val body: WorkflowJobBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowJobBuilder<out C> {
    val context: C

    fun require(value: String)

    fun requires(requires: List<String>)

    fun context(value: String)

    fun type(value: WorkflowJobType)

    fun filters(body: WorkflowJobFilterBuilder<C>.() -> Unit)

    fun filters(spec: WorkflowJobFilterSpec<C>)

    fun filters(ref: WorkflowJobFilterRef)

    fun filters(value: WorkflowJobFilter)

    fun include(body: WorkflowJobBuilder<C>.() -> Unit)

    fun include(spec: WorkflowJobSpec<C>)

    fun <C2> include(context: C2, body: WorkflowJobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowJobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobSpec<C2>)
}

class WorkflowJobRef(internal val key: String)

internal data class WorkflowJobShell(
        var requires: List<Scaffold<String>>? = null,
        var context: Scaffold<String>? = null,
        var type: Scaffold<WorkflowJobType>? = null,
        var filters: Scaffold<WorkflowJobFilter>? = null
) : Scaffold<WorkflowJob> {
    override suspend fun resolve(registry: Registry): WorkflowJob {
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJob(
            requires?.let{ it.map { it.resolve(registry) } },
            context?.let{ it.resolve(registry) },
            type?.let{ it.resolve(registry) },
            filters?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowJobShellBuilder<out C>(override val context: C, internal var shell: WorkflowJobShell = WorkflowJobShell()) : WorkflowJobBuilder<C> {
    override fun require(value: String) {
        shell = shell.copy(requires = shell.requires.orEmpty() + Wrapper(value))
    }

    override fun requires(requires: List<String>) {
        shell = shell.copy(requires = shell.requires.orEmpty() + requires.map { Wrapper(it) })
    }

    override fun context(value: String) {
        shell = shell.copy(context = Wrapper(value))
    }

    override fun type(value: WorkflowJobType) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun filters(body: WorkflowJobFilterBuilder<C>.() -> Unit) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder<C>(WorkflowJobFilterSpec<C>(body)).createScaffold(context))
    }

    override fun filters(spec: WorkflowJobFilterSpec<C>) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun filters(ref: WorkflowJobFilterRef) {
        shell = shell.copy(filters = Deferred(ref.key))
    }

    override fun filters(value: WorkflowJobFilter) {
        shell = shell.copy(filters = Wrapper(value))
    }

    override fun include(body: WorkflowJobBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowJobSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowJobBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowJobSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowJobShellBuilder<C2> = WorkflowJobShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowJobShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowJobScaffolder<in C>(internal val spec: WorkflowJobSpec<C>) : Scaffolder<C, WorkflowJob> {
    override fun createScaffold(context: C): Scaffold<WorkflowJob> {
        val builder = WorkflowJobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
