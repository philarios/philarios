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

@DslBuilder
interface ConcourseBuilder<out C> {
    val context: C

    fun team(body: TeamBuilder<C>.() -> Unit)

    fun team(spec: TeamSpec<C>)

    fun team(ref: TeamRef)

    fun team(team: Team)

    fun teams(teams: List<Team>)

    fun include(body: ConcourseBuilder<C>.() -> Unit)

    fun include(spec: ConcourseSpec<C>)

    fun <C2> include(context: C2, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConcourseSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>)
}

@DslBuilder
interface TeamBuilder<out C> {
    val context: C

    fun name(name: String)

    fun pipeline(body: PipelineBuilder<C>.() -> Unit)

    fun pipeline(spec: PipelineSpec<C>)

    fun pipeline(ref: PipelineRef)

    fun pipeline(pipeline: Pipeline)

    fun pipelines(pipelines: List<Pipeline>)

    fun include(body: TeamBuilder<C>.() -> Unit)

    fun include(spec: TeamSpec<C>)

    fun <C2> include(context: C2, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TeamSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>)
}

@DslBuilder
interface PipelineBuilder<out C> {
    val context: C

    fun name(name: String)

    fun job(body: JobBuilder<C>.() -> Unit)

    fun job(spec: JobSpec<C>)

    fun job(ref: JobRef)

    fun job(job: Job)

    fun jobs(jobs: List<Job>)

    fun resource(body: ResourceBuilder<C>.() -> Unit)

    fun resource(spec: ResourceSpec<C>)

    fun resource(ref: ResourceRef)

    fun resource(resource: Resource)

    fun resources(resources: List<Resource>)

    fun resource_type(body: ResourceTypeBuilder<C>.() -> Unit)

    fun resource_type(spec: ResourceTypeSpec<C>)

    fun resource_type(ref: ResourceTypeRef)

    fun resource_type(resource_type: ResourceType)

    fun resource_types(resource_types: List<ResourceType>)

    fun group(body: GroupBuilder<C>.() -> Unit)

    fun group(spec: GroupSpec<C>)

    fun group(ref: GroupRef)

    fun group(group: Group)

    fun groups(groups: List<Group>)

    fun include(body: PipelineBuilder<C>.() -> Unit)

    fun include(spec: PipelineSpec<C>)

    fun <C2> include(context: C2, body: PipelineBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PipelineSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PipelineBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PipelineSpec<C2>)
}

@DslBuilder
interface JobBuilder<out C> {
    val context: C

    fun name(name: String)

    fun plan(spec: GetSpec<C>)

    fun plan(ref: GetRef)

    fun plan(spec: PutSpec<C>)

    fun plan(ref: PutRef)

    fun plan(spec: TaskSpec<C>)

    fun plan(ref: TaskRef)

    fun plan(spec: AggregateSpec<C>)

    fun plan(ref: AggregateRef)

    fun plan(spec: DoSpec<C>)

    fun plan(ref: DoRef)

    fun plan(spec: TrySpec<C>)

    fun plan(ref: TryRef)

    fun serial(serial: Boolean)

    fun build_logs_to_retain(build_logs_to_retain: Int)

    fun serial_group(serial_group: String)

    fun serial_groups(serial_groups: List<String>)

    fun max_in_flight(max_in_flight: Int)

    fun public(public: Boolean)

    fun disable_manual_trigger(disable_manual_trigger: Boolean)

    fun interruptible(interruptible: Boolean)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun include(body: JobBuilder<C>.() -> Unit)

    fun include(spec: JobSpec<C>)

    fun <C2> include(context: C2, body: JobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: JobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: JobSpec<C2>)
}

@DslBuilder
interface GetBuilder<out C> {
    val context: C

    fun get(get: String)

    fun resource(resource: String)

    fun version(version: String)

    fun passed(passed: String)

    fun passed(passed: List<String>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun trigger(trigger: Boolean)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

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

    fun put(put: String)

    fun resource(resource: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun get_params(key: String, value: Any)

    fun get_params(pair: Pair<String, Any>)

    fun get_params(get_params: Map<String, Any>)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

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

    fun task(task: String)

    fun config(body: TaskConfigBuilder<C>.() -> Unit)

    fun config(spec: TaskConfigSpec<C>)

    fun config(ref: TaskConfigRef)

    fun config(config: TaskConfig)

    fun file(file: String)

    fun privileged(privileged: String)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun image(image: String)

    fun input_mapping(key: String, value: String)

    fun input_mapping(pair: Pair<String, String>)

    fun input_mapping(input_mapping: Map<String, String>)

    fun output_mapping(key: String, value: String)

    fun output_mapping(pair: Pair<String, String>)

    fun output_mapping(output_mapping: Map<String, String>)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

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

    fun aggregate(spec: GetSpec<C>)

    fun aggregate(ref: GetRef)

    fun aggregate(spec: PutSpec<C>)

    fun aggregate(ref: PutRef)

    fun aggregate(spec: TaskSpec<C>)

    fun aggregate(ref: TaskRef)

    fun aggregate(spec: AggregateSpec<C>)

    fun aggregate(ref: AggregateRef)

    fun aggregate(spec: DoSpec<C>)

    fun aggregate(ref: DoRef)

    fun aggregate(spec: TrySpec<C>)

    fun aggregate(ref: TryRef)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

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

    fun `do`(spec: GetSpec<C>)

    fun `do`(ref: GetRef)

    fun `do`(spec: PutSpec<C>)

    fun `do`(ref: PutRef)

    fun `do`(spec: TaskSpec<C>)

    fun `do`(ref: TaskRef)

    fun `do`(spec: AggregateSpec<C>)

    fun `do`(ref: AggregateRef)

    fun `do`(spec: DoSpec<C>)

    fun `do`(ref: DoRef)

    fun `do`(spec: TrySpec<C>)

    fun `do`(ref: TryRef)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

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

    fun `try`(spec: GetSpec<C>)

    fun `try`(ref: GetRef)

    fun `try`(spec: PutSpec<C>)

    fun `try`(ref: PutRef)

    fun `try`(spec: TaskSpec<C>)

    fun `try`(ref: TaskRef)

    fun `try`(spec: AggregateSpec<C>)

    fun `try`(ref: AggregateRef)

    fun `try`(spec: DoSpec<C>)

    fun `try`(ref: DoRef)

    fun `try`(spec: TrySpec<C>)

    fun `try`(ref: TryRef)

    fun on_success(spec: GetSpec<C>)

    fun on_success(ref: GetRef)

    fun on_success(spec: PutSpec<C>)

    fun on_success(ref: PutRef)

    fun on_success(spec: TaskSpec<C>)

    fun on_success(ref: TaskRef)

    fun on_success(spec: AggregateSpec<C>)

    fun on_success(ref: AggregateRef)

    fun on_success(spec: DoSpec<C>)

    fun on_success(ref: DoRef)

    fun on_success(spec: TrySpec<C>)

    fun on_success(ref: TryRef)

    fun on_failure(spec: GetSpec<C>)

    fun on_failure(ref: GetRef)

    fun on_failure(spec: PutSpec<C>)

    fun on_failure(ref: PutRef)

    fun on_failure(spec: TaskSpec<C>)

    fun on_failure(ref: TaskRef)

    fun on_failure(spec: AggregateSpec<C>)

    fun on_failure(ref: AggregateRef)

    fun on_failure(spec: DoSpec<C>)

    fun on_failure(ref: DoRef)

    fun on_failure(spec: TrySpec<C>)

    fun on_failure(ref: TryRef)

    fun on_abort(spec: GetSpec<C>)

    fun on_abort(ref: GetRef)

    fun on_abort(spec: PutSpec<C>)

    fun on_abort(ref: PutRef)

    fun on_abort(spec: TaskSpec<C>)

    fun on_abort(ref: TaskRef)

    fun on_abort(spec: AggregateSpec<C>)

    fun on_abort(ref: AggregateRef)

    fun on_abort(spec: DoSpec<C>)

    fun on_abort(ref: DoRef)

    fun on_abort(spec: TrySpec<C>)

    fun on_abort(ref: TryRef)

    fun ensure(spec: GetSpec<C>)

    fun ensure(ref: GetRef)

    fun ensure(spec: PutSpec<C>)

    fun ensure(ref: PutRef)

    fun ensure(spec: TaskSpec<C>)

    fun ensure(ref: TaskRef)

    fun ensure(spec: AggregateSpec<C>)

    fun ensure(ref: AggregateRef)

    fun ensure(spec: DoSpec<C>)

    fun ensure(ref: DoRef)

    fun ensure(spec: TrySpec<C>)

    fun ensure(ref: TryRef)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun timeout(timeout: String)

    fun attempts(attempts: Int)

    fun include(body: TryBuilder<C>.() -> Unit)

    fun include(spec: TrySpec<C>)

    fun <C2> include(context: C2, body: TryBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TrySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TrySpec<C2>)
}

@DslBuilder
interface TaskConfigBuilder<out C> {
    val context: C

    fun platform(platform: String)

    fun image_resource(body: TaskResourceBuilder<C>.() -> Unit)

    fun image_resource(spec: TaskResourceSpec<C>)

    fun image_resource(ref: TaskResourceRef)

    fun image_resource(image_resource: TaskResource)

    fun rootfs_uri(rootfs_uri: String)

    fun input(body: TaskInputBuilder<C>.() -> Unit)

    fun input(spec: TaskInputSpec<C>)

    fun input(ref: TaskInputRef)

    fun input(input: TaskInput)

    fun inputs(inputs: List<TaskInput>)

    fun output(body: TaskOutputBuilder<C>.() -> Unit)

    fun output(spec: TaskOutputSpec<C>)

    fun output(ref: TaskOutputRef)

    fun output(output: TaskOutput)

    fun outputs(outputs: List<TaskOutput>)

    fun cache(body: TaskCacheBuilder<C>.() -> Unit)

    fun cache(spec: TaskCacheSpec<C>)

    fun cache(ref: TaskCacheRef)

    fun cache(cache: TaskCache)

    fun caches(caches: List<TaskCache>)

    fun run(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun run(spec: TaskRunConfigSpec<C>)

    fun run(ref: TaskRunConfigRef)

    fun run(run: TaskRunConfig)

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

@DslBuilder
interface TaskResourceBuilder<out C> {
    val context: C

    fun type(type: String)

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

@DslBuilder
interface TaskInputBuilder<out C> {
    val context: C

    fun name(name: String)

    fun path(path: String)

    fun optional(optional: Boolean)

    fun include(body: TaskInputBuilder<C>.() -> Unit)

    fun include(spec: TaskInputSpec<C>)

    fun <C2> include(context: C2, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskInputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>)
}

@DslBuilder
interface TaskOutputBuilder<out C> {
    val context: C

    fun name(name: String)

    fun path(path: String)

    fun include(body: TaskOutputBuilder<C>.() -> Unit)

    fun include(spec: TaskOutputSpec<C>)

    fun <C2> include(context: C2, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskOutputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskOutputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskOutputSpec<C2>)
}

@DslBuilder
interface TaskCacheBuilder<out C> {
    val context: C

    fun path(path: String)

    fun include(body: TaskCacheBuilder<C>.() -> Unit)

    fun include(spec: TaskCacheSpec<C>)

    fun <C2> include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>)
}

@DslBuilder
interface TaskRunConfigBuilder<out C> {
    val context: C

    fun path(path: String)

    fun arg(arg: String)

    fun args(args: List<String>)

    fun dir(dir: String)

    fun user(user: String)

    fun include(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun include(spec: TaskRunConfigSpec<C>)

    fun <C2> include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskRunConfigSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>)
}

@DslBuilder
interface ResourceBuilder<out C> {
    val context: C

    fun name(name: String)

    fun type(type: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun check_every(check_every: String)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun webhook_token(webhook_token: String)

    fun include(body: ResourceBuilder<C>.() -> Unit)

    fun include(spec: ResourceSpec<C>)

    fun <C2> include(context: C2, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>)
}

@DslBuilder
interface ResourceTypeBuilder<out C> {
    val context: C

    fun name(name: String)

    fun type(type: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun privileged(privileged: Boolean)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun tag(tag: String)

    fun tags(tags: List<String>)

    fun include(body: ResourceTypeBuilder<C>.() -> Unit)

    fun include(spec: ResourceTypeSpec<C>)

    fun <C2> include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>)
}

@DslBuilder
interface GroupBuilder<out C> {
    val context: C

    fun name(name: String)

    fun job(job: String)

    fun jobs(jobs: List<String>)

    fun resource(resource: String)

    fun resources(resources: List<String>)

    fun include(body: GroupBuilder<C>.() -> Unit)

    fun include(spec: GroupSpec<C>)

    fun <C2> include(context: C2, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: GroupSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>)
}
