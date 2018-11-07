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

@DslBuilder
internal class ConcourseShellBuilder<out C>(override val context: C, internal var shell: ConcourseShell = ConcourseShell()) : ConcourseBuilder<C> {
    override fun team(body: TeamBuilder<C>.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamSpec<C>(body).connect(context))
    }

    override fun team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + spec.connect(context))
    }

    override fun team(ref: TeamRef) {
        shell = shell.copy(teams = shell.teams.orEmpty() + ref)
    }

    override fun team(team: Team) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Wrapper(team))
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

@DslBuilder
internal class TeamShellBuilder<out C>(override val context: C, internal var shell: TeamShell = TeamShell()) : TeamBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun pipeline(body: PipelineBuilder<C>.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineSpec<C>(body).connect(context))
    }

    override fun pipeline(spec: PipelineSpec<C>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + spec.connect(context))
    }

    override fun pipeline(ref: PipelineRef) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + ref)
    }

    override fun pipeline(pipeline: Pipeline) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Wrapper(pipeline))
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

@DslBuilder
internal class PipelineShellBuilder<out C>(override val context: C, internal var shell: PipelineShell = PipelineShell()) : PipelineBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun job(body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobSpec<C>(body).connect(context))
    }

    override fun job(spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + spec.connect(context))
    }

    override fun job(ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + ref)
    }

    override fun job(job: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Wrapper(job))
    }

    override fun jobs(jobs: List<Job>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs.map { Wrapper(it) })
    }

    override fun resource(body: ResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceSpec<C>(body).connect(context))
    }

    override fun resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + spec.connect(context))
    }

    override fun resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ref)
    }

    override fun resource(resource: Resource) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(resource))
    }

    override fun resources(resources: List<Resource>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    override fun resource_type(body: ResourceTypeBuilder<C>.() -> Unit) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeSpec<C>(body).connect(context))
    }

    override fun resource_type(spec: ResourceTypeSpec<C>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + spec.connect(context))
    }

    override fun resource_type(ref: ResourceTypeRef) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ref)
    }

    override fun resource_type(resource_type: ResourceType) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + Wrapper(resource_type))
    }

    override fun resource_types(resource_types: List<ResourceType>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + resource_types.map { Wrapper(it) })
    }

    override fun group(body: GroupBuilder<C>.() -> Unit) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupSpec<C>(body).connect(context))
    }

    override fun group(spec: GroupSpec<C>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + spec.connect(context))
    }

    override fun group(ref: GroupRef) {
        shell = shell.copy(groups = shell.groups.orEmpty() + ref)
    }

    override fun group(group: Group) {
        shell = shell.copy(groups = shell.groups.orEmpty() + Wrapper(group))
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

@DslBuilder
internal class JobShellBuilder<out C>(override val context: C, internal var shell: JobShell = JobShell()) : JobBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun plan(spec: GetSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: GetRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun plan(spec: PutSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: PutRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun plan(spec: TaskSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: TaskRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun plan(spec: AggregateSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: AggregateRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun plan(spec: DoSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: DoRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun plan(spec: TrySpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + spec.connect(context))
    }

    override fun plan(ref: TryRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    override fun serial(serial: Boolean) {
        shell = shell.copy(serial = serial)
    }

    override fun build_logs_to_retain(build_logs_to_retain: Int) {
        shell = shell.copy(build_logs_to_retain = build_logs_to_retain)
    }

    override fun serial_group(serial_group: String) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + serial_group)
    }

    override fun serial_groups(serial_groups: List<String>) {
        shell = shell.copy(serial_groups = shell.serial_groups.orEmpty() + serial_groups)
    }

    override fun max_in_flight(max_in_flight: Int) {
        shell = shell.copy(max_in_flight = max_in_flight)
    }

    override fun public(public: Boolean) {
        shell = shell.copy(public = public)
    }

    override fun disable_manual_trigger(disable_manual_trigger: Boolean) {
        shell = shell.copy(disable_manual_trigger = disable_manual_trigger)
    }

    override fun interruptible(interruptible: Boolean) {
        shell = shell.copy(interruptible = interruptible)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
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

@DslBuilder
internal class GetShellBuilder<out C>(override val context: C, internal var shell: GetShell = GetShell()) : GetBuilder<C> {
    override fun get(get: String) {
        shell = shell.copy(get = get)
    }

    override fun resource(resource: String) {
        shell = shell.copy(resource = resource)
    }

    override fun version(version: String) {
        shell = shell.copy(version = version)
    }

    override fun passed(passed: String) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed)
    }

    override fun passed(passed: List<String>) {
        shell = shell.copy(passed = shell.passed.orEmpty() + passed)
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    override fun trigger(trigger: Boolean) {
        shell = shell.copy(trigger = trigger)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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
    override fun put(put: String) {
        shell = shell.copy(put = put)
    }

    override fun resource(resource: String) {
        shell = shell.copy(resource = resource)
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    override fun get_params(key: String, value: Any) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(key,value))
    }

    override fun get_params(pair: Pair<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun get_params(get_params: Map<String, Any>) {
        shell = shell.copy(get_params = shell.get_params.orEmpty() + get_params)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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
    override fun task(task: String) {
        shell = shell.copy(task = task)
    }

    override fun config(body: TaskConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(config = TaskConfigSpec<C>(body).connect(context))
    }

    override fun config(spec: TaskConfigSpec<C>) {
        shell = shell.copy(config = spec.connect(context))
    }

    override fun config(ref: TaskConfigRef) {
        shell = shell.copy(config = ref)
    }

    override fun config(config: TaskConfig) {
        shell = shell.copy(config = Wrapper(config))
    }

    override fun file(file: String) {
        shell = shell.copy(file = file)
    }

    override fun privileged(privileged: String) {
        shell = shell.copy(privileged = privileged)
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    override fun image(image: String) {
        shell = shell.copy(image = image)
    }

    override fun input_mapping(key: String, value: String) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(key,value))
    }

    override fun input_mapping(pair: Pair<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun input_mapping(input_mapping: Map<String, String>) {
        shell = shell.copy(input_mapping = shell.input_mapping.orEmpty() + input_mapping)
    }

    override fun output_mapping(key: String, value: String) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(key,value))
    }

    override fun output_mapping(pair: Pair<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun output_mapping(output_mapping: Map<String, String>) {
        shell = shell.copy(output_mapping = shell.output_mapping.orEmpty() + output_mapping)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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
    override fun aggregate(spec: GetSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: GetRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun aggregate(spec: PutSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: PutRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun aggregate(spec: TaskSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: TaskRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun aggregate(spec: AggregateSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: AggregateRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun aggregate(spec: DoSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: DoRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun aggregate(spec: TrySpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + spec.connect(context))
    }

    override fun aggregate(ref: TryRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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
    override fun `do`(spec: GetSpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: GetRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun `do`(spec: PutSpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: PutRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun `do`(spec: TaskSpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: TaskRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun `do`(spec: AggregateSpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: AggregateRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun `do`(spec: DoSpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: DoRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun `do`(spec: TrySpec<C>) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + spec.connect(context))
    }

    override fun `do`(ref: TryRef) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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
    override fun `try`(spec: GetSpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: GetRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun `try`(spec: PutSpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: PutRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun `try`(spec: TaskSpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: TaskRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun `try`(spec: AggregateSpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: AggregateRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun `try`(spec: DoSpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: DoRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun `try`(spec: TrySpec<C>) {
        shell = shell.copy(`try` = spec.connect(context))
    }

    override fun `try`(ref: TryRef) {
        shell = shell.copy(`try` = ref)
    }

    override fun on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = spec.connect(context))
    }

    override fun on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    override fun on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = spec.connect(context))
    }

    override fun on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    override fun on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = spec.connect(context))
    }

    override fun on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    override fun ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = spec.connect(context))
    }

    override fun ensure(ref: TryRef) {
        shell = shell.copy(ensure = ref)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun timeout(timeout: String) {
        shell = shell.copy(timeout = timeout)
    }

    override fun attempts(attempts: Int) {
        shell = shell.copy(attempts = attempts)
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

@DslBuilder
internal class TaskConfigShellBuilder<out C>(override val context: C, internal var shell: TaskConfigShell = TaskConfigShell()) : TaskConfigBuilder<C> {
    override fun platform(platform: String) {
        shell = shell.copy(platform = platform)
    }

    override fun image_resource(body: TaskResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceSpec<C>(body).connect(context))
    }

    override fun image_resource(spec: TaskResourceSpec<C>) {
        shell = shell.copy(image_resource = spec.connect(context))
    }

    override fun image_resource(ref: TaskResourceRef) {
        shell = shell.copy(image_resource = ref)
    }

    override fun image_resource(image_resource: TaskResource) {
        shell = shell.copy(image_resource = Wrapper(image_resource))
    }

    override fun rootfs_uri(rootfs_uri: String) {
        shell = shell.copy(rootfs_uri = rootfs_uri)
    }

    override fun input(body: TaskInputBuilder<C>.() -> Unit) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputSpec<C>(body).connect(context))
    }

    override fun input(spec: TaskInputSpec<C>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + spec.connect(context))
    }

    override fun input(ref: TaskInputRef) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + ref)
    }

    override fun input(input: TaskInput) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Wrapper(input))
    }

    override fun inputs(inputs: List<TaskInput>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + inputs.map { Wrapper(it) })
    }

    override fun output(body: TaskOutputBuilder<C>.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputSpec<C>(body).connect(context))
    }

    override fun output(spec: TaskOutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + spec.connect(context))
    }

    override fun output(ref: TaskOutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + ref)
    }

    override fun output(output: TaskOutput) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Wrapper(output))
    }

    override fun outputs(outputs: List<TaskOutput>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { Wrapper(it) })
    }

    override fun cache(body: TaskCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheSpec<C>(body).connect(context))
    }

    override fun cache(spec: TaskCacheSpec<C>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + spec.connect(context))
    }

    override fun cache(ref: TaskCacheRef) {
        shell = shell.copy(caches = shell.caches.orEmpty() + ref)
    }

    override fun cache(cache: TaskCache) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Wrapper(cache))
    }

    override fun caches(caches: List<TaskCache>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + caches.map { Wrapper(it) })
    }

    override fun run(body: TaskRunConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(run = TaskRunConfigSpec<C>(body).connect(context))
    }

    override fun run(spec: TaskRunConfigSpec<C>) {
        shell = shell.copy(run = spec.connect(context))
    }

    override fun run(ref: TaskRunConfigRef) {
        shell = shell.copy(run = ref)
    }

    override fun run(run: TaskRunConfig) {
        shell = shell.copy(run = Wrapper(run))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
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

@DslBuilder
internal class TaskResourceShellBuilder<out C>(override val context: C, internal var shell: TaskResourceShell = TaskResourceShell()) : TaskResourceBuilder<C> {
    override fun type(type: String) {
        shell = shell.copy(type = type)
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    override fun version(key: String, value: String) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(key,value))
    }

    override fun version(pair: Pair<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun version(version: Map<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + version)
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

@DslBuilder
internal class TaskInputShellBuilder<out C>(override val context: C, internal var shell: TaskInputShell = TaskInputShell()) : TaskInputBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun path(path: String) {
        shell = shell.copy(path = path)
    }

    override fun optional(optional: Boolean) {
        shell = shell.copy(optional = optional)
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

@DslBuilder
internal class TaskOutputShellBuilder<out C>(override val context: C, internal var shell: TaskOutputShell = TaskOutputShell()) : TaskOutputBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun path(path: String) {
        shell = shell.copy(path = path)
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

@DslBuilder
internal class TaskCacheShellBuilder<out C>(override val context: C, internal var shell: TaskCacheShell = TaskCacheShell()) : TaskCacheBuilder<C> {
    override fun path(path: String) {
        shell = shell.copy(path = path)
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

@DslBuilder
internal class TaskRunConfigShellBuilder<out C>(override val context: C, internal var shell: TaskRunConfigShell = TaskRunConfigShell()) : TaskRunConfigBuilder<C> {
    override fun path(path: String) {
        shell = shell.copy(path = path)
    }

    override fun arg(arg: String) {
        shell = shell.copy(args = shell.args.orEmpty() + arg)
    }

    override fun args(args: List<String>) {
        shell = shell.copy(args = shell.args.orEmpty() + args)
    }

    override fun dir(dir: String) {
        shell = shell.copy(dir = dir)
    }

    override fun user(user: String) {
        shell = shell.copy(user = user)
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

@DslBuilder
internal class ResourceShellBuilder<out C>(override val context: C, internal var shell: ResourceShell = ResourceShell()) : ResourceBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun type(type: String) {
        shell = shell.copy(type = type)
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    override fun check_every(check_every: String) {
        shell = shell.copy(check_every = check_every)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
    }

    override fun webhook_token(webhook_token: String) {
        shell = shell.copy(webhook_token = webhook_token)
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

@DslBuilder
internal class ResourceTypeShellBuilder<out C>(override val context: C, internal var shell: ResourceTypeShell = ResourceTypeShell()) : ResourceTypeBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun type(type: String) {
        shell = shell.copy(type = type)
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(key,value))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source)
    }

    override fun privileged(privileged: Boolean) {
        shell = shell.copy(privileged = privileged)
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(key,value))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(pair.first,pair.second))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params)
    }

    override fun tag(tag: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tag)
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags)
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

@DslBuilder
internal class GroupShellBuilder<out C>(override val context: C, internal var shell: GroupShell = GroupShell()) : GroupBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun job(job: String) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + job)
    }

    override fun jobs(jobs: List<String>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs)
    }

    override fun resource(resource: String) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resource)
    }

    override fun resources(resources: List<String>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources)
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
