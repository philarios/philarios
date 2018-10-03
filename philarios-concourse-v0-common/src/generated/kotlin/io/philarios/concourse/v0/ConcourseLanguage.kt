package io.philarios.concourse.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Translator
import io.philarios.core.v0.Wrapper
import kotlinx.coroutines.experimental.launch
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

data class Concourse(val teams: List<Team>)

data class ConcourseShell(var teams: List<Scaffold<Team>> = emptyList()) : Scaffold<Concourse> {
    override suspend fun resolve(registry: Registry): Concourse {
        kotlinx.coroutines.experimental.coroutineScope {
        	teams.forEach { launch { it.resolve(registry) } }
        }
        val value = Concourse(teams.map { it.resolve(registry) })
        return value
    }
}

class ConcourseRef(key: String) : Scaffold<Concourse> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Concourse::class, key)

class ConcourseTemplate<in C>(private val spec: ConcourseSpec<C>, private val context: C) : Builder<Concourse> {
    constructor(body: ConcourseBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.ConcourseSpec<C>(body), context)

    override fun scaffold(): Scaffold<Concourse> {
        val builder = ConcourseBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit)

@DslBuilder
class ConcourseBuilder<out C>(val context: C, internal var shell: ConcourseShell = ConcourseShell()) {
    fun <C> ConcourseBuilder<C>.team(body: TeamBuilder<C>.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamTemplate<C>(body, context).scaffold())
    }

    fun <C> ConcourseBuilder<C>.team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamTemplate<C>(spec, context).scaffold())
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

open class ConcourseTranslator<in C>(private val spec: ConcourseSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Concourse> {
    constructor(body: ConcourseBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.ConcourseSpec<C>(body), registry)

    override suspend fun translate(context: C): Concourse {
        val builder = ConcourseTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Team(val name: String, val pipelines: List<Pipeline>)

data class TeamShell(var name: String? = null, var pipelines: List<Scaffold<Pipeline>> = emptyList()) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        kotlinx.coroutines.experimental.coroutineScope {
        	pipelines.forEach { launch { it.resolve(registry) } }
        }
        val value = Team(name!!,pipelines.map { it.resolve(registry) })
        return value
    }
}

class TeamRef(key: String) : Scaffold<Team> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Team::class, key)

class TeamTemplate<in C>(private val spec: TeamSpec<C>, private val context: C) : Builder<Team> {
    constructor(body: TeamBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TeamSpec<C>(body), context)

    override fun scaffold(): Scaffold<Team> {
        val builder = TeamBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit)

@DslBuilder
class TeamBuilder<out C>(val context: C, internal var shell: TeamShell = TeamShell()) {
    fun <C> TeamBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> TeamBuilder<C>.pipeline(body: PipelineBuilder<C>.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineTemplate<C>(body, context).scaffold())
    }

    fun <C> TeamBuilder<C>.pipeline(spec: PipelineSpec<C>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineTemplate<C>(spec, context).scaffold())
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

open class TeamTranslator<in C>(private val spec: TeamSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Team> {
    constructor(body: TeamBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TeamSpec<C>(body), registry)

    override suspend fun translate(context: C): Team {
        val builder = TeamTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Pipeline(
        val name: String,
        val jobs: List<Job>,
        val resources: List<Resource>,
        val resource_types: List<ResourceType>,
        val groups: List<Group>
)

data class PipelineShell(
        var name: String? = null,
        var jobs: List<Scaffold<Job>> = emptyList(),
        var resources: List<Scaffold<Resource>> = emptyList(),
        var resource_types: List<Scaffold<ResourceType>> = emptyList(),
        var groups: List<Scaffold<Group>> = emptyList()
) : Scaffold<Pipeline> {
    override suspend fun resolve(registry: Registry): Pipeline {
        kotlinx.coroutines.experimental.coroutineScope {
        	jobs.forEach { launch { it.resolve(registry) } }
        	resources.forEach { launch { it.resolve(registry) } }
        	resource_types.forEach { launch { it.resolve(registry) } }
        	groups.forEach { launch { it.resolve(registry) } }
        }
        val value = Pipeline(name!!,jobs.map { it.resolve(registry) },resources.map { it.resolve(registry) },resource_types.map { it.resolve(registry) },groups.map { it.resolve(registry) })
        return value
    }
}

class PipelineRef(key: String) : Scaffold<Pipeline> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Pipeline::class, key)

class PipelineTemplate<in C>(private val spec: PipelineSpec<C>, private val context: C) : Builder<Pipeline> {
    constructor(body: PipelineBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.PipelineSpec<C>(body), context)

    override fun scaffold(): Scaffold<Pipeline> {
        val builder = PipelineBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit)

@DslBuilder
class PipelineBuilder<out C>(val context: C, internal var shell: PipelineShell = PipelineShell()) {
    fun <C> PipelineBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> PipelineBuilder<C>.job(body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobTemplate<C>(body, context).scaffold())
    }

    fun <C> PipelineBuilder<C>.job(spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + JobTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceTemplate<C>(body, context).scaffold())
    }

    fun <C> PipelineBuilder<C>.resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeTemplate<C>(body, context).scaffold())
    }

    fun <C> PipelineBuilder<C>.resource_type(spec: ResourceTypeSpec<C>) {
        shell = shell.copy(resource_types = shell.resource_types.orEmpty() + ResourceTypeTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupTemplate<C>(body, context).scaffold())
    }

    fun <C> PipelineBuilder<C>.group(spec: GroupSpec<C>) {
        shell = shell.copy(groups = shell.groups.orEmpty() + GroupTemplate<C>(spec, context).scaffold())
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

open class PipelineTranslator<in C>(private val spec: PipelineSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Pipeline> {
    constructor(body: PipelineBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.PipelineSpec<C>(body), registry)

    override suspend fun translate(context: C): Pipeline {
        val builder = PipelineTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Job(
        val name: String,
        val plan: List<Step>,
        val serial: Boolean?,
        val build_logs_to_retain: Int?,
        val serial_groups: List<String>,
        val max_in_flight: Int?,
        val public: Boolean?,
        val disable_manual_trigger: Boolean?,
        val interruptible: Boolean?,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?
)

data class JobShell(
        var name: String? = null,
        var plan: List<Scaffold<Step>> = emptyList(),
        var serial: Boolean? = null,
        var build_logs_to_retain: Int? = null,
        var serial_groups: List<String> = emptyList(),
        var max_in_flight: Int? = null,
        var public: Boolean? = null,
        var disable_manual_trigger: Boolean? = null,
        var interruptible: Boolean? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null
) : Scaffold<Job> {
    override suspend fun resolve(registry: Registry): Job {
        kotlinx.coroutines.experimental.coroutineScope {
        	plan.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Job(name!!,plan.map { it.resolve(registry) },serial,build_logs_to_retain,serial_groups,max_in_flight,public,disable_manual_trigger,interruptible,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry))
        return value
    }
}

class JobRef(key: String) : Scaffold<Job> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Job::class, key)

class JobTemplate<in C>(private val spec: JobSpec<C>, private val context: C) : Builder<Job> {
    constructor(body: JobBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.JobSpec<C>(body), context)

    override fun scaffold(): Scaffold<Job> {
        val builder = JobBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

@DslBuilder
class JobBuilder<out C>(val context: C, internal var shell: JobShell = JobShell()) {
    fun <C> JobBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> JobBuilder<C>.plan(spec: GetSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.plan(ref: GetRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: PutSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.plan(ref: PutRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: TaskSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.plan(ref: TaskRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: AggregateSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.plan(ref: AggregateRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: DoSpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.plan(ref: DoRef) {
        shell = shell.copy(plan = shell.plan.orEmpty() + ref)
    }

    fun <C> JobBuilder<C>.plan(spec: TrySpec<C>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> JobBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> JobBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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

open class JobTranslator<in C>(private val spec: JobSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Job> {
    constructor(body: JobBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.JobSpec<C>(body), registry)

    override suspend fun translate(context: C): Job {
        val builder = JobTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

sealed class Step

data class Get(
        val get: String,
        val resource: String?,
        val version: String?,
        val passed: List<String>,
        val params: Map<String, Any>,
        val trigger: Boolean?,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Put(
        val put: String,
        val resource: String?,
        val params: Map<String, Any>,
        val get_params: Map<String, Any>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Task(
        val task: String,
        val config: TaskConfig,
        val file: String?,
        val privileged: String?,
        val params: Map<String, Any>,
        val image: String?,
        val input_mapping: Map<String, String>,
        val output_mapping: Map<String, String>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Aggregate(
        val aggregate: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Do(
        val doIt: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Try(
        val tryIt: Step,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

sealed class StepShell

data class GetShell(
        var get: String? = null,
        var resource: String? = null,
        var version: String? = null,
        var passed: List<String> = emptyList(),
        var params: Map<String, Any>? = emptyMap(),
        var trigger: Boolean? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Get> {
    override suspend fun resolve(registry: Registry): Get {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Get(get!!,resource,version,passed,params!!,trigger,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class PutShell(
        var put: String? = null,
        var resource: String? = null,
        var params: Map<String, Any>? = emptyMap(),
        var get_params: Map<String, Any>? = emptyMap(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Put> {
    override suspend fun resolve(registry: Registry): Put {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Put(put!!,resource,params!!,get_params!!,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class TaskShell(
        var task: String? = null,
        var config: Scaffold<TaskConfig>? = null,
        var file: String? = null,
        var privileged: String? = null,
        var params: Map<String, Any>? = emptyMap(),
        var image: String? = null,
        var input_mapping: Map<String, String>? = emptyMap(),
        var output_mapping: Map<String, String>? = emptyMap(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Task> {
    override suspend fun resolve(registry: Registry): Task {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { config!!.resolve(registry) }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Task(task!!,config!!.resolve(registry),file,privileged,params!!,image,input_mapping!!,output_mapping!!,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class AggregateShell(
        var aggregate: List<Scaffold<Step>> = emptyList(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Aggregate> {
    override suspend fun resolve(registry: Registry): Aggregate {
        kotlinx.coroutines.experimental.coroutineScope {
        	aggregate.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Aggregate(aggregate.map { it.resolve(registry) },on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class DoShell(
        var doIt: List<Scaffold<Step>> = emptyList(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Do> {
    override suspend fun resolve(registry: Registry): Do {
        kotlinx.coroutines.experimental.coroutineScope {
        	doIt.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Do(doIt.map { it.resolve(registry) },on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class TryShell(
        var tryIt: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Try> {
    override suspend fun resolve(registry: Registry): Try {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { tryIt!!.resolve(registry) }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Try(tryIt!!.resolve(registry),on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

class GetRef(key: String) : Scaffold<Get> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Get::class, key)

class PutRef(key: String) : Scaffold<Put> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Put::class, key)

class TaskRef(key: String) : Scaffold<Task> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Task::class, key)

class AggregateRef(key: String) : Scaffold<Aggregate> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Aggregate::class, key)

class DoRef(key: String) : Scaffold<Do> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Do::class, key)

class TryRef(key: String) : Scaffold<Try> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Try::class, key)

class GetTemplate<in C>(private val spec: GetSpec<C>, private val context: C) : Builder<Get> {
    constructor(body: GetBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.GetSpec<C>(body), context)

    override fun scaffold(): Scaffold<Get> {
        val builder = GetBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PutTemplate<in C>(private val spec: PutSpec<C>, private val context: C) : Builder<Put> {
    constructor(body: PutBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.PutSpec<C>(body), context)

    override fun scaffold(): Scaffold<Put> {
        val builder = PutBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskTemplate<in C>(private val spec: TaskSpec<C>, private val context: C) : Builder<Task> {
    constructor(body: TaskBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskSpec<C>(body), context)

    override fun scaffold(): Scaffold<Task> {
        val builder = TaskBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AggregateTemplate<in C>(private val spec: AggregateSpec<C>, private val context: C) : Builder<Aggregate> {
    constructor(body: AggregateBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.AggregateSpec<C>(body), context)

    override fun scaffold(): Scaffold<Aggregate> {
        val builder = AggregateBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoTemplate<in C>(private val spec: DoSpec<C>, private val context: C) : Builder<Do> {
    constructor(body: DoBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.DoSpec<C>(body), context)

    override fun scaffold(): Scaffold<Do> {
        val builder = DoBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TryTemplate<in C>(private val spec: TrySpec<C>, private val context: C) : Builder<Try> {
    constructor(body: TryBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TrySpec<C>(body), context)

    override fun scaffold(): Scaffold<Try> {
        val builder = TryBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit)

open class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit)

open class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit)

open class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit)

open class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit)

open class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit)

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
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> GetBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> GetBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> PutBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> PutBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(config = TaskConfigTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskBuilder<C>.config(spec: TaskConfigSpec<C>) {
        shell = shell.copy(config = TaskConfigTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TaskBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TaskBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: GetRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: PutSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: PutRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TaskSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: TaskRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: AggregateSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: AggregateRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: DoSpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: DoRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TrySpec<C>) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.aggregate(ref: TryRef) {
        shell = shell.copy(aggregate = shell.aggregate.orEmpty() + ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> AggregateBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> AggregateBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(doIt = shell.doIt.orEmpty() + GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: GetRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: PutSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: PutRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: TaskSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: TaskRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: AggregateSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: AggregateRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: DoSpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: DoRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.doIt(spec: TrySpec<C>) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.doIt(ref: TryRef) {
        shell = shell.copy(doIt = shell.doIt.orEmpty() + ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> DoBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> DoBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(tryIt = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: GetRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: PutSpec<C>) {
        shell = shell.copy(tryIt = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: PutRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TaskSpec<C>) {
        shell = shell.copy(tryIt = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: TaskRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: AggregateSpec<C>) {
        shell = shell.copy(tryIt = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: AggregateRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: DoSpec<C>) {
        shell = shell.copy(tryIt = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: DoRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TrySpec<C>) {
        shell = shell.copy(tryIt = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.tryIt(ref: TryRef) {
        shell = shell.copy(tryIt = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: GetSpec<C>) {
        shell = shell.copy(on_success = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: GetRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: PutSpec<C>) {
        shell = shell.copy(on_success = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: PutRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: TaskSpec<C>) {
        shell = shell.copy(on_success = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: TaskRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: AggregateSpec<C>) {
        shell = shell.copy(on_success = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: AggregateRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: DoSpec<C>) {
        shell = shell.copy(on_success = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: DoRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_success(spec: TrySpec<C>) {
        shell = shell.copy(on_success = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_success(ref: TryRef) {
        shell = shell.copy(on_success = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: GetSpec<C>) {
        shell = shell.copy(on_failure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: GetRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: PutSpec<C>) {
        shell = shell.copy(on_failure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: PutRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: TaskSpec<C>) {
        shell = shell.copy(on_failure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: TaskRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: AggregateSpec<C>) {
        shell = shell.copy(on_failure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: AggregateRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: DoSpec<C>) {
        shell = shell.copy(on_failure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: DoRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_failure(spec: TrySpec<C>) {
        shell = shell.copy(on_failure = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_failure(ref: TryRef) {
        shell = shell.copy(on_failure = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: GetSpec<C>) {
        shell = shell.copy(on_abort = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: GetRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: PutSpec<C>) {
        shell = shell.copy(on_abort = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: PutRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: TaskSpec<C>) {
        shell = shell.copy(on_abort = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: TaskRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: AggregateSpec<C>) {
        shell = shell.copy(on_abort = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: AggregateRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: DoSpec<C>) {
        shell = shell.copy(on_abort = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: DoRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.on_abort(spec: TrySpec<C>) {
        shell = shell.copy(on_abort = TryTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.on_abort(ref: TryRef) {
        shell = shell.copy(on_abort = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: GetSpec<C>) {
        shell = shell.copy(ensure = GetTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.ensure(ref: GetRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: PutSpec<C>) {
        shell = shell.copy(ensure = PutTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.ensure(ref: PutRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: TaskSpec<C>) {
        shell = shell.copy(ensure = TaskTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.ensure(ref: TaskRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: AggregateSpec<C>) {
        shell = shell.copy(ensure = AggregateTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.ensure(ref: AggregateRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: DoSpec<C>) {
        shell = shell.copy(ensure = DoTemplate<C>(spec, context).scaffold())
    }

    fun <C> TryBuilder<C>.ensure(ref: DoRef) {
        shell = shell.copy(ensure = ref)
    }

    fun <C> TryBuilder<C>.ensure(spec: TrySpec<C>) {
        shell = shell.copy(ensure = TryTemplate<C>(spec, context).scaffold())
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

open class GetTranslator<in C>(private val spec: GetSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Get> {
    constructor(body: GetBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.GetSpec<C>(body), registry)

    override suspend fun translate(context: C): Get {
        val builder = GetTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class PutTranslator<in C>(private val spec: PutSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Put> {
    constructor(body: PutBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.PutSpec<C>(body), registry)

    override suspend fun translate(context: C): Put {
        val builder = PutTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class TaskTranslator<in C>(private val spec: TaskSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Task> {
    constructor(body: TaskBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskSpec<C>(body), registry)

    override suspend fun translate(context: C): Task {
        val builder = TaskTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class AggregateTranslator<in C>(private val spec: AggregateSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Aggregate> {
    constructor(body: AggregateBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.AggregateSpec<C>(body), registry)

    override suspend fun translate(context: C): Aggregate {
        val builder = AggregateTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class DoTranslator<in C>(private val spec: DoSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Do> {
    constructor(body: DoBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.DoSpec<C>(body), registry)

    override suspend fun translate(context: C): Do {
        val builder = DoTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class TryTranslator<in C>(private val spec: TrySpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Try> {
    constructor(body: TryBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TrySpec<C>(body), registry)

    override suspend fun translate(context: C): Try {
        val builder = TryTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskConfig(
        val platform: String,
        val image_resource: TaskResource,
        val rootfs_uri: String?,
        val inputs: List<TaskInput>,
        val outputs: List<TaskOutput>,
        val caches: List<TaskCache>,
        val run: TaskRunConfig?,
        val params: Map<String, Any>
)

data class TaskConfigShell(
        var platform: String? = null,
        var image_resource: Scaffold<TaskResource>? = null,
        var rootfs_uri: String? = null,
        var inputs: List<Scaffold<TaskInput>> = emptyList(),
        var outputs: List<Scaffold<TaskOutput>> = emptyList(),
        var caches: List<Scaffold<TaskCache>> = emptyList(),
        var run: Scaffold<TaskRunConfig>? = null,
        var params: Map<String, Any>? = emptyMap()
) : Scaffold<TaskConfig> {
    override suspend fun resolve(registry: Registry): TaskConfig {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { image_resource!!.resolve(registry) }
        	inputs.forEach { launch { it.resolve(registry) } }
        	outputs.forEach { launch { it.resolve(registry) } }
        	caches.forEach { launch { it.resolve(registry) } }
        	launch { run?.resolve(registry) }
        }
        val value = TaskConfig(platform!!,image_resource!!.resolve(registry),rootfs_uri,inputs.map { it.resolve(registry) },outputs.map { it.resolve(registry) },caches.map { it.resolve(registry) },run?.resolve(registry),params!!)
        return value
    }
}

class TaskConfigRef(key: String) : Scaffold<TaskConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskConfig::class, key)

class TaskConfigTemplate<in C>(private val spec: TaskConfigSpec<C>, private val context: C) : Builder<TaskConfig> {
    constructor(body: TaskConfigBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskConfigSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskConfig> {
        val builder = TaskConfigBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit)

@DslBuilder
class TaskConfigBuilder<out C>(val context: C, internal var shell: TaskConfigShell = TaskConfigShell()) {
    fun <C> TaskConfigBuilder<C>.platform(platform: String) {
        shell = shell.copy(platform = platform)
    }

    fun <C> TaskConfigBuilder<C>.image_resource(body: TaskResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskConfigBuilder<C>.image_resource(spec: TaskResourceSpec<C>) {
        shell = shell.copy(image_resource = TaskResourceTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskConfigBuilder<C>.input(spec: TaskInputSpec<C>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskConfigBuilder<C>.output(spec: TaskOutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskConfigBuilder<C>.cache(spec: TaskCacheSpec<C>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(run = TaskRunConfigTemplate<C>(body, context).scaffold())
    }

    fun <C> TaskConfigBuilder<C>.run(spec: TaskRunConfigSpec<C>) {
        shell = shell.copy(run = TaskRunConfigTemplate<C>(spec, context).scaffold())
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

open class TaskConfigTranslator<in C>(private val spec: TaskConfigSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskConfig> {
    constructor(body: TaskConfigBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskConfigSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskConfig {
        val builder = TaskConfigTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskResource(
        val type: String,
        val source: Map<String, Any>,
        val params: Map<String, Any>,
        val version: Map<String, String>
)

data class TaskResourceShell(
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var params: Map<String, Any>? = emptyMap(),
        var version: Map<String, String>? = emptyMap()
) : Scaffold<TaskResource> {
    override suspend fun resolve(registry: Registry): TaskResource {
        val value = TaskResource(type!!,source!!,params!!,version!!)
        return value
    }
}

class TaskResourceRef(key: String) : Scaffold<TaskResource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskResource::class, key)

class TaskResourceTemplate<in C>(private val spec: TaskResourceSpec<C>, private val context: C) : Builder<TaskResource> {
    constructor(body: TaskResourceBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskResourceSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskResource> {
        val builder = TaskResourceBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit)

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

open class TaskResourceTranslator<in C>(private val spec: TaskResourceSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskResource> {
    constructor(body: TaskResourceBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskResourceSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskResource {
        val builder = TaskResourceTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskInput(
        val name: String,
        val path: String?,
        val optional: Boolean
)

data class TaskInputShell(
        var name: String? = null,
        var path: String? = null,
        var optional: Boolean? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        val value = TaskInput(name!!,path,optional!!)
        return value
    }
}

class TaskInputRef(key: String) : Scaffold<TaskInput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskInput::class, key)

class TaskInputTemplate<in C>(private val spec: TaskInputSpec<C>, private val context: C) : Builder<TaskInput> {
    constructor(body: TaskInputBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskInputSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskInput> {
        val builder = TaskInputBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit)

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

open class TaskInputTranslator<in C>(private val spec: TaskInputSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskInput> {
    constructor(body: TaskInputBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskInputSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskInput {
        val builder = TaskInputTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskOutput(val name: String, val path: String?)

data class TaskOutputShell(var name: String? = null, var path: String? = null) : Scaffold<TaskOutput> {
    override suspend fun resolve(registry: Registry): TaskOutput {
        val value = TaskOutput(name!!,path)
        return value
    }
}

class TaskOutputRef(key: String) : Scaffold<TaskOutput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskOutput::class, key)

class TaskOutputTemplate<in C>(private val spec: TaskOutputSpec<C>, private val context: C) : Builder<TaskOutput> {
    constructor(body: TaskOutputBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskOutputSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskOutput> {
        val builder = TaskOutputBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit)

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

open class TaskOutputTranslator<in C>(private val spec: TaskOutputSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskOutput> {
    constructor(body: TaskOutputBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskOutputSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskOutput {
        val builder = TaskOutputTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskCache(val path: String)

data class TaskCacheShell(var path: String? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        val value = TaskCache(path!!)
        return value
    }
}

class TaskCacheRef(key: String) : Scaffold<TaskCache> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskCache::class, key)

class TaskCacheTemplate<in C>(private val spec: TaskCacheSpec<C>, private val context: C) : Builder<TaskCache> {
    constructor(body: TaskCacheBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskCacheSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskCache> {
        val builder = TaskCacheBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit)

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

open class TaskCacheTranslator<in C>(private val spec: TaskCacheSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskCache> {
    constructor(body: TaskCacheBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskCacheSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskCache {
        val builder = TaskCacheTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class TaskRunConfig(
        val path: String,
        val args: List<String>,
        val dir: String?,
        val user: String?
)

data class TaskRunConfigShell(
        var path: String? = null,
        var args: List<String> = emptyList(),
        var dir: String? = null,
        var user: String? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        val value = TaskRunConfig(path!!,args,dir,user)
        return value
    }
}

class TaskRunConfigRef(key: String) : Scaffold<TaskRunConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskRunConfig::class, key)

class TaskRunConfigTemplate<in C>(private val spec: TaskRunConfigSpec<C>, private val context: C) : Builder<TaskRunConfig> {
    constructor(body: TaskRunConfigBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.TaskRunConfigSpec<C>(body), context)

    override fun scaffold(): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit)

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

open class TaskRunConfigTranslator<in C>(private val spec: TaskRunConfigSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, TaskRunConfig> {
    constructor(body: TaskRunConfigBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.TaskRunConfigSpec<C>(body), registry)

    override suspend fun translate(context: C): TaskRunConfig {
        val builder = TaskRunConfigTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Resource(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val check_every: String?,
        val tags: List<String>,
        val webhook_token: String?
)

data class ResourceShell(
        var name: String? = null,
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var check_every: String? = null,
        var tags: List<String> = emptyList(),
        var webhook_token: String? = null
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        val value = Resource(name!!,type!!,source!!,check_every,tags,webhook_token)
        return value
    }
}

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Resource::class, key)

class ResourceTemplate<in C>(private val spec: ResourceSpec<C>, private val context: C) : Builder<Resource> {
    constructor(body: ResourceBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.ResourceSpec<C>(body), context)

    override fun scaffold(): Scaffold<Resource> {
        val builder = ResourceBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit)

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

open class ResourceTranslator<in C>(private val spec: ResourceSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Resource> {
    constructor(body: ResourceBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.ResourceSpec<C>(body), registry)

    override suspend fun translate(context: C): Resource {
        val builder = ResourceTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class ResourceType(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val privileged: Boolean?,
        val params: Map<String, Any>,
        val tags: List<String>
)

data class ResourceTypeShell(
        var name: String? = null,
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var privileged: Boolean? = null,
        var params: Map<String, Any>? = emptyMap(),
        var tags: List<String> = emptyList()
) : Scaffold<ResourceType> {
    override suspend fun resolve(registry: Registry): ResourceType {
        val value = ResourceType(name!!,type!!,source!!,privileged,params!!,tags)
        return value
    }
}

class ResourceTypeRef(key: String) : Scaffold<ResourceType> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.ResourceType::class, key)

class ResourceTypeTemplate<in C>(private val spec: ResourceTypeSpec<C>, private val context: C) : Builder<ResourceType> {
    constructor(body: ResourceTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.ResourceTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<ResourceType> {
        val builder = ResourceTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit)

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

open class ResourceTypeTranslator<in C>(private val spec: ResourceTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, ResourceType> {
    constructor(body: ResourceTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.ResourceTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): ResourceType {
        val builder = ResourceTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Group(
        val name: String,
        val jobs: List<String>,
        val resources: List<String>
)

data class GroupShell(
        var name: String? = null,
        var jobs: List<String> = emptyList(),
        var resources: List<String> = emptyList()
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        val value = Group(name!!,jobs,resources)
        return value
    }
}

class GroupRef(key: String) : Scaffold<Group> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Group::class, key)

class GroupTemplate<in C>(private val spec: GroupSpec<C>, private val context: C) : Builder<Group> {
    constructor(body: GroupBuilder<C>.() -> Unit, context: C) : this(io.philarios.concourse.v0.GroupSpec<C>(body), context)

    override fun scaffold(): Scaffold<Group> {
        val builder = GroupBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit)

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

open class GroupTranslator<in C>(private val spec: GroupSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Group> {
    constructor(body: GroupBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.concourse.v0.GroupSpec<C>(body), registry)

    override suspend fun translate(context: C): Group {
        val builder = GroupTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}
