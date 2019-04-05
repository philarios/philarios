package io.philarios.concourse

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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

@DslBuilder
internal class JobShellBuilder<out C>(override val context: C, internal var shell: JobShell = JobShell()) : JobBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Step> plan(spec: StepSpec<C, T>) {
        shell = shell.copy(plan = shell.plan.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
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

    override fun <T : Step> on_success(spec: StepSpec<C, T>) {
        shell = shell.copy(on_success = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_success(ref: StepRef<T>) {
        shell = shell.copy(on_success = Deferred(ref.key))
    }

    override fun <T : Step> on_success(value: T) {
        shell = shell.copy(on_success = Wrapper(value))
    }

    override fun <T : Step> on_failure(spec: StepSpec<C, T>) {
        shell = shell.copy(on_failure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_failure(ref: StepRef<T>) {
        shell = shell.copy(on_failure = Deferred(ref.key))
    }

    override fun <T : Step> on_failure(value: T) {
        shell = shell.copy(on_failure = Wrapper(value))
    }

    override fun <T : Step> on_abort(spec: StepSpec<C, T>) {
        shell = shell.copy(on_abort = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> on_abort(ref: StepRef<T>) {
        shell = shell.copy(on_abort = Deferred(ref.key))
    }

    override fun <T : Step> on_abort(value: T) {
        shell = shell.copy(on_abort = Wrapper(value))
    }

    override fun <T : Step> ensure(spec: StepSpec<C, T>) {
        shell = shell.copy(ensure = StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> ensure(ref: StepRef<T>) {
        shell = shell.copy(ensure = Deferred(ref.key))
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

class JobScaffolder<in C>(internal val spec: JobSpec<C>) : Scaffolder<C, Job> {
    override fun createScaffold(context: C): Scaffold<Job> {
        val builder = JobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
