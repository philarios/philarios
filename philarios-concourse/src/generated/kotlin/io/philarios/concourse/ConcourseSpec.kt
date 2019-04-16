// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.concourse

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

class ConcourseSpec(override val body: ConcourseBuilder.() -> Unit) : Spec<ConcourseBuilder>

@DslBuilder
interface ConcourseBuilder : Builder<ConcourseSpec, ConcourseBuilder> {
    fun team(body: TeamBuilder.() -> Unit)

    fun team(spec: TeamSpec)

    fun team(ref: TeamRef)

    fun team(value: Team)

    fun teams(teams: List<Team>)
}

class ConcourseRef(internal val key: String)

class TeamSpec(override val body: TeamBuilder.() -> Unit) : Spec<TeamBuilder>

@DslBuilder
interface TeamBuilder : Builder<TeamSpec, TeamBuilder> {
    fun name(value: String)

    fun pipeline(body: PipelineBuilder.() -> Unit)

    fun pipeline(spec: PipelineSpec)

    fun pipeline(ref: PipelineRef)

    fun pipeline(value: Pipeline)

    fun pipelines(pipelines: List<Pipeline>)
}

class TeamRef(internal val key: String)

class PipelineSpec(override val body: PipelineBuilder.() -> Unit) : Spec<PipelineBuilder>

@DslBuilder
interface PipelineBuilder : Builder<PipelineSpec, PipelineBuilder> {
    fun name(value: String)

    fun job(body: JobBuilder.() -> Unit)

    fun job(spec: JobSpec)

    fun job(ref: JobRef)

    fun job(value: Job)

    fun jobs(jobs: List<Job>)

    fun resource(body: ResourceBuilder.() -> Unit)

    fun resource(spec: ResourceSpec)

    fun resource(ref: ResourceRef)

    fun resource(value: Resource)

    fun resources(resources: List<Resource>)

    fun resource_type(body: ResourceTypeBuilder.() -> Unit)

    fun resource_type(spec: ResourceTypeSpec)

    fun resource_type(ref: ResourceTypeRef)

    fun resource_type(value: ResourceType)

    fun resource_types(resource_types: List<ResourceType>)

    fun group(body: GroupBuilder.() -> Unit)

    fun group(spec: GroupSpec)

    fun group(ref: GroupRef)

    fun group(value: Group)

    fun groups(groups: List<Group>)
}

class PipelineRef(internal val key: String)

class JobSpec(override val body: JobBuilder.() -> Unit) : Spec<JobBuilder>

@DslBuilder
interface JobBuilder : Builder<JobSpec, JobBuilder> {
    fun name(value: String)

    fun <T : Step> plan(spec: StepSpec<T>)

    fun <T : Step> plan(ref: StepRef<T>)

    fun <T : Step> plan(value: T)

    fun serial(value: Boolean)

    fun build_logs_to_retain(value: Int)

    fun serial_group(value: String)

    fun serial_groups(serial_groups: List<String>)

    fun max_in_flight(value: Int)

    fun public(value: Boolean)

    fun disable_manual_trigger(value: Boolean)

    fun interruptible(value: Boolean)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)
}

class JobRef(internal val key: String)

sealed class StepSpec<out T : Step>

class GetSpec(override val body: GetBuilder.() -> Unit) : StepSpec<Get>(), Spec<GetBuilder>

class PutSpec(override val body: PutBuilder.() -> Unit) : StepSpec<Put>(), Spec<PutBuilder>

class TaskSpec(override val body: TaskBuilder.() -> Unit) : StepSpec<Task>(), Spec<TaskBuilder>

class AggregateSpec(override val body: AggregateBuilder.() -> Unit) : StepSpec<Aggregate>(),
        Spec<AggregateBuilder>

class DoSpec(override val body: DoBuilder.() -> Unit) : StepSpec<Do>(), Spec<DoBuilder>

class TrySpec(override val body: TryBuilder.() -> Unit) : StepSpec<Try>(), Spec<TryBuilder>

@DslBuilder
interface GetBuilder : Builder<GetSpec, GetBuilder> {
    fun get(value: String)

    fun resource(value: String)

    fun version(value: String)

    fun passed(value: String)

    fun passed(passed: List<String>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun trigger(value: Boolean)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

@DslBuilder
interface PutBuilder : Builder<PutSpec, PutBuilder> {
    fun put(value: String)

    fun resource(value: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun get_params(key: String, value: Any)

    fun get_params(pair: Pair<String, Any>)

    fun get_params(get_params: Map<String, Any>)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

@DslBuilder
interface TaskBuilder : Builder<TaskSpec, TaskBuilder> {
    fun task(value: String)

    fun config(body: TaskConfigBuilder.() -> Unit)

    fun config(spec: TaskConfigSpec)

    fun config(ref: TaskConfigRef)

    fun config(value: TaskConfig)

    fun file(value: String)

    fun privileged(value: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun image(value: String)

    fun input_mapping(key: String, value: String)

    fun input_mapping(pair: Pair<String, String>)

    fun input_mapping(input_mapping: Map<String, String>)

    fun output_mapping(key: String, value: String)

    fun output_mapping(pair: Pair<String, String>)

    fun output_mapping(output_mapping: Map<String, String>)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

@DslBuilder
interface AggregateBuilder : Builder<AggregateSpec, AggregateBuilder> {
    fun <T : Step> aggregate(spec: StepSpec<T>)

    fun <T : Step> aggregate(ref: StepRef<T>)

    fun <T : Step> aggregate(value: T)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

@DslBuilder
interface DoBuilder : Builder<DoSpec, DoBuilder> {
    fun <T : Step> `do`(spec: StepSpec<T>)

    fun <T : Step> `do`(ref: StepRef<T>)

    fun <T : Step> `do`(value: T)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

@DslBuilder
interface TryBuilder : Builder<TrySpec, TryBuilder> {
    fun <T : Step> `try`(spec: StepSpec<T>)

    fun <T : Step> `try`(ref: StepRef<T>)

    fun <T : Step> `try`(value: T)

    fun <T : Step> on_success(spec: StepSpec<T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)
}

sealed class StepRef<T : Step> {
    internal abstract val key: String
}

class GetRef(override val key: String) : StepRef<Get>()

class PutRef(override val key: String) : StepRef<Put>()

class TaskRef(override val key: String) : StepRef<Task>()

class AggregateRef(override val key: String) : StepRef<Aggregate>()

class DoRef(override val key: String) : StepRef<Do>()

class TryRef(override val key: String) : StepRef<Try>()

class TaskConfigSpec(override val body: TaskConfigBuilder.() -> Unit) : Spec<TaskConfigBuilder>

@DslBuilder
interface TaskConfigBuilder : Builder<TaskConfigSpec, TaskConfigBuilder> {
    fun platform(value: String)

    fun image_resource(body: TaskResourceBuilder.() -> Unit)

    fun image_resource(spec: TaskResourceSpec)

    fun image_resource(ref: TaskResourceRef)

    fun image_resource(value: TaskResource)

    fun rootfs_uri(value: String)

    fun input(body: TaskInputBuilder.() -> Unit)

    fun input(spec: TaskInputSpec)

    fun input(ref: TaskInputRef)

    fun input(value: TaskInput)

    fun inputs(inputs: List<TaskInput>)

    fun output(body: TaskOutputBuilder.() -> Unit)

    fun output(spec: TaskOutputSpec)

    fun output(ref: TaskOutputRef)

    fun output(value: TaskOutput)

    fun outputs(outputs: List<TaskOutput>)

    fun cache(body: TaskCacheBuilder.() -> Unit)

    fun cache(spec: TaskCacheSpec)

    fun cache(ref: TaskCacheRef)

    fun cache(value: TaskCache)

    fun caches(caches: List<TaskCache>)

    fun run(body: TaskRunConfigBuilder.() -> Unit)

    fun run(spec: TaskRunConfigSpec)

    fun run(ref: TaskRunConfigRef)

    fun run(value: TaskRunConfig)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)
}

class TaskConfigRef(internal val key: String)

class TaskResourceSpec(override val body: TaskResourceBuilder.() -> Unit) : Spec<TaskResourceBuilder>

@DslBuilder
interface TaskResourceBuilder : Builder<TaskResourceSpec, TaskResourceBuilder> {
    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun version(key: String, value: String)

    fun version(pair: Pair<String, String>)

    fun version(version: Map<String, String>)
}

class TaskResourceRef(internal val key: String)

class TaskInputSpec(override val body: TaskInputBuilder.() -> Unit) : Spec<TaskInputBuilder>

@DslBuilder
interface TaskInputBuilder : Builder<TaskInputSpec, TaskInputBuilder> {
    fun name(value: String)

    fun path(value: String)

    fun optional(value: Boolean)
}

class TaskInputRef(internal val key: String)

class TaskOutputSpec(override val body: TaskOutputBuilder.() -> Unit) : Spec<TaskOutputBuilder>

@DslBuilder
interface TaskOutputBuilder : Builder<TaskOutputSpec, TaskOutputBuilder> {
    fun name(value: String)

    fun path(value: String)
}

class TaskOutputRef(internal val key: String)

class TaskCacheSpec(override val body: TaskCacheBuilder.() -> Unit) : Spec<TaskCacheBuilder>

@DslBuilder
interface TaskCacheBuilder : Builder<TaskCacheSpec, TaskCacheBuilder> {
    fun path(value: String)
}

class TaskCacheRef(internal val key: String)

class TaskRunConfigSpec(override val body: TaskRunConfigBuilder.() -> Unit) : Spec<TaskRunConfigBuilder>

@DslBuilder
interface TaskRunConfigBuilder : Builder<TaskRunConfigSpec, TaskRunConfigBuilder> {
    fun path(value: String)

    fun arg(value: String)

    fun args(args: List<String>)

    fun dir(value: String)

    fun user(value: String)
}

class TaskRunConfigRef(internal val key: String)

class ResourceSpec(override val body: ResourceBuilder.() -> Unit) : Spec<ResourceBuilder>

@DslBuilder
interface ResourceBuilder : Builder<ResourceSpec, ResourceBuilder> {
    fun name(value: String)

    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun check_every(value: String)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun webhook_token(value: String)
}

class ResourceRef(internal val key: String)

class ResourceTypeSpec(override val body: ResourceTypeBuilder.() -> Unit) : Spec<ResourceTypeBuilder>

@DslBuilder
interface ResourceTypeBuilder : Builder<ResourceTypeSpec, ResourceTypeBuilder> {
    fun name(value: String)

    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun privileged(value: Boolean)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun tag(value: String)

    fun tags(tags: List<String>)
}

class ResourceTypeRef(internal val key: String)

class GroupSpec(override val body: GroupBuilder.() -> Unit) : Spec<GroupBuilder>

@DslBuilder
interface GroupBuilder : Builder<GroupSpec, GroupBuilder> {
    fun name(value: String)

    fun job(value: String)

    fun jobs(jobs: List<String>)

    fun resource(value: String)

    fun resources(resources: List<String>)
}

class GroupRef(internal val key: String)
