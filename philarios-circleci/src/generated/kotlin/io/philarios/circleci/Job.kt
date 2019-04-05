package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Job(
        val docker: List<DockerExecutor>?,
        val resource_class: ResourceClass?,
        val machine: MachineExecutor?,
        val macos: MacosExecutor?,
        val shell: String?,
        val steps: List<Step>?,
        val working_directory: String?,
        val parallelism: Int?,
        val environment: Map<String, String>?,
        val branches: Map<String, String>?
)

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

@DslBuilder
interface JobBuilder<out C> {
    val context: C

    fun docker(body: DockerExecutorBuilder<C>.() -> Unit)

    fun docker(spec: DockerExecutorSpec<C>)

    fun docker(ref: DockerExecutorRef)

    fun docker(value: DockerExecutor)

    fun docker(docker: List<DockerExecutor>)

    fun resource_class(value: ResourceClass)

    fun machine(body: MachineExecutorBuilder<C>.() -> Unit)

    fun machine(spec: MachineExecutorSpec<C>)

    fun machine(ref: MachineExecutorRef)

    fun machine(value: MachineExecutor)

    fun macos(body: MacosExecutorBuilder<C>.() -> Unit)

    fun macos(spec: MacosExecutorSpec<C>)

    fun macos(ref: MacosExecutorRef)

    fun macos(value: MacosExecutor)

    fun shell(value: String)

    fun <T : Step> step(spec: StepSpec<C, T>)

    fun <T : Step> step(ref: StepRef<T>)

    fun <T : Step> step(value: T)

    fun working_directory(value: String)

    fun parallelism(value: Int)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun branches(key: String, value: String)

    fun branches(pair: Pair<String, String>)

    fun branches(branches: Map<String, String>)

    fun include(body: JobBuilder<C>.() -> Unit)

    fun include(spec: JobSpec<C>)

    fun <C2> include(context: C2, body: JobBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: JobSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: JobBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: JobSpec<C2>)
}

class JobRef(internal val key: String)

internal data class JobShell(
        var docker: List<Scaffold<DockerExecutor>>? = null,
        var resource_class: Scaffold<ResourceClass>? = null,
        var machine: Scaffold<MachineExecutor>? = null,
        var macos: Scaffold<MacosExecutor>? = null,
        var shell: Scaffold<String>? = null,
        var steps: List<Scaffold<Step>>? = null,
        var working_directory: Scaffold<String>? = null,
        var parallelism: Scaffold<Int>? = null,
        var environment: Map<Scaffold<String>, Scaffold<String>>? = null,
        var branches: Map<Scaffold<String>, Scaffold<String>>? = null
) : Scaffold<Job> {
    override suspend fun resolve(registry: Registry): Job {
        coroutineScope {
            docker?.let{ it.forEach { launch { it.resolve(registry) } } }
            machine?.let{ launch { it.resolve(registry) } }
            macos?.let{ launch { it.resolve(registry) } }
            steps?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Job(
            docker?.let{ it.map { it.resolve(registry) } },
            resource_class?.let{ it.resolve(registry) },
            machine?.let{ it.resolve(registry) },
            macos?.let{ it.resolve(registry) },
            shell?.let{ it.resolve(registry) },
            steps?.let{ it.map { it.resolve(registry) } },
            working_directory?.let{ it.resolve(registry) },
            parallelism?.let{ it.resolve(registry) },
            environment?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            branches?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

@DslBuilder
internal class JobShellBuilder<out C>(override val context: C, internal var shell: JobShell = JobShell()) : JobBuilder<C> {
    override fun docker(body: DockerExecutorBuilder<C>.() -> Unit) {
        shell = shell.copy(docker = shell.docker.orEmpty() + DockerExecutorScaffolder<C>(DockerExecutorSpec<C>(body)).createScaffold(context))
    }

    override fun docker(spec: DockerExecutorSpec<C>) {
        shell = shell.copy(docker = shell.docker.orEmpty() + DockerExecutorScaffolder<C>(spec).createScaffold(context))
    }

    override fun docker(ref: DockerExecutorRef) {
        shell = shell.copy(docker = shell.docker.orEmpty() + Deferred(ref.key))
    }

    override fun docker(value: DockerExecutor) {
        shell = shell.copy(docker = shell.docker.orEmpty() + Wrapper(value))
    }

    override fun docker(docker: List<DockerExecutor>) {
        shell = shell.copy(docker = shell.docker.orEmpty() + docker.map { Wrapper(it) })
    }

    override fun resource_class(value: ResourceClass) {
        shell = shell.copy(resource_class = Wrapper(value))
    }

    override fun machine(body: MachineExecutorBuilder<C>.() -> Unit) {
        shell = shell.copy(machine = MachineExecutorScaffolder<C>(MachineExecutorSpec<C>(body)).createScaffold(context))
    }

    override fun machine(spec: MachineExecutorSpec<C>) {
        shell = shell.copy(machine = MachineExecutorScaffolder<C>(spec).createScaffold(context))
    }

    override fun machine(ref: MachineExecutorRef) {
        shell = shell.copy(machine = Deferred(ref.key))
    }

    override fun machine(value: MachineExecutor) {
        shell = shell.copy(machine = Wrapper(value))
    }

    override fun macos(body: MacosExecutorBuilder<C>.() -> Unit) {
        shell = shell.copy(macos = MacosExecutorScaffolder<C>(MacosExecutorSpec<C>(body)).createScaffold(context))
    }

    override fun macos(spec: MacosExecutorSpec<C>) {
        shell = shell.copy(macos = MacosExecutorScaffolder<C>(spec).createScaffold(context))
    }

    override fun macos(ref: MacosExecutorRef) {
        shell = shell.copy(macos = Deferred(ref.key))
    }

    override fun macos(value: MacosExecutor) {
        shell = shell.copy(macos = Wrapper(value))
    }

    override fun shell(value: String) {
        shell = shell.copy(shell = Wrapper(value))
    }

    override fun <T : Step> step(spec: StepSpec<C, T>) {
        shell = shell.copy(steps = shell.steps.orEmpty() + StepScaffolder<C, Step>(spec).createScaffold(context))
    }

    override fun <T : Step> step(ref: StepRef<T>) {
        shell = shell.copy(steps = shell.steps.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Step> step(value: T) {
        shell = shell.copy(steps = shell.steps.orEmpty() + Wrapper(value))
    }

    override fun working_directory(value: String) {
        shell = shell.copy(working_directory = Wrapper(value))
    }

    override fun parallelism(value: Int) {
        shell = shell.copy(parallelism = Wrapper(value))
    }

    override fun environment(key: String, value: String) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + environment.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun branches(key: String, value: String) {
        shell = shell.copy(branches = shell.branches.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun branches(pair: Pair<String, String>) {
        shell = shell.copy(branches = shell.branches.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun branches(branches: Map<String, String>) {
        shell = shell.copy(branches = shell.branches.orEmpty() + branches.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
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
