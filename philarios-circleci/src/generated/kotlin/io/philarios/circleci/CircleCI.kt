package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class CircleCI(
        val version: String?,
        val jobs: Map<String, Job>?,
        val workflows: Map<String, Workflow>?
)

class CircleCISpec<in C>(internal val body: CircleCIBuilder<C>.() -> Unit)

@DslBuilder
interface CircleCIBuilder<out C> {
    val context: C

    fun version(value: String)

    fun jobs(key: String, body: JobBuilder<C>.() -> Unit)

    fun jobs(key: String, spec: JobSpec<C>)

    fun jobs(key: String, ref: JobRef)

    fun jobs(key: String, value: Job)

    fun workflows(key: String, body: WorkflowBuilder<C>.() -> Unit)

    fun workflows(key: String, spec: WorkflowSpec<C>)

    fun workflows(key: String, ref: WorkflowRef)

    fun workflows(key: String, value: Workflow)

    fun include(body: CircleCIBuilder<C>.() -> Unit)

    fun include(spec: CircleCISpec<C>)

    fun <C2> include(context: C2, body: CircleCIBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CircleCISpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CircleCIBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CircleCISpec<C2>)
}

class CircleCIRef(internal val key: String)

internal data class CircleCIShell(
        var version: Scaffold<String>? = null,
        var jobs: Map<Scaffold<String>, Scaffold<Job>>? = null,
        var workflows: Map<Scaffold<String>, Scaffold<Workflow>>? = null
) : Scaffold<CircleCI> {
    override suspend fun resolve(registry: Registry): CircleCI {
        coroutineScope {
            jobs?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
            workflows?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
        }
        val value = CircleCI(
            version?.let{ it.resolve(registry) },
            jobs?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            workflows?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

@DslBuilder
internal class CircleCIShellBuilder<out C>(override val context: C, internal var shell: CircleCIShell = CircleCIShell()) : CircleCIBuilder<C> {
    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun jobs(key: String, body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),JobScaffolder<C>(JobSpec<C>(body)).createScaffold(context)))
    }

    override fun jobs(key: String, spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),JobScaffolder<C>(spec).createScaffold(context)))
    }

    override fun jobs(key: String, ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),Deferred(ref.key)))
    }

    override fun jobs(key: String, value: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun workflows(key: String, body: WorkflowBuilder<C>.() -> Unit) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),WorkflowScaffolder<C>(WorkflowSpec<C>(body)).createScaffold(context)))
    }

    override fun workflows(key: String, spec: WorkflowSpec<C>) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),WorkflowScaffolder<C>(spec).createScaffold(context)))
    }

    override fun workflows(key: String, ref: WorkflowRef) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),Deferred(ref.key)))
    }

    override fun workflows(key: String, value: Workflow) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun include(body: CircleCIBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CircleCISpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CircleCIBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CircleCISpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CircleCIBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CircleCISpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CircleCIShellBuilder<C2> = CircleCIShellBuilder(context, shell)

    private fun <C2> merge(other: CircleCIShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class CircleCIScaffolder<in C>(internal val spec: CircleCISpec<C>) : Scaffolder<C, CircleCI> {
    override fun createScaffold(context: C): Scaffold<CircleCI> {
        val builder = CircleCIShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
