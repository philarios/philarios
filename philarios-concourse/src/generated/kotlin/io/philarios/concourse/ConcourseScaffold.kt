// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.RefScaffold
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
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
        val builder = ConcourseScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ConcourseScaffoldBuilder(internal var scaffold: ConcourseScaffold = ConcourseScaffold()) : ConcourseBuilder {
    override fun team(body: TeamBuilder.() -> Unit) {
        scaffold = scaffold.copy(teams = scaffold.teams.orEmpty() + TeamScaffolder(TeamSpec(body)).createScaffold())
    }

    override fun team(spec: TeamSpec) {
        scaffold = scaffold.copy(teams = scaffold.teams.orEmpty() + TeamScaffolder(spec).createScaffold())
    }

    override fun team(ref: TeamRef) {
        scaffold = scaffold.copy(teams = scaffold.teams.orEmpty() + RefScaffold(ref.key))
    }

    override fun team(value: Team) {
        scaffold = scaffold.copy(teams = scaffold.teams.orEmpty() + ValueScaffold(value))
    }

    override fun teams(teams: List<Team>) {
        scaffold = scaffold.copy(teams = scaffold.teams.orEmpty() + teams.map { ValueScaffold(it) })
    }
}

internal data class ConcourseScaffold(var teams: List<Scaffold<Team>>? = null) : Scaffold<Concourse> {
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
        val builder = TeamScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TeamScaffoldBuilder(internal var scaffold: TeamScaffold = TeamScaffold()) : TeamBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun pipeline(body: PipelineBuilder.() -> Unit) {
        scaffold = scaffold.copy(pipelines = scaffold.pipelines.orEmpty() + PipelineScaffolder(PipelineSpec(body)).createScaffold())
    }

    override fun pipeline(spec: PipelineSpec) {
        scaffold = scaffold.copy(pipelines = scaffold.pipelines.orEmpty() + PipelineScaffolder(spec).createScaffold())
    }

    override fun pipeline(ref: PipelineRef) {
        scaffold = scaffold.copy(pipelines = scaffold.pipelines.orEmpty() + RefScaffold(ref.key))
    }

    override fun pipeline(value: Pipeline) {
        scaffold = scaffold.copy(pipelines = scaffold.pipelines.orEmpty() + ValueScaffold(value))
    }

    override fun pipelines(pipelines: List<Pipeline>) {
        scaffold = scaffold.copy(pipelines = scaffold.pipelines.orEmpty() + pipelines.map { ValueScaffold(it) })
    }
}

internal data class TeamScaffold(var name: Scaffold<String>? = null, var pipelines: List<Scaffold<Pipeline>>? = null) : Scaffold<Team> {
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
        val builder = PipelineScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class PipelineScaffoldBuilder(internal var scaffold: PipelineScaffold = PipelineScaffold()) : PipelineBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun job(body: JobBuilder.() -> Unit) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + JobScaffolder(JobSpec(body)).createScaffold())
    }

    override fun job(spec: JobSpec) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + JobScaffolder(spec).createScaffold())
    }

    override fun job(ref: JobRef) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + RefScaffold(ref.key))
    }

    override fun job(value: Job) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + ValueScaffold(value))
    }

    override fun jobs(jobs: List<Job>) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + jobs.map { ValueScaffold(it) })
    }

    override fun resource(body: ResourceBuilder.() -> Unit) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ResourceScaffolder(ResourceSpec(body)).createScaffold())
    }

    override fun resource(spec: ResourceSpec) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ResourceScaffolder(spec).createScaffold())
    }

    override fun resource(ref: ResourceRef) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + RefScaffold(ref.key))
    }

    override fun resource(value: Resource) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ValueScaffold(value))
    }

    override fun resources(resources: List<Resource>) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + resources.map { ValueScaffold(it) })
    }

    override fun resource_type(body: ResourceTypeBuilder.() -> Unit) {
        scaffold = scaffold.copy(resource_types = scaffold.resource_types.orEmpty() + ResourceTypeScaffolder(ResourceTypeSpec(body)).createScaffold())
    }

    override fun resource_type(spec: ResourceTypeSpec) {
        scaffold = scaffold.copy(resource_types = scaffold.resource_types.orEmpty() + ResourceTypeScaffolder(spec).createScaffold())
    }

    override fun resource_type(ref: ResourceTypeRef) {
        scaffold = scaffold.copy(resource_types = scaffold.resource_types.orEmpty() + RefScaffold(ref.key))
    }

    override fun resource_type(value: ResourceType) {
        scaffold = scaffold.copy(resource_types = scaffold.resource_types.orEmpty() + ValueScaffold(value))
    }

    override fun resource_types(resource_types: List<ResourceType>) {
        scaffold = scaffold.copy(resource_types = scaffold.resource_types.orEmpty() + resource_types.map { ValueScaffold(it) })
    }

    override fun group(body: GroupBuilder.() -> Unit) {
        scaffold = scaffold.copy(groups = scaffold.groups.orEmpty() + GroupScaffolder(GroupSpec(body)).createScaffold())
    }

    override fun group(spec: GroupSpec) {
        scaffold = scaffold.copy(groups = scaffold.groups.orEmpty() + GroupScaffolder(spec).createScaffold())
    }

    override fun group(ref: GroupRef) {
        scaffold = scaffold.copy(groups = scaffold.groups.orEmpty() + RefScaffold(ref.key))
    }

    override fun group(value: Group) {
        scaffold = scaffold.copy(groups = scaffold.groups.orEmpty() + ValueScaffold(value))
    }

    override fun groups(groups: List<Group>) {
        scaffold = scaffold.copy(groups = scaffold.groups.orEmpty() + groups.map { ValueScaffold(it) })
    }
}

internal data class PipelineScaffold(
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
        val builder = JobScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class JobScaffoldBuilder(internal var scaffold: JobScaffold = JobScaffold()) : JobBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun <T : Step> plan(spec: StepSpec<T>) {
        scaffold = scaffold.copy(plan = scaffold.plan.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> plan(ref: StepRef<T>) {
        scaffold = scaffold.copy(plan = scaffold.plan.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Step> plan(value: T) {
        scaffold = scaffold.copy(plan = scaffold.plan.orEmpty() + ValueScaffold(value))
    }

    override fun serial(value: Boolean) {
        scaffold = scaffold.copy(serial = ValueScaffold(value))
    }

    override fun build_logs_to_retain(value: Int) {
        scaffold = scaffold.copy(build_logs_to_retain = ValueScaffold(value))
    }

    override fun serial_group(value: String) {
        scaffold = scaffold.copy(serial_groups = scaffold.serial_groups.orEmpty() + ValueScaffold(value))
    }

    override fun serial_groups(serial_groups: List<String>) {
        scaffold = scaffold.copy(serial_groups = scaffold.serial_groups.orEmpty() + serial_groups.map { ValueScaffold(it) })
    }

    override fun max_in_flight(value: Int) {
        scaffold = scaffold.copy(max_in_flight = ValueScaffold(value))
    }

    override fun public(value: Boolean) {
        scaffold = scaffold.copy(public = ValueScaffold(value))
    }

    override fun disable_manual_trigger(value: Boolean) {
        scaffold = scaffold.copy(disable_manual_trigger = ValueScaffold(value))
    }

    override fun interruptible(value: Boolean) {
        scaffold = scaffold.copy(interruptible = ValueScaffold(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }
}

internal data class JobScaffold(
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
        val builder = GetScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class PutScaffolder(internal val spec: PutSpec) : Scaffolder<Put> {
    override fun createScaffold(): Scaffold<Put> {
        val builder = PutScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class TaskScaffolder(internal val spec: TaskSpec) : Scaffolder<Task> {
    override fun createScaffold(): Scaffold<Task> {
        val builder = TaskScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class AggregateScaffolder(internal val spec: AggregateSpec) : Scaffolder<Aggregate> {
    override fun createScaffold(): Scaffold<Aggregate> {
        val builder = AggregateScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class DoScaffolder(internal val spec: DoSpec) : Scaffolder<Do> {
    override fun createScaffold(): Scaffold<Do> {
        val builder = DoScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class TryScaffolder(internal val spec: TrySpec) : Scaffolder<Try> {
    override fun createScaffold(): Scaffold<Try> {
        val builder = TryScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class GetScaffoldBuilder(internal var scaffold: GetScaffold = GetScaffold()) : GetBuilder {
    override fun get(value: String) {
        scaffold = scaffold.copy(get = ValueScaffold(value))
    }

    override fun resource(value: String) {
        scaffold = scaffold.copy(resource = ValueScaffold(value))
    }

    override fun version(value: String) {
        scaffold = scaffold.copy(version = ValueScaffold(value))
    }

    override fun passed(value: String) {
        scaffold = scaffold.copy(passed = scaffold.passed.orEmpty() + ValueScaffold(value))
    }

    override fun passed(passed: List<String>) {
        scaffold = scaffold.copy(passed = scaffold.passed.orEmpty() + passed.map { ValueScaffold(it) })
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun trigger(value: Boolean) {
        scaffold = scaffold.copy(trigger = ValueScaffold(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

@DslBuilder
internal class PutScaffoldBuilder(internal var scaffold: PutScaffold = PutScaffold()) : PutBuilder {
    override fun put(value: String) {
        scaffold = scaffold.copy(put = ValueScaffold(value))
    }

    override fun resource(value: String) {
        scaffold = scaffold.copy(resource = ValueScaffold(value))
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun get_params(key: String, value: Any) {
        scaffold = scaffold.copy(get_params = scaffold.get_params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun get_params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(get_params = scaffold.get_params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun get_params(get_params: Map<String, Any>) {
        scaffold = scaffold.copy(get_params = scaffold.get_params.orEmpty() + get_params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

@DslBuilder
internal class TaskScaffoldBuilder(internal var scaffold: TaskScaffold = TaskScaffold()) : TaskBuilder {
    override fun task(value: String) {
        scaffold = scaffold.copy(task = ValueScaffold(value))
    }

    override fun config(body: TaskConfigBuilder.() -> Unit) {
        scaffold = scaffold.copy(config = TaskConfigScaffolder(TaskConfigSpec(body)).createScaffold())
    }

    override fun config(spec: TaskConfigSpec) {
        scaffold = scaffold.copy(config = TaskConfigScaffolder(spec).createScaffold())
    }

    override fun config(ref: TaskConfigRef) {
        scaffold = scaffold.copy(config = RefScaffold(ref.key))
    }

    override fun config(value: TaskConfig) {
        scaffold = scaffold.copy(config = ValueScaffold(value))
    }

    override fun file(value: String) {
        scaffold = scaffold.copy(file = ValueScaffold(value))
    }

    override fun privileged(value: String) {
        scaffold = scaffold.copy(privileged = ValueScaffold(value))
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun image(value: String) {
        scaffold = scaffold.copy(image = ValueScaffold(value))
    }

    override fun input_mapping(key: String, value: String) {
        scaffold = scaffold.copy(input_mapping = scaffold.input_mapping.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun input_mapping(pair: Pair<String, String>) {
        scaffold = scaffold.copy(input_mapping = scaffold.input_mapping.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun input_mapping(input_mapping: Map<String, String>) {
        scaffold = scaffold.copy(input_mapping = scaffold.input_mapping.orEmpty() + input_mapping.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun output_mapping(key: String, value: String) {
        scaffold = scaffold.copy(output_mapping = scaffold.output_mapping.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun output_mapping(pair: Pair<String, String>) {
        scaffold = scaffold.copy(output_mapping = scaffold.output_mapping.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun output_mapping(output_mapping: Map<String, String>) {
        scaffold = scaffold.copy(output_mapping = scaffold.output_mapping.orEmpty() + output_mapping.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

@DslBuilder
internal class AggregateScaffoldBuilder(internal var scaffold: AggregateScaffold = AggregateScaffold()) : AggregateBuilder {
    override fun <T : Step> aggregate(spec: StepSpec<T>) {
        scaffold = scaffold.copy(aggregate = scaffold.aggregate.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> aggregate(ref: StepRef<T>) {
        scaffold = scaffold.copy(aggregate = scaffold.aggregate.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Step> aggregate(value: T) {
        scaffold = scaffold.copy(aggregate = scaffold.aggregate.orEmpty() + ValueScaffold(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

@DslBuilder
internal class DoScaffoldBuilder(internal var scaffold: DoScaffold = DoScaffold()) : DoBuilder {
    override fun <T : Step> `do`(spec: StepSpec<T>) {
        scaffold = scaffold.copy(`do` = scaffold.`do`.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> `do`(ref: StepRef<T>) {
        scaffold = scaffold.copy(`do` = scaffold.`do`.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Step> `do`(value: T) {
        scaffold = scaffold.copy(`do` = scaffold.`do`.orEmpty() + ValueScaffold(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

@DslBuilder
internal class TryScaffoldBuilder(internal var scaffold: TryScaffold = TryScaffold()) : TryBuilder {
    override fun <T : Step> `try`(spec: StepSpec<T>) {
        scaffold = scaffold.copy(`try` = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> `try`(ref: StepRef<T>) {
        scaffold = scaffold.copy(`try` = RefScaffold(ref.key))
    }

    override fun <T : Step> `try`(value: T) {
        scaffold = scaffold.copy(`try` = ValueScaffold(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_success = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_success = RefScaffold(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        scaffold = scaffold.copy(on_success = ValueScaffold(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_failure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_failure = RefScaffold(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        scaffold = scaffold.copy(on_failure = ValueScaffold(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<T>) {
        scaffold = scaffold.copy(on_abort = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        scaffold = scaffold.copy(on_abort = RefScaffold(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        scaffold = scaffold.copy(on_abort = ValueScaffold(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<T>) {
        scaffold = scaffold.copy(ensure = StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        scaffold = scaffold.copy(ensure = RefScaffold(ref.key))
    }

    override fun <T : Step> ensure(value: T) {
        scaffold = scaffold.copy(ensure = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun timeout(value: String) {
        scaffold = scaffold.copy(timeout = ValueScaffold(value))
    }

    override fun attempts(value: Int) {
        scaffold = scaffold.copy(attempts = ValueScaffold(value))
    }
}

internal sealed class StepScaffold

internal data class GetScaffold(
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
) : StepScaffold(), Scaffold<Get> {
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

internal data class PutScaffold(
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
) : StepScaffold(), Scaffold<Put> {
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

internal data class TaskScaffold(
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
) : StepScaffold(), Scaffold<Task> {
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

internal data class AggregateScaffold(
        var aggregate: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepScaffold(), Scaffold<Aggregate> {
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

internal data class DoScaffold(
        var `do`: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepScaffold(), Scaffold<Do> {
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

internal data class TryScaffold(
        var `try`: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepScaffold(), Scaffold<Try> {
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
        val builder = TaskConfigScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskConfigScaffoldBuilder(internal var scaffold: TaskConfigScaffold = TaskConfigScaffold()) : TaskConfigBuilder {
    override fun platform(value: String) {
        scaffold = scaffold.copy(platform = ValueScaffold(value))
    }

    override fun image_resource(body: TaskResourceBuilder.() -> Unit) {
        scaffold = scaffold.copy(image_resource = TaskResourceScaffolder(TaskResourceSpec(body)).createScaffold())
    }

    override fun image_resource(spec: TaskResourceSpec) {
        scaffold = scaffold.copy(image_resource = TaskResourceScaffolder(spec).createScaffold())
    }

    override fun image_resource(ref: TaskResourceRef) {
        scaffold = scaffold.copy(image_resource = RefScaffold(ref.key))
    }

    override fun image_resource(value: TaskResource) {
        scaffold = scaffold.copy(image_resource = ValueScaffold(value))
    }

    override fun rootfs_uri(value: String) {
        scaffold = scaffold.copy(rootfs_uri = ValueScaffold(value))
    }

    override fun input(body: TaskInputBuilder.() -> Unit) {
        scaffold = scaffold.copy(inputs = scaffold.inputs.orEmpty() + TaskInputScaffolder(TaskInputSpec(body)).createScaffold())
    }

    override fun input(spec: TaskInputSpec) {
        scaffold = scaffold.copy(inputs = scaffold.inputs.orEmpty() + TaskInputScaffolder(spec).createScaffold())
    }

    override fun input(ref: TaskInputRef) {
        scaffold = scaffold.copy(inputs = scaffold.inputs.orEmpty() + RefScaffold(ref.key))
    }

    override fun input(value: TaskInput) {
        scaffold = scaffold.copy(inputs = scaffold.inputs.orEmpty() + ValueScaffold(value))
    }

    override fun inputs(inputs: List<TaskInput>) {
        scaffold = scaffold.copy(inputs = scaffold.inputs.orEmpty() + inputs.map { ValueScaffold(it) })
    }

    override fun output(body: TaskOutputBuilder.() -> Unit) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + TaskOutputScaffolder(TaskOutputSpec(body)).createScaffold())
    }

    override fun output(spec: TaskOutputSpec) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + TaskOutputScaffolder(spec).createScaffold())
    }

    override fun output(ref: TaskOutputRef) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + RefScaffold(ref.key))
    }

    override fun output(value: TaskOutput) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + ValueScaffold(value))
    }

    override fun outputs(outputs: List<TaskOutput>) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + outputs.map { ValueScaffold(it) })
    }

    override fun cache(body: TaskCacheBuilder.() -> Unit) {
        scaffold = scaffold.copy(caches = scaffold.caches.orEmpty() + TaskCacheScaffolder(TaskCacheSpec(body)).createScaffold())
    }

    override fun cache(spec: TaskCacheSpec) {
        scaffold = scaffold.copy(caches = scaffold.caches.orEmpty() + TaskCacheScaffolder(spec).createScaffold())
    }

    override fun cache(ref: TaskCacheRef) {
        scaffold = scaffold.copy(caches = scaffold.caches.orEmpty() + RefScaffold(ref.key))
    }

    override fun cache(value: TaskCache) {
        scaffold = scaffold.copy(caches = scaffold.caches.orEmpty() + ValueScaffold(value))
    }

    override fun caches(caches: List<TaskCache>) {
        scaffold = scaffold.copy(caches = scaffold.caches.orEmpty() + caches.map { ValueScaffold(it) })
    }

    override fun run(body: TaskRunConfigBuilder.() -> Unit) {
        scaffold = scaffold.copy(run = TaskRunConfigScaffolder(TaskRunConfigSpec(body)).createScaffold())
    }

    override fun run(spec: TaskRunConfigSpec) {
        scaffold = scaffold.copy(run = TaskRunConfigScaffolder(spec).createScaffold())
    }

    override fun run(ref: TaskRunConfigRef) {
        scaffold = scaffold.copy(run = RefScaffold(ref.key))
    }

    override fun run(value: TaskRunConfig) {
        scaffold = scaffold.copy(run = ValueScaffold(value))
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class TaskConfigScaffold(
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
        val builder = TaskResourceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskResourceScaffoldBuilder(internal var scaffold: TaskResourceScaffold = TaskResourceScaffold()) : TaskResourceBuilder {
    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun source(key: String, value: Any) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + source.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun version(key: String, value: String) {
        scaffold = scaffold.copy(version = scaffold.version.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun version(pair: Pair<String, String>) {
        scaffold = scaffold.copy(version = scaffold.version.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun version(version: Map<String, String>) {
        scaffold = scaffold.copy(version = scaffold.version.orEmpty() + version.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class TaskResourceScaffold(
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
        val builder = TaskInputScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskInputScaffoldBuilder(internal var scaffold: TaskInputScaffold = TaskInputScaffold()) : TaskInputBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }

    override fun optional(value: Boolean) {
        scaffold = scaffold.copy(optional = ValueScaffold(value))
    }
}

internal data class TaskInputScaffold(
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
        val builder = TaskOutputScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskOutputScaffoldBuilder(internal var scaffold: TaskOutputScaffold = TaskOutputScaffold()) : TaskOutputBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }
}

internal data class TaskOutputScaffold(var name: Scaffold<String>? = null, var path: Scaffold<String>? = null) : Scaffold<TaskOutput> {
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
        val builder = TaskCacheScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskCacheScaffoldBuilder(internal var scaffold: TaskCacheScaffold = TaskCacheScaffold()) : TaskCacheBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }
}

internal data class TaskCacheScaffold(var path: Scaffold<String>? = null) : Scaffold<TaskCache> {
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
        val builder = TaskRunConfigScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TaskRunConfigScaffoldBuilder(internal var scaffold: TaskRunConfigScaffold = TaskRunConfigScaffold()) : TaskRunConfigBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }

    override fun arg(value: String) {
        scaffold = scaffold.copy(args = scaffold.args.orEmpty() + ValueScaffold(value))
    }

    override fun args(args: List<String>) {
        scaffold = scaffold.copy(args = scaffold.args.orEmpty() + args.map { ValueScaffold(it) })
    }

    override fun dir(value: String) {
        scaffold = scaffold.copy(dir = ValueScaffold(value))
    }

    override fun user(value: String) {
        scaffold = scaffold.copy(user = ValueScaffold(value))
    }
}

internal data class TaskRunConfigScaffold(
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
        val builder = ResourceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ResourceScaffoldBuilder(internal var scaffold: ResourceScaffold = ResourceScaffold()) : ResourceBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun source(key: String, value: Any) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + source.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun check_every(value: String) {
        scaffold = scaffold.copy(check_every = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }

    override fun webhook_token(value: String) {
        scaffold = scaffold.copy(webhook_token = ValueScaffold(value))
    }
}

internal data class ResourceScaffold(
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
        val builder = ResourceTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ResourceTypeScaffoldBuilder(internal var scaffold: ResourceTypeScaffold = ResourceTypeScaffold()) : ResourceTypeBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun source(key: String, value: Any) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        scaffold = scaffold.copy(source = scaffold.source.orEmpty() + source.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun privileged(value: Boolean) {
        scaffold = scaffold.copy(privileged = ValueScaffold(value))
    }

    override fun params(key: String, value: Any) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        scaffold = scaffold.copy(params = scaffold.params.orEmpty() + params.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class ResourceTypeScaffold(
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
        val builder = GroupScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class GroupScaffoldBuilder(internal var scaffold: GroupScaffold = GroupScaffold()) : GroupBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun job(value: String) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + ValueScaffold(value))
    }

    override fun jobs(jobs: List<String>) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + jobs.map { ValueScaffold(it) })
    }

    override fun resource(value: String) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ValueScaffold(value))
    }

    override fun resources(resources: List<String>) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + resources.map { ValueScaffold(it) })
    }
}

internal data class GroupScaffold(
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
