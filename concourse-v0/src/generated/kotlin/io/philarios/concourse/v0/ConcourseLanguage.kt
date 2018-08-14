package io.philarios.concourse.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

data class Concourse(val teams: List<Team>) {
    companion object {
        operator fun <C> invoke(body: ConcourseBuilder<C>.() -> Unit = {}): ConcourseSpec<C> = ConcourseSpec<C>(body)
    }
}

@DslBuilder
class ConcourseBuilder<out C>(val context: C, private var teams: List<Team>? = emptyList()) : Builder<Concourse> {
    fun <C> ConcourseBuilder<C>.team(spec: TeamSpec<C>) {
        this.teams = this.teams.orEmpty() + TeamTranslator<C>(spec).translate(context)
    }

    fun <C, C2> ConcourseBuilder<C>.context(context: C2, body: ConcourseBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> ConcourseBuilder<C>.forEachContext(context: List<C2>, body: ConcourseBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): ConcourseBuilder<C2> = ConcourseBuilder(context,teams)

    private fun <C2> merge(other: ConcourseBuilder<C2>) {
        this.teams = other.teams
    }

    override fun build(): Concourse = Concourse(teams!!)

    operator fun ConcourseSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit = {}) : Spec<ConcourseBuilder<C>, Concourse> {
    override fun ConcourseBuilder<C>.body() {
        this@ConcourseSpec.body.invoke(this)
    }
}

open class ConcourseTranslator<in C>(private val spec: ConcourseSpec<C>) : Translator<C, Concourse> {
    constructor(body: ConcourseBuilder<C>.() -> Unit = {}) : this(ConcourseSpec<C>(body))

    override fun translate(context: C): Concourse {
        val builder = ConcourseBuilder(context)
        val translator = BuilderSpecTranslator<C, ConcourseBuilder<C>, Concourse>(builder, spec)
        return translator.translate(context)
    }
}

data class Team(val name: String, val pipelines: List<Pipeline>) {
    companion object {
        operator fun <C> invoke(body: TeamBuilder<C>.() -> Unit = {}): TeamSpec<C> = TeamSpec<C>(body)
    }
}

@DslBuilder
class TeamBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var pipelines: List<Pipeline>? = emptyList()
) : Builder<Team> {
    fun <C> TeamBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> TeamBuilder<C>.pipeline(spec: PipelineSpec<C>) {
        this.pipelines = this.pipelines.orEmpty() + PipelineTranslator<C>(spec).translate(context)
    }

    fun <C, C2> TeamBuilder<C>.context(context: C2, body: TeamBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TeamBuilder<C>.forEachContext(context: List<C2>, body: TeamBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TeamBuilder<C2> = TeamBuilder(context,name,pipelines)

    private fun <C2> merge(other: TeamBuilder<C2>) {
        this.name = other.name
        this.pipelines = other.pipelines
    }

    override fun build(): Team = Team(name!!,pipelines!!)

    operator fun TeamSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit = {}) : Spec<TeamBuilder<C>, Team> {
    override fun TeamBuilder<C>.body() {
        this@TeamSpec.body.invoke(this)
    }
}

open class TeamTranslator<in C>(private val spec: TeamSpec<C>) : Translator<C, Team> {
    constructor(body: TeamBuilder<C>.() -> Unit = {}) : this(TeamSpec<C>(body))

    override fun translate(context: C): Team {
        val builder = TeamBuilder(context)
        val translator = BuilderSpecTranslator<C, TeamBuilder<C>, Team>(builder, spec)
        return translator.translate(context)
    }
}

data class Pipeline(
        val name: String,
        val jobs: List<Job>,
        val resources: List<Resource>,
        val resource_types: List<ResourceType>,
        val groups: List<Group>
) {
    companion object {
        operator fun <C> invoke(body: PipelineBuilder<C>.() -> Unit = {}): PipelineSpec<C> = PipelineSpec<C>(body)
    }
}

@DslBuilder
class PipelineBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var jobs: List<Job>? = emptyList(),
        private var resources: List<Resource>? = emptyList(),
        private var resource_types: List<ResourceType>? = emptyList(),
        private var groups: List<Group>? = emptyList()
) : Builder<Pipeline> {
    fun <C> PipelineBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> PipelineBuilder<C>.job(spec: JobSpec<C>) {
        this.jobs = this.jobs.orEmpty() + JobTranslator<C>(spec).translate(context)
    }

    fun <C> PipelineBuilder<C>.resource(spec: ResourceSpec<C>) {
        this.resources = this.resources.orEmpty() + ResourceTranslator<C>(spec).translate(context)
    }

    fun <C> PipelineBuilder<C>.resource_type(spec: ResourceTypeSpec<C>) {
        this.resource_types = this.resource_types.orEmpty() + ResourceTypeTranslator<C>(spec).translate(context)
    }

    fun <C> PipelineBuilder<C>.group(spec: GroupSpec<C>) {
        this.groups = this.groups.orEmpty() + GroupTranslator<C>(spec).translate(context)
    }

    fun <C, C2> PipelineBuilder<C>.context(context: C2, body: PipelineBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> PipelineBuilder<C>.forEachContext(context: List<C2>, body: PipelineBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): PipelineBuilder<C2> = PipelineBuilder(context,name,jobs,resources,resource_types,groups)

    private fun <C2> merge(other: PipelineBuilder<C2>) {
        this.name = other.name
        this.jobs = other.jobs
        this.resources = other.resources
        this.resource_types = other.resource_types
        this.groups = other.groups
    }

    override fun build(): Pipeline = Pipeline(name!!,jobs!!,resources!!,resource_types!!,groups!!)

    operator fun PipelineSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit = {}) : Spec<PipelineBuilder<C>, Pipeline> {
    override fun PipelineBuilder<C>.body() {
        this@PipelineSpec.body.invoke(this)
    }
}

open class PipelineTranslator<in C>(private val spec: PipelineSpec<C>) : Translator<C, Pipeline> {
    constructor(body: PipelineBuilder<C>.() -> Unit = {}) : this(PipelineSpec<C>(body))

    override fun translate(context: C): Pipeline {
        val builder = PipelineBuilder(context)
        val translator = BuilderSpecTranslator<C, PipelineBuilder<C>, Pipeline>(builder, spec)
        return translator.translate(context)
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
) {
    companion object {
        operator fun <C> invoke(body: JobBuilder<C>.() -> Unit = {}): JobSpec<C> = JobSpec<C>(body)
    }
}

@DslBuilder
class JobBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var plan: List<Step>? = emptyList(),
        private var serial: Boolean? = null,
        private var build_logs_to_retain: Int? = null,
        private var serial_groups: List<String>? = emptyList(),
        private var max_in_flight: Int? = null,
        private var public: Boolean? = null,
        private var disable_manual_trigger: Boolean? = null,
        private var interruptible: Boolean? = null,
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null
) : Builder<Job> {
    fun <C> JobBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> JobBuilder<C>.plan(spec: GetSpec<C>) {
        this.plan = this.plan.orEmpty() + GetTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.plan(spec: PutSpec<C>) {
        this.plan = this.plan.orEmpty() + PutTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.plan(spec: TaskSpec<C>) {
        this.plan = this.plan.orEmpty() + TaskTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.plan(spec: AggregateSpec<C>) {
        this.plan = this.plan.orEmpty() + AggregateTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.plan(spec: DoSpec<C>) {
        this.plan = this.plan.orEmpty() + DoTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.plan(spec: TrySpec<C>) {
        this.plan = this.plan.orEmpty() + TryTranslator<C>(spec).translate(context)
    }

    fun <C> JobBuilder<C>.serial(serial: Boolean?) {
        this.serial = serial
    }

    fun <C> JobBuilder<C>.build_logs_to_retain(build_logs_to_retain: Int?) {
        this.build_logs_to_retain = build_logs_to_retain
    }

    fun <C> JobBuilder<C>.serial_group(serial_group: String) {
        this.serial_groups = this.serial_groups.orEmpty() + serial_group
    }

    fun <C> JobBuilder<C>.serial_groups(serial_groups: List<String>) {
        this.serial_groups = this.serial_groups.orEmpty() + serial_groups
    }

    fun <C> JobBuilder<C>.max_in_flight(max_in_flight: Int?) {
        this.max_in_flight = max_in_flight
    }

    fun <C> JobBuilder<C>.public(public: Boolean?) {
        this.public = public
    }

    fun <C> JobBuilder<C>.disable_manual_trigger(disable_manual_trigger: Boolean?) {
        this.disable_manual_trigger = disable_manual_trigger
    }

    fun <C> JobBuilder<C>.interruptible(interruptible: Boolean?) {
        this.interruptible = interruptible
    }

    fun <C> JobBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> JobBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> JobBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> JobBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C, C2> JobBuilder<C>.context(context: C2, body: JobBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> JobBuilder<C>.forEachContext(context: List<C2>, body: JobBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): JobBuilder<C2> = JobBuilder(context,name,plan,serial,build_logs_to_retain,serial_groups,max_in_flight,public,disable_manual_trigger,interruptible,on_success,on_failure,on_abort,ensure)

    private fun <C2> merge(other: JobBuilder<C2>) {
        this.name = other.name
        this.plan = other.plan
        this.serial = other.serial
        this.build_logs_to_retain = other.build_logs_to_retain
        this.serial_groups = other.serial_groups
        this.max_in_flight = other.max_in_flight
        this.public = other.public
        this.disable_manual_trigger = other.disable_manual_trigger
        this.interruptible = other.interruptible
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
    }

    override fun build(): Job = Job(name!!,plan!!,serial,build_logs_to_retain,serial_groups!!,max_in_flight,public,disable_manual_trigger,interruptible,on_success,on_failure,on_abort,ensure)

    operator fun JobSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit = {}) : Spec<JobBuilder<C>, Job> {
    override fun JobBuilder<C>.body() {
        this@JobSpec.body.invoke(this)
    }
}

open class JobTranslator<in C>(private val spec: JobSpec<C>) : Translator<C, Job> {
    constructor(body: JobBuilder<C>.() -> Unit = {}) : this(JobSpec<C>(body))

    override fun translate(context: C): Job {
        val builder = JobBuilder(context)
        val translator = BuilderSpecTranslator<C, JobBuilder<C>, Job>(builder, spec)
        return translator.translate(context)
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
) : Step() {
    companion object {
        operator fun <C> invoke(body: GetBuilder<C>.() -> Unit = {}): GetSpec<C> = GetSpec<C>(body)
    }
}

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
) : Step() {
    companion object {
        operator fun <C> invoke(body: PutBuilder<C>.() -> Unit = {}): PutSpec<C> = PutSpec<C>(body)
    }
}

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
) : Step() {
    companion object {
        operator fun <C> invoke(body: TaskBuilder<C>.() -> Unit = {}): TaskSpec<C> = TaskSpec<C>(body)
    }
}

data class Aggregate(
        val aggregate: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step() {
    companion object {
        operator fun <C> invoke(body: AggregateBuilder<C>.() -> Unit = {}): AggregateSpec<C> = AggregateSpec<C>(body)
    }
}

data class Do(
        val doIt: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step() {
    companion object {
        operator fun <C> invoke(body: DoBuilder<C>.() -> Unit = {}): DoSpec<C> = DoSpec<C>(body)
    }
}

data class Try(
        val tryIt: Step,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step() {
    companion object {
        operator fun <C> invoke(body: TryBuilder<C>.() -> Unit = {}): TrySpec<C> = TrySpec<C>(body)
    }
}

@DslBuilder
class GetBuilder<out C>(
        val context: C,
        private var get: String? = null,
        private var resource: String? = null,
        private var version: String? = null,
        private var passed: List<String>? = emptyList(),
        private var params: Map<String, Any>? = emptyMap(),
        private var trigger: Boolean? = null,
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Get> {
    fun <C> GetBuilder<C>.get(get: String) {
        this.get = get
    }

    fun <C> GetBuilder<C>.resource(resource: String?) {
        this.resource = resource
    }

    fun <C> GetBuilder<C>.version(version: String?) {
        this.version = version
    }

    fun <C> GetBuilder<C>.passed(passed: String) {
        this.passed = this.passed.orEmpty() + passed
    }

    fun <C> GetBuilder<C>.passed(passed: List<String>) {
        this.passed = this.passed.orEmpty() + passed
    }

    fun <C> GetBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> GetBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> GetBuilder<C>.trigger(trigger: Boolean?) {
        this.trigger = trigger
    }

    fun <C> GetBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> GetBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> GetBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> GetBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> GetBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> GetBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> GetBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> GetBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> GetBuilder<C>.context(context: C2, body: GetBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> GetBuilder<C>.forEachContext(context: List<C2>, body: GetBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): GetBuilder<C2> = GetBuilder(context,get,resource,version,passed,params,trigger,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: GetBuilder<C2>) {
        this.get = other.get
        this.resource = other.resource
        this.version = other.version
        this.passed = other.passed
        this.params = other.params
        this.trigger = other.trigger
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Get = Get(get!!,resource,version,passed!!,params!!,trigger,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun GetSpec<C>.unaryPlus() {
        apply(body)
    }
}

@DslBuilder
class PutBuilder<out C>(
        val context: C,
        private var put: String? = null,
        private var resource: String? = null,
        private var params: Map<String, Any>? = emptyMap(),
        private var get_params: Map<String, Any>? = emptyMap(),
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Put> {
    fun <C> PutBuilder<C>.put(put: String) {
        this.put = put
    }

    fun <C> PutBuilder<C>.resource(resource: String?) {
        this.resource = resource
    }

    fun <C> PutBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> PutBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> PutBuilder<C>.get_params(key: String, value: Any) {
        this.get_params = this.get_params.orEmpty() + Pair(key,value)
    }

    fun <C> PutBuilder<C>.get_params(pair: Pair<String, Any>) {
        this.get_params = this.get_params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> PutBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> PutBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> PutBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> PutBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> PutBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> PutBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> PutBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> PutBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> PutBuilder<C>.context(context: C2, body: PutBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> PutBuilder<C>.forEachContext(context: List<C2>, body: PutBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): PutBuilder<C2> = PutBuilder(context,put,resource,params,get_params,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: PutBuilder<C2>) {
        this.put = other.put
        this.resource = other.resource
        this.params = other.params
        this.get_params = other.get_params
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Put = Put(put!!,resource,params!!,get_params!!,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun PutSpec<C>.unaryPlus() {
        apply(body)
    }
}

@DslBuilder
class TaskBuilder<out C>(
        val context: C,
        private var task: String? = null,
        private var config: TaskConfig? = null,
        private var file: String? = null,
        private var privileged: String? = null,
        private var params: Map<String, Any>? = emptyMap(),
        private var image: String? = null,
        private var input_mapping: Map<String, String>? = emptyMap(),
        private var output_mapping: Map<String, String>? = emptyMap(),
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Task> {
    fun <C> TaskBuilder<C>.task(task: String) {
        this.task = task
    }

    fun <C> TaskBuilder<C>.config(spec: TaskConfigSpec<C>) {
        this.config = TaskConfigTranslator<C>(spec).translate(context)
    }

    fun <C> TaskBuilder<C>.file(file: String?) {
        this.file = file
    }

    fun <C> TaskBuilder<C>.privileged(privileged: String?) {
        this.privileged = privileged
    }

    fun <C> TaskBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> TaskBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> TaskBuilder<C>.image(image: String?) {
        this.image = image
    }

    fun <C> TaskBuilder<C>.input_mapping(key: String, value: String) {
        this.input_mapping = this.input_mapping.orEmpty() + Pair(key,value)
    }

    fun <C> TaskBuilder<C>.input_mapping(pair: Pair<String, String>) {
        this.input_mapping = this.input_mapping.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> TaskBuilder<C>.output_mapping(key: String, value: String) {
        this.output_mapping = this.output_mapping.orEmpty() + Pair(key,value)
    }

    fun <C> TaskBuilder<C>.output_mapping(pair: Pair<String, String>) {
        this.output_mapping = this.output_mapping.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> TaskBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> TaskBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> TaskBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> TaskBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> TaskBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> TaskBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> TaskBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> TaskBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> TaskBuilder<C>.context(context: C2, body: TaskBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskBuilder<C>.forEachContext(context: List<C2>, body: TaskBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskBuilder<C2> = TaskBuilder(context,task,config,file,privileged,params,image,input_mapping,output_mapping,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: TaskBuilder<C2>) {
        this.task = other.task
        this.config = other.config
        this.file = other.file
        this.privileged = other.privileged
        this.params = other.params
        this.image = other.image
        this.input_mapping = other.input_mapping
        this.output_mapping = other.output_mapping
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Task = Task(task!!,config!!,file,privileged,params!!,image,input_mapping!!,output_mapping!!,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun TaskSpec<C>.unaryPlus() {
        apply(body)
    }
}

@DslBuilder
class AggregateBuilder<out C>(
        val context: C,
        private var aggregate: List<Step>? = emptyList(),
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Aggregate> {
    fun <C> AggregateBuilder<C>.aggregate(spec: GetSpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + GetTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: PutSpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + PutTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TaskSpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + TaskTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: AggregateSpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + AggregateTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: DoSpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + DoTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.aggregate(spec: TrySpec<C>) {
        this.aggregate = this.aggregate.orEmpty() + TryTranslator<C>(spec).translate(context)
    }

    fun <C> AggregateBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> AggregateBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> AggregateBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> AggregateBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> AggregateBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> AggregateBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> AggregateBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> AggregateBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> AggregateBuilder<C>.context(context: C2, body: AggregateBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> AggregateBuilder<C>.forEachContext(context: List<C2>, body: AggregateBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): AggregateBuilder<C2> = AggregateBuilder(context,aggregate,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: AggregateBuilder<C2>) {
        this.aggregate = other.aggregate
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Aggregate = Aggregate(aggregate!!,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun AggregateSpec<C>.unaryPlus() {
        apply(body)
    }
}

@DslBuilder
class DoBuilder<out C>(
        val context: C,
        private var doIt: List<Step>? = emptyList(),
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Do> {
    fun <C> DoBuilder<C>.doIt(spec: GetSpec<C>) {
        this.doIt = this.doIt.orEmpty() + GetTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.doIt(spec: PutSpec<C>) {
        this.doIt = this.doIt.orEmpty() + PutTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.doIt(spec: TaskSpec<C>) {
        this.doIt = this.doIt.orEmpty() + TaskTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.doIt(spec: AggregateSpec<C>) {
        this.doIt = this.doIt.orEmpty() + AggregateTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.doIt(spec: DoSpec<C>) {
        this.doIt = this.doIt.orEmpty() + DoTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.doIt(spec: TrySpec<C>) {
        this.doIt = this.doIt.orEmpty() + TryTranslator<C>(spec).translate(context)
    }

    fun <C> DoBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> DoBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> DoBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> DoBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> DoBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> DoBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> DoBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> DoBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> DoBuilder<C>.context(context: C2, body: DoBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> DoBuilder<C>.forEachContext(context: List<C2>, body: DoBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): DoBuilder<C2> = DoBuilder(context,doIt,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: DoBuilder<C2>) {
        this.doIt = other.doIt
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Do = Do(doIt!!,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun DoSpec<C>.unaryPlus() {
        apply(body)
    }
}

@DslBuilder
class TryBuilder<out C>(
        val context: C,
        private var tryIt: Step? = null,
        private var on_success: Step? = null,
        private var on_failure: Step? = null,
        private var on_abort: Step? = null,
        private var ensure: Step? = null,
        private var tags: List<String>? = emptyList(),
        private var timeout: String? = null,
        private var attempts: Int? = null
) : Builder<Try> {
    fun <C> TryBuilder<C>.tryIt(spec: GetSpec<C>) {
        this.tryIt = GetTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.tryIt(spec: PutSpec<C>) {
        this.tryIt = PutTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TaskSpec<C>) {
        this.tryIt = TaskTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.tryIt(spec: AggregateSpec<C>) {
        this.tryIt = AggregateTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.tryIt(spec: DoSpec<C>) {
        this.tryIt = DoTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.tryIt(spec: TrySpec<C>) {
        this.tryIt = TryTranslator<C>(spec).translate(context)
    }

    fun <C> TryBuilder<C>.on_success(on_success: Step?) {
        this.on_success = on_success
    }

    fun <C> TryBuilder<C>.on_failure(on_failure: Step?) {
        this.on_failure = on_failure
    }

    fun <C> TryBuilder<C>.on_abort(on_abort: Step?) {
        this.on_abort = on_abort
    }

    fun <C> TryBuilder<C>.ensure(ensure: Step?) {
        this.ensure = ensure
    }

    fun <C> TryBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> TryBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> TryBuilder<C>.timeout(timeout: String?) {
        this.timeout = timeout
    }

    fun <C> TryBuilder<C>.attempts(attempts: Int?) {
        this.attempts = attempts
    }

    fun <C, C2> TryBuilder<C>.context(context: C2, body: TryBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TryBuilder<C>.forEachContext(context: List<C2>, body: TryBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TryBuilder<C2> = TryBuilder(context,tryIt,on_success,on_failure,on_abort,ensure,tags,timeout,attempts)

    private fun <C2> merge(other: TryBuilder<C2>) {
        this.tryIt = other.tryIt
        this.on_success = other.on_success
        this.on_failure = other.on_failure
        this.on_abort = other.on_abort
        this.ensure = other.ensure
        this.tags = other.tags
        this.timeout = other.timeout
        this.attempts = other.attempts
    }

    override fun build(): Try = Try(tryIt!!,on_success,on_failure,on_abort,ensure,tags!!,timeout,attempts)

    operator fun TrySpec<C>.unaryPlus() {
        apply(body)
    }
}

open class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit = {}) : Spec<GetBuilder<C>, Get> {
    override fun GetBuilder<C>.body() {
        this@GetSpec.body.invoke(this)
    }
}

open class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit = {}) : Spec<PutBuilder<C>, Put> {
    override fun PutBuilder<C>.body() {
        this@PutSpec.body.invoke(this)
    }
}

open class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit = {}) : Spec<TaskBuilder<C>, Task> {
    override fun TaskBuilder<C>.body() {
        this@TaskSpec.body.invoke(this)
    }
}

open class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit = {}) : Spec<AggregateBuilder<C>, Aggregate> {
    override fun AggregateBuilder<C>.body() {
        this@AggregateSpec.body.invoke(this)
    }
}

open class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit = {}) : Spec<DoBuilder<C>, Do> {
    override fun DoBuilder<C>.body() {
        this@DoSpec.body.invoke(this)
    }
}

open class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit = {}) : Spec<TryBuilder<C>, Try> {
    override fun TryBuilder<C>.body() {
        this@TrySpec.body.invoke(this)
    }
}

open class GetTranslator<in C>(private val spec: GetSpec<C>) : Translator<C, Get> {
    constructor(body: GetBuilder<C>.() -> Unit = {}) : this(GetSpec<C>(body))

    override fun translate(context: C): Get {
        val builder = GetBuilder(context)
        val translator = BuilderSpecTranslator<C, GetBuilder<C>, Get>(builder, spec)
        return translator.translate(context)
    }
}

open class PutTranslator<in C>(private val spec: PutSpec<C>) : Translator<C, Put> {
    constructor(body: PutBuilder<C>.() -> Unit = {}) : this(PutSpec<C>(body))

    override fun translate(context: C): Put {
        val builder = PutBuilder(context)
        val translator = BuilderSpecTranslator<C, PutBuilder<C>, Put>(builder, spec)
        return translator.translate(context)
    }
}

open class TaskTranslator<in C>(private val spec: TaskSpec<C>) : Translator<C, Task> {
    constructor(body: TaskBuilder<C>.() -> Unit = {}) : this(TaskSpec<C>(body))

    override fun translate(context: C): Task {
        val builder = TaskBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskBuilder<C>, Task>(builder, spec)
        return translator.translate(context)
    }
}

open class AggregateTranslator<in C>(private val spec: AggregateSpec<C>) : Translator<C, Aggregate> {
    constructor(body: AggregateBuilder<C>.() -> Unit = {}) : this(AggregateSpec<C>(body))

    override fun translate(context: C): Aggregate {
        val builder = AggregateBuilder(context)
        val translator = BuilderSpecTranslator<C, AggregateBuilder<C>, Aggregate>(builder, spec)
        return translator.translate(context)
    }
}

open class DoTranslator<in C>(private val spec: DoSpec<C>) : Translator<C, Do> {
    constructor(body: DoBuilder<C>.() -> Unit = {}) : this(DoSpec<C>(body))

    override fun translate(context: C): Do {
        val builder = DoBuilder(context)
        val translator = BuilderSpecTranslator<C, DoBuilder<C>, Do>(builder, spec)
        return translator.translate(context)
    }
}

open class TryTranslator<in C>(private val spec: TrySpec<C>) : Translator<C, Try> {
    constructor(body: TryBuilder<C>.() -> Unit = {}) : this(TrySpec<C>(body))

    override fun translate(context: C): Try {
        val builder = TryBuilder(context)
        val translator = BuilderSpecTranslator<C, TryBuilder<C>, Try>(builder, spec)
        return translator.translate(context)
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
) {
    companion object {
        operator fun <C> invoke(body: TaskConfigBuilder<C>.() -> Unit = {}): TaskConfigSpec<C> = TaskConfigSpec<C>(body)
    }
}

@DslBuilder
class TaskConfigBuilder<out C>(
        val context: C,
        private var platform: String? = null,
        private var image_resource: TaskResource? = null,
        private var rootfs_uri: String? = null,
        private var inputs: List<TaskInput>? = emptyList(),
        private var outputs: List<TaskOutput>? = emptyList(),
        private var caches: List<TaskCache>? = emptyList(),
        private var run: TaskRunConfig? = null,
        private var params: Map<String, Any>? = emptyMap()
) : Builder<TaskConfig> {
    fun <C> TaskConfigBuilder<C>.platform(platform: String) {
        this.platform = platform
    }

    fun <C> TaskConfigBuilder<C>.image_resource(spec: TaskResourceSpec<C>) {
        this.image_resource = TaskResourceTranslator<C>(spec).translate(context)
    }

    fun <C> TaskConfigBuilder<C>.rootfs_uri(rootfs_uri: String?) {
        this.rootfs_uri = rootfs_uri
    }

    fun <C> TaskConfigBuilder<C>.input(spec: TaskInputSpec<C>) {
        this.inputs = this.inputs.orEmpty() + TaskInputTranslator<C>(spec).translate(context)
    }

    fun <C> TaskConfigBuilder<C>.output(spec: TaskOutputSpec<C>) {
        this.outputs = this.outputs.orEmpty() + TaskOutputTranslator<C>(spec).translate(context)
    }

    fun <C> TaskConfigBuilder<C>.cache(spec: TaskCacheSpec<C>) {
        this.caches = this.caches.orEmpty() + TaskCacheTranslator<C>(spec).translate(context)
    }

    fun <C> TaskConfigBuilder<C>.run(run: TaskRunConfig?) {
        this.run = run
    }

    fun <C> TaskConfigBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> TaskConfigBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C, C2> TaskConfigBuilder<C>.context(context: C2, body: TaskConfigBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskConfigBuilder<C>.forEachContext(context: List<C2>, body: TaskConfigBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskConfigBuilder<C2> = TaskConfigBuilder(context,platform,image_resource,rootfs_uri,inputs,outputs,caches,run,params)

    private fun <C2> merge(other: TaskConfigBuilder<C2>) {
        this.platform = other.platform
        this.image_resource = other.image_resource
        this.rootfs_uri = other.rootfs_uri
        this.inputs = other.inputs
        this.outputs = other.outputs
        this.caches = other.caches
        this.run = other.run
        this.params = other.params
    }

    override fun build(): TaskConfig = TaskConfig(platform!!,image_resource!!,rootfs_uri,inputs!!,outputs!!,caches!!,run,params!!)

    operator fun TaskConfigSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit = {}) : Spec<TaskConfigBuilder<C>, TaskConfig> {
    override fun TaskConfigBuilder<C>.body() {
        this@TaskConfigSpec.body.invoke(this)
    }
}

open class TaskConfigTranslator<in C>(private val spec: TaskConfigSpec<C>) : Translator<C, TaskConfig> {
    constructor(body: TaskConfigBuilder<C>.() -> Unit = {}) : this(TaskConfigSpec<C>(body))

    override fun translate(context: C): TaskConfig {
        val builder = TaskConfigBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskConfigBuilder<C>, TaskConfig>(builder, spec)
        return translator.translate(context)
    }
}

data class TaskResource(
        val type: String,
        val source: Map<String, Any>,
        val params: Map<String, Any>,
        val version: Map<String, String>
) {
    companion object {
        operator fun <C> invoke(body: TaskResourceBuilder<C>.() -> Unit = {}): TaskResourceSpec<C> = TaskResourceSpec<C>(body)
    }
}

@DslBuilder
class TaskResourceBuilder<out C>(
        val context: C,
        private var type: String? = null,
        private var source: Map<String, Any>? = emptyMap(),
        private var params: Map<String, Any>? = emptyMap(),
        private var version: Map<String, String>? = emptyMap()
) : Builder<TaskResource> {
    fun <C> TaskResourceBuilder<C>.type(type: String) {
        this.type = type
    }

    fun <C> TaskResourceBuilder<C>.source(key: String, value: Any) {
        this.source = this.source.orEmpty() + Pair(key,value)
    }

    fun <C> TaskResourceBuilder<C>.source(pair: Pair<String, Any>) {
        this.source = this.source.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> TaskResourceBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> TaskResourceBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> TaskResourceBuilder<C>.version(key: String, value: String) {
        this.version = this.version.orEmpty() + Pair(key,value)
    }

    fun <C> TaskResourceBuilder<C>.version(pair: Pair<String, String>) {
        this.version = this.version.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C, C2> TaskResourceBuilder<C>.context(context: C2, body: TaskResourceBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskResourceBuilder<C>.forEachContext(context: List<C2>, body: TaskResourceBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskResourceBuilder<C2> = TaskResourceBuilder(context,type,source,params,version)

    private fun <C2> merge(other: TaskResourceBuilder<C2>) {
        this.type = other.type
        this.source = other.source
        this.params = other.params
        this.version = other.version
    }

    override fun build(): TaskResource = TaskResource(type!!,source!!,params!!,version!!)

    operator fun TaskResourceSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit = {}) : Spec<TaskResourceBuilder<C>, TaskResource> {
    override fun TaskResourceBuilder<C>.body() {
        this@TaskResourceSpec.body.invoke(this)
    }
}

open class TaskResourceTranslator<in C>(private val spec: TaskResourceSpec<C>) : Translator<C, TaskResource> {
    constructor(body: TaskResourceBuilder<C>.() -> Unit = {}) : this(TaskResourceSpec<C>(body))

    override fun translate(context: C): TaskResource {
        val builder = TaskResourceBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskResourceBuilder<C>, TaskResource>(builder, spec)
        return translator.translate(context)
    }
}

data class TaskInput(
        val name: String,
        val path: String?,
        val optional: Boolean
) {
    companion object {
        operator fun <C> invoke(body: TaskInputBuilder<C>.() -> Unit = {}): TaskInputSpec<C> = TaskInputSpec<C>(body)
    }
}

@DslBuilder
class TaskInputBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var path: String? = null,
        private var optional: Boolean? = null
) : Builder<TaskInput> {
    fun <C> TaskInputBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> TaskInputBuilder<C>.path(path: String?) {
        this.path = path
    }

    fun <C> TaskInputBuilder<C>.optional(optional: Boolean) {
        this.optional = optional
    }

    fun <C, C2> TaskInputBuilder<C>.context(context: C2, body: TaskInputBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskInputBuilder<C>.forEachContext(context: List<C2>, body: TaskInputBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskInputBuilder<C2> = TaskInputBuilder(context,name,path,optional)

    private fun <C2> merge(other: TaskInputBuilder<C2>) {
        this.name = other.name
        this.path = other.path
        this.optional = other.optional
    }

    override fun build(): TaskInput = TaskInput(name!!,path,optional!!)

    operator fun TaskInputSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit = {}) : Spec<TaskInputBuilder<C>, TaskInput> {
    override fun TaskInputBuilder<C>.body() {
        this@TaskInputSpec.body.invoke(this)
    }
}

open class TaskInputTranslator<in C>(private val spec: TaskInputSpec<C>) : Translator<C, TaskInput> {
    constructor(body: TaskInputBuilder<C>.() -> Unit = {}) : this(TaskInputSpec<C>(body))

    override fun translate(context: C): TaskInput {
        val builder = TaskInputBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskInputBuilder<C>, TaskInput>(builder, spec)
        return translator.translate(context)
    }
}

data class TaskOutput(val name: String, val path: String?) {
    companion object {
        operator fun <C> invoke(body: TaskOutputBuilder<C>.() -> Unit = {}): TaskOutputSpec<C> = TaskOutputSpec<C>(body)
    }
}

@DslBuilder
class TaskOutputBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var path: String? = null
) : Builder<TaskOutput> {
    fun <C> TaskOutputBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> TaskOutputBuilder<C>.path(path: String?) {
        this.path = path
    }

    fun <C, C2> TaskOutputBuilder<C>.context(context: C2, body: TaskOutputBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskOutputBuilder<C>.forEachContext(context: List<C2>, body: TaskOutputBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskOutputBuilder<C2> = TaskOutputBuilder(context,name,path)

    private fun <C2> merge(other: TaskOutputBuilder<C2>) {
        this.name = other.name
        this.path = other.path
    }

    override fun build(): TaskOutput = TaskOutput(name!!,path)

    operator fun TaskOutputSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit = {}) : Spec<TaskOutputBuilder<C>, TaskOutput> {
    override fun TaskOutputBuilder<C>.body() {
        this@TaskOutputSpec.body.invoke(this)
    }
}

open class TaskOutputTranslator<in C>(private val spec: TaskOutputSpec<C>) : Translator<C, TaskOutput> {
    constructor(body: TaskOutputBuilder<C>.() -> Unit = {}) : this(TaskOutputSpec<C>(body))

    override fun translate(context: C): TaskOutput {
        val builder = TaskOutputBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskOutputBuilder<C>, TaskOutput>(builder, spec)
        return translator.translate(context)
    }
}

data class TaskCache(val path: String) {
    companion object {
        operator fun <C> invoke(body: TaskCacheBuilder<C>.() -> Unit = {}): TaskCacheSpec<C> = TaskCacheSpec<C>(body)
    }
}

@DslBuilder
class TaskCacheBuilder<out C>(val context: C, private var path: String? = null) : Builder<TaskCache> {
    fun <C> TaskCacheBuilder<C>.path(path: String) {
        this.path = path
    }

    fun <C, C2> TaskCacheBuilder<C>.context(context: C2, body: TaskCacheBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskCacheBuilder<C>.forEachContext(context: List<C2>, body: TaskCacheBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskCacheBuilder<C2> = TaskCacheBuilder(context,path)

    private fun <C2> merge(other: TaskCacheBuilder<C2>) {
        this.path = other.path
    }

    override fun build(): TaskCache = TaskCache(path!!)

    operator fun TaskCacheSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit = {}) : Spec<TaskCacheBuilder<C>, TaskCache> {
    override fun TaskCacheBuilder<C>.body() {
        this@TaskCacheSpec.body.invoke(this)
    }
}

open class TaskCacheTranslator<in C>(private val spec: TaskCacheSpec<C>) : Translator<C, TaskCache> {
    constructor(body: TaskCacheBuilder<C>.() -> Unit = {}) : this(TaskCacheSpec<C>(body))

    override fun translate(context: C): TaskCache {
        val builder = TaskCacheBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskCacheBuilder<C>, TaskCache>(builder, spec)
        return translator.translate(context)
    }
}

data class TaskRunConfig(
        val path: String,
        val args: List<String>,
        val dir: String?,
        val user: String?
) {
    companion object {
        operator fun <C> invoke(body: TaskRunConfigBuilder<C>.() -> Unit = {}): TaskRunConfigSpec<C> = TaskRunConfigSpec<C>(body)
    }
}

@DslBuilder
class TaskRunConfigBuilder<out C>(
        val context: C,
        private var path: String? = null,
        private var args: List<String>? = emptyList(),
        private var dir: String? = null,
        private var user: String? = null
) : Builder<TaskRunConfig> {
    fun <C> TaskRunConfigBuilder<C>.path(path: String) {
        this.path = path
    }

    fun <C> TaskRunConfigBuilder<C>.arg(arg: String) {
        this.args = this.args.orEmpty() + arg
    }

    fun <C> TaskRunConfigBuilder<C>.args(args: List<String>) {
        this.args = this.args.orEmpty() + args
    }

    fun <C> TaskRunConfigBuilder<C>.dir(dir: String?) {
        this.dir = dir
    }

    fun <C> TaskRunConfigBuilder<C>.user(user: String?) {
        this.user = user
    }

    fun <C, C2> TaskRunConfigBuilder<C>.context(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> TaskRunConfigBuilder<C>.forEachContext(context: List<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): TaskRunConfigBuilder<C2> = TaskRunConfigBuilder(context,path,args,dir,user)

    private fun <C2> merge(other: TaskRunConfigBuilder<C2>) {
        this.path = other.path
        this.args = other.args
        this.dir = other.dir
        this.user = other.user
    }

    override fun build(): TaskRunConfig = TaskRunConfig(path!!,args!!,dir,user)

    operator fun TaskRunConfigSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit = {}) : Spec<TaskRunConfigBuilder<C>, TaskRunConfig> {
    override fun TaskRunConfigBuilder<C>.body() {
        this@TaskRunConfigSpec.body.invoke(this)
    }
}

open class TaskRunConfigTranslator<in C>(private val spec: TaskRunConfigSpec<C>) : Translator<C, TaskRunConfig> {
    constructor(body: TaskRunConfigBuilder<C>.() -> Unit = {}) : this(TaskRunConfigSpec<C>(body))

    override fun translate(context: C): TaskRunConfig {
        val builder = TaskRunConfigBuilder(context)
        val translator = BuilderSpecTranslator<C, TaskRunConfigBuilder<C>, TaskRunConfig>(builder, spec)
        return translator.translate(context)
    }
}

data class Resource(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val check_every: String?,
        val tags: List<String>,
        val webhook_token: String?
) {
    companion object {
        operator fun <C> invoke(body: ResourceBuilder<C>.() -> Unit = {}): ResourceSpec<C> = ResourceSpec<C>(body)
    }
}

@DslBuilder
class ResourceBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var type: String? = null,
        private var source: Map<String, Any>? = emptyMap(),
        private var check_every: String? = null,
        private var tags: List<String>? = emptyList(),
        private var webhook_token: String? = null
) : Builder<Resource> {
    fun <C> ResourceBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> ResourceBuilder<C>.type(type: String) {
        this.type = type
    }

    fun <C> ResourceBuilder<C>.source(key: String, value: Any) {
        this.source = this.source.orEmpty() + Pair(key,value)
    }

    fun <C> ResourceBuilder<C>.source(pair: Pair<String, Any>) {
        this.source = this.source.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> ResourceBuilder<C>.check_every(check_every: String?) {
        this.check_every = check_every
    }

    fun <C> ResourceBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> ResourceBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C> ResourceBuilder<C>.webhook_token(webhook_token: String?) {
        this.webhook_token = webhook_token
    }

    fun <C, C2> ResourceBuilder<C>.context(context: C2, body: ResourceBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> ResourceBuilder<C>.forEachContext(context: List<C2>, body: ResourceBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): ResourceBuilder<C2> = ResourceBuilder(context,name,type,source,check_every,tags,webhook_token)

    private fun <C2> merge(other: ResourceBuilder<C2>) {
        this.name = other.name
        this.type = other.type
        this.source = other.source
        this.check_every = other.check_every
        this.tags = other.tags
        this.webhook_token = other.webhook_token
    }

    override fun build(): Resource = Resource(name!!,type!!,source!!,check_every,tags!!,webhook_token)

    operator fun ResourceSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit = {}) : Spec<ResourceBuilder<C>, Resource> {
    override fun ResourceBuilder<C>.body() {
        this@ResourceSpec.body.invoke(this)
    }
}

open class ResourceTranslator<in C>(private val spec: ResourceSpec<C>) : Translator<C, Resource> {
    constructor(body: ResourceBuilder<C>.() -> Unit = {}) : this(ResourceSpec<C>(body))

    override fun translate(context: C): Resource {
        val builder = ResourceBuilder(context)
        val translator = BuilderSpecTranslator<C, ResourceBuilder<C>, Resource>(builder, spec)
        return translator.translate(context)
    }
}

data class ResourceType(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val privileged: Boolean?,
        val params: Map<String, Any>,
        val tags: List<String>
) {
    companion object {
        operator fun <C> invoke(body: ResourceTypeBuilder<C>.() -> Unit = {}): ResourceTypeSpec<C> = ResourceTypeSpec<C>(body)
    }
}

@DslBuilder
class ResourceTypeBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var type: String? = null,
        private var source: Map<String, Any>? = emptyMap(),
        private var privileged: Boolean? = null,
        private var params: Map<String, Any>? = emptyMap(),
        private var tags: List<String>? = emptyList()
) : Builder<ResourceType> {
    fun <C> ResourceTypeBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> ResourceTypeBuilder<C>.type(type: String) {
        this.type = type
    }

    fun <C> ResourceTypeBuilder<C>.source(key: String, value: Any) {
        this.source = this.source.orEmpty() + Pair(key,value)
    }

    fun <C> ResourceTypeBuilder<C>.source(pair: Pair<String, Any>) {
        this.source = this.source.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> ResourceTypeBuilder<C>.privileged(privileged: Boolean?) {
        this.privileged = privileged
    }

    fun <C> ResourceTypeBuilder<C>.params(key: String, value: Any) {
        this.params = this.params.orEmpty() + Pair(key,value)
    }

    fun <C> ResourceTypeBuilder<C>.params(pair: Pair<String, Any>) {
        this.params = this.params.orEmpty() + Pair(pair.first,pair.second)
    }

    fun <C> ResourceTypeBuilder<C>.tag(tag: String) {
        this.tags = this.tags.orEmpty() + tag
    }

    fun <C> ResourceTypeBuilder<C>.tags(tags: List<String>) {
        this.tags = this.tags.orEmpty() + tags
    }

    fun <C, C2> ResourceTypeBuilder<C>.context(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> ResourceTypeBuilder<C>.forEachContext(context: List<C2>, body: ResourceTypeBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): ResourceTypeBuilder<C2> = ResourceTypeBuilder(context,name,type,source,privileged,params,tags)

    private fun <C2> merge(other: ResourceTypeBuilder<C2>) {
        this.name = other.name
        this.type = other.type
        this.source = other.source
        this.privileged = other.privileged
        this.params = other.params
        this.tags = other.tags
    }

    override fun build(): ResourceType = ResourceType(name!!,type!!,source!!,privileged,params!!,tags!!)

    operator fun ResourceTypeSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit = {}) : Spec<ResourceTypeBuilder<C>, ResourceType> {
    override fun ResourceTypeBuilder<C>.body() {
        this@ResourceTypeSpec.body.invoke(this)
    }
}

open class ResourceTypeTranslator<in C>(private val spec: ResourceTypeSpec<C>) : Translator<C, ResourceType> {
    constructor(body: ResourceTypeBuilder<C>.() -> Unit = {}) : this(ResourceTypeSpec<C>(body))

    override fun translate(context: C): ResourceType {
        val builder = ResourceTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, ResourceTypeBuilder<C>, ResourceType>(builder, spec)
        return translator.translate(context)
    }
}

data class Group(
        val name: String,
        val jobs: List<String>,
        val resources: List<String>
) {
    companion object {
        operator fun <C> invoke(body: GroupBuilder<C>.() -> Unit = {}): GroupSpec<C> = GroupSpec<C>(body)
    }
}

@DslBuilder
class GroupBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var jobs: List<String>? = emptyList(),
        private var resources: List<String>? = emptyList()
) : Builder<Group> {
    fun <C> GroupBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> GroupBuilder<C>.job(job: String) {
        this.jobs = this.jobs.orEmpty() + job
    }

    fun <C> GroupBuilder<C>.jobs(jobs: List<String>) {
        this.jobs = this.jobs.orEmpty() + jobs
    }

    fun <C> GroupBuilder<C>.resource(resource: String) {
        this.resources = this.resources.orEmpty() + resource
    }

    fun <C> GroupBuilder<C>.resources(resources: List<String>) {
        this.resources = this.resources.orEmpty() + resources
    }

    fun <C, C2> GroupBuilder<C>.context(context: C2, body: GroupBuilder<C2>.() -> Unit = {}) {
        val builder = split(context)
        body.invoke(builder)
        merge(builder)
    }

    fun <C, C2> GroupBuilder<C>.forEachContext(context: List<C2>, body: GroupBuilder<C2>.() -> Unit = {}) {
        context.forEach { context(it, body) }
    }

    private fun <C2> split(context: C2): GroupBuilder<C2> = GroupBuilder(context,name,jobs,resources)

    private fun <C2> merge(other: GroupBuilder<C2>) {
        this.name = other.name
        this.jobs = other.jobs
        this.resources = other.resources
    }

    override fun build(): Group = Group(name!!,jobs!!,resources!!)

    operator fun GroupSpec<C>.unaryPlus() {
        apply(body)
    }
}

open class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit = {}) : Spec<GroupBuilder<C>, Group> {
    override fun GroupBuilder<C>.body() {
        this@GroupSpec.body.invoke(this)
    }
}

open class GroupTranslator<in C>(private val spec: GroupSpec<C>) : Translator<C, Group> {
    constructor(body: GroupBuilder<C>.() -> Unit = {}) : this(GroupSpec<C>(body))

    override fun translate(context: C): Group {
        val builder = GroupBuilder(context)
        val translator = BuilderSpecTranslator<C, GroupBuilder<C>, Group>(builder, spec)
        return translator.translate(context)
    }
}
