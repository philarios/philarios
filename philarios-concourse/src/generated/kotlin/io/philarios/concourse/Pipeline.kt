package io.philarios.concourse

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

data class Pipeline(
        val name: String,
        val jobs: List<Job>,
        val resources: List<Resource>,
        val resource_types: List<ResourceType>,
        val groups: List<Group>
)

class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit)

@DslBuilder
interface PipelineBuilder<out C> {
    val context: C

    fun name(value: String)

    fun job(body: JobBuilder<C>.() -> Unit)

    fun job(spec: JobSpec<C>)

    fun job(ref: JobRef)

    fun job(value: Job)

    fun jobs(jobs: List<Job>)

    fun resource(body: ResourceBuilder<C>.() -> Unit)

    fun resource(spec: ResourceSpec<C>)

    fun resource(ref: ResourceRef)

    fun resource(value: Resource)

    fun resources(resources: List<Resource>)

    fun resource_type(body: ResourceTypeBuilder<C>.() -> Unit)

    fun resource_type(spec: ResourceTypeSpec<C>)

    fun resource_type(ref: ResourceTypeRef)

    fun resource_type(value: ResourceType)

    fun resource_types(resource_types: List<ResourceType>)

    fun group(body: GroupBuilder<C>.() -> Unit)

    fun group(spec: GroupSpec<C>)

    fun group(ref: GroupRef)

    fun group(value: Group)

    fun groups(groups: List<Group>)

    fun include(body: PipelineBuilder<C>.() -> Unit)

    fun include(spec: PipelineSpec<C>)

    fun <C2> include(context: C2, body: PipelineBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PipelineSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PipelineBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PipelineSpec<C2>)
}

class PipelineRef(internal val key: String)

internal data class PipelineShell(
        var name: Scaffold<String>? = null,
        var jobs: List<Scaffold<Job>>? = null,
        var resources: List<Scaffold<Resource>>? = null,
        var resource_types: List<Scaffold<ResourceType>>? = null,
        var groups: List<Scaffold<Group>>? = null
) : Scaffold<Pipeline> {
    override suspend fun resolve(registry: Registry): Pipeline {
        checkNotNull(name) { "Pipeline is missing the name property" }
        coroutineScope {
            jobs?.let{ it.forEach { launch { it.resolve(registry) } } }
            resources?.let{ it.forEach { launch { it.resolve(registry) } } }
            resource_types?.let{ it.forEach { launch { it.resolve(registry) } } }
            groups?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Pipeline(
            name!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { it.resolve(registry) } },
            resources.orEmpty().let{ it.map { it.resolve(registry) } },
            resource_types.orEmpty().let{ it.map { it.resolve(registry) } },
            groups.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class PipelineShellBuilder<out C>(override val context: C, internal var shell: PipelineShell = PipelineShell()) : PipelineBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun job(body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobScaffolder<C>(JobSpec<C>(body)).createScaffold(context))
    }

    override fun job(spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobScaffolder<C>(spec).createScaffold(context))
    }

    override fun job(ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Deferred(ref.key))
    }

    override fun job(value: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Wrapper(value))
    }

    override fun jobs(jobs: List<Job>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs.map { Wrapper(it) })
    }

    override fun resource(body: ResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(ResourceSpec<C>(body)).createScaffold(context))
    }

    override fun resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Deferred(ref.key))
    }

    override fun resource(value: Resource) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(value))
    }

    override fun resources(resources: List<Resource>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    override fun resource_type(body: ResourceTypeBuilder<C>.() -> Unit) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeScaffolder<C>(ResourceTypeSpec<C>(body)).createScaffold(context))
    }

    override fun resource_type(spec: ResourceTypeSpec<C>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeScaffolder<C>(spec).createScaffold(context))
    }

    override fun resource_type(ref: ResourceTypeRef) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + Deferred(ref.key))
    }

    override fun resource_type(value: ResourceType) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + Wrapper(value))
    }

    override fun resource_types(resource_types: List<ResourceType>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + resource_types.map { Wrapper(it) })
    }

    override fun group(body: GroupBuilder<C>.() -> Unit) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupScaffolder<C>(GroupSpec<C>(body)).createScaffold(context))
    }

    override fun group(spec: GroupSpec<C>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupScaffolder<C>(spec).createScaffold(context))
    }

    override fun group(ref: GroupRef) {
        shell = shell.copy(groups = shell.groups.orEmpty() + Deferred(ref.key))
    }

    override fun group(value: Group) {
        shell = shell.copy(groups = shell.groups.orEmpty() + Wrapper(value))
    }

    override fun groups(groups: List<Group>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + groups.map { Wrapper(it) })
    }

    override fun include(body: PipelineBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PipelineSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PipelineBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PipelineSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PipelineBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PipelineSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PipelineShellBuilder<C2> = PipelineShellBuilder(context, shell)

    private fun <C2> merge(other: PipelineShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class PipelineScaffolder<in C>(internal val spec: PipelineSpec<C>) : Scaffolder<C, Pipeline> {
    override fun createScaffold(context: C): Scaffold<Pipeline> {
        val builder = PipelineShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
