package io.philarios.concourse.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

@DslBuilder
class ConcourseBuilder<out C>(val context: C, internal var shell: ConcourseShell = ConcourseShell()) {
    fun <C> ConcourseBuilder<C>.team(body: TeamBuilder<C>.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamSpec<C>(body).connect(context))
    }

    fun <C> ConcourseBuilder<C>.team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + spec.connect(context))
    }

    fun <C> ConcourseBuilder<C>.team(ref: TeamRef) {
        shell = shell.copy(teams = shell.teams.orEmpty() + ref)
    }

    fun <C> ConcourseBuilder<C>.team(team: Team) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Wrapper(team))
    }

    fun <C> ConcourseBuilder<C>.teams(teams: List<Team>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + teams.map { Wrapper(it) })
    }

    fun <C> ConcourseBuilder<C>.include(body: ConcourseBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ConcourseBuilder<C>.include(spec: ConcourseSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ConcourseBuilder<C>.include(context: C2, body: ConcourseBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ConcourseBuilder<C>.include(context: C2, spec: ConcourseSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> ConcourseBuilder<C>.includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ConcourseBuilder<C>.includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConcourseBuilder<C2> = ConcourseBuilder(context, shell)

    private fun <C2> merge(other: ConcourseBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TeamBuilder<out C>(val context: C, internal var shell: TeamShell = TeamShell()) {
    fun <C> TeamBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> TeamBuilder<C>.pipeline(body: PipelineBuilder<C>.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineSpec<C>(body).connect(context))
    }

    fun <C> TeamBuilder<C>.pipeline(spec: PipelineSpec<C>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + spec.connect(context))
    }

    fun <C> TeamBuilder<C>.pipeline(ref: PipelineRef) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + ref)
    }

    fun <C> TeamBuilder<C>.pipeline(pipeline: Pipeline) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Wrapper(pipeline))
    }

    fun <C> TeamBuilder<C>.pipelines(pipelines: List<Pipeline>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + pipelines.map { Wrapper(it) })
    }

    fun <C> TeamBuilder<C>.include(body: TeamBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TeamBuilder<C>.include(spec: TeamSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TeamBuilder<C>.include(context: C2, body: TeamBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TeamBuilder<C>.include(context: C2, spec: TeamSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TeamBuilder<C>.includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TeamBuilder<C>.includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TeamBuilder<C2> = TeamBuilder(context, shell)

    private fun <C2> merge(other: TeamBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class PipelineBuilder<out C>(val context: C, internal var shell: PipelineShell = PipelineShell()) {
    fun <C> PipelineBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> PipelineBuilder<C>.job(body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobSpec<C>(body).connect(context))
    }

    fun <C> PipelineBuilder<C>.job(spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + spec.connect(context))
    }

    fun <C> PipelineBuilder<C>.job(ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + ref)
    }

    fun <C> PipelineBuilder<C>.job(job: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Wrapper(job))
    }

    fun <C> PipelineBuilder<C>.jobs(jobs: List<Job>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs.map { Wrapper(it) })
    }

    fun <C> PipelineBuilder<C>.resource(body: ResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceSpec<C>(body).connect(context))
    }

    fun <C> PipelineBuilder<C>.resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + spec.connect(context))
    }

    fun <C> PipelineBuilder<C>.resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ref)
    }

    fun <C> PipelineBuilder<C>.resource(resource: Resource) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(resource))
    }

    fun <C> PipelineBuilder<C>.resources(resources: List<Resource>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    fun <C> PipelineBuilder<C>.resource_type(body: ResourceTypeBuilder<C>.() -> Unit) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeSpec<C>(body).connect(context))
    }

    fun <C> PipelineBuilder<C>.resource_type(spec: ResourceTypeSpec<C>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + spec.connect(context))
    }

    fun <C> PipelineBuilder<C>.resource_type(ref: ResourceTypeRef) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ref)
    }

    fun <C> PipelineBuilder<C>.resource_type(resource_type: ResourceType) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + Wrapper(resource_type))
    }

    fun <C> PipelineBuilder<C>.resource_types(resource_types: List<ResourceType>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + resource_types.map { Wrapper(it) })
    }

    fun <C> PipelineBuilder<C>.group(body: GroupBuilder<C>.() -> Unit) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupSpec<C>(body).connect(context))
    }

    fun <C> PipelineBuilder<C>.group(spec: GroupSpec<C>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + spec.connect(context))
    }

    fun <C> PipelineBuilder<C>.group(ref: GroupRef) {
        shell = shell.copy(groups = shell.groups.orEmpty() + ref)
    }

    fun <C> PipelineBuilder<C>.group(group: Group) {
        shell = shell.copy(groups = shell.groups.orEmpty() + Wrapper(group))
    }

    fun <C> PipelineBuilder<C>.groups(groups: List<Group>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + groups.map { Wrapper(it) })
    }

    fun <C> PipelineBuilder<C>.include(body: PipelineBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> PipelineBuilder<C>.include(spec: PipelineSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> PipelineBuilder<C>.include(context: C2, body: PipelineBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> PipelineBuilder<C>.include(context: C2, spec: PipelineSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> PipelineBuilder<C>.includeForEach(context: Iterable<C2>, body: PipelineBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> PipelineBuilder<C>.includeForEach(context: Iterable<C2>, spec: PipelineSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PipelineBuilder<C2> = PipelineBuilder(context, shell)

    private fun <C2> merge(other: PipelineBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class JobBuilder<out C>(val context: C, internal var shell: JobShell = JobShell()) {
    fun <C> JobBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> JobBuilder<C>.plan(spec: GetSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: GetRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: PutSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: PutRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: TaskSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: TaskRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: AggregateSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: AggregateRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: DoSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: DoRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: TrySpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    fun <C> JobBuilder<C>.plan(ref: TryRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.serial(serial: Boolean) {
        shell = shell.copy(serial = serial)
    }

    fun <C> JobBuilder<C>.build_logs_to_retain(build_logs_to_retain: Int) {
        shell = shell.copy(build_logs_to_retain = build_logs_to_retain)
    }

    fun <C> JobBuilder<C>.serial_group(serial_group: String) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + serial_group)
    }

    fun <C> JobBuilder<C>.serial_groups(serial_groups: List<String>) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + serial_groups)
    }

    fun <C> JobBuilder<C>.max_in_flight(max_in_flight: Int) {
        shell = shell.copy(max_in_flight = max_in_flight)
    }

    fun <C> JobBuilder<C>.public(public: Boolean) {
        shell = shell.copy(public = public)
    }

    fun <C> JobBuilder<C>.disable_manual_trigger(disable_manual_trigger: Boolean) {
        shell = shell.copy(disable_manual_trigger = disable_manual_trigger)
    }

    fun <C> JobBuilder<C>.interruptible(interruptible: Boolean) {
        shell = shell.copy(interruptible = interruptible)
    }

    fun <C> JobBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> JobBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> JobBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.include(body: JobBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> JobBuilder<C>.include(spec: JobSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> JobBuilder<C>.include(context: C2, body: JobBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> JobBuilder<C>.include(context: C2, spec: JobSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> JobBuilder<C>.includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> JobBuilder<C>.includeForEach(context: Iterable<C2>, spec: JobSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): JobBuilder<C2> = JobBuilder(context, shell)

    private fun <C2> merge(other: JobBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class GetBuilder<out C>(val context: C, internal var shell: GetShell = GetShell()) {
    fun <C> GetBuilder<C>.get(get: String) {
        shell = shell.copy(get = get)
    }

    fun <C> GetBuilder<C>.resource(resource: String) {
        shell = shell.copy(resource = resource)
    }

    fun <C> GetBuilder<C>.version(version: String) {
        shell = shell.copy(version = version)
    }

    fun <C> GetBuilder<C>.passed(passed: String) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed)
    }

    fun <C> GetBuilder<C>.passed(passed: List<String>) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed)
    }

    fun <C> GetBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> GetBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> GetBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> GetBuilder<C>.trigger(trigger: Boolean) {
        shell = shell.copy(trigger = trigger)
    }

    fun <C> GetBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> GetBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> GetBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> GetBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> GetBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> GetBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> GetBuilder<C>.include(body: GetBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> GetBuilder<C>.include(spec: GetSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> GetBuilder<C>.include(context: C2, body: GetBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> GetBuilder<C>.include(context: C2, spec: GetSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> GetBuilder<C>.includeForEach(context: Iterable<C2>, body: GetBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> GetBuilder<C>.includeForEach(context: Iterable<C2>, spec: GetSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GetBuilder<C2> = GetBuilder(context, shell)

    private fun <C2> merge(other: GetBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class PutBuilder<out C>(val context: C, internal var shell: PutShell = PutShell()) {
    fun <C> PutBuilder<C>.put(put: String) {
        shell = shell.copy(put = put)
    }

    fun <C> PutBuilder<C>.resource(resource: String) {
        shell = shell.copy(resource = resource)
    }

    fun <C> PutBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> PutBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> PutBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> PutBuilder<C>.get_params(key: String, value: Any) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(key,value))
    }

    fun <C> PutBuilder<C>.get_params(pair: Pair<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> PutBuilder<C>.get_params(get_params: Map<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + get_params)
    }

    fun <C> PutBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> PutBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> PutBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> PutBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> PutBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> PutBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> PutBuilder<C>.include(body: PutBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> PutBuilder<C>.include(spec: PutSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> PutBuilder<C>.include(context: C2, body: PutBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> PutBuilder<C>.include(context: C2, spec: PutSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> PutBuilder<C>.includeForEach(context: Iterable<C2>, body: PutBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> PutBuilder<C>.includeForEach(context: Iterable<C2>, spec: PutSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PutBuilder<C2> = PutBuilder(context, shell)

    private fun <C2> merge(other: PutBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskBuilder<out C>(val context: C, internal var shell: TaskShell = TaskShell()) {
    fun <C> TaskBuilder<C>.task(task: String) {
        shell = shell.copy(task = task)
    }

    fun <C> TaskBuilder<C>.config(body: TaskConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(config = TaskConfigSpec<C>(body).connect(context))
    }

    fun <C> TaskBuilder<C>.config(spec: TaskConfigSpec<C>) {
        shell = shell.copy(config = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.config(ref: TaskConfigRef) {
        shell = shell.copy(config = ref)
    }

    fun <C> TaskBuilder<C>.config(config: TaskConfig) {
        shell = shell.copy(config = Wrapper(config))
    }

    fun <C> TaskBuilder<C>.file(file: String) {
        shell = shell.copy(file = file)
    }

    fun <C> TaskBuilder<C>.privileged(privileged: String) {
        shell = shell.copy(privileged = privileged)
    }

    fun <C> TaskBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> TaskBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> TaskBuilder<C>.image(image: String) {
        shell = shell.copy(image = image)
    }

    fun <C> TaskBuilder<C>.input_mapping(key: String, value: String) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(key,value))
    }

    fun <C> TaskBuilder<C>.input_mapping(pair: Pair<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskBuilder<C>.input_mapping(input_mapping: Map<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + input_mapping)
    }

    fun <C> TaskBuilder<C>.output_mapping(key: String, value: String) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(key,value))
    }

    fun <C> TaskBuilder<C>.output_mapping(pair: Pair<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskBuilder<C>.output_mapping(output_mapping: Map<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + output_mapping)
    }

    fun <C> TaskBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TaskBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> TaskBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> TaskBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> TaskBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> TaskBuilder<C>.include(body: TaskBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskBuilder<C>.include(spec: TaskSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskBuilder<C>.include(context: C2, body: TaskBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskBuilder<C>.include(context: C2, spec: TaskSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskBuilder<C2> = TaskBuilder(context, shell)

    private fun <C2> merge(other: TaskBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class AggregateBuilder<out C>(val context: C, internal var shell: AggregateShell = AggregateShell()) {
    fun <C> AggregateBuilder<C>.aggregate(spec: GetSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: GetRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: PutSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: PutRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TaskSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: TaskRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: AggregateSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: AggregateRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: DoSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: DoRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TrySpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: TryRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> AggregateBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> AggregateBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> AggregateBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> AggregateBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> AggregateBuilder<C>.include(body: AggregateBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> AggregateBuilder<C>.include(spec: AggregateSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> AggregateBuilder<C>.include(context: C2, body: AggregateBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> AggregateBuilder<C>.include(context: C2, spec: AggregateSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> AggregateBuilder<C>.includeForEach(context: Iterable<C2>, body: AggregateBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> AggregateBuilder<C>.includeForEach(context: Iterable<C2>, spec: AggregateSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AggregateBuilder<C2> = AggregateBuilder(context, shell)

    private fun <C2> merge(other: AggregateBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class DoBuilder<out C>(val context: C, internal var shell: DoShell = DoShell()) {
    fun <C> DoBuilder<C>.doIt(spec: GetSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: GetRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: PutSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: PutRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: TaskSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: TaskRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: AggregateSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: AggregateRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: DoSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: DoRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: TrySpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + spec.connect(context))
    }

    fun <C> DoBuilder<C>.doIt(ref: TryRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> DoBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> DoBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> DoBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> DoBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> DoBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> DoBuilder<C>.include(body: DoBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DoBuilder<C>.include(spec: DoSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DoBuilder<C>.include(context: C2, body: DoBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> DoBuilder<C>.include(context: C2, spec: DoSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> DoBuilder<C>.includeForEach(context: Iterable<C2>, body: DoBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DoBuilder<C>.includeForEach(context: Iterable<C2>, spec: DoSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DoBuilder<C2> = DoBuilder(context, shell)

    private fun <C2> merge(other: DoBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TryBuilder<out C>(val context: C, internal var shell: TryShell = TryShell()) {
    fun <C> TryBuilder<C>.tryIt(spec: GetSpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: GetRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: PutSpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: PutRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TaskSpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: TaskRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: AggregateSpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: AggregateRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: DoSpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: DoRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TrySpec<C>) {
        shell = shell.copy(tryIt = spec.connect(context))
    }

    fun <C> TryBuilder<C>.tryIt(ref: TryRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    fun <C> TryBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    fun <C> TryBuilder<C>.ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> TryBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> TryBuilder<C>.timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    fun <C> TryBuilder<C>.attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
    }

    fun <C> TryBuilder<C>.include(body: TryBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TryBuilder<C>.include(spec: TrySpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TryBuilder<C>.include(context: C2, body: TryBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TryBuilder<C>.include(context: C2, spec: TrySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TryBuilder<C>.includeForEach(context: Iterable<C2>, body: TryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TryBuilder<C>.includeForEach(context: Iterable<C2>, spec: TrySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TryBuilder<C2> = TryBuilder(context, shell)

    private fun <C2> merge(other: TryBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskConfigBuilder<out C>(val context: C, internal var shell: TaskConfigShell = TaskConfigShell()) {
    fun <C> TaskConfigBuilder<C>.platform(platform: String) {
        shell = shell.copy(platform = platform)
    }

    fun <C> TaskConfigBuilder<C>.image_resource(body: TaskResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceSpec<C>(body).connect(context))
    }

    fun <C> TaskConfigBuilder<C>.image_resource(spec: TaskResourceSpec<C>) {
        shell = shell.copy(image_resource = spec.connect(context))
    }

    fun <C> TaskConfigBuilder<C>.image_resource(ref: TaskResourceRef) {
        shell = shell.copy(image_resource = ref)
    }

    fun <C> TaskConfigBuilder<C>.image_resource(image_resource: TaskResource) {
        shell = shell.copy(image_resource = Wrapper(image_resource))
    }

    fun <C> TaskConfigBuilder<C>.rootfs_uri(rootfs_uri: String) {
        shell = shell.copy(rootfs_uri = rootfs_uri)
    }

    fun <C> TaskConfigBuilder<C>.input(body: TaskInputBuilder<C>.() -> Unit) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputSpec<C>(body).connect(context))
    }

    fun <C> TaskConfigBuilder<C>.input(spec: TaskInputSpec<C>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + spec.connect(context))
    }

    fun <C> TaskConfigBuilder<C>.input(ref: TaskInputRef) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + ref)
    }

    fun <C> TaskConfigBuilder<C>.input(input: TaskInput) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Wrapper(input))
    }

    fun <C> TaskConfigBuilder<C>.inputs(inputs: List<TaskInput>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + inputs.map { Wrapper(it) })
    }

    fun <C> TaskConfigBuilder<C>.output(body: TaskOutputBuilder<C>.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputSpec<C>(body).connect(context))
    }

    fun <C> TaskConfigBuilder<C>.output(spec: TaskOutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + spec.connect(context))
    }

    fun <C> TaskConfigBuilder<C>.output(ref: TaskOutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + ref)
    }

    fun <C> TaskConfigBuilder<C>.output(output: TaskOutput) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Wrapper(output))
    }

    fun <C> TaskConfigBuilder<C>.outputs(outputs: List<TaskOutput>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { Wrapper(it) })
    }

    fun <C> TaskConfigBuilder<C>.cache(body: TaskCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheSpec<C>(body).connect(context))
    }

    fun <C> TaskConfigBuilder<C>.cache(spec: TaskCacheSpec<C>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + spec.connect(context))
    }

    fun <C> TaskConfigBuilder<C>.cache(ref: TaskCacheRef) {
        shell = shell.copy(caches = shell.caches.orEmpty() + ref)
    }

    fun <C> TaskConfigBuilder<C>.cache(cache: TaskCache) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Wrapper(cache))
    }

    fun <C> TaskConfigBuilder<C>.caches(caches: List<TaskCache>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + caches.map { Wrapper(it) })
    }

    fun <C> TaskConfigBuilder<C>.run(body: TaskRunConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(run = TaskRunConfigSpec<C>(body).connect(context))
    }

    fun <C> TaskConfigBuilder<C>.run(spec: TaskRunConfigSpec<C>) {
        shell = shell.copy(run = spec.connect(context))
    }

    fun <C> TaskConfigBuilder<C>.run(ref: TaskRunConfigRef) {
        shell = shell.copy(run = ref)
    }

    fun <C> TaskConfigBuilder<C>.run(run: TaskRunConfig) {
        shell = shell.copy(run = Wrapper(run))
    }

    fun <C> TaskConfigBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> TaskConfigBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskConfigBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> TaskConfigBuilder<C>.include(body: TaskConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskConfigBuilder<C>.include(spec: TaskConfigSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskConfigBuilder<C>.include(context: C2, body: TaskConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskConfigBuilder<C>.include(context: C2, spec: TaskConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskConfigBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskConfigBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskConfigBuilder<C2> = TaskConfigBuilder(context, shell)

    private fun <C2> merge(other: TaskConfigBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskResourceBuilder<out C>(val context: C, internal var shell: TaskResourceShell = TaskResourceShell()) {
    fun <C> TaskResourceBuilder<C>.type(type: String) {
        shell = shell.copy(type = type)
    }

    fun <C> TaskResourceBuilder<C>.source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    fun <C> TaskResourceBuilder<C>.source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskResourceBuilder<C>.source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    fun <C> TaskResourceBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> TaskResourceBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskResourceBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> TaskResourceBuilder<C>.version(key: String, value: String) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(key,value))
    }

    fun <C> TaskResourceBuilder<C>.version(pair: Pair<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> TaskResourceBuilder<C>.version(version: Map<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + version)
    }

    fun <C> TaskResourceBuilder<C>.include(body: TaskResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskResourceBuilder<C>.include(spec: TaskResourceSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskResourceBuilder<C>.include(context: C2, body: TaskResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskResourceBuilder<C>.include(context: C2, spec: TaskResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskResourceBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskResourceBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskResourceBuilder<C2> = TaskResourceBuilder(context, shell)

    private fun <C2> merge(other: TaskResourceBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskInputBuilder<out C>(val context: C, internal var shell: TaskInputShell = TaskInputShell()) {
    fun <C> TaskInputBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> TaskInputBuilder<C>.path(path: String) {
        shell = shell.copy(path = path)
    }

    fun <C> TaskInputBuilder<C>.optional(optional: Boolean) {
        shell = shell.copy(optional = optional)
    }

    fun <C> TaskInputBuilder<C>.include(body: TaskInputBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskInputBuilder<C>.include(spec: TaskInputSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskInputBuilder<C>.include(context: C2, body: TaskInputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskInputBuilder<C>.include(context: C2, spec: TaskInputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskInputBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskInputBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskInputBuilder<C2> = TaskInputBuilder(context, shell)

    private fun <C2> merge(other: TaskInputBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskOutputBuilder<out C>(val context: C, internal var shell: TaskOutputShell = TaskOutputShell()) {
    fun <C> TaskOutputBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> TaskOutputBuilder<C>.path(path: String) {
        shell = shell.copy(path = path)
    }

    fun <C> TaskOutputBuilder<C>.include(body: TaskOutputBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskOutputBuilder<C>.include(spec: TaskOutputSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskOutputBuilder<C>.include(context: C2, body: TaskOutputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskOutputBuilder<C>.include(context: C2, spec: TaskOutputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskOutputBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskOutputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskOutputBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskOutputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskOutputBuilder<C2> = TaskOutputBuilder(context, shell)

    private fun <C2> merge(other: TaskOutputBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskCacheBuilder<out C>(val context: C, internal var shell: TaskCacheShell = TaskCacheShell()) {
    fun <C> TaskCacheBuilder<C>.path(path: String) {
        shell = shell.copy(path = path)
    }

    fun <C> TaskCacheBuilder<C>.include(body: TaskCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskCacheBuilder<C>.include(spec: TaskCacheSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskCacheBuilder<C>.include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskCacheBuilder<C>.include(context: C2, spec: TaskCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskCacheBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskCacheBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskCacheBuilder<C2> = TaskCacheBuilder(context, shell)

    private fun <C2> merge(other: TaskCacheBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class TaskRunConfigBuilder<out C>(val context: C, internal var shell: TaskRunConfigShell = TaskRunConfigShell()) {
    fun <C> TaskRunConfigBuilder<C>.path(path: String) {
        shell = shell.copy(path = path)
    }

    fun <C> TaskRunConfigBuilder<C>.arg(arg: String) {
        shell = shell.copy(args = shell.args.orEmpty() + arg)
    }

    fun <C> TaskRunConfigBuilder<C>.args(args: List<String>) {
        shell = shell.copy(args = shell.args.orEmpty() + args)
    }

    fun <C> TaskRunConfigBuilder<C>.dir(dir: String) {
        shell = shell.copy(dir = dir)
    }

    fun <C> TaskRunConfigBuilder<C>.user(user: String) {
        shell = shell.copy(user = user)
    }

    fun <C> TaskRunConfigBuilder<C>.include(body: TaskRunConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TaskRunConfigBuilder<C>.include(spec: TaskRunConfigSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TaskRunConfigBuilder<C>.include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TaskRunConfigBuilder<C>.include(context: C2, spec: TaskRunConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TaskRunConfigBuilder<C>.includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TaskRunConfigBuilder<C>.includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskRunConfigBuilder<C2> = TaskRunConfigBuilder(context, shell)

    private fun <C2> merge(other: TaskRunConfigBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class ResourceBuilder<out C>(val context: C, internal var shell: ResourceShell = ResourceShell()) {
    fun <C> ResourceBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> ResourceBuilder<C>.type(type: String) {
        shell = shell.copy(type = type)
    }

    fun <C> ResourceBuilder<C>.source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    fun <C> ResourceBuilder<C>.source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> ResourceBuilder<C>.source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    fun <C> ResourceBuilder<C>.check_every(check_every: String) {
        shell = shell.copy(check_every = check_every)
    }

    fun <C> ResourceBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> ResourceBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> ResourceBuilder<C>.webhook_token(webhook_token: String) {
        shell = shell.copy(webhook_token = webhook_token)
    }

    fun <C> ResourceBuilder<C>.include(body: ResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ResourceBuilder<C>.include(spec: ResourceSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ResourceBuilder<C>.include(context: C2, body: ResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ResourceBuilder<C>.include(context: C2, spec: ResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> ResourceBuilder<C>.includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ResourceBuilder<C>.includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceBuilder<C2> = ResourceBuilder(context, shell)

    private fun <C2> merge(other: ResourceBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class ResourceTypeBuilder<out C>(val context: C, internal var shell: ResourceTypeShell = ResourceTypeShell()) {
    fun <C> ResourceTypeBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> ResourceTypeBuilder<C>.type(type: String) {
        shell = shell.copy(type = type)
    }

    fun <C> ResourceTypeBuilder<C>.source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    fun <C> ResourceTypeBuilder<C>.source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> ResourceTypeBuilder<C>.source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    fun <C> ResourceTypeBuilder<C>.privileged(privileged: Boolean) {
        shell = shell.copy(privileged = privileged)
    }

    fun <C> ResourceTypeBuilder<C>.params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    fun <C> ResourceTypeBuilder<C>.params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    fun <C> ResourceTypeBuilder<C>.params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    fun <C> ResourceTypeBuilder<C>.tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    fun <C> ResourceTypeBuilder<C>.tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    fun <C> ResourceTypeBuilder<C>.include(body: ResourceTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ResourceTypeBuilder<C>.include(spec: ResourceTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ResourceTypeBuilder<C>.include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ResourceTypeBuilder<C>.include(context: C2, spec: ResourceTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> ResourceTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ResourceTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceTypeBuilder<C2> = ResourceTypeBuilder(context, shell)

    private fun <C2> merge(other: ResourceTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class GroupBuilder<out C>(val context: C, internal var shell: GroupShell = GroupShell()) {
    fun <C> GroupBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> GroupBuilder<C>.job(job: String) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + job)
    }

    fun <C> GroupBuilder<C>.jobs(jobs: List<String>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs)
    }

    fun <C> GroupBuilder<C>.resource(resource: String) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resource)
    }

    fun <C> GroupBuilder<C>.resources(resources: List<String>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources)
    }

    fun <C> GroupBuilder<C>.include(body: GroupBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> GroupBuilder<C>.include(spec: GroupSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> GroupBuilder<C>.include(context: C2, body: GroupBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> GroupBuilder<C>.include(context: C2, spec: GroupSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> GroupBuilder<C>.includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> GroupBuilder<C>.includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GroupBuilder<C2> = GroupBuilder(context, shell)

    private fun <C2> merge(other: GroupBuilder<C2>) {
        this.shell = other.shell
    }
}
