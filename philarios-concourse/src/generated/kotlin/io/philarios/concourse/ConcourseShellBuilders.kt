package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
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
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(TeamSpec<C>(body)).createScaffold(context))
    }

    override fun team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(spec).createScaffold(context))
    }

    override fun team(ref: TeamRef) {
        shell = shell.copy(teams = shell.teams.orEmpty() + ref)
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
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + ref)
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
        shell = shell.copy(jobs = shell.jobs.orEmpty() + ref)
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
        shell = shell.copy(resources = shell.resources.orEmpty() + ref)
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
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ref)
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
        shell = shell.copy(groups = shell.groups.orEmpty() + ref)
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

@DslBuilder
internal class JobShellBuilder<out C>(override val context: C, internal var shell: JobShell = JobShell()) : JobBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Step> plan(spec: StepSpec<C, T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> plan(ref: StepRef<T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
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
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(config = ref)
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
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    override fun <T : Step> aggregate(value: T) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(`do` = shell.`do`.orEmpty() + ref)
    }

    override fun <T : Step> `do`(value: T) {
        shell = shell.copy(`do` = shell.`do`.orEmpty() + Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(`try` = ref)
    }

    override fun <T : Step> `try`(value: T) {
        shell = shell.copy(`try` = Wrapper(value))
    }

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = ref)
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = ref)
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = ref)
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = ref)
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
        shell = shell.copy(image_resource = ref)
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
        shell = shell.copy(inputs = shell.inputs.orEmpty() + ref)
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
        shell = shell.copy(outputs = shell.outputs.orEmpty() + ref)
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
        shell = shell.copy(caches = shell.caches.orEmpty() + ref)
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
        shell = shell.copy(run = ref)
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
