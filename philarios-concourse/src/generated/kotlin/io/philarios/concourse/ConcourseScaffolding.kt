// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
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
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ConcourseScaffolder(internal val spec: ConcourseSpec) : Scaffolder<Concourse> {
    override fun createScaffold(): Scaffold<Concourse> {
        val builder = ConcourseShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ConcourseShellBuilder(internal var shell: ConcourseShell = ConcourseShell()) : ConcourseBuilder {
    override fun team(body: TeamBuilder.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder(TeamSpec(body)).createScaffold())
    }

    override fun team(spec: TeamSpec) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder(spec).createScaffold())
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

class TeamScaffolder(internal val spec: TeamSpec) : Scaffolder<Team> {
    override fun createScaffold(): Scaffold<Team> {
        val builder = TeamShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TeamShellBuilder(internal var shell: TeamShell = TeamShell()) : TeamBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun pipeline(body: PipelineBuilder.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder(PipelineSpec(body)).createScaffold())
    }

    override fun pipeline(spec: PipelineSpec) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder(spec).createScaffold())
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

class PipelineScaffolder(internal val spec: PipelineSpec) : Scaffolder<Pipeline> {
    override fun createScaffold(): Scaffold<Pipeline> {
        val builder = PipelineShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class PipelineShellBuilder(internal var shell: PipelineShell = PipelineShell()) : PipelineBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun job(body: JobBuilder.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobScaffolder(JobSpec(body)).createScaffold())
    }

    override fun job(spec: JobSpec) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobScaffolder(spec).createScaffold())
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

    override fun resource(body: ResourceBuilder.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder(ResourceSpec(body)).createScaffold())
    }

    override fun resource(spec: ResourceSpec) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder(spec).createScaffold())
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

    override fun resource_type(body: ResourceTypeBuilder.() -> Unit) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeScaffolder(ResourceTypeSpec(body)).createScaffold())
    }

    override fun resource_type(spec: ResourceTypeSpec) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeScaffolder(spec).createScaffold())
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

    override fun group(body: GroupBuilder.() -> Unit) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupScaffolder(GroupSpec(body)).createScaffold())
    }

    override fun group(spec: GroupSpec) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupScaffolder(spec).createScaffold())
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

class JobScaffolder(internal val spec: JobSpec) : Scaffolder<Job> {
    override fun createScaffold(): Scaffold<Job> {
        val builder = JobShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class JobShellBuilder(internal var shell: JobShell = JobShell()) : JobBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Step> plan(spec: StepSpec<T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
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

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        shell = shell.copy(ensure = Wrapper(value))
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

class StepScaffolder<out T : Step>(internal val spec: StepSpec<T>) : Scaffolder<T> {
    override fun createScaffold(): Scaffold<T> {
        val result = when (spec) {
            is GetSpec -> GetScaffolder(spec).createScaffold()
            is PutSpec -> PutScaffolder(spec).createScaffold()
            is TaskSpec -> TaskScaffolder(spec).createScaffold()
            is AggregateSpec -> AggregateScaffolder(spec).createScaffold()
            is DoSpec -> DoScaffolder(spec).createScaffold()
            is TrySpec -> TryScaffolder(spec).createScaffold()
        }
        return result as Scaffold<T>
    }
}

class GetScaffolder(internal val spec: GetSpec) : Scaffolder<Get> {
    override fun createScaffold(): Scaffold<Get> {
        val builder = GetShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class PutScaffolder(internal val spec: PutSpec) : Scaffolder<Put> {
    override fun createScaffold(): Scaffold<Put> {
        val builder = PutShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskScaffolder(internal val spec: TaskSpec) : Scaffolder<Task> {
    override fun createScaffold(): Scaffold<Task> {
        val builder = TaskShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class AggregateScaffolder(internal val spec: AggregateSpec) : Scaffolder<Aggregate> {
    override fun createScaffold(): Scaffold<Aggregate> {
        val builder = AggregateShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoScaffolder(internal val spec: DoSpec) : Scaffolder<Do> {
    override fun createScaffold(): Scaffold<Do> {
        val builder = DoShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class TryScaffolder(internal val spec: TrySpec) : Scaffolder<Try> {
    override fun createScaffold(): Scaffold<Try> {
        val builder = TryShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class GetShellBuilder(internal var shell: GetShell = GetShell()) : GetBuilder {
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

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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
}

@DslBuilder
internal class PutShellBuilder(internal var shell: PutShell = PutShell()) : PutBuilder {
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

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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
}

@DslBuilder
internal class TaskShellBuilder(internal var shell: TaskShell = TaskShell()) : TaskBuilder {
    override fun task(value: String) {
        shell = shell.copy(task = Wrapper(value))
    }

    override fun config(body: TaskConfigBuilder.() -> Unit) {
        shell = shell.copy(config = TaskConfigScaffolder(TaskConfigSpec(body)).createScaffold())
    }

    override fun config(spec: TaskConfigSpec) {
        shell = shell.copy(config = TaskConfigScaffolder(spec).createScaffold())
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

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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
}

@DslBuilder
internal class AggregateShellBuilder(internal var shell: AggregateShell = AggregateShell()) : AggregateBuilder {
    override fun <T : Step> aggregate(spec: StepSpec<T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> aggregate(ref: StepRef<T>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> aggregate(value: T) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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
}

@DslBuilder
internal class DoShellBuilder(internal var shell: DoShell = DoShell()) : DoBuilder {
    override fun <T : Step> `do`(spec: StepSpec<T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> `do`(ref: StepRef<T>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> `do`(value: T) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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
}

@DslBuilder
internal class TryShellBuilder(internal var shell: TryShell = TryShell()) : TryBuilder {
    override fun <T : Step> `try`(spec: StepSpec<T>) {
        shell = shell.copy(`try` = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> `try`(ref: StepRef<T>) {
        shell = shell.copy(`try` = Deferred(ref.key))
    }

    override fun <T : Step> `try`(value: T) {
        shell = shell.copy(`try` = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        shell = shell.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        shell = shell.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        shell = shell.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        shell = shell.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
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

class TaskConfigScaffolder(internal val spec: TaskConfigSpec) : Scaffolder<TaskConfig> {
    override fun createScaffold(): Scaffold<TaskConfig> {
        val builder = TaskConfigShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskConfigShellBuilder(internal var shell: TaskConfigShell = TaskConfigShell()) : TaskConfigBuilder {
    override fun platform(value: String) {
        shell = shell.copy(platform = Wrapper(value))
    }

    override fun image_resource(body: TaskResourceBuilder.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceScaffolder(TaskResourceSpec(body)).createScaffold())
    }

    override fun image_resource(spec: TaskResourceSpec) {
        shell = shell.copy(image_resource = TaskResourceScaffolder(spec).createScaffold())
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

    override fun input(body: TaskInputBuilder.() -> Unit) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder(TaskInputSpec(body)).createScaffold())
    }

    override fun input(spec: TaskInputSpec) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder(spec).createScaffold())
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

    override fun output(body: TaskOutputBuilder.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder(TaskOutputSpec(body)).createScaffold())
    }

    override fun output(spec: TaskOutputSpec) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder(spec).createScaffold())
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

    override fun cache(body: TaskCacheBuilder.() -> Unit) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder(TaskCacheSpec(body)).createScaffold())
    }

    override fun cache(spec: TaskCacheSpec) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder(spec).createScaffold())
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

    override fun run(body: TaskRunConfigBuilder.() -> Unit) {
        shell = shell.copy(run = TaskRunConfigScaffolder(TaskRunConfigSpec(body)).createScaffold())
    }

    override fun run(spec: TaskRunConfigSpec) {
        shell = shell.copy(run = TaskRunConfigScaffolder(spec).createScaffold())
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

class TaskResourceScaffolder(internal val spec: TaskResourceSpec) : Scaffolder<TaskResource> {
    override fun createScaffold(): Scaffold<TaskResource> {
        val builder = TaskResourceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskResourceShellBuilder(internal var shell: TaskResourceShell = TaskResourceShell()) : TaskResourceBuilder {
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

class TaskInputScaffolder(internal val spec: TaskInputSpec) : Scaffolder<TaskInput> {
    override fun createScaffold(): Scaffold<TaskInput> {
        val builder = TaskInputShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskInputShellBuilder(internal var shell: TaskInputShell = TaskInputShell()) : TaskInputBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun optional(value: Boolean) {
        shell = shell.copy(optional = Wrapper(value))
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

class TaskOutputScaffolder(internal val spec: TaskOutputSpec) : Scaffolder<TaskOutput> {
    override fun createScaffold(): Scaffold<TaskOutput> {
        val builder = TaskOutputShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskOutputShellBuilder(internal var shell: TaskOutputShell = TaskOutputShell()) : TaskOutputBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
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

class TaskCacheScaffolder(internal val spec: TaskCacheSpec) : Scaffolder<TaskCache> {
    override fun createScaffold(): Scaffold<TaskCache> {
        val builder = TaskCacheShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskCacheShellBuilder(internal var shell: TaskCacheShell = TaskCacheShell()) : TaskCacheBuilder {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
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

class TaskRunConfigScaffolder(internal val spec: TaskRunConfigSpec) : Scaffolder<TaskRunConfig> {
    override fun createScaffold(): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TaskRunConfigShellBuilder(internal var shell: TaskRunConfigShell = TaskRunConfigShell()) : TaskRunConfigBuilder {
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

class ResourceScaffolder(internal val spec: ResourceSpec) : Scaffolder<Resource> {
    override fun createScaffold(): Scaffold<Resource> {
        val builder = ResourceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ResourceShellBuilder(internal var shell: ResourceShell = ResourceShell()) : ResourceBuilder {
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

class ResourceTypeScaffolder(internal val spec: ResourceTypeSpec) : Scaffolder<ResourceType> {
    override fun createScaffold(): Scaffold<ResourceType> {
        val builder = ResourceTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ResourceTypeShellBuilder(internal var shell: ResourceTypeShell = ResourceTypeShell()) : ResourceTypeBuilder {
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

class GroupScaffolder(internal val spec: GroupSpec) : Scaffolder<Group> {
    override fun createScaffold(): Scaffold<Group> {
        val builder = GroupShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class GroupShellBuilder(internal var shell: GroupShell = GroupShell()) : GroupBuilder {
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
