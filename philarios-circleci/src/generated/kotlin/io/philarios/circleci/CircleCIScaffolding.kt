// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CircleCIScaffolder<in C>(internal val spec: CircleCISpec<C>) : Scaffolder<C, CircleCI> {
    override fun createScaffold(context: C): Scaffold<CircleCI> {
        val builder = CircleCIShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class CircleCIShellBuilder<out C>(override val context: C, internal var shell: CircleCIShell = CircleCIShell()) : CircleCIBuilder<C> {
    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun jobs(key: String, body: JobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),JobScaffolder<C>(JobSpec<C>(body)).createScaffold(context)))
    }

    override fun jobs(key: String, spec: JobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),JobScaffolder<C>(spec).createScaffold(context)))
    }

    override fun jobs(key: String, ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),Deferred(ref.key)))
    }

    override fun jobs(key: String, value: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun workflows(key: String, body: WorkflowBuilder<C>.() -> Unit) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),WorkflowScaffolder<C>(WorkflowSpec<C>(body)).createScaffold(context)))
    }

    override fun workflows(key: String, spec: WorkflowSpec<C>) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),WorkflowScaffolder<C>(spec).createScaffold(context)))
    }

    override fun workflows(key: String, ref: WorkflowRef) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),Deferred(ref.key)))
    }

    override fun workflows(key: String, value: Workflow) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun include(body: CircleCIBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CircleCISpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CircleCIBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CircleCISpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CircleCIBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CircleCISpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CircleCIShellBuilder<C2> = CircleCIShellBuilder(context, shell)

    private fun <C2> merge(other: CircleCIShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class CircleCIShell(
        var version: Scaffold<String>? = null,
        var jobs: Map<Scaffold<String>, Scaffold<Job>>? = null,
        var workflows: Map<Scaffold<String>, Scaffold<Workflow>>? = null
) : Scaffold<CircleCI> {
    override suspend fun resolve(registry: Registry): CircleCI {
        coroutineScope {
            jobs?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
            workflows?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
        }
        val value = CircleCI(
            version?.let{ it.resolve(registry) },
            jobs?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            workflows?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

class JobScaffolder<in C>(internal val spec: JobSpec<C>) : Scaffolder<C, Job> {
    override fun createScaffold(context: C): Scaffold<Job> {
        val builder = JobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class DockerExecutorScaffolder<in C>(internal val spec: DockerExecutorSpec<C>) : Scaffolder<C, DockerExecutor> {
    override fun createScaffold(context: C): Scaffold<DockerExecutor> {
        val builder = DockerExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DockerExecutorShellBuilder<out C>(override val context: C, internal var shell: DockerExecutorShell = DockerExecutorShell()) : DockerExecutorBuilder<C> {
    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun entrypoint(value: String) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + Wrapper(value))
    }

    override fun entrypoint(entrypoint: List<String>) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + entrypoint.map { Wrapper(it) })
    }

    override fun command(value: String) {
        shell = shell.copy(command = shell.command.orEmpty() + Wrapper(value))
    }

    override fun command(command: List<String>) {
        shell = shell.copy(command = shell.command.orEmpty() + command.map { Wrapper(it) })
    }

    override fun user(value: String) {
        shell = shell.copy(user = Wrapper(value))
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

    override fun auth(body: AuthBuilder<C>.() -> Unit) {
        shell = shell.copy(auth = AuthScaffolder<C>(AuthSpec<C>(body)).createScaffold(context))
    }

    override fun auth(spec: AuthSpec<C>) {
        shell = shell.copy(auth = AuthScaffolder<C>(spec).createScaffold(context))
    }

    override fun auth(ref: AuthRef) {
        shell = shell.copy(auth = Deferred(ref.key))
    }

    override fun auth(value: Auth) {
        shell = shell.copy(auth = Wrapper(value))
    }

    override fun aws_auth(body: AwsAuthBuilder<C>.() -> Unit) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder<C>(AwsAuthSpec<C>(body)).createScaffold(context))
    }

    override fun aws_auth(spec: AwsAuthSpec<C>) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder<C>(spec).createScaffold(context))
    }

    override fun aws_auth(ref: AwsAuthRef) {
        shell = shell.copy(aws_auth = Deferred(ref.key))
    }

    override fun aws_auth(value: AwsAuth) {
        shell = shell.copy(aws_auth = Wrapper(value))
    }

    override fun include(body: DockerExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DockerExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DockerExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DockerExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DockerExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DockerExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DockerExecutorShellBuilder<C2> = DockerExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: DockerExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class DockerExecutorShell(
        var image: Scaffold<String>? = null,
        var entrypoint: List<Scaffold<String>>? = null,
        var command: List<Scaffold<String>>? = null,
        var user: Scaffold<String>? = null,
        var environment: Map<Scaffold<String>, Scaffold<String>>? = null,
        var auth: Scaffold<Auth>? = null,
        var aws_auth: Scaffold<AwsAuth>? = null
) : Scaffold<DockerExecutor> {
    override suspend fun resolve(registry: Registry): DockerExecutor {
        coroutineScope {
            auth?.let{ launch { it.resolve(registry) } }
            aws_auth?.let{ launch { it.resolve(registry) } }
        }
        val value = DockerExecutor(
            image?.let{ it.resolve(registry) },
            entrypoint?.let{ it.map { it.resolve(registry) } },
            command?.let{ it.map { it.resolve(registry) } },
            user?.let{ it.resolve(registry) },
            environment?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            auth?.let{ it.resolve(registry) },
            aws_auth?.let{ it.resolve(registry) }
        )
        return value
    }
}

class AuthScaffolder<in C>(internal val spec: AuthSpec<C>) : Scaffolder<C, Auth> {
    override fun createScaffold(context: C): Scaffold<Auth> {
        val builder = AuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AuthShellBuilder<out C>(override val context: C, internal var shell: AuthShell = AuthShell()) : AuthBuilder<C> {
    override fun username(value: String) {
        shell = shell.copy(username = Wrapper(value))
    }

    override fun password(value: String) {
        shell = shell.copy(password = Wrapper(value))
    }

    override fun include(body: AuthBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AuthSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AuthBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AuthSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AuthBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AuthSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AuthShellBuilder<C2> = AuthShellBuilder(context, shell)

    private fun <C2> merge(other: AuthShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class AuthShell(var username: Scaffold<String>? = null, var password: Scaffold<String>? = null) : Scaffold<Auth> {
    override suspend fun resolve(registry: Registry): Auth {
        val value = Auth(
            username?.let{ it.resolve(registry) },
            password?.let{ it.resolve(registry) }
        )
        return value
    }
}

class AwsAuthScaffolder<in C>(internal val spec: AwsAuthSpec<C>) : Scaffolder<C, AwsAuth> {
    override fun createScaffold(context: C): Scaffold<AwsAuth> {
        val builder = AwsAuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AwsAuthShellBuilder<out C>(override val context: C, internal var shell: AwsAuthShell = AwsAuthShell()) : AwsAuthBuilder<C> {
    override fun aws_access_key_id(value: String) {
        shell = shell.copy(aws_access_key_id = Wrapper(value))
    }

    override fun aws_secret_access_key(value: String) {
        shell = shell.copy(aws_secret_access_key = Wrapper(value))
    }

    override fun include(body: AwsAuthBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AwsAuthSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AwsAuthBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AwsAuthSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AwsAuthBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AwsAuthSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AwsAuthShellBuilder<C2> = AwsAuthShellBuilder(context, shell)

    private fun <C2> merge(other: AwsAuthShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class AwsAuthShell(var aws_access_key_id: Scaffold<String>? = null, var aws_secret_access_key: Scaffold<String>? = null) : Scaffold<AwsAuth> {
    override suspend fun resolve(registry: Registry): AwsAuth {
        val value = AwsAuth(
            aws_access_key_id?.let{ it.resolve(registry) },
            aws_secret_access_key?.let{ it.resolve(registry) }
        )
        return value
    }
}

class MachineExecutorScaffolder<in C>(internal val spec: MachineExecutorSpec<C>) : Scaffolder<C, MachineExecutor> {
    override fun createScaffold(context: C): Scaffold<MachineExecutor> {
        val builder = MachineExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class MachineExecutorShellBuilder<out C>(override val context: C, internal var shell: MachineExecutorShell = MachineExecutorShell()) : MachineExecutorBuilder<C> {
    override fun enabled(value: Boolean) {
        shell = shell.copy(enabled = Wrapper(value))
    }

    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun include(body: MachineExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: MachineExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: MachineExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: MachineExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: MachineExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: MachineExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MachineExecutorShellBuilder<C2> = MachineExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: MachineExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class MachineExecutorShell(var enabled: Scaffold<Boolean>? = null, var image: Scaffold<String>? = null) : Scaffold<MachineExecutor> {
    override suspend fun resolve(registry: Registry): MachineExecutor {
        val value = MachineExecutor(
            enabled?.let{ it.resolve(registry) },
            image?.let{ it.resolve(registry) }
        )
        return value
    }
}

class MacosExecutorScaffolder<in C>(internal val spec: MacosExecutorSpec<C>) : Scaffolder<C, MacosExecutor> {
    override fun createScaffold(context: C): Scaffold<MacosExecutor> {
        val builder = MacosExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class MacosExecutorShellBuilder<out C>(override val context: C, internal var shell: MacosExecutorShell = MacosExecutorShell()) : MacosExecutorBuilder<C> {
    override fun xcode(value: String) {
        shell = shell.copy(xcode = Wrapper(value))
    }

    override fun include(body: MacosExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: MacosExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: MacosExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: MacosExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: MacosExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: MacosExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MacosExecutorShellBuilder<C2> = MacosExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: MacosExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class MacosExecutorShell(var xcode: Scaffold<String>? = null) : Scaffold<MacosExecutor> {
    override suspend fun resolve(registry: Registry): MacosExecutor {
        val value = MacosExecutor(
            xcode?.let{ it.resolve(registry) }
        )
        return value
    }
}

class StepScaffolder<in C, out T : Step>(internal val spec: StepSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is RunStepSpec<C> -> RunStepScaffolder(spec).createScaffold(context)
            is CheckoutStepSpec<C> -> CheckoutStepScaffolder(spec).createScaffold(context)
            is SetupRemoteDockerStepSpec<C> -> SetupRemoteDockerStepScaffolder(spec).createScaffold(context)
            is SaveCacheStepSpec<C> -> SaveCacheStepScaffolder(spec).createScaffold(context)
            is RestoreCacheStepSpec<C> -> RestoreCacheStepScaffolder(spec).createScaffold(context)
            is DeployStepSpec<C> -> DeployStepScaffolder(spec).createScaffold(context)
            is StoreArtifactsStepSpec<C> -> StoreArtifactsStepScaffolder(spec).createScaffold(context)
            is StoreTestResultsStepSpec<C> -> StoreTestResultsStepScaffolder(spec).createScaffold(context)
            is PersistToWorkspaceStepSpec<C> -> PersistToWorkspaceStepScaffolder(spec).createScaffold(context)
            is AttachWorkspaceStepSpec<C> -> AttachWorkspaceStepScaffolder(spec).createScaffold(context)
            is AddSshKeysStepSpec<C> -> AddSshKeysStepScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class RunStepScaffolder<in C>(internal val spec: RunStepSpec<C>) : Scaffolder<C, RunStep> {
    override fun createScaffold(context: C): Scaffold<RunStep> {
        val builder = RunStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class CheckoutStepScaffolder<in C>(internal val spec: CheckoutStepSpec<C>) : Scaffolder<C, CheckoutStep> {
    override fun createScaffold(context: C): Scaffold<CheckoutStep> {
        val builder = CheckoutStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SetupRemoteDockerStepScaffolder<in C>(internal val spec: SetupRemoteDockerStepSpec<C>) : Scaffolder<C, SetupRemoteDockerStep> {
    override fun createScaffold(context: C): Scaffold<SetupRemoteDockerStep> {
        val builder = SetupRemoteDockerStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SaveCacheStepScaffolder<in C>(internal val spec: SaveCacheStepSpec<C>) : Scaffolder<C, SaveCacheStep> {
    override fun createScaffold(context: C): Scaffold<SaveCacheStep> {
        val builder = SaveCacheStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RestoreCacheStepScaffolder<in C>(internal val spec: RestoreCacheStepSpec<C>) : Scaffolder<C, RestoreCacheStep> {
    override fun createScaffold(context: C): Scaffold<RestoreCacheStep> {
        val builder = RestoreCacheStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DeployStepScaffolder<in C>(internal val spec: DeployStepSpec<C>) : Scaffolder<C, DeployStep> {
    override fun createScaffold(context: C): Scaffold<DeployStep> {
        val builder = DeployStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreArtifactsStepScaffolder<in C>(internal val spec: StoreArtifactsStepSpec<C>) : Scaffolder<C, StoreArtifactsStep> {
    override fun createScaffold(context: C): Scaffold<StoreArtifactsStep> {
        val builder = StoreArtifactsStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreTestResultsStepScaffolder<in C>(internal val spec: StoreTestResultsStepSpec<C>) : Scaffolder<C, StoreTestResultsStep> {
    override fun createScaffold(context: C): Scaffold<StoreTestResultsStep> {
        val builder = StoreTestResultsStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PersistToWorkspaceStepScaffolder<in C>(internal val spec: PersistToWorkspaceStepSpec<C>) : Scaffolder<C, PersistToWorkspaceStep> {
    override fun createScaffold(context: C): Scaffold<PersistToWorkspaceStep> {
        val builder = PersistToWorkspaceStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AttachWorkspaceStepScaffolder<in C>(internal val spec: AttachWorkspaceStepSpec<C>) : Scaffolder<C, AttachWorkspaceStep> {
    override fun createScaffold(context: C): Scaffold<AttachWorkspaceStep> {
        val builder = AttachWorkspaceStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AddSshKeysStepScaffolder<in C>(internal val spec: AddSshKeysStepSpec<C>) : Scaffolder<C, AddSshKeysStep> {
    override fun createScaffold(context: C): Scaffold<AddSshKeysStep> {
        val builder = AddSshKeysStepShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RunStepShellBuilder<out C>(override val context: C, internal var shell: RunStepShell = RunStepShell()) : RunStepBuilder<C> {
    override fun run(body: RunBuilder<C>.() -> Unit) {
        shell = shell.copy(run = RunScaffolder<C>(RunSpec<C>(body)).createScaffold(context))
    }

    override fun run(spec: RunSpec<C>) {
        shell = shell.copy(run = RunScaffolder<C>(spec).createScaffold(context))
    }

    override fun run(ref: RunRef) {
        shell = shell.copy(run = Deferred(ref.key))
    }

    override fun run(value: Run) {
        shell = shell.copy(run = Wrapper(value))
    }

    override fun include(body: RunStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RunStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RunStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RunStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RunStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RunStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RunStepShellBuilder<C2> = RunStepShellBuilder(context, shell)

    private fun <C2> merge(other: RunStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class CheckoutStepShellBuilder<out C>(override val context: C, internal var shell: CheckoutStepShell = CheckoutStepShell()) : CheckoutStepBuilder<C> {
    override fun checkout(body: CheckoutBuilder<C>.() -> Unit) {
        shell = shell.copy(checkout = CheckoutScaffolder<C>(CheckoutSpec<C>(body)).createScaffold(context))
    }

    override fun checkout(spec: CheckoutSpec<C>) {
        shell = shell.copy(checkout = CheckoutScaffolder<C>(spec).createScaffold(context))
    }

    override fun checkout(ref: CheckoutRef) {
        shell = shell.copy(checkout = Deferred(ref.key))
    }

    override fun checkout(value: Checkout) {
        shell = shell.copy(checkout = Wrapper(value))
    }

    override fun include(body: CheckoutStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CheckoutStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CheckoutStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CheckoutStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CheckoutStepShellBuilder<C2> = CheckoutStepShellBuilder(context, shell)

    private fun <C2> merge(other: CheckoutStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SetupRemoteDockerStepShellBuilder<out C>(override val context: C, internal var shell: SetupRemoteDockerStepShell = SetupRemoteDockerStepShell()) : SetupRemoteDockerStepBuilder<C> {
    override fun setup_remote_docker(body: SetupRemoteDockerBuilder<C>.() -> Unit) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder<C>(SetupRemoteDockerSpec<C>(body)).createScaffold(context))
    }

    override fun setup_remote_docker(spec: SetupRemoteDockerSpec<C>) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder<C>(spec).createScaffold(context))
    }

    override fun setup_remote_docker(ref: SetupRemoteDockerRef) {
        shell = shell.copy(setup_remote_docker = Deferred(ref.key))
    }

    override fun setup_remote_docker(value: SetupRemoteDocker) {
        shell = shell.copy(setup_remote_docker = Wrapper(value))
    }

    override fun include(body: SetupRemoteDockerStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SetupRemoteDockerStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SetupRemoteDockerStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SetupRemoteDockerStepShellBuilder<C2> = SetupRemoteDockerStepShellBuilder(context, shell)

    private fun <C2> merge(other: SetupRemoteDockerStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SaveCacheStepShellBuilder<out C>(override val context: C, internal var shell: SaveCacheStepShell = SaveCacheStepShell()) : SaveCacheStepBuilder<C> {
    override fun save_cache(body: SaveCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(save_cache = SaveCacheScaffolder<C>(SaveCacheSpec<C>(body)).createScaffold(context))
    }

    override fun save_cache(spec: SaveCacheSpec<C>) {
        shell = shell.copy(save_cache = SaveCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun save_cache(ref: SaveCacheRef) {
        shell = shell.copy(save_cache = Deferred(ref.key))
    }

    override fun save_cache(value: SaveCache) {
        shell = shell.copy(save_cache = Wrapper(value))
    }

    override fun include(body: SaveCacheStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SaveCacheStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SaveCacheStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SaveCacheStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SaveCacheStepShellBuilder<C2> = SaveCacheStepShellBuilder(context, shell)

    private fun <C2> merge(other: SaveCacheStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RestoreCacheStepShellBuilder<out C>(override val context: C, internal var shell: RestoreCacheStepShell = RestoreCacheStepShell()) : RestoreCacheStepBuilder<C> {
    override fun restore_cache(body: RestoreCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder<C>(RestoreCacheSpec<C>(body)).createScaffold(context))
    }

    override fun restore_cache(spec: RestoreCacheSpec<C>) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun restore_cache(ref: RestoreCacheRef) {
        shell = shell.copy(restore_cache = Deferred(ref.key))
    }

    override fun restore_cache(value: RestoreCache) {
        shell = shell.copy(restore_cache = Wrapper(value))
    }

    override fun include(body: RestoreCacheStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RestoreCacheStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RestoreCacheStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RestoreCacheStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RestoreCacheStepShellBuilder<C2> = RestoreCacheStepShellBuilder(context, shell)

    private fun <C2> merge(other: RestoreCacheStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DeployStepShellBuilder<out C>(override val context: C, internal var shell: DeployStepShell = DeployStepShell()) : DeployStepBuilder<C> {
    override fun deploy(body: RunBuilder<C>.() -> Unit) {
        shell = shell.copy(deploy = RunScaffolder<C>(RunSpec<C>(body)).createScaffold(context))
    }

    override fun deploy(spec: RunSpec<C>) {
        shell = shell.copy(deploy = RunScaffolder<C>(spec).createScaffold(context))
    }

    override fun deploy(ref: RunRef) {
        shell = shell.copy(deploy = Deferred(ref.key))
    }

    override fun deploy(value: Run) {
        shell = shell.copy(deploy = Wrapper(value))
    }

    override fun include(body: DeployStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DeployStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DeployStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DeployStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DeployStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DeployStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DeployStepShellBuilder<C2> = DeployStepShellBuilder(context, shell)

    private fun <C2> merge(other: DeployStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StoreArtifactsStepShellBuilder<out C>(override val context: C, internal var shell: StoreArtifactsStepShell = StoreArtifactsStepShell()) : StoreArtifactsStepBuilder<C> {
    override fun store_artifacts(body: StoreArtifactsBuilder<C>.() -> Unit) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder<C>(StoreArtifactsSpec<C>(body)).createScaffold(context))
    }

    override fun store_artifacts(spec: StoreArtifactsSpec<C>) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder<C>(spec).createScaffold(context))
    }

    override fun store_artifacts(ref: StoreArtifactsRef) {
        shell = shell.copy(store_artifacts = Deferred(ref.key))
    }

    override fun store_artifacts(value: StoreArtifacts) {
        shell = shell.copy(store_artifacts = Wrapper(value))
    }

    override fun include(body: StoreArtifactsStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreArtifactsStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreArtifactsStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreArtifactsStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreArtifactsStepShellBuilder<C2> = StoreArtifactsStepShellBuilder(context, shell)

    private fun <C2> merge(other: StoreArtifactsStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StoreTestResultsStepShellBuilder<out C>(override val context: C, internal var shell: StoreTestResultsStepShell = StoreTestResultsStepShell()) : StoreTestResultsStepBuilder<C> {
    override fun store_test_results(body: StoreTestResultsBuilder<C>.() -> Unit) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder<C>(StoreTestResultsSpec<C>(body)).createScaffold(context))
    }

    override fun store_test_results(spec: StoreTestResultsSpec<C>) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder<C>(spec).createScaffold(context))
    }

    override fun store_test_results(ref: StoreTestResultsRef) {
        shell = shell.copy(store_test_results = Deferred(ref.key))
    }

    override fun store_test_results(value: StoreTestResults) {
        shell = shell.copy(store_test_results = Wrapper(value))
    }

    override fun include(body: StoreTestResultsStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreTestResultsStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreTestResultsStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreTestResultsStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreTestResultsStepShellBuilder<C2> = StoreTestResultsStepShellBuilder(context, shell)

    private fun <C2> merge(other: StoreTestResultsStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PersistToWorkspaceStepShellBuilder<out C>(override val context: C, internal var shell: PersistToWorkspaceStepShell = PersistToWorkspaceStepShell()) : PersistToWorkspaceStepBuilder<C> {
    override fun persist_to_workspace(body: PersistToWorkspaceBuilder<C>.() -> Unit) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder<C>(PersistToWorkspaceSpec<C>(body)).createScaffold(context))
    }

    override fun persist_to_workspace(spec: PersistToWorkspaceSpec<C>) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder<C>(spec).createScaffold(context))
    }

    override fun persist_to_workspace(ref: PersistToWorkspaceRef) {
        shell = shell.copy(persist_to_workspace = Deferred(ref.key))
    }

    override fun persist_to_workspace(value: PersistToWorkspace) {
        shell = shell.copy(persist_to_workspace = Wrapper(value))
    }

    override fun include(body: PersistToWorkspaceStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersistToWorkspaceStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersistToWorkspaceStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersistToWorkspaceStepShellBuilder<C2> = PersistToWorkspaceStepShellBuilder(context, shell)

    private fun <C2> merge(other: PersistToWorkspaceStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AttachWorkspaceStepShellBuilder<out C>(override val context: C, internal var shell: AttachWorkspaceStepShell = AttachWorkspaceStepShell()) : AttachWorkspaceStepBuilder<C> {
    override fun attach_workspace(body: AttachWorkspaceBuilder<C>.() -> Unit) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder<C>(AttachWorkspaceSpec<C>(body)).createScaffold(context))
    }

    override fun attach_workspace(spec: AttachWorkspaceSpec<C>) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder<C>(spec).createScaffold(context))
    }

    override fun attach_workspace(ref: AttachWorkspaceRef) {
        shell = shell.copy(attach_workspace = Deferred(ref.key))
    }

    override fun attach_workspace(value: AttachWorkspace) {
        shell = shell.copy(attach_workspace = Wrapper(value))
    }

    override fun include(body: AttachWorkspaceStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AttachWorkspaceStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AttachWorkspaceStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AttachWorkspaceStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttachWorkspaceStepShellBuilder<C2> = AttachWorkspaceStepShellBuilder(context, shell)

    private fun <C2> merge(other: AttachWorkspaceStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AddSshKeysStepShellBuilder<out C>(override val context: C, internal var shell: AddSshKeysStepShell = AddSshKeysStepShell()) : AddSshKeysStepBuilder<C> {
    override fun add_ssh_keys(body: AddSshKeysBuilder<C>.() -> Unit) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder<C>(AddSshKeysSpec<C>(body)).createScaffold(context))
    }

    override fun add_ssh_keys(spec: AddSshKeysSpec<C>) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder<C>(spec).createScaffold(context))
    }

    override fun add_ssh_keys(ref: AddSshKeysRef) {
        shell = shell.copy(add_ssh_keys = Deferred(ref.key))
    }

    override fun add_ssh_keys(value: AddSshKeys) {
        shell = shell.copy(add_ssh_keys = Wrapper(value))
    }

    override fun include(body: AddSshKeysStepBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AddSshKeysStepSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AddSshKeysStepBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AddSshKeysStepSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysStepBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysStepSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AddSshKeysStepShellBuilder<C2> = AddSshKeysStepShellBuilder(context, shell)

    private fun <C2> merge(other: AddSshKeysStepShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal sealed class StepShell

internal data class RunStepShell(var run: Scaffold<Run>? = null) : StepShell(), Scaffold<RunStep> {
    override suspend fun resolve(registry: Registry): RunStep {
        coroutineScope {
            run?.let{ launch { it.resolve(registry) } }
        }
        val value = RunStep(
            run?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class CheckoutStepShell(var checkout: Scaffold<Checkout>? = null) : StepShell(),
        Scaffold<CheckoutStep> {
    override suspend fun resolve(registry: Registry): CheckoutStep {
        coroutineScope {
            checkout?.let{ launch { it.resolve(registry) } }
        }
        val value = CheckoutStep(
            checkout?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SetupRemoteDockerStepShell(var setup_remote_docker: Scaffold<SetupRemoteDocker>? = null) : StepShell(),
        Scaffold<SetupRemoteDockerStep> {
    override suspend fun resolve(registry: Registry): SetupRemoteDockerStep {
        coroutineScope {
            setup_remote_docker?.let{ launch { it.resolve(registry) } }
        }
        val value = SetupRemoteDockerStep(
            setup_remote_docker?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SaveCacheStepShell(var save_cache: Scaffold<SaveCache>? = null) : StepShell(),
        Scaffold<SaveCacheStep> {
    override suspend fun resolve(registry: Registry): SaveCacheStep {
        coroutineScope {
            save_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = SaveCacheStep(
            save_cache?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class RestoreCacheStepShell(var restore_cache: Scaffold<RestoreCache>? = null) : StepShell(),
        Scaffold<RestoreCacheStep> {
    override suspend fun resolve(registry: Registry): RestoreCacheStep {
        coroutineScope {
            restore_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = RestoreCacheStep(
            restore_cache?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DeployStepShell(var deploy: Scaffold<Run>? = null) : StepShell(),
        Scaffold<DeployStep> {
    override suspend fun resolve(registry: Registry): DeployStep {
        coroutineScope {
            deploy?.let{ launch { it.resolve(registry) } }
        }
        val value = DeployStep(
            deploy?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreArtifactsStepShell(var store_artifacts: Scaffold<StoreArtifacts>? = null) : StepShell(),
        Scaffold<StoreArtifactsStep> {
    override suspend fun resolve(registry: Registry): StoreArtifactsStep {
        coroutineScope {
            store_artifacts?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreArtifactsStep(
            store_artifacts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreTestResultsStepShell(var store_test_results: Scaffold<StoreTestResults>? = null) : StepShell(),
        Scaffold<StoreTestResultsStep> {
    override suspend fun resolve(registry: Registry): StoreTestResultsStep {
        coroutineScope {
            store_test_results?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreTestResultsStep(
            store_test_results?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PersistToWorkspaceStepShell(var persist_to_workspace: Scaffold<PersistToWorkspace>? = null) : StepShell(),
        Scaffold<PersistToWorkspaceStep> {
    override suspend fun resolve(registry: Registry): PersistToWorkspaceStep {
        coroutineScope {
            persist_to_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = PersistToWorkspaceStep(
            persist_to_workspace?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AttachWorkspaceStepShell(var attach_workspace: Scaffold<AttachWorkspace>? = null) : StepShell(),
        Scaffold<AttachWorkspaceStep> {
    override suspend fun resolve(registry: Registry): AttachWorkspaceStep {
        coroutineScope {
            attach_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = AttachWorkspaceStep(
            attach_workspace?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AddSshKeysStepShell(var add_ssh_keys: Scaffold<AddSshKeys>? = null) : StepShell(),
        Scaffold<AddSshKeysStep> {
    override suspend fun resolve(registry: Registry): AddSshKeysStep {
        coroutineScope {
            add_ssh_keys?.let{ launch { it.resolve(registry) } }
        }
        val value = AddSshKeysStep(
            add_ssh_keys?.let{ it.resolve(registry) }
        )
        return value
    }
}

class RunScaffolder<in C>(internal val spec: RunSpec<C>) : Scaffolder<C, Run> {
    override fun createScaffold(context: C): Scaffold<Run> {
        val builder = RunShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RunShellBuilder<out C>(override val context: C, internal var shell: RunShell = RunShell()) : RunBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun command(value: String) {
        shell = shell.copy(command = Wrapper(value))
    }

    override fun shell(value: String) {
        shell = shell.copy(shell = Wrapper(value))
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

    override fun background(value: Boolean) {
        shell = shell.copy(background = Wrapper(value))
    }

    override fun working_directory(value: String) {
        shell = shell.copy(working_directory = Wrapper(value))
    }

    override fun no_output_timeout(value: String) {
        shell = shell.copy(no_output_timeout = Wrapper(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = Wrapper(value))
    }

    override fun include(body: RunBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RunSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RunBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RunSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RunBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RunSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RunShellBuilder<C2> = RunShellBuilder(context, shell)

    private fun <C2> merge(other: RunShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class RunShell(
        var name: Scaffold<String>? = null,
        var command: Scaffold<String>? = null,
        var shell: Scaffold<String>? = null,
        var environment: Map<Scaffold<String>, Scaffold<String>>? = null,
        var background: Scaffold<Boolean>? = null,
        var working_directory: Scaffold<String>? = null,
        var no_output_timeout: Scaffold<String>? = null,
        var `when`: Scaffold<When>? = null
) : Scaffold<Run> {
    override suspend fun resolve(registry: Registry): Run {
        val value = Run(
            name?.let{ it.resolve(registry) },
            command?.let{ it.resolve(registry) },
            shell?.let{ it.resolve(registry) },
            environment?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            background?.let{ it.resolve(registry) },
            working_directory?.let{ it.resolve(registry) },
            no_output_timeout?.let{ it.resolve(registry) },
            `when`?.let{ it.resolve(registry) }
        )
        return value
    }
}

class CheckoutScaffolder<in C>(internal val spec: CheckoutSpec<C>) : Scaffolder<C, Checkout> {
    override fun createScaffold(context: C): Scaffold<Checkout> {
        val builder = CheckoutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class CheckoutShellBuilder<out C>(override val context: C, internal var shell: CheckoutShell = CheckoutShell()) : CheckoutBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: CheckoutBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CheckoutSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CheckoutBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CheckoutSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CheckoutShellBuilder<C2> = CheckoutShellBuilder(context, shell)

    private fun <C2> merge(other: CheckoutShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class CheckoutShell(var path: Scaffold<String>? = null) : Scaffold<Checkout> {
    override suspend fun resolve(registry: Registry): Checkout {
        val value = Checkout(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

class SetupRemoteDockerScaffolder<in C>(internal val spec: SetupRemoteDockerSpec<C>) : Scaffolder<C, SetupRemoteDocker> {
    override fun createScaffold(context: C): Scaffold<SetupRemoteDocker> {
        val builder = SetupRemoteDockerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SetupRemoteDockerShellBuilder<out C>(override val context: C, internal var shell: SetupRemoteDockerShell = SetupRemoteDockerShell()) : SetupRemoteDockerBuilder<C> {
    override fun docker_layer_caching(value: Boolean) {
        shell = shell.copy(docker_layer_caching = Wrapper(value))
    }

    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun include(body: SetupRemoteDockerBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SetupRemoteDockerSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SetupRemoteDockerBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SetupRemoteDockerSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SetupRemoteDockerShellBuilder<C2> = SetupRemoteDockerShellBuilder(context, shell)

    private fun <C2> merge(other: SetupRemoteDockerShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class SetupRemoteDockerShell(var docker_layer_caching: Scaffold<Boolean>? = null, var version: Scaffold<String>? = null) : Scaffold<SetupRemoteDocker> {
    override suspend fun resolve(registry: Registry): SetupRemoteDocker {
        val value = SetupRemoteDocker(
            docker_layer_caching?.let{ it.resolve(registry) },
            version?.let{ it.resolve(registry) }
        )
        return value
    }
}

class SaveCacheScaffolder<in C>(internal val spec: SaveCacheSpec<C>) : Scaffolder<C, SaveCache> {
    override fun createScaffold(context: C): Scaffold<SaveCache> {
        val builder = SaveCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SaveCacheShellBuilder<out C>(override val context: C, internal var shell: SaveCacheShell = SaveCacheShell()) : SaveCacheBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + Wrapper(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { Wrapper(it) })
    }

    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun include(body: SaveCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SaveCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SaveCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SaveCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SaveCacheShellBuilder<C2> = SaveCacheShellBuilder(context, shell)

    private fun <C2> merge(other: SaveCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class SaveCacheShell(
        var paths: List<Scaffold<String>>? = null,
        var key: Scaffold<String>? = null,
        var `when`: Scaffold<When>? = null,
        var name: Scaffold<String>? = null
) : Scaffold<SaveCache> {
    override suspend fun resolve(registry: Registry): SaveCache {
        val value = SaveCache(
            paths?.let{ it.map { it.resolve(registry) } },
            key?.let{ it.resolve(registry) },
            `when`?.let{ it.resolve(registry) },
            name?.let{ it.resolve(registry) }
        )
        return value
    }
}

class RestoreCacheScaffolder<in C>(internal val spec: RestoreCacheSpec<C>) : Scaffolder<C, RestoreCache> {
    override fun createScaffold(context: C): Scaffold<RestoreCache> {
        val builder = RestoreCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RestoreCacheShellBuilder<out C>(override val context: C, internal var shell: RestoreCacheShell = RestoreCacheShell()) : RestoreCacheBuilder<C> {
    override fun key(value: String) {
        shell = shell.copy(keys = shell.keys.orEmpty() + Wrapper(value))
    }

    override fun keys(keys: List<String>) {
        shell = shell.copy(keys = shell.keys.orEmpty() + keys.map { Wrapper(it) })
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun include(body: RestoreCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RestoreCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RestoreCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RestoreCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RestoreCacheShellBuilder<C2> = RestoreCacheShellBuilder(context, shell)

    private fun <C2> merge(other: RestoreCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class RestoreCacheShell(var keys: List<Scaffold<String>>? = null, var name: Scaffold<String>? = null) : Scaffold<RestoreCache> {
    override suspend fun resolve(registry: Registry): RestoreCache {
        val value = RestoreCache(
            keys?.let{ it.map { it.resolve(registry) } },
            name?.let{ it.resolve(registry) }
        )
        return value
    }
}

class StoreArtifactsScaffolder<in C>(internal val spec: StoreArtifactsSpec<C>) : Scaffolder<C, StoreArtifacts> {
    override fun createScaffold(context: C): Scaffold<StoreArtifacts> {
        val builder = StoreArtifactsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class StoreArtifactsShellBuilder<out C>(override val context: C, internal var shell: StoreArtifactsShell = StoreArtifactsShell()) : StoreArtifactsBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun destination(value: String) {
        shell = shell.copy(destination = Wrapper(value))
    }

    override fun include(body: StoreArtifactsBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreArtifactsSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreArtifactsBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreArtifactsSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreArtifactsShellBuilder<C2> = StoreArtifactsShellBuilder(context, shell)

    private fun <C2> merge(other: StoreArtifactsShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class StoreArtifactsShell(var path: Scaffold<String>? = null, var destination: Scaffold<String>? = null) : Scaffold<StoreArtifacts> {
    override suspend fun resolve(registry: Registry): StoreArtifacts {
        val value = StoreArtifacts(
            path?.let{ it.resolve(registry) },
            destination?.let{ it.resolve(registry) }
        )
        return value
    }
}

class StoreTestResultsScaffolder<in C>(internal val spec: StoreTestResultsSpec<C>) : Scaffolder<C, StoreTestResults> {
    override fun createScaffold(context: C): Scaffold<StoreTestResults> {
        val builder = StoreTestResultsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class StoreTestResultsShellBuilder<out C>(override val context: C, internal var shell: StoreTestResultsShell = StoreTestResultsShell()) : StoreTestResultsBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: StoreTestResultsBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreTestResultsSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreTestResultsBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreTestResultsSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreTestResultsShellBuilder<C2> = StoreTestResultsShellBuilder(context, shell)

    private fun <C2> merge(other: StoreTestResultsShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class StoreTestResultsShell(var path: Scaffold<String>? = null) : Scaffold<StoreTestResults> {
    override suspend fun resolve(registry: Registry): StoreTestResults {
        val value = StoreTestResults(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

class PersistToWorkspaceScaffolder<in C>(internal val spec: PersistToWorkspaceSpec<C>) : Scaffolder<C, PersistToWorkspace> {
    override fun createScaffold(context: C): Scaffold<PersistToWorkspace> {
        val builder = PersistToWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class PersistToWorkspaceShellBuilder<out C>(override val context: C, internal var shell: PersistToWorkspaceShell = PersistToWorkspaceShell()) : PersistToWorkspaceBuilder<C> {
    override fun root(value: String) {
        shell = shell.copy(root = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + Wrapper(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { Wrapper(it) })
    }

    override fun include(body: PersistToWorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersistToWorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersistToWorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersistToWorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersistToWorkspaceShellBuilder<C2> = PersistToWorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: PersistToWorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class PersistToWorkspaceShell(var root: Scaffold<String>? = null, var paths: List<Scaffold<String>>? = null) : Scaffold<PersistToWorkspace> {
    override suspend fun resolve(registry: Registry): PersistToWorkspace {
        val value = PersistToWorkspace(
            root?.let{ it.resolve(registry) },
            paths?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class AttachWorkspaceScaffolder<in C>(internal val spec: AttachWorkspaceSpec<C>) : Scaffolder<C, AttachWorkspace> {
    override fun createScaffold(context: C): Scaffold<AttachWorkspace> {
        val builder = AttachWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AttachWorkspaceShellBuilder<out C>(override val context: C, internal var shell: AttachWorkspaceShell = AttachWorkspaceShell()) : AttachWorkspaceBuilder<C> {
    override fun at(value: String) {
        shell = shell.copy(at = Wrapper(value))
    }

    override fun include(body: AttachWorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AttachWorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AttachWorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AttachWorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttachWorkspaceShellBuilder<C2> = AttachWorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: AttachWorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class AttachWorkspaceShell(var at: Scaffold<String>? = null) : Scaffold<AttachWorkspace> {
    override suspend fun resolve(registry: Registry): AttachWorkspace {
        val value = AttachWorkspace(
            at?.let{ it.resolve(registry) }
        )
        return value
    }
}

class AddSshKeysScaffolder<in C>(internal val spec: AddSshKeysSpec<C>) : Scaffolder<C, AddSshKeys> {
    override fun createScaffold(context: C): Scaffold<AddSshKeys> {
        val builder = AddSshKeysShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AddSshKeysShellBuilder<out C>(override val context: C, internal var shell: AddSshKeysShell = AddSshKeysShell()) : AddSshKeysBuilder<C> {
    override fun fingerprint(value: String) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + Wrapper(value))
    }

    override fun fingerprints(fingerprints: List<String>) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + fingerprints.map { Wrapper(it) })
    }

    override fun include(body: AddSshKeysBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AddSshKeysSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AddSshKeysBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AddSshKeysSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AddSshKeysShellBuilder<C2> = AddSshKeysShellBuilder(context, shell)

    private fun <C2> merge(other: AddSshKeysShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class AddSshKeysShell(var fingerprints: List<Scaffold<String>>? = null) : Scaffold<AddSshKeys> {
    override suspend fun resolve(registry: Registry): AddSshKeys {
        val value = AddSshKeys(
            fingerprints?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class WorkflowScaffolder<in C>(internal val spec: WorkflowSpec<C>) : Scaffolder<C, Workflow> {
    override fun createScaffold(context: C): Scaffold<Workflow> {
        val builder = WorkflowShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowShellBuilder<out C>(override val context: C, internal var shell: WorkflowShell = WorkflowShell()) : WorkflowBuilder<C> {
    override fun trigger(body: WorkflowTriggerBuilder<C>.() -> Unit) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder<C>(WorkflowTriggerSpec<C>(body)).createScaffold(context))
    }

    override fun trigger(spec: WorkflowTriggerSpec<C>) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder<C>(spec).createScaffold(context))
    }

    override fun trigger(ref: WorkflowTriggerRef) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + Deferred(ref.key))
    }

    override fun trigger(value: WorkflowTrigger) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + Wrapper(value))
    }

    override fun triggers(triggers: List<WorkflowTrigger>) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + triggers.map { Wrapper(it) })
    }

    override fun jobs(key: String, body: WorkflowJobBuilder<C>.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),WorkflowJobScaffolder<C>(WorkflowJobSpec<C>(body)).createScaffold(context))))
    }

    override fun jobs(key: String, spec: WorkflowJobSpec<C>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),WorkflowJobScaffolder<C>(spec).createScaffold(context))))
    }

    override fun jobs(key: String, ref: WorkflowJobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),Deferred(ref.key))))
    }

    override fun jobs(key: String, value: WorkflowJob) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(Wrapper(key),Wrapper(value))))
    }

    override fun include(body: WorkflowBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowShellBuilder<C2> = WorkflowShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowShell(var triggers: List<Scaffold<WorkflowTrigger>>? = null, var jobs: List<Map<Scaffold<String>, Scaffold<WorkflowJob>>>? = null) : Scaffold<Workflow> {
    override suspend fun resolve(registry: Registry): Workflow {
        coroutineScope {
            triggers?.let{ it.forEach { launch { it.resolve(registry) } } }
            jobs?.let{ it.forEach { it.forEach { it.value.let { launch { it.resolve(registry) } } } } }
        }
        val value = Workflow(
            triggers?.let{ it.map { it.resolve(registry) } },
            jobs?.let{ it.map { it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() } }
        )
        return value
    }
}

class WorkflowTriggerScaffolder<in C>(internal val spec: WorkflowTriggerSpec<C>) : Scaffolder<C, WorkflowTrigger> {
    override fun createScaffold(context: C): Scaffold<WorkflowTrigger> {
        val builder = WorkflowTriggerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerShell = WorkflowTriggerShell()) : WorkflowTriggerBuilder<C> {
    override fun schedule(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder<C>(WorkflowTriggerScheduleSpec<C>(body)).createScaffold(context))
    }

    override fun schedule(spec: WorkflowTriggerScheduleSpec<C>) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder<C>(spec).createScaffold(context))
    }

    override fun schedule(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(schedule = Deferred(ref.key))
    }

    override fun schedule(value: WorkflowTriggerSchedule) {
        shell = shell.copy(schedule = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerShellBuilder<C2> = WorkflowTriggerShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowTriggerShell(var schedule: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTrigger> {
    override suspend fun resolve(registry: Registry): WorkflowTrigger {
        coroutineScope {
            schedule?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTrigger(
            schedule?.let{ it.resolve(registry) }
        )
        return value
    }
}

class WorkflowTriggerScheduleScaffolder<in C>(internal val spec: WorkflowTriggerScheduleSpec<C>) : Scaffolder<C, WorkflowTriggerSchedule> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerSchedule> {
        val builder = WorkflowTriggerScheduleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerScheduleShell = WorkflowTriggerScheduleShell()) : WorkflowTriggerScheduleBuilder<C> {
    override fun cron(value: String) {
        shell = shell.copy(cron = Wrapper(value))
    }

    override fun filters(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder<C>(WorkflowTriggerScheduleSpec<C>(body)).createScaffold(context))
    }

    override fun filters(spec: WorkflowTriggerScheduleSpec<C>) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder<C>(spec).createScaffold(context))
    }

    override fun filters(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(filters = Deferred(ref.key))
    }

    override fun filters(value: WorkflowTriggerSchedule) {
        shell = shell.copy(filters = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerScheduleBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerScheduleSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerScheduleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerScheduleShellBuilder<C2> = WorkflowTriggerScheduleShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerScheduleShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowTriggerScheduleShell(var cron: Scaffold<String>? = null, var filters: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTriggerSchedule> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerSchedule {
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerSchedule(
            cron?.let{ it.resolve(registry) },
            filters?.let{ it.resolve(registry) }
        )
        return value
    }
}

class WorkflowTriggerScheduleFiltersScaffolder<in C>(internal val spec: WorkflowTriggerScheduleFiltersSpec<C>) : Scaffolder<C, WorkflowTriggerScheduleFilters> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerScheduleFilters> {
        val builder = WorkflowTriggerScheduleFiltersShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleFiltersShellBuilder<out C>(override val context: C, internal var shell: WorkflowTriggerScheduleFiltersShell = WorkflowTriggerScheduleFiltersShell()) : WorkflowTriggerScheduleFiltersBuilder<C> {
    override fun branches(body: FilterBuilder<C>.() -> Unit) {
        shell = shell.copy(branches = FilterScaffolder<C>(FilterSpec<C>(body)).createScaffold(context))
    }

    override fun branches(spec: FilterSpec<C>) {
        shell = shell.copy(branches = FilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun branches(ref: FilterRef) {
        shell = shell.copy(branches = Deferred(ref.key))
    }

    override fun branches(value: Filter) {
        shell = shell.copy(branches = Wrapper(value))
    }

    override fun include(body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowTriggerScheduleFiltersSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowTriggerScheduleFiltersSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowTriggerScheduleFiltersBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowTriggerScheduleFiltersSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowTriggerScheduleFiltersShellBuilder<C2> = WorkflowTriggerScheduleFiltersShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowTriggerScheduleFiltersShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowTriggerScheduleFiltersShell(var branches: Scaffold<Filter>? = null) : Scaffold<WorkflowTriggerScheduleFilters> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerScheduleFilters {
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerScheduleFilters(
            branches?.let{ it.resolve(registry) }
        )
        return value
    }
}

class WorkflowJobScaffolder<in C>(internal val spec: WorkflowJobSpec<C>) : Scaffolder<C, WorkflowJob> {
    override fun createScaffold(context: C): Scaffold<WorkflowJob> {
        val builder = WorkflowJobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowJobShellBuilder<out C>(override val context: C, internal var shell: WorkflowJobShell = WorkflowJobShell()) : WorkflowJobBuilder<C> {
    override fun require(value: String) {
        shell = shell.copy(requires = shell.requires.orEmpty() + Wrapper(value))
    }

    override fun requires(requires: List<String>) {
        shell = shell.copy(requires = shell.requires.orEmpty() + requires.map { Wrapper(it) })
    }

    override fun context(value: String) {
        shell = shell.copy(context = Wrapper(value))
    }

    override fun type(value: WorkflowJobType) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun filters(body: WorkflowJobFilterBuilder<C>.() -> Unit) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder<C>(WorkflowJobFilterSpec<C>(body)).createScaffold(context))
    }

    override fun filters(spec: WorkflowJobFilterSpec<C>) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun filters(ref: WorkflowJobFilterRef) {
        shell = shell.copy(filters = Deferred(ref.key))
    }

    override fun filters(value: WorkflowJobFilter) {
        shell = shell.copy(filters = Wrapper(value))
    }

    override fun include(body: WorkflowJobBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowJobSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowJobBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowJobSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowJobShellBuilder<C2> = WorkflowJobShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowJobShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowJobShell(
        var requires: List<Scaffold<String>>? = null,
        var context: Scaffold<String>? = null,
        var type: Scaffold<WorkflowJobType>? = null,
        var filters: Scaffold<WorkflowJobFilter>? = null
) : Scaffold<WorkflowJob> {
    override suspend fun resolve(registry: Registry): WorkflowJob {
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJob(
            requires?.let{ it.map { it.resolve(registry) } },
            context?.let{ it.resolve(registry) },
            type?.let{ it.resolve(registry) },
            filters?.let{ it.resolve(registry) }
        )
        return value
    }
}

class WorkflowJobFilterScaffolder<in C>(internal val spec: WorkflowJobFilterSpec<C>) : Scaffolder<C, WorkflowJobFilter> {
    override fun createScaffold(context: C): Scaffold<WorkflowJobFilter> {
        val builder = WorkflowJobFilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowJobFilterShellBuilder<out C>(override val context: C, internal var shell: WorkflowJobFilterShell = WorkflowJobFilterShell()) : WorkflowJobFilterBuilder<C> {
    override fun branches(body: FilterBuilder<C>.() -> Unit) {
        shell = shell.copy(branches = FilterScaffolder<C>(FilterSpec<C>(body)).createScaffold(context))
    }

    override fun branches(spec: FilterSpec<C>) {
        shell = shell.copy(branches = FilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun branches(ref: FilterRef) {
        shell = shell.copy(branches = Deferred(ref.key))
    }

    override fun branches(value: Filter) {
        shell = shell.copy(branches = Wrapper(value))
    }

    override fun tags(body: FilterBuilder<C>.() -> Unit) {
        shell = shell.copy(tags = FilterScaffolder<C>(FilterSpec<C>(body)).createScaffold(context))
    }

    override fun tags(spec: FilterSpec<C>) {
        shell = shell.copy(tags = FilterScaffolder<C>(spec).createScaffold(context))
    }

    override fun tags(ref: FilterRef) {
        shell = shell.copy(tags = Deferred(ref.key))
    }

    override fun tags(value: Filter) {
        shell = shell.copy(tags = Wrapper(value))
    }

    override fun include(body: WorkflowJobFilterBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkflowJobFilterSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkflowJobFilterBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkflowJobFilterSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkflowJobFilterBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkflowJobFilterSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkflowJobFilterShellBuilder<C2> = WorkflowJobFilterShellBuilder(context, shell)

    private fun <C2> merge(other: WorkflowJobFilterShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class WorkflowJobFilterShell(var branches: Scaffold<Filter>? = null, var tags: Scaffold<Filter>? = null) : Scaffold<WorkflowJobFilter> {
    override suspend fun resolve(registry: Registry): WorkflowJobFilter {
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
            tags?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJobFilter(
            branches?.let{ it.resolve(registry) },
            tags?.let{ it.resolve(registry) }
        )
        return value
    }
}

class FilterScaffolder<in C>(internal val spec: FilterSpec<C>) : Scaffolder<C, Filter> {
    override fun createScaffold(context: C): Scaffold<Filter> {
        val builder = FilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class FilterShellBuilder<out C>(override val context: C, internal var shell: FilterShell = FilterShell()) : FilterBuilder<C> {
    override fun only(value: String) {
        shell = shell.copy(only = shell.only.orEmpty() + Wrapper(value))
    }

    override fun only(only: List<String>) {
        shell = shell.copy(only = shell.only.orEmpty() + only.map { Wrapper(it) })
    }

    override fun ignore(value: String) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + Wrapper(value))
    }

    override fun ignore(ignore: List<String>) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + ignore.map { Wrapper(it) })
    }

    override fun include(body: FilterBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FilterSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FilterBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FilterSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FilterBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FilterSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FilterShellBuilder<C2> = FilterShellBuilder(context, shell)

    private fun <C2> merge(other: FilterShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class FilterShell(var only: List<Scaffold<String>>? = null, var ignore: List<Scaffold<String>>? = null) : Scaffold<Filter> {
    override suspend fun resolve(registry: Registry): Filter {
        val value = Filter(
            only?.let{ it.map { it.resolve(registry) } },
            ignore?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}
