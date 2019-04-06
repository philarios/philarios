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

import io.philarios.core.DslBuilder
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit)

@DslBuilder
interface ConcourseBuilder<out C> {
    val context: C

    fun team(body: TeamBuilder<C>.() -> Unit)

    fun team(spec: TeamSpec<C>)

    fun team(ref: TeamRef)

    fun team(value: Team)

    fun teams(teams: List<Team>)

    fun include(body: ConcourseBuilder<C>.() -> Unit)

    fun include(spec: ConcourseSpec<C>)

    fun <C2> include(context: C2, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConcourseSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>)
}

class ConcourseRef(internal val key: String)

class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit)

@DslBuilder
interface TeamBuilder<out C> {
    val context: C

    fun name(value: String)

    fun pipeline(body: PipelineBuilder<C>.() -> Unit)

    fun pipeline(spec: PipelineSpec<C>)

    fun pipeline(ref: PipelineRef)

    fun pipeline(value: Pipeline)

    fun pipelines(pipelines: List<Pipeline>)

    fun include(body: TeamBuilder<C>.() -> Unit)

    fun include(spec: TeamSpec<C>)

    fun <C2> include(context: C2, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TeamSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>)
}

class TeamRef(internal val key: String)

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

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

@DslBuilder
interface JobBuilder<out C> {
    val context: C

    fun name(value: String)

    fun <T : Step> plan(spec: StepSpec<C, T>)

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

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun include(body: JobBuilder<C>.() -> Unit)

    fun include(spec: JobSpec<C>)

    fun <C2> include(context: C2, body: JobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: JobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: JobSpec<C2>)
}

class JobRef(internal val key: String)

sealed class StepSpec<in C, out T : Step>

class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit) : StepSpec<C, Get>()

class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit) : StepSpec<C, Put>()

class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit) : StepSpec<C, Task>()

class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit) : StepSpec<C, Aggregate>()

class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit) : StepSpec<C, Do>()

class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit) : StepSpec<C, Try>()

@DslBuilder
interface GetBuilder<out C> {
    val context: C

    fun get(value: String)

    fun resource(value: String)

    fun version(value: String)

    fun passed(value: String)

    fun passed(passed: List<String>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun trigger(value: Boolean)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: GetBuilder<C>.() -> Unit)

    fun include(spec: GetSpec<C>)

    fun <C2> include(context: C2, body: GetBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: GetSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: GetBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: GetSpec<C2>)
}

@DslBuilder
interface PutBuilder<out C> {
    val context: C

    fun put(value: String)

    fun resource(value: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun get_params(key: String, value: Any)

    fun get_params(pair: Pair<String, Any>)

    fun get_params(get_params: Map<String, Any>)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: PutBuilder<C>.() -> Unit)

    fun include(spec: PutSpec<C>)

    fun <C2> include(context: C2, body: PutBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PutSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PutBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PutSpec<C2>)
}

@DslBuilder
interface TaskBuilder<out C> {
    val context: C

    fun task(value: String)

    fun config(body: TaskConfigBuilder<C>.() -> Unit)

    fun config(spec: TaskConfigSpec<C>)

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

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: TaskBuilder<C>.() -> Unit)

    fun include(spec: TaskSpec<C>)

    fun <C2> include(context: C2, body: TaskBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskSpec<C2>)
}

@DslBuilder
interface AggregateBuilder<out C> {
    val context: C

    fun <T : Step> aggregate(spec: StepSpec<C, T>)

    fun <T : Step> aggregate(ref: StepRef<T>)

    fun <T : Step> aggregate(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: AggregateBuilder<C>.() -> Unit)

    fun include(spec: AggregateSpec<C>)

    fun <C2> include(context: C2, body: AggregateBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AggregateSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AggregateBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AggregateSpec<C2>)
}

@DslBuilder
interface DoBuilder<out C> {
    val context: C

    fun <T : Step> `do`(spec: StepSpec<C, T>)

    fun <T : Step> `do`(ref: StepRef<T>)

    fun <T : Step> `do`(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: DoBuilder<C>.() -> Unit)

    fun include(spec: DoSpec<C>)

    fun <C2> include(context: C2, body: DoBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DoSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DoBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DoSpec<C2>)
}

@DslBuilder
interface TryBuilder<out C> {
    val context: C

    fun <T : Step> `try`(spec: StepSpec<C, T>)

    fun <T : Step> `try`(ref: StepRef<T>)

    fun <T : Step> `try`(value: T)

    fun <T : Step> on_success(spec: StepSpec<C, T>)

    fun <T : Step> on_success(ref: StepRef<T>)

    fun <T : Step> on_success(value: T)

    fun <T : Step> on_failure(spec: StepSpec<C, T>)

    fun <T : Step> on_failure(ref: StepRef<T>)

    fun <T : Step> on_failure(value: T)

    fun <T : Step> on_abort(spec: StepSpec<C, T>)

    fun <T : Step> on_abort(ref: StepRef<T>)

    fun <T : Step> on_abort(value: T)

    fun <T : Step> ensure(spec: StepSpec<C, T>)

    fun <T : Step> ensure(ref: StepRef<T>)

    fun <T : Step> ensure(value: T)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun timeout(value: String)

    fun attempts(value: Int)

    fun include(body: TryBuilder<C>.() -> Unit)

    fun include(spec: TrySpec<C>)

    fun <C2> include(context: C2, body: TryBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TrySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TrySpec<C2>)
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

class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit)

@DslBuilder
interface TaskConfigBuilder<out C> {
    val context: C

    fun platform(value: String)

    fun image_resource(body: TaskResourceBuilder<C>.() -> Unit)

    fun image_resource(spec: TaskResourceSpec<C>)

    fun image_resource(ref: TaskResourceRef)

    fun image_resource(value: TaskResource)

    fun rootfs_uri(value: String)

    fun input(body: TaskInputBuilder<C>.() -> Unit)

    fun input(spec: TaskInputSpec<C>)

    fun input(ref: TaskInputRef)

    fun input(value: TaskInput)

    fun inputs(inputs: List<TaskInput>)

    fun output(body: TaskOutputBuilder<C>.() -> Unit)

    fun output(spec: TaskOutputSpec<C>)

    fun output(ref: TaskOutputRef)

    fun output(value: TaskOutput)

    fun outputs(outputs: List<TaskOutput>)

    fun cache(body: TaskCacheBuilder<C>.() -> Unit)

    fun cache(spec: TaskCacheSpec<C>)

    fun cache(ref: TaskCacheRef)

    fun cache(value: TaskCache)

    fun caches(caches: List<TaskCache>)

    fun run(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun run(spec: TaskRunConfigSpec<C>)

    fun run(ref: TaskRunConfigRef)

    fun run(value: TaskRunConfig)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun include(body: TaskConfigBuilder<C>.() -> Unit)

    fun include(spec: TaskConfigSpec<C>)

    fun <C2> include(context: C2, body: TaskConfigBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskConfigSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskConfigBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskConfigSpec<C2>)
}

class TaskConfigRef(internal val key: String)

class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit)

@DslBuilder
interface TaskResourceBuilder<out C> {
    val context: C

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

    fun include(body: TaskResourceBuilder<C>.() -> Unit)

    fun include(spec: TaskResourceSpec<C>)

    fun <C2> include(context: C2, body: TaskResourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskResourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskResourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskResourceSpec<C2>)
}

class TaskResourceRef(internal val key: String)

class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit)

@DslBuilder
interface TaskInputBuilder<out C> {
    val context: C

    fun name(value: String)

    fun path(value: String)

    fun optional(value: Boolean)

    fun include(body: TaskInputBuilder<C>.() -> Unit)

    fun include(spec: TaskInputSpec<C>)

    fun <C2> include(context: C2, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskInputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>)
}

class TaskInputRef(internal val key: String)

class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit)

@DslBuilder
interface TaskOutputBuilder<out C> {
    val context: C

    fun name(value: String)

    fun path(value: String)

    fun include(body: TaskOutputBuilder<C>.() -> Unit)

    fun include(spec: TaskOutputSpec<C>)

    fun <C2> include(context: C2, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskOutputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskOutputSpec<C2>)
}

class TaskOutputRef(internal val key: String)

class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit)

@DslBuilder
interface TaskCacheBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: TaskCacheBuilder<C>.() -> Unit)

    fun include(spec: TaskCacheSpec<C>)

    fun <C2> include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>)
}

class TaskCacheRef(internal val key: String)

class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit)

@DslBuilder
interface TaskRunConfigBuilder<out C> {
    val context: C

    fun path(value: String)

    fun arg(value: String)

    fun args(args: List<String>)

    fun dir(value: String)

    fun user(value: String)

    fun include(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun include(spec: TaskRunConfigSpec<C>)

    fun <C2> include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskRunConfigSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>)
}

class TaskRunConfigRef(internal val key: String)

class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit)

@DslBuilder
interface ResourceBuilder<out C> {
    val context: C

    fun name(value: String)

    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun check_every(value: String)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun webhook_token(value: String)

    fun include(body: ResourceBuilder<C>.() -> Unit)

    fun include(spec: ResourceSpec<C>)

    fun <C2> include(context: C2, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>)
}

class ResourceRef(internal val key: String)

class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit)

@DslBuilder
interface ResourceTypeBuilder<out C> {
    val context: C

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

    fun include(body: ResourceTypeBuilder<C>.() -> Unit)

    fun include(spec: ResourceTypeSpec<C>)

    fun <C2> include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>)
}

class ResourceTypeRef(internal val key: String)

class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit)

@DslBuilder
interface GroupBuilder<out C> {
    val context: C

    fun name(value: String)

    fun job(value: String)

    fun jobs(jobs: List<String>)

    fun resource(value: String)

    fun resources(resources: List<String>)

    fun include(body: GroupBuilder<C>.() -> Unit)

    fun include(spec: GroupSpec<C>)

    fun <C2> include(context: C2, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: GroupSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>)
}

class GroupRef(internal val key: String)
