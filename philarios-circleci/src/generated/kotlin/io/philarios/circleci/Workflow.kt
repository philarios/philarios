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
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Workflow(val triggers: List<WorkflowTrigger>?, val jobs: List<Map<String, WorkflowJob>>?)

class WorkflowSpec<in C>(internal val body: WorkflowBuilder<C>.() -> Unit)

@DslBuilder
interface WorkflowBuilder<out C> {
    val context: C

    fun trigger(body: WorkflowTriggerBuilder<C>.() -> Unit)

    fun trigger(spec: WorkflowTriggerSpec<C>)

    fun trigger(ref: WorkflowTriggerRef)

    fun trigger(value: WorkflowTrigger)

    fun triggers(triggers: List<WorkflowTrigger>)

    fun jobs(key: String, body: WorkflowJobBuilder<C>.() -> Unit)

    fun jobs(key: String, spec: WorkflowJobSpec<C>)

    fun jobs(key: String, ref: WorkflowJobRef)

    fun jobs(key: String, value: WorkflowJob)

    fun include(body: WorkflowBuilder<C>.() -> Unit)

    fun include(spec: WorkflowSpec<C>)

    fun <C2> include(context: C2, body: WorkflowBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkflowSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowSpec<C2>)
}

class WorkflowRef(internal val key: String)

internal data class WorkflowShell(var triggers: List<Scaffold<WorkflowTrigger>>? = null, var jobs: List<Map<Scaffold<String>, Scaffold<WorkflowJob>>>? = null) : Scaffold<Workflow> {
    override suspend fun resolve(registry: Registry): Workflow {
        coroutineScope {
            triggers?.let{ it.forEach { launch { it.resolve(registry) } } }
            jobs?.let{ it.forEach { it.forEach { it.value.let { launch { it.resolve(registry) } } } } }
        }
        val value = Workflow(
            triggers?.let{ it.map { it.resolve(registry) } },
            jobs?.let{ it.map { it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() } }
        )
        return value
    }
}

@DslBuilder
internal class WorkflowShellBuilder<out C>(override val context: C, internal var shell: WorkflowShell = WorkflowShell()) : WorkflowBuilder<C> {
    override fun trigger(body: WorkflowTriggerBuilder<C>.() -> Unit) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder<C>(WorkflowTriggerSpec<C>(body)).createScaffold(context))
    }

    override fun trigger(spec: WorkflowTriggerSpec<C>) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder<C>(spec).createScaffold(context))
    }

    override fun trigger(ref: WorkflowTriggerRef) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + Deferred(ref.key))
    }

    override fun trigger(value: WorkflowTrigger) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + Wrapper(value))
    }

    override fun triggers(triggers: List<WorkflowTrigger>) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + triggers.map { Wrapper(it) })
    }

    override fun jobs(key: String, body: WorkflowJobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),WorkflowJobScaffolder<C>(WorkflowJobSpec<C>(body)).createScaffold(context))))
    }

    override fun jobs(key: String, spec: WorkflowJobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),WorkflowJobScaffolder<C>(spec).createScaffold(context))))
    }

    override fun jobs(key: String, ref: WorkflowJobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),Deferred(ref.key))))
    }

    override fun jobs(key: String, value: WorkflowJob) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),Wrapper(value))))
    }

    override fun include(body: WorkflowBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowShellBuilder<C2> = WorkflowShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkflowScaffolder<in C>(internal val spec: WorkflowSpec<C>) : Scaffolder<C, Workflow> {
    override fun createScaffold(context: C): Scaffold<Workflow> {
        val builder = WorkflowShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
