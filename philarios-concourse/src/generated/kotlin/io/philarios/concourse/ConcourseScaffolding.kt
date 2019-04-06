package io.philarios.concourse

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ConcourseScaffolder<in C>(internal val spec: ConcourseSpec<C>) : Scaffolder<C, Concourse> {
    override fun createScaffold(context: C): Scaffold<Concourse> {
        val builder = ConcourseShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ConcourseShellBuilder<out C>(override val context: C, internal var shell: ConcourseShell = ConcourseShell()) : ConcourseBuilder<C> {
    override fun team(body: TeamBuilder<C>.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(TeamSpec<C>(body)).createScaffold(context))
    }

    override fun team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(spec).createScaffold(context))
    }

    override fun team(ref: TeamRef) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Deferred(ref.key))
    }

    override fun team(value: Team) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Wrapper(value))
    }

    override fun teams(teams: List<Team>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + teams.map { Wrapper(it) })
    }

    override fun include(body: ConcourseBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ConcourseSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ConcourseBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ConcourseSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConcourseShellBuilder<C2> = ConcourseShellBuilder(context, shell)

    private fun <C2> merge(other: ConcourseShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class ConcourseShell(var teams: List<Scaffold<Team>>? = null) : Scaffold<Concourse> {
    override suspend fun resolve(registry: Registry): Concourse {
        coroutineScope {
            teams?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Concourse(
            teams.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class TeamScaffolder<in C>(internal val spec: TeamSpec<C>) : Scaffolder<C, Team> {
    override fun createScaffold(context: C): Scaffold<Team> {
        val builder = TeamShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TeamShellBuilder<out C>(override val context: C, internal var shell: TeamShell = TeamShell()) : TeamBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun pipeline(body: PipelineBuilder<C>.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder<C>(PipelineSpec<C>(body)).createScaffold(context))
    }

    override fun pipeline(spec: PipelineSpec<C>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder<C>(spec).createScaffold(context))
    }

    override fun pipeline(ref: PipelineRef) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Deferred(ref.key))
    }

    override fun pipeline(value: Pipeline) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Wrapper(value))
    }

    override fun pipelines(pipelines: List<Pipeline>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + pipelines.map { Wrapper(it) })
    }

    override fun include(body: TeamBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TeamSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TeamBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TeamSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TeamShellBuilder<C2> = TeamShellBuilder(context, shell)

    private fun <C2> merge(other: TeamShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TeamShell(var name: Scaffold<String>? = null, var pipelines: List<Scaffold<Pipeline>>? = null) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        checkNotNull(name) { "Team is missing the name property" }
        coroutineScope {
            pipelines?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Team(
            name!!.let{ it.resolve(registry) },
            pipelines.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class PipelineScaffolder<in C>(internal val spec: PipelineSpec<C>) : Scaffolder<C, Pipeline> {
    override fun createScaffold(context: C): Scaffold<Pipeline> {
        val builder = PipelineShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class JobScaffolder<in C>(internal val spec: JobSpec<C>) : Scaffolder<C, Job> {
    override fun createScaffold(context: C): Scaffold<Job> {
        val builder = JobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class JobShellBuilder<out C>(override val context: C, internal var shell: JobShell = JobShell()) : JobBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Step> plan(spec: StepSpec<C, T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> plan(ref: StepRef<T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> plan(value: T) {
        shell = shell.copy(plan = shell.plan.orEmpty() + Wrapper(value))
    }

    override fun serial(value: Boolean) {
        shell = shell.copy(serial = Wrapper(value))
    }

    override fun build_logs_to_retain(value: Int) {
        shell = shell.copy(build_logs_to_retain = Wrapper(value))
    }

    override fun serial_group(value: String) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + Wrapper(value))
    }

    override fun serial_groups(serial_groups: List<String>) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + serial_groups.map { Wrapper(it) })
    }

    override fun max_in_flight(value: Int) {
        shell = shell.copy(max_in_flight = Wrapper(value))
    }

    override fun public(value: Boolean) {
        shell = shell.copy(public = Wrapper(value))
    }

    override fun disable_manual_trigger(value: Boolean) {
        shell = shell.copy(disable_manual_trigger = Wrapper(value))
    }

    override fun interruptible(value: Boolean) {
        shell = shell.copy(interruptible = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun include(body: JobBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: JobSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: JobBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: JobSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: JobSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): JobShellBuilder<C2> = JobShellBuilder(context, shell)

    private fun <C2> merge(other: JobShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class JobShell(
        var name: Scaffold<String>? = null,
        var plan: List<Scaffold<Step>>? = null,
        var serial: Scaffold<Boolean>? = null,
        var build_logs_to_retain: Scaffold<Int>? = null,
        var serial_groups: List<Scaffold<String>>? = null,
        var max_in_flight: Scaffold<Int>? = null,
        var public: Scaffold<Boolean>? = null,
        var disable_manual_trigger: Scaffold<Boolean>? = null,
        var interruptible: Scaffold<Boolean>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null
) : Scaffold<Job> {
    override suspend fun resolve(registry: Registry): Job {
        checkNotNull(name) { "Job is missing the name property" }
        coroutineScope {
            plan?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Job(
            name!!.let{ it.resolve(registry) },
            plan.orEmpty().let{ it.map { it.resolve(registry) } },
            serial?.let{ it.resolve(registry) },
            build_logs_to_retain?.let{ it.resolve(registry) },
            serial_groups.orEmpty().let{ it.map { it.resolve(registry) } },
            max_in_flight?.let{ it.resolve(registry) },
            public?.let{ it.resolve(registry) },
            disable_manual_trigger?.let{ it.resolve(registry) },
            interruptible?.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) }
        )
        return value
    }
}

class StepScaffolder<in C, out T : Step>(internal val spec: StepSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is GetSpec<C> -> GetScaffolder(spec).createScaffold(context)
            is PutSpec<C> -> PutScaffolder(spec).createScaffold(context)
            is TaskSpec<C> -> TaskScaffolder(spec).createScaffold(context)
            is AggregateSpec<C> -> AggregateScaffolder(spec).createScaffold(context)
            is DoSpec<C> -> DoScaffolder(spec).createScaffold(context)
            is TrySpec<C> -> TryScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class GetScaffolder<in C>(internal val spec: GetSpec<C>) : Scaffolder<C, Get> {
    override fun createScaffold(context: C): Scaffold<Get> {
        val builder = GetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PutScaffolder<in C>(internal val spec: PutSpec<C>) : Scaffolder<C, Put> {
    override fun createScaffold(context: C): Scaffold<Put> {
        val builder = PutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskScaffolder<in C>(internal val spec: TaskSpec<C>) : Scaffolder<C, Task> {
    override fun createScaffold(context: C): Scaffold<Task> {
        val builder = TaskShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AggregateScaffolder<in C>(internal val spec: AggregateSpec<C>) : Scaffolder<C, Aggregate> {
    override fun createScaffold(context: C): Scaffold<Aggregate> {
        val builder = AggregateShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoScaffolder<in C>(internal val spec: DoSpec<C>) : Scaffolder<C, Do> {
    override fun createScaffold(context: C): Scaffold<Do> {
        val builder = DoShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TryScaffolder<in C>(internal val spec: TrySpec<C>) : Scaffolder<C, Try> {
    override fun createScaffold(context: C): Scaffold<Try> {
        val builder = TryShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class GetShellBuilder<out C>(override val context: C, internal var shell: GetShell = GetShell()) : GetBuilder<C> {
    override fun get(value: String) {
        shell = shell.copy(get = Wrapper(value))
    }

    override fun resource(value: String) {
        shell = shell.copy(resource = Wrapper(value))
    }

    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun passed(value: String) {
        shell = shell.copy(passed = shell.passed.orEmpty() + Wrapper(value))
    }

    override fun passed(passed: List<String>) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed.map { Wrapper(it) })
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun trigger(value: Boolean) {
        shell = shell.copy(trigger = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: GetBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: GetSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: GetBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: GetSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: GetBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: GetSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GetShellBuilder<C2> = GetShellBuilder(context, shell)

    private fun <C2> merge(other: GetShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PutShellBuilder<out C>(override val context: C, internal var shell: PutShell = PutShell()) : PutBuilder<C> {
    override fun put(value: String) {
        shell = shell.copy(put = Wrapper(value))
    }

    override fun resource(value: String) {
        shell = shell.copy(resource = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun get_params(key: String, value: Any) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun get_params(pair: Pair<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun get_params(get_params: Map<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + get_params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: PutBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PutSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PutBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PutSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PutBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PutSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PutShellBuilder<C2> = PutShellBuilder(context, shell)

    private fun <C2> merge(other: PutShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TaskShellBuilder<out C>(override val context: C, internal var shell: TaskShell = TaskShell()) : TaskBuilder<C> {
    override fun task(value: String) {
        shell = shell.copy(task = Wrapper(value))
    }

    override fun config(body: TaskConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(config = TaskConfigScaffolder<C>(TaskConfigSpec<C>(body)).createScaffold(context))
    }

    override fun config(spec: TaskConfigSpec<C>) {
        shell = shell.copy(config = TaskConfigScaffolder<C>(spec).createScaffold(context))
    }

    override fun config(ref: TaskConfigRef) {
        shell = shell.copy(config = Deferred(ref.key))
    }

    override fun config(value: TaskConfig) {
        shell = shell.copy(config = Wrapper(value))
    }

    override fun file(value: String) {
        shell = shell.copy(file = Wrapper(value))
    }

    override fun privileged(value: String) {
        shell = shell.copy(privileged = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun input_mapping(key: String, value: String) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun input_mapping(pair: Pair<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun input_mapping(input_mapping: Map<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + input_mapping.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun output_mapping(key: String, value: String) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun output_mapping(pair: Pair<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun output_mapping(output_mapping: Map<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + output_mapping.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: TaskBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskShellBuilder<C2> = TaskShellBuilder(context, shell)

    private fun <C2> merge(other: TaskShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AggregateShellBuilder<out C>(override val context: C, internal var shell: AggregateShell = AggregateShell()) : AggregateBuilder<C> {
    override fun <T : Step> aggregate(spec: StepSpec<C, T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> aggregate(ref: StepRef<T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> aggregate(value: T) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: AggregateBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AggregateSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AggregateBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AggregateSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AggregateBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AggregateSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AggregateShellBuilder<C2> = AggregateShellBuilder(context, shell)

    private fun <C2> merge(other: AggregateShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DoShellBuilder<out C>(override val context: C, internal var shell: DoShell = DoShell()) : DoBuilder<C> {
    override fun <T : Step> `do`(spec: StepSpec<C, T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> `do`(ref: StepRef<T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> `do`(value: T) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: DoBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DoSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DoBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DoSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DoBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DoSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DoShellBuilder<C2> = DoShellBuilder(context, shell)

    private fun <C2> merge(other: DoShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TryShellBuilder<out C>(override val context: C, internal var shell: TryShell = TryShell()) : TryBuilder<C> {
    override fun <T : Step> `try`(spec: StepSpec<C, T>) {
        shell = shell.copy(`try` = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> `try`(ref: StepRef<T>) {
        shell = shell.copy(`try` = Deferred(ref.key))
    }

    override fun <T : Step> `try`(value: T) {
        shell = shell.copy(`try` = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun timeout(value: String) {
        shell = shell.copy(timeout = Wrapper(value))
    }

    override fun attempts(value: Int) {
        shell = shell.copy(attempts = Wrapper(value))
    }

    override fun include(body: TryBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TrySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TryBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TrySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TrySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TryShellBuilder<C2> = TryShellBuilder(context, shell)

    private fun <C2> merge(other: TryShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal sealed class StepShell

internal data class GetShell(
        var get: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var version: Scaffold<String>? = null,
        var passed: List<Scaffold<String>>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var trigger: Scaffold<Boolean>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Get> {
    override suspend fun resolve(registry: Registry): Get {
        checkNotNull(get) { "Get is missing the get property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Get(
            get!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            version?.let{ it.resolve(registry) },
            passed.orEmpty().let{ it.map { it.resolve(registry) } },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            trigger?.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PutShell(
        var put: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var get_params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Put> {
    override suspend fun resolve(registry: Registry): Put {
        checkNotNull(put) { "Put is missing the put property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Put(
            put!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            get_params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskShell(
        var task: Scaffold<String>? = null,
        var config: Scaffold<TaskConfig>? = null,
        var file: Scaffold<String>? = null,
        var privileged: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var image: Scaffold<String>? = null,
        var input_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var output_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Task> {
    override suspend fun resolve(registry: Registry): Task {
        checkNotNull(task) { "Task is missing the task property" }
        checkNotNull(config) { "Task is missing the config property" }
        coroutineScope {
            config?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Task(
            task!!.let{ it.resolve(registry) },
            config!!.let{ it.resolve(registry) },
            file?.let{ it.resolve(registry) },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            image?.let{ it.resolve(registry) },
            input_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            output_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AggregateShell(
        var aggregate: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Aggregate> {
    override suspend fun resolve(registry: Registry): Aggregate {
        coroutineScope {
            aggregate?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Aggregate(
            aggregate.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DoShell(
        var `do`: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Do> {
    override suspend fun resolve(registry: Registry): Do {
        coroutineScope {
            `do`?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Do(
            `do`.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TryShell(
        var `try`: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Try> {
    override suspend fun resolve(registry: Registry): Try {
        checkNotNull(`try`) { "Try is missing the try property" }
        coroutineScope {
            `try`?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Try(
            `try`!!.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

class TaskConfigScaffolder<in C>(internal val spec: TaskConfigSpec<C>) : Scaffolder<C, TaskConfig> {
    override fun createScaffold(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskConfigShellBuilder<out C>(override val context: C, internal var shell: TaskConfigShell = TaskConfigShell()) : TaskConfigBuilder<C> {
    override fun platform(value: String) {
        shell = shell.copy(platform = Wrapper(value))
    }

    override fun image_resource(body: TaskResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceScaffolder<C>(TaskResourceSpec<C>(body)).createScaffold(context))
    }

    override fun image_resource(spec: TaskResourceSpec<C>) {
        shell = shell.copy(image_resource = TaskResourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun image_resource(ref: TaskResourceRef) {
        shell = shell.copy(image_resource = Deferred(ref.key))
    }

    override fun image_resource(value: TaskResource) {
        shell = shell.copy(image_resource = Wrapper(value))
    }

    override fun rootfs_uri(value: String) {
        shell = shell.copy(rootfs_uri = Wrapper(value))
    }

    override fun input(body: TaskInputBuilder<C>.() -> Unit) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder<C>(TaskInputSpec<C>(body)).createScaffold(context))
    }

    override fun input(spec: TaskInputSpec<C>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder<C>(spec).createScaffold(context))
    }

    override fun input(ref: TaskInputRef) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Deferred(ref.key))
    }

    override fun input(value: TaskInput) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Wrapper(value))
    }

    override fun inputs(inputs: List<TaskInput>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + inputs.map { Wrapper(it) })
    }

    override fun output(body: TaskOutputBuilder<C>.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder<C>(TaskOutputSpec<C>(body)).createScaffold(context))
    }

    override fun output(spec: TaskOutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder<C>(spec).createScaffold(context))
    }

    override fun output(ref: TaskOutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Deferred(ref.key))
    }

    override fun output(value: TaskOutput) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Wrapper(value))
    }

    override fun outputs(outputs: List<TaskOutput>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { Wrapper(it) })
    }

    override fun cache(body: TaskCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder<C>(TaskCacheSpec<C>(body)).createScaffold(context))
    }

    override fun cache(spec: TaskCacheSpec<C>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun cache(ref: TaskCacheRef) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Deferred(ref.key))
    }

    override fun cache(value: TaskCache) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Wrapper(value))
    }

    override fun caches(caches: List<TaskCache>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + caches.map { Wrapper(it) })
    }

    override fun run(body: TaskRunConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(run = TaskRunConfigScaffolder<C>(TaskRunConfigSpec<C>(body)).createScaffold(context))
    }

    override fun run(spec: TaskRunConfigSpec<C>) {
        shell = shell.copy(run = TaskRunConfigScaffolder<C>(spec).createScaffold(context))
    }

    override fun run(ref: TaskRunConfigRef) {
        shell = shell.copy(run = Deferred(ref.key))
    }

    override fun run(value: TaskRunConfig) {
        shell = shell.copy(run = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun include(body: TaskConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskConfigSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskConfigShellBuilder<C2> = TaskConfigShellBuilder(context, shell)

    private fun <C2> merge(other: TaskConfigShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TaskConfigShell(
        var platform: Scaffold<String>? = null,
        var image_resource: Scaffold<TaskResource>? = null,
        var rootfs_uri: Scaffold<String>? = null,
        var inputs: List<Scaffold<TaskInput>>? = null,
        var outputs: List<Scaffold<TaskOutput>>? = null,
        var caches: List<Scaffold<TaskCache>>? = null,
        var run: Scaffold<TaskRunConfig>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null
) : Scaffold<TaskConfig> {
    override suspend fun resolve(registry: Registry): TaskConfig {
        checkNotNull(platform) { "TaskConfig is missing the platform property" }
        checkNotNull(image_resource) { "TaskConfig is missing the image_resource property" }
        coroutineScope {
            image_resource?.let{ launch { it.resolve(registry) } }
            inputs?.let{ it.forEach { launch { it.resolve(registry) } } }
            outputs?.let{ it.forEach { launch { it.resolve(registry) } } }
            caches?.let{ it.forEach { launch { it.resolve(registry) } } }
            run?.let{ launch { it.resolve(registry) } }
        }
        val value = TaskConfig(
            platform!!.let{ it.resolve(registry) },
            image_resource!!.let{ it.resolve(registry) },
            rootfs_uri?.let{ it.resolve(registry) },
            inputs.orEmpty().let{ it.map { it.resolve(registry) } },
            outputs.orEmpty().let{ it.map { it.resolve(registry) } },
            caches.orEmpty().let{ it.map { it.resolve(registry) } },
            run?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

class TaskResourceScaffolder<in C>(internal val spec: TaskResourceSpec<C>) : Scaffolder<C, TaskResource> {
    override fun createScaffold(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskResourceShellBuilder<out C>(override val context: C, internal var shell: TaskResourceShell = TaskResourceShell()) : TaskResourceBuilder<C> {
    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun version(key: String, value: String) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun version(pair: Pair<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun version(version: Map<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + version.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun include(body: TaskResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskResourceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskResourceShellBuilder<C2> = TaskResourceShellBuilder(context, shell)

    private fun <C2> merge(other: TaskResourceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TaskResourceShell(
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var version: Map<Scaffold<String>, Scaffold<String>>? = null
) : Scaffold<TaskResource> {
    override suspend fun resolve(registry: Registry): TaskResource {
        checkNotNull(type) { "TaskResource is missing the type property" }
        val value = TaskResource(
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            version.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

class TaskInputScaffolder<in C>(internal val spec: TaskInputSpec<C>) : Scaffolder<C, TaskInput> {
    override fun createScaffold(context: C): Scaffold<TaskInput> {
        val builder = TaskInputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskInputShellBuilder<out C>(override val context: C, internal var shell: TaskInputShell = TaskInputShell()) : TaskInputBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun optional(value: Boolean) {
        shell = shell.copy(optional = Wrapper(value))
    }

    override fun include(body: TaskInputBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskInputSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskInputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskInputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskInputShellBuilder<C2> = TaskInputShellBuilder(context, shell)

    private fun <C2> merge(other: TaskInputShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TaskInputShell(
        var name: Scaffold<String>? = null,
        var path: Scaffold<String>? = null,
        var optional: Scaffold<Boolean>? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        checkNotNull(name) { "TaskInput is missing the name property" }
        val value = TaskInput(
            name!!.let{ it.resolve(registry) },
            path?.let{ it.resolve(registry) },
            optional?.let{ it.resolve(registry) }
        )
        return value
    }
}

class TaskOutputScaffolder<in C>(internal val spec: TaskOutputSpec<C>) : Scaffolder<C, TaskOutput> {
    override fun createScaffold(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class TaskCacheScaffolder<in C>(internal val spec: TaskCacheSpec<C>) : Scaffolder<C, TaskCache> {
    override fun createScaffold(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskCacheShellBuilder<out C>(override val context: C, internal var shell: TaskCacheShell = TaskCacheShell()) : TaskCacheBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: TaskCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskCacheShellBuilder<C2> = TaskCacheShellBuilder(context, shell)

    private fun <C2> merge(other: TaskCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TaskCacheShell(var path: Scaffold<String>? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        checkNotNull(path) { "TaskCache is missing the path property" }
        val value = TaskCache(
            path!!.let{ it.resolve(registry) }
        )
        return value
    }
}

class TaskRunConfigScaffolder<in C>(internal val spec: TaskRunConfigSpec<C>) : Scaffolder<C, TaskRunConfig> {
    override fun createScaffold(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskRunConfigShellBuilder<out C>(override val context: C, internal var shell: TaskRunConfigShell = TaskRunConfigShell()) : TaskRunConfigBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun arg(value: String) {
        shell = shell.copy(args = shell.args.orEmpty() + Wrapper(value))
    }

    override fun args(args: List<String>) {
        shell = shell.copy(args = shell.args.orEmpty() + args.map { Wrapper(it) })
    }

    override fun dir(value: String) {
        shell = shell.copy(dir = Wrapper(value))
    }

    override fun user(value: String) {
        shell = shell.copy(user = Wrapper(value))
    }

    override fun include(body: TaskRunConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskRunConfigSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskRunConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskRunConfigShellBuilder<C2> = TaskRunConfigShellBuilder(context, shell)

    private fun <C2> merge(other: TaskRunConfigShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class TaskRunConfigShell(
        var path: Scaffold<String>? = null,
        var args: List<Scaffold<String>>? = null,
        var dir: Scaffold<String>? = null,
        var user: Scaffold<String>? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        checkNotNull(path) { "TaskRunConfig is missing the path property" }
        val value = TaskRunConfig(
            path!!.let{ it.resolve(registry) },
            args.orEmpty().let{ it.map { it.resolve(registry) } },
            dir?.let{ it.resolve(registry) },
            user?.let{ it.resolve(registry) }
        )
        return value
    }
}

class ResourceScaffolder<in C>(internal val spec: ResourceSpec<C>) : Scaffolder<C, Resource> {
    override fun createScaffold(context: C): Scaffold<Resource> {
        val builder = ResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ResourceShellBuilder<out C>(override val context: C, internal var shell: ResourceShell = ResourceShell()) : ResourceBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun check_every(value: String) {
        shell = shell.copy(check_every = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun webhook_token(value: String) {
        shell = shell.copy(webhook_token = Wrapper(value))
    }

    override fun include(body: ResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ResourceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceShellBuilder<C2> = ResourceShellBuilder(context, shell)

    private fun <C2> merge(other: ResourceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class ResourceShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var check_every: Scaffold<String>? = null,
        var tags: List<Scaffold<String>>? = null,
        var webhook_token: Scaffold<String>? = null
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        checkNotNull(name) { "Resource is missing the name property" }
        checkNotNull(type) { "Resource is missing the type property" }
        val value = Resource(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            check_every?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            webhook_token?.let{ it.resolve(registry) }
        )
        return value
    }
}

class ResourceTypeScaffolder<in C>(internal val spec: ResourceTypeSpec<C>) : Scaffolder<C, ResourceType> {
    override fun createScaffold(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ResourceTypeShellBuilder<out C>(override val context: C, internal var shell: ResourceTypeShell = ResourceTypeShell()) : ResourceTypeBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun privileged(value: Boolean) {
        shell = shell.copy(privileged = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: ResourceTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ResourceTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ResourceTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceTypeShellBuilder<C2> = ResourceTypeShellBuilder(context, shell)

    private fun <C2> merge(other: ResourceTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class ResourceTypeShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var privileged: Scaffold<Boolean>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<ResourceType> {
    override suspend fun resolve(registry: Registry): ResourceType {
        checkNotNull(name) { "ResourceType is missing the name property" }
        checkNotNull(type) { "ResourceType is missing the type property" }
        val value = ResourceType(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class GroupScaffolder<in C>(internal val spec: GroupSpec<C>) : Scaffolder<C, Group> {
    override fun createScaffold(context: C): Scaffold<Group> {
        val builder = GroupShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class GroupShellBuilder<out C>(override val context: C, internal var shell: GroupShell = GroupShell()) : GroupBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun job(value: String) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Wrapper(value))
    }

    override fun jobs(jobs: List<String>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs.map { Wrapper(it) })
    }

    override fun resource(value: String) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(value))
    }

    override fun resources(resources: List<String>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    override fun include(body: GroupBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: GroupSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: GroupBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: GroupSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GroupShellBuilder<C2> = GroupShellBuilder(context, shell)

    private fun <C2> merge(other: GroupShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class GroupShell(
        var name: Scaffold<String>? = null,
        var jobs: List<Scaffold<String>>? = null,
        var resources: List<Scaffold<String>>? = null
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        checkNotNull(name) { "Group is missing the name property" }
        val value = Group(
            name!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { it.resolve(registry) } },
            resources.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}
