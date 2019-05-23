// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.circleci

import io.philarios.core.RefScaffold
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CircleCIScaffolder(internal val spec: CircleCISpec) : Scaffolder<CircleCI> {
    override fun createScaffold(): Scaffold<CircleCI> {
        val builder = CircleCIShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class CircleCIShellBuilder(internal var shell: CircleCIShell = CircleCIShell()) : CircleCIBuilder {
    override fun version(value: String) {
        shell = shell.copy(version = ValueScaffold(value))
    }

    override fun jobs(key: String, body: JobBuilder.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(ValueScaffold(key),JobScaffolder(JobSpec(body)).createScaffold()))
    }

    override fun jobs(key: String, spec: JobSpec) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(ValueScaffold(key),JobScaffolder(spec).createScaffold()))
    }

    override fun jobs(key: String, ref: JobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(ValueScaffold(key),RefScaffold(ref.key)))
    }

    override fun jobs(key: String, value: Job) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun workflows(key: String, body: WorkflowBuilder.() -> Unit) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(ValueScaffold(key),WorkflowScaffolder(WorkflowSpec(body)).createScaffold()))
    }

    override fun workflows(key: String, spec: WorkflowSpec) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(ValueScaffold(key),WorkflowScaffolder(spec).createScaffold()))
    }

    override fun workflows(key: String, ref: WorkflowRef) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(ValueScaffold(key),RefScaffold(ref.key)))
    }

    override fun workflows(key: String, value: Workflow) {
        shell = shell.copy(workflows = shell.workflows.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
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

class JobScaffolder(internal val spec: JobSpec) : Scaffolder<Job> {
    override fun createScaffold(): Scaffold<Job> {
        val builder = JobShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class JobShellBuilder(internal var shell: JobShell = JobShell()) : JobBuilder {
    override fun docker(body: DockerExecutorBuilder.() -> Unit) {
        shell = shell.copy(docker = shell.docker.orEmpty() + DockerExecutorScaffolder(DockerExecutorSpec(body)).createScaffold())
    }

    override fun docker(spec: DockerExecutorSpec) {
        shell = shell.copy(docker = shell.docker.orEmpty() + DockerExecutorScaffolder(spec).createScaffold())
    }

    override fun docker(ref: DockerExecutorRef) {
        shell = shell.copy(docker = shell.docker.orEmpty() + RefScaffold(ref.key))
    }

    override fun docker(value: DockerExecutor) {
        shell = shell.copy(docker = shell.docker.orEmpty() + ValueScaffold(value))
    }

    override fun docker(docker: List<DockerExecutor>) {
        shell = shell.copy(docker = shell.docker.orEmpty() + docker.map { ValueScaffold(it) })
    }

    override fun resource_class(value: ResourceClass) {
        shell = shell.copy(resource_class = ValueScaffold(value))
    }

    override fun machine(body: MachineExecutorBuilder.() -> Unit) {
        shell = shell.copy(machine = MachineExecutorScaffolder(MachineExecutorSpec(body)).createScaffold())
    }

    override fun machine(spec: MachineExecutorSpec) {
        shell = shell.copy(machine = MachineExecutorScaffolder(spec).createScaffold())
    }

    override fun machine(ref: MachineExecutorRef) {
        shell = shell.copy(machine = RefScaffold(ref.key))
    }

    override fun machine(value: MachineExecutor) {
        shell = shell.copy(machine = ValueScaffold(value))
    }

    override fun macos(body: MacosExecutorBuilder.() -> Unit) {
        shell = shell.copy(macos = MacosExecutorScaffolder(MacosExecutorSpec(body)).createScaffold())
    }

    override fun macos(spec: MacosExecutorSpec) {
        shell = shell.copy(macos = MacosExecutorScaffolder(spec).createScaffold())
    }

    override fun macos(ref: MacosExecutorRef) {
        shell = shell.copy(macos = RefScaffold(ref.key))
    }

    override fun macos(value: MacosExecutor) {
        shell = shell.copy(macos = ValueScaffold(value))
    }

    override fun shell(value: String) {
        shell = shell.copy(shell = ValueScaffold(value))
    }

    override fun <T : Step> step(spec: StepSpec<T>) {
        shell = shell.copy(steps = shell.steps.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> step(ref: StepRef<T>) {
        shell = shell.copy(steps = shell.steps.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Step> step(value: T) {
        shell = shell.copy(steps = shell.steps.orEmpty() + ValueScaffold(value))
    }

    override fun working_directory(value: String) {
        shell = shell.copy(working_directory = ValueScaffold(value))
    }

    override fun parallelism(value: Int) {
        shell = shell.copy(parallelism = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun branches(key: String, value: String) {
        shell = shell.copy(branches = shell.branches.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun branches(pair: Pair<String, String>) {
        shell = shell.copy(branches = shell.branches.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun branches(branches: Map<String, String>) {
        shell = shell.copy(branches = shell.branches.orEmpty() + branches.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
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

class DockerExecutorScaffolder(internal val spec: DockerExecutorSpec) : Scaffolder<DockerExecutor> {
    override fun createScaffold(): Scaffold<DockerExecutor> {
        val builder = DockerExecutorShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DockerExecutorShellBuilder(internal var shell: DockerExecutorShell = DockerExecutorShell()) : DockerExecutorBuilder {
    override fun image(value: String) {
        shell = shell.copy(image = ValueScaffold(value))
    }

    override fun entrypoint(value: String) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + ValueScaffold(value))
    }

    override fun entrypoint(entrypoint: List<String>) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + entrypoint.map { ValueScaffold(it) })
    }

    override fun command(value: String) {
        shell = shell.copy(command = shell.command.orEmpty() + ValueScaffold(value))
    }

    override fun command(command: List<String>) {
        shell = shell.copy(command = shell.command.orEmpty() + command.map { ValueScaffold(it) })
    }

    override fun user(value: String) {
        shell = shell.copy(user = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun auth(body: AuthBuilder.() -> Unit) {
        shell = shell.copy(auth = AuthScaffolder(AuthSpec(body)).createScaffold())
    }

    override fun auth(spec: AuthSpec) {
        shell = shell.copy(auth = AuthScaffolder(spec).createScaffold())
    }

    override fun auth(ref: AuthRef) {
        shell = shell.copy(auth = RefScaffold(ref.key))
    }

    override fun auth(value: Auth) {
        shell = shell.copy(auth = ValueScaffold(value))
    }

    override fun aws_auth(body: AwsAuthBuilder.() -> Unit) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder(AwsAuthSpec(body)).createScaffold())
    }

    override fun aws_auth(spec: AwsAuthSpec) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder(spec).createScaffold())
    }

    override fun aws_auth(ref: AwsAuthRef) {
        shell = shell.copy(aws_auth = RefScaffold(ref.key))
    }

    override fun aws_auth(value: AwsAuth) {
        shell = shell.copy(aws_auth = ValueScaffold(value))
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

class AuthScaffolder(internal val spec: AuthSpec) : Scaffolder<Auth> {
    override fun createScaffold(): Scaffold<Auth> {
        val builder = AuthShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AuthShellBuilder(internal var shell: AuthShell = AuthShell()) : AuthBuilder {
    override fun username(value: String) {
        shell = shell.copy(username = ValueScaffold(value))
    }

    override fun password(value: String) {
        shell = shell.copy(password = ValueScaffold(value))
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

class AwsAuthScaffolder(internal val spec: AwsAuthSpec) : Scaffolder<AwsAuth> {
    override fun createScaffold(): Scaffold<AwsAuth> {
        val builder = AwsAuthShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AwsAuthShellBuilder(internal var shell: AwsAuthShell = AwsAuthShell()) : AwsAuthBuilder {
    override fun aws_access_key_id(value: String) {
        shell = shell.copy(aws_access_key_id = ValueScaffold(value))
    }

    override fun aws_secret_access_key(value: String) {
        shell = shell.copy(aws_secret_access_key = ValueScaffold(value))
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

class MachineExecutorScaffolder(internal val spec: MachineExecutorSpec) : Scaffolder<MachineExecutor> {
    override fun createScaffold(): Scaffold<MachineExecutor> {
        val builder = MachineExecutorShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class MachineExecutorShellBuilder(internal var shell: MachineExecutorShell = MachineExecutorShell()) : MachineExecutorBuilder {
    override fun enabled(value: Boolean) {
        shell = shell.copy(enabled = ValueScaffold(value))
    }

    override fun image(value: String) {
        shell = shell.copy(image = ValueScaffold(value))
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

class MacosExecutorScaffolder(internal val spec: MacosExecutorSpec) : Scaffolder<MacosExecutor> {
    override fun createScaffold(): Scaffold<MacosExecutor> {
        val builder = MacosExecutorShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class MacosExecutorShellBuilder(internal var shell: MacosExecutorShell = MacosExecutorShell()) : MacosExecutorBuilder {
    override fun xcode(value: String) {
        shell = shell.copy(xcode = ValueScaffold(value))
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

class StepScaffolder<out T : Step>(internal val spec: StepSpec<T>) : Scaffolder<T> {
    override fun createScaffold(): Scaffold<T> {
        val result = when (spec) {
            is RunStepSpec -> RunStepScaffolder(spec).createScaffold()
            is CheckoutStepSpec -> CheckoutStepScaffolder(spec).createScaffold()
            is SetupRemoteDockerStepSpec -> SetupRemoteDockerStepScaffolder(spec).createScaffold()
            is SaveCacheStepSpec -> SaveCacheStepScaffolder(spec).createScaffold()
            is RestoreCacheStepSpec -> RestoreCacheStepScaffolder(spec).createScaffold()
            is DeployStepSpec -> DeployStepScaffolder(spec).createScaffold()
            is StoreArtifactsStepSpec -> StoreArtifactsStepScaffolder(spec).createScaffold()
            is StoreTestResultsStepSpec -> StoreTestResultsStepScaffolder(spec).createScaffold()
            is PersistToWorkspaceStepSpec -> PersistToWorkspaceStepScaffolder(spec).createScaffold()
            is AttachWorkspaceStepSpec -> AttachWorkspaceStepScaffolder(spec).createScaffold()
            is AddSshKeysStepSpec -> AddSshKeysStepScaffolder(spec).createScaffold()
        }
        return result as Scaffold<T>
    }
}

class RunStepScaffolder(internal val spec: RunStepSpec) : Scaffolder<RunStep> {
    override fun createScaffold(): Scaffold<RunStep> {
        val builder = RunStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class CheckoutStepScaffolder(internal val spec: CheckoutStepSpec) : Scaffolder<CheckoutStep> {
    override fun createScaffold(): Scaffold<CheckoutStep> {
        val builder = CheckoutStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class SetupRemoteDockerStepScaffolder(internal val spec: SetupRemoteDockerStepSpec) : Scaffolder<SetupRemoteDockerStep> {
    override fun createScaffold(): Scaffold<SetupRemoteDockerStep> {
        val builder = SetupRemoteDockerStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class SaveCacheStepScaffolder(internal val spec: SaveCacheStepSpec) : Scaffolder<SaveCacheStep> {
    override fun createScaffold(): Scaffold<SaveCacheStep> {
        val builder = SaveCacheStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class RestoreCacheStepScaffolder(internal val spec: RestoreCacheStepSpec) : Scaffolder<RestoreCacheStep> {
    override fun createScaffold(): Scaffold<RestoreCacheStep> {
        val builder = RestoreCacheStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class DeployStepScaffolder(internal val spec: DeployStepSpec) : Scaffolder<DeployStep> {
    override fun createScaffold(): Scaffold<DeployStep> {
        val builder = DeployStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreArtifactsStepScaffolder(internal val spec: StoreArtifactsStepSpec) : Scaffolder<StoreArtifactsStep> {
    override fun createScaffold(): Scaffold<StoreArtifactsStep> {
        val builder = StoreArtifactsStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreTestResultsStepScaffolder(internal val spec: StoreTestResultsStepSpec) : Scaffolder<StoreTestResultsStep> {
    override fun createScaffold(): Scaffold<StoreTestResultsStep> {
        val builder = StoreTestResultsStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class PersistToWorkspaceStepScaffolder(internal val spec: PersistToWorkspaceStepSpec) : Scaffolder<PersistToWorkspaceStep> {
    override fun createScaffold(): Scaffold<PersistToWorkspaceStep> {
        val builder = PersistToWorkspaceStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class AttachWorkspaceStepScaffolder(internal val spec: AttachWorkspaceStepSpec) : Scaffolder<AttachWorkspaceStep> {
    override fun createScaffold(): Scaffold<AttachWorkspaceStep> {
        val builder = AttachWorkspaceStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class AddSshKeysStepScaffolder(internal val spec: AddSshKeysStepSpec) : Scaffolder<AddSshKeysStep> {
    override fun createScaffold(): Scaffold<AddSshKeysStep> {
        val builder = AddSshKeysStepShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RunStepShellBuilder(internal var shell: RunStepShell = RunStepShell()) : RunStepBuilder {
    override fun run(body: RunBuilder.() -> Unit) {
        shell = shell.copy(run = RunScaffolder(RunSpec(body)).createScaffold())
    }

    override fun run(spec: RunSpec) {
        shell = shell.copy(run = RunScaffolder(spec).createScaffold())
    }

    override fun run(ref: RunRef) {
        shell = shell.copy(run = RefScaffold(ref.key))
    }

    override fun run(value: Run) {
        shell = shell.copy(run = ValueScaffold(value))
    }
}

@DslBuilder
internal class CheckoutStepShellBuilder(internal var shell: CheckoutStepShell = CheckoutStepShell()) : CheckoutStepBuilder {
    override fun checkout(body: CheckoutBuilder.() -> Unit) {
        shell = shell.copy(checkout = CheckoutScaffolder(CheckoutSpec(body)).createScaffold())
    }

    override fun checkout(spec: CheckoutSpec) {
        shell = shell.copy(checkout = CheckoutScaffolder(spec).createScaffold())
    }

    override fun checkout(ref: CheckoutRef) {
        shell = shell.copy(checkout = RefScaffold(ref.key))
    }

    override fun checkout(value: Checkout) {
        shell = shell.copy(checkout = ValueScaffold(value))
    }
}

@DslBuilder
internal class SetupRemoteDockerStepShellBuilder(internal var shell: SetupRemoteDockerStepShell = SetupRemoteDockerStepShell()) : SetupRemoteDockerStepBuilder {
    override fun setup_remote_docker(body: SetupRemoteDockerBuilder.() -> Unit) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder(SetupRemoteDockerSpec(body)).createScaffold())
    }

    override fun setup_remote_docker(spec: SetupRemoteDockerSpec) {
        shell = shell.copy(setup_remote_docker = SetupRemoteDockerScaffolder(spec).createScaffold())
    }

    override fun setup_remote_docker(ref: SetupRemoteDockerRef) {
        shell = shell.copy(setup_remote_docker = RefScaffold(ref.key))
    }

    override fun setup_remote_docker(value: SetupRemoteDocker) {
        shell = shell.copy(setup_remote_docker = ValueScaffold(value))
    }
}

@DslBuilder
internal class SaveCacheStepShellBuilder(internal var shell: SaveCacheStepShell = SaveCacheStepShell()) : SaveCacheStepBuilder {
    override fun save_cache(body: SaveCacheBuilder.() -> Unit) {
        shell = shell.copy(save_cache = SaveCacheScaffolder(SaveCacheSpec(body)).createScaffold())
    }

    override fun save_cache(spec: SaveCacheSpec) {
        shell = shell.copy(save_cache = SaveCacheScaffolder(spec).createScaffold())
    }

    override fun save_cache(ref: SaveCacheRef) {
        shell = shell.copy(save_cache = RefScaffold(ref.key))
    }

    override fun save_cache(value: SaveCache) {
        shell = shell.copy(save_cache = ValueScaffold(value))
    }
}

@DslBuilder
internal class RestoreCacheStepShellBuilder(internal var shell: RestoreCacheStepShell = RestoreCacheStepShell()) : RestoreCacheStepBuilder {
    override fun restore_cache(body: RestoreCacheBuilder.() -> Unit) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder(RestoreCacheSpec(body)).createScaffold())
    }

    override fun restore_cache(spec: RestoreCacheSpec) {
        shell = shell.copy(restore_cache = RestoreCacheScaffolder(spec).createScaffold())
    }

    override fun restore_cache(ref: RestoreCacheRef) {
        shell = shell.copy(restore_cache = RefScaffold(ref.key))
    }

    override fun restore_cache(value: RestoreCache) {
        shell = shell.copy(restore_cache = ValueScaffold(value))
    }
}

@DslBuilder
internal class DeployStepShellBuilder(internal var shell: DeployStepShell = DeployStepShell()) : DeployStepBuilder {
    override fun deploy(body: RunBuilder.() -> Unit) {
        shell = shell.copy(deploy = RunScaffolder(RunSpec(body)).createScaffold())
    }

    override fun deploy(spec: RunSpec) {
        shell = shell.copy(deploy = RunScaffolder(spec).createScaffold())
    }

    override fun deploy(ref: RunRef) {
        shell = shell.copy(deploy = RefScaffold(ref.key))
    }

    override fun deploy(value: Run) {
        shell = shell.copy(deploy = ValueScaffold(value))
    }
}

@DslBuilder
internal class StoreArtifactsStepShellBuilder(internal var shell: StoreArtifactsStepShell = StoreArtifactsStepShell()) : StoreArtifactsStepBuilder {
    override fun store_artifacts(body: StoreArtifactsBuilder.() -> Unit) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder(StoreArtifactsSpec(body)).createScaffold())
    }

    override fun store_artifacts(spec: StoreArtifactsSpec) {
        shell = shell.copy(store_artifacts = StoreArtifactsScaffolder(spec).createScaffold())
    }

    override fun store_artifacts(ref: StoreArtifactsRef) {
        shell = shell.copy(store_artifacts = RefScaffold(ref.key))
    }

    override fun store_artifacts(value: StoreArtifacts) {
        shell = shell.copy(store_artifacts = ValueScaffold(value))
    }
}

@DslBuilder
internal class StoreTestResultsStepShellBuilder(internal var shell: StoreTestResultsStepShell = StoreTestResultsStepShell()) : StoreTestResultsStepBuilder {
    override fun store_test_results(body: StoreTestResultsBuilder.() -> Unit) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder(StoreTestResultsSpec(body)).createScaffold())
    }

    override fun store_test_results(spec: StoreTestResultsSpec) {
        shell = shell.copy(store_test_results = StoreTestResultsScaffolder(spec).createScaffold())
    }

    override fun store_test_results(ref: StoreTestResultsRef) {
        shell = shell.copy(store_test_results = RefScaffold(ref.key))
    }

    override fun store_test_results(value: StoreTestResults) {
        shell = shell.copy(store_test_results = ValueScaffold(value))
    }
}

@DslBuilder
internal class PersistToWorkspaceStepShellBuilder(internal var shell: PersistToWorkspaceStepShell = PersistToWorkspaceStepShell()) : PersistToWorkspaceStepBuilder {
    override fun persist_to_workspace(body: PersistToWorkspaceBuilder.() -> Unit) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder(PersistToWorkspaceSpec(body)).createScaffold())
    }

    override fun persist_to_workspace(spec: PersistToWorkspaceSpec) {
        shell = shell.copy(persist_to_workspace = PersistToWorkspaceScaffolder(spec).createScaffold())
    }

    override fun persist_to_workspace(ref: PersistToWorkspaceRef) {
        shell = shell.copy(persist_to_workspace = RefScaffold(ref.key))
    }

    override fun persist_to_workspace(value: PersistToWorkspace) {
        shell = shell.copy(persist_to_workspace = ValueScaffold(value))
    }
}

@DslBuilder
internal class AttachWorkspaceStepShellBuilder(internal var shell: AttachWorkspaceStepShell = AttachWorkspaceStepShell()) : AttachWorkspaceStepBuilder {
    override fun attach_workspace(body: AttachWorkspaceBuilder.() -> Unit) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder(AttachWorkspaceSpec(body)).createScaffold())
    }

    override fun attach_workspace(spec: AttachWorkspaceSpec) {
        shell = shell.copy(attach_workspace = AttachWorkspaceScaffolder(spec).createScaffold())
    }

    override fun attach_workspace(ref: AttachWorkspaceRef) {
        shell = shell.copy(attach_workspace = RefScaffold(ref.key))
    }

    override fun attach_workspace(value: AttachWorkspace) {
        shell = shell.copy(attach_workspace = ValueScaffold(value))
    }
}

@DslBuilder
internal class AddSshKeysStepShellBuilder(internal var shell: AddSshKeysStepShell = AddSshKeysStepShell()) : AddSshKeysStepBuilder {
    override fun add_ssh_keys(body: AddSshKeysBuilder.() -> Unit) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder(AddSshKeysSpec(body)).createScaffold())
    }

    override fun add_ssh_keys(spec: AddSshKeysSpec) {
        shell = shell.copy(add_ssh_keys = AddSshKeysScaffolder(spec).createScaffold())
    }

    override fun add_ssh_keys(ref: AddSshKeysRef) {
        shell = shell.copy(add_ssh_keys = RefScaffold(ref.key))
    }

    override fun add_ssh_keys(value: AddSshKeys) {
        shell = shell.copy(add_ssh_keys = ValueScaffold(value))
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

class RunScaffolder(internal val spec: RunSpec) : Scaffolder<Run> {
    override fun createScaffold(): Scaffold<Run> {
        val builder = RunShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RunShellBuilder(internal var shell: RunShell = RunShell()) : RunBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun command(value: String) {
        shell = shell.copy(command = ValueScaffold(value))
    }

    override fun shell(value: String) {
        shell = shell.copy(shell = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun background(value: Boolean) {
        shell = shell.copy(background = ValueScaffold(value))
    }

    override fun working_directory(value: String) {
        shell = shell.copy(working_directory = ValueScaffold(value))
    }

    override fun no_output_timeout(value: String) {
        shell = shell.copy(no_output_timeout = ValueScaffold(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = ValueScaffold(value))
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

class CheckoutScaffolder(internal val spec: CheckoutSpec) : Scaffolder<Checkout> {
    override fun createScaffold(): Scaffold<Checkout> {
        val builder = CheckoutShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class CheckoutShellBuilder(internal var shell: CheckoutShell = CheckoutShell()) : CheckoutBuilder {
    override fun path(value: String) {
        shell = shell.copy(path = ValueScaffold(value))
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

class SetupRemoteDockerScaffolder(internal val spec: SetupRemoteDockerSpec) : Scaffolder<SetupRemoteDocker> {
    override fun createScaffold(): Scaffold<SetupRemoteDocker> {
        val builder = SetupRemoteDockerShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SetupRemoteDockerShellBuilder(internal var shell: SetupRemoteDockerShell = SetupRemoteDockerShell()) : SetupRemoteDockerBuilder {
    override fun docker_layer_caching(value: Boolean) {
        shell = shell.copy(docker_layer_caching = ValueScaffold(value))
    }

    override fun version(value: String) {
        shell = shell.copy(version = ValueScaffold(value))
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

class SaveCacheScaffolder(internal val spec: SaveCacheSpec) : Scaffolder<SaveCache> {
    override fun createScaffold(): Scaffold<SaveCache> {
        val builder = SaveCacheShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SaveCacheShellBuilder(internal var shell: SaveCacheShell = SaveCacheShell()) : SaveCacheBuilder {
    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + ValueScaffold(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { ValueScaffold(it) })
    }

    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
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

class RestoreCacheScaffolder(internal val spec: RestoreCacheSpec) : Scaffolder<RestoreCache> {
    override fun createScaffold(): Scaffold<RestoreCache> {
        val builder = RestoreCacheShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RestoreCacheShellBuilder(internal var shell: RestoreCacheShell = RestoreCacheShell()) : RestoreCacheBuilder {
    override fun key(value: String) {
        shell = shell.copy(keys = shell.keys.orEmpty() + ValueScaffold(value))
    }

    override fun keys(keys: List<String>) {
        shell = shell.copy(keys = shell.keys.orEmpty() + keys.map { ValueScaffold(it) })
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
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

class StoreArtifactsScaffolder(internal val spec: StoreArtifactsSpec) : Scaffolder<StoreArtifacts> {
    override fun createScaffold(): Scaffold<StoreArtifacts> {
        val builder = StoreArtifactsShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class StoreArtifactsShellBuilder(internal var shell: StoreArtifactsShell = StoreArtifactsShell()) : StoreArtifactsBuilder {
    override fun path(value: String) {
        shell = shell.copy(path = ValueScaffold(value))
    }

    override fun destination(value: String) {
        shell = shell.copy(destination = ValueScaffold(value))
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

class StoreTestResultsScaffolder(internal val spec: StoreTestResultsSpec) : Scaffolder<StoreTestResults> {
    override fun createScaffold(): Scaffold<StoreTestResults> {
        val builder = StoreTestResultsShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class StoreTestResultsShellBuilder(internal var shell: StoreTestResultsShell = StoreTestResultsShell()) : StoreTestResultsBuilder {
    override fun path(value: String) {
        shell = shell.copy(path = ValueScaffold(value))
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

class PersistToWorkspaceScaffolder(internal val spec: PersistToWorkspaceSpec) : Scaffolder<PersistToWorkspace> {
    override fun createScaffold(): Scaffold<PersistToWorkspace> {
        val builder = PersistToWorkspaceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class PersistToWorkspaceShellBuilder(internal var shell: PersistToWorkspaceShell = PersistToWorkspaceShell()) : PersistToWorkspaceBuilder {
    override fun root(value: String) {
        shell = shell.copy(root = ValueScaffold(value))
    }

    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + ValueScaffold(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { ValueScaffold(it) })
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

class AttachWorkspaceScaffolder(internal val spec: AttachWorkspaceSpec) : Scaffolder<AttachWorkspace> {
    override fun createScaffold(): Scaffold<AttachWorkspace> {
        val builder = AttachWorkspaceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AttachWorkspaceShellBuilder(internal var shell: AttachWorkspaceShell = AttachWorkspaceShell()) : AttachWorkspaceBuilder {
    override fun at(value: String) {
        shell = shell.copy(at = ValueScaffold(value))
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

class AddSshKeysScaffolder(internal val spec: AddSshKeysSpec) : Scaffolder<AddSshKeys> {
    override fun createScaffold(): Scaffold<AddSshKeys> {
        val builder = AddSshKeysShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class AddSshKeysShellBuilder(internal var shell: AddSshKeysShell = AddSshKeysShell()) : AddSshKeysBuilder {
    override fun fingerprint(value: String) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + ValueScaffold(value))
    }

    override fun fingerprints(fingerprints: List<String>) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + fingerprints.map { ValueScaffold(it) })
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

class WorkflowScaffolder(internal val spec: WorkflowSpec) : Scaffolder<Workflow> {
    override fun createScaffold(): Scaffold<Workflow> {
        val builder = WorkflowShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowShellBuilder(internal var shell: WorkflowShell = WorkflowShell()) : WorkflowBuilder {
    override fun trigger(body: WorkflowTriggerBuilder.() -> Unit) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder(WorkflowTriggerSpec(body)).createScaffold())
    }

    override fun trigger(spec: WorkflowTriggerSpec) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + WorkflowTriggerScaffolder(spec).createScaffold())
    }

    override fun trigger(ref: WorkflowTriggerRef) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + RefScaffold(ref.key))
    }

    override fun trigger(value: WorkflowTrigger) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + ValueScaffold(value))
    }

    override fun triggers(triggers: List<WorkflowTrigger>) {
        shell = shell.copy(triggers = shell.triggers.orEmpty() + triggers.map { ValueScaffold(it) })
    }

    override fun jobs(key: String, body: WorkflowJobBuilder.() -> Unit) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),WorkflowJobScaffolder(WorkflowJobSpec(body)).createScaffold())))
    }

    override fun jobs(key: String, spec: WorkflowJobSpec) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),WorkflowJobScaffolder(spec).createScaffold())))
    }

    override fun jobs(key: String, ref: WorkflowJobRef) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),RefScaffold(ref.key))))
    }

    override fun jobs(key: String, value: WorkflowJob) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),ValueScaffold(value))))
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

class WorkflowTriggerScaffolder(internal val spec: WorkflowTriggerSpec) : Scaffolder<WorkflowTrigger> {
    override fun createScaffold(): Scaffold<WorkflowTrigger> {
        val builder = WorkflowTriggerShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerShellBuilder(internal var shell: WorkflowTriggerShell = WorkflowTriggerShell()) : WorkflowTriggerBuilder {
    override fun schedule(body: WorkflowTriggerScheduleBuilder.() -> Unit) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder(WorkflowTriggerScheduleSpec(body)).createScaffold())
    }

    override fun schedule(spec: WorkflowTriggerScheduleSpec) {
        shell = shell.copy(schedule = WorkflowTriggerScheduleScaffolder(spec).createScaffold())
    }

    override fun schedule(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(schedule = RefScaffold(ref.key))
    }

    override fun schedule(value: WorkflowTriggerSchedule) {
        shell = shell.copy(schedule = ValueScaffold(value))
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

class WorkflowTriggerScheduleScaffolder(internal val spec: WorkflowTriggerScheduleSpec) : Scaffolder<WorkflowTriggerSchedule> {
    override fun createScaffold(): Scaffold<WorkflowTriggerSchedule> {
        val builder = WorkflowTriggerScheduleShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleShellBuilder(internal var shell: WorkflowTriggerScheduleShell = WorkflowTriggerScheduleShell()) : WorkflowTriggerScheduleBuilder {
    override fun cron(value: String) {
        shell = shell.copy(cron = ValueScaffold(value))
    }

    override fun filters(body: WorkflowTriggerScheduleBuilder.() -> Unit) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder(WorkflowTriggerScheduleSpec(body)).createScaffold())
    }

    override fun filters(spec: WorkflowTriggerScheduleSpec) {
        shell = shell.copy(filters = WorkflowTriggerScheduleScaffolder(spec).createScaffold())
    }

    override fun filters(ref: WorkflowTriggerScheduleRef) {
        shell = shell.copy(filters = RefScaffold(ref.key))
    }

    override fun filters(value: WorkflowTriggerSchedule) {
        shell = shell.copy(filters = ValueScaffold(value))
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

class WorkflowTriggerScheduleFiltersScaffolder(internal val spec: WorkflowTriggerScheduleFiltersSpec) : Scaffolder<WorkflowTriggerScheduleFilters> {
    override fun createScaffold(): Scaffold<WorkflowTriggerScheduleFilters> {
        val builder = WorkflowTriggerScheduleFiltersShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleFiltersShellBuilder(internal var shell: WorkflowTriggerScheduleFiltersShell = WorkflowTriggerScheduleFiltersShell()) : WorkflowTriggerScheduleFiltersBuilder {
    override fun branches(body: FilterBuilder.() -> Unit) {
        shell = shell.copy(branches = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun branches(spec: FilterSpec) {
        shell = shell.copy(branches = FilterScaffolder(spec).createScaffold())
    }

    override fun branches(ref: FilterRef) {
        shell = shell.copy(branches = RefScaffold(ref.key))
    }

    override fun branches(value: Filter) {
        shell = shell.copy(branches = ValueScaffold(value))
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

class WorkflowJobScaffolder(internal val spec: WorkflowJobSpec) : Scaffolder<WorkflowJob> {
    override fun createScaffold(): Scaffold<WorkflowJob> {
        val builder = WorkflowJobShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowJobShellBuilder(internal var shell: WorkflowJobShell = WorkflowJobShell()) : WorkflowJobBuilder {
    override fun require(value: String) {
        shell = shell.copy(requires = shell.requires.orEmpty() + ValueScaffold(value))
    }

    override fun requires(requires: List<String>) {
        shell = shell.copy(requires = shell.requires.orEmpty() + requires.map { ValueScaffold(it) })
    }

    override fun context(value: String) {
        shell = shell.copy(context = ValueScaffold(value))
    }

    override fun type(value: WorkflowJobType) {
        shell = shell.copy(type = ValueScaffold(value))
    }

    override fun filters(body: WorkflowJobFilterBuilder.() -> Unit) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder(WorkflowJobFilterSpec(body)).createScaffold())
    }

    override fun filters(spec: WorkflowJobFilterSpec) {
        shell = shell.copy(filters = WorkflowJobFilterScaffolder(spec).createScaffold())
    }

    override fun filters(ref: WorkflowJobFilterRef) {
        shell = shell.copy(filters = RefScaffold(ref.key))
    }

    override fun filters(value: WorkflowJobFilter) {
        shell = shell.copy(filters = ValueScaffold(value))
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

class WorkflowJobFilterScaffolder(internal val spec: WorkflowJobFilterSpec) : Scaffolder<WorkflowJobFilter> {
    override fun createScaffold(): Scaffold<WorkflowJobFilter> {
        val builder = WorkflowJobFilterShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkflowJobFilterShellBuilder(internal var shell: WorkflowJobFilterShell = WorkflowJobFilterShell()) : WorkflowJobFilterBuilder {
    override fun branches(body: FilterBuilder.() -> Unit) {
        shell = shell.copy(branches = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun branches(spec: FilterSpec) {
        shell = shell.copy(branches = FilterScaffolder(spec).createScaffold())
    }

    override fun branches(ref: FilterRef) {
        shell = shell.copy(branches = RefScaffold(ref.key))
    }

    override fun branches(value: Filter) {
        shell = shell.copy(branches = ValueScaffold(value))
    }

    override fun tags(body: FilterBuilder.() -> Unit) {
        shell = shell.copy(tags = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun tags(spec: FilterSpec) {
        shell = shell.copy(tags = FilterScaffolder(spec).createScaffold())
    }

    override fun tags(ref: FilterRef) {
        shell = shell.copy(tags = RefScaffold(ref.key))
    }

    override fun tags(value: Filter) {
        shell = shell.copy(tags = ValueScaffold(value))
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

class FilterScaffolder(internal val spec: FilterSpec) : Scaffolder<Filter> {
    override fun createScaffold(): Scaffold<Filter> {
        val builder = FilterShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class FilterShellBuilder(internal var shell: FilterShell = FilterShell()) : FilterBuilder {
    override fun only(value: String) {
        shell = shell.copy(only = shell.only.orEmpty() + ValueScaffold(value))
    }

    override fun only(only: List<String>) {
        shell = shell.copy(only = shell.only.orEmpty() + only.map { ValueScaffold(it) })
    }

    override fun ignore(value: String) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + ValueScaffold(value))
    }

    override fun ignore(ignore: List<String>) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + ignore.map { ValueScaffold(it) })
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
