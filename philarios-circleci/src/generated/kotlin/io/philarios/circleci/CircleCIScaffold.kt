// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.RefScaffold
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
        val builder = CircleCIScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class CircleCIScaffoldBuilder(internal var scaffold: CircleCIScaffold = CircleCIScaffold()) : CircleCIBuilder {
    override fun version(value: String) {
        scaffold = scaffold.copy(version = ValueScaffold(value))
    }

    override fun jobs(key: String, body: JobBuilder.() -> Unit) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + Pair(ValueScaffold(key),JobScaffolder(JobSpec(body)).createScaffold()))
    }

    override fun jobs(key: String, spec: JobSpec) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + Pair(ValueScaffold(key),JobScaffolder(spec).createScaffold()))
    }

    override fun jobs(key: String, ref: JobRef) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + Pair(ValueScaffold(key),RefScaffold(ref.key)))
    }

    override fun jobs(key: String, value: Job) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun workflows(key: String, body: WorkflowBuilder.() -> Unit) {
        scaffold = scaffold.copy(workflows = scaffold.workflows.orEmpty() + Pair(ValueScaffold(key),WorkflowScaffolder(WorkflowSpec(body)).createScaffold()))
    }

    override fun workflows(key: String, spec: WorkflowSpec) {
        scaffold = scaffold.copy(workflows = scaffold.workflows.orEmpty() + Pair(ValueScaffold(key),WorkflowScaffolder(spec).createScaffold()))
    }

    override fun workflows(key: String, ref: WorkflowRef) {
        scaffold = scaffold.copy(workflows = scaffold.workflows.orEmpty() + Pair(ValueScaffold(key),RefScaffold(ref.key)))
    }

    override fun workflows(key: String, value: Workflow) {
        scaffold = scaffold.copy(workflows = scaffold.workflows.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }
}

internal data class CircleCIScaffold(
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
        val builder = JobScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class JobScaffoldBuilder(internal var scaffold: JobScaffold = JobScaffold()) : JobBuilder {
    override fun docker(body: DockerExecutorBuilder.() -> Unit) {
        scaffold = scaffold.copy(docker = scaffold.docker.orEmpty() + DockerExecutorScaffolder(DockerExecutorSpec(body)).createScaffold())
    }

    override fun docker(spec: DockerExecutorSpec) {
        scaffold = scaffold.copy(docker = scaffold.docker.orEmpty() + DockerExecutorScaffolder(spec).createScaffold())
    }

    override fun docker(ref: DockerExecutorRef) {
        scaffold = scaffold.copy(docker = scaffold.docker.orEmpty() + RefScaffold(ref.key))
    }

    override fun docker(value: DockerExecutor) {
        scaffold = scaffold.copy(docker = scaffold.docker.orEmpty() + ValueScaffold(value))
    }

    override fun docker(docker: List<DockerExecutor>) {
        scaffold = scaffold.copy(docker = scaffold.docker.orEmpty() + docker.map { ValueScaffold(it) })
    }

    override fun resource_class(value: ResourceClass) {
        scaffold = scaffold.copy(resource_class = ValueScaffold(value))
    }

    override fun machine(body: MachineExecutorBuilder.() -> Unit) {
        scaffold = scaffold.copy(machine = MachineExecutorScaffolder(MachineExecutorSpec(body)).createScaffold())
    }

    override fun machine(spec: MachineExecutorSpec) {
        scaffold = scaffold.copy(machine = MachineExecutorScaffolder(spec).createScaffold())
    }

    override fun machine(ref: MachineExecutorRef) {
        scaffold = scaffold.copy(machine = RefScaffold(ref.key))
    }

    override fun machine(value: MachineExecutor) {
        scaffold = scaffold.copy(machine = ValueScaffold(value))
    }

    override fun macos(body: MacosExecutorBuilder.() -> Unit) {
        scaffold = scaffold.copy(macos = MacosExecutorScaffolder(MacosExecutorSpec(body)).createScaffold())
    }

    override fun macos(spec: MacosExecutorSpec) {
        scaffold = scaffold.copy(macos = MacosExecutorScaffolder(spec).createScaffold())
    }

    override fun macos(ref: MacosExecutorRef) {
        scaffold = scaffold.copy(macos = RefScaffold(ref.key))
    }

    override fun macos(value: MacosExecutor) {
        scaffold = scaffold.copy(macos = ValueScaffold(value))
    }

    override fun shell(value: String) {
        scaffold = scaffold.copy(shell = ValueScaffold(value))
    }

    override fun <T : Step> step(spec: StepSpec<T>) {
        scaffold = scaffold.copy(steps = scaffold.steps.orEmpty() + StepScaffolder<Step>(spec).createScaffold())
    }

    override fun <T : Step> step(ref: StepRef<T>) {
        scaffold = scaffold.copy(steps = scaffold.steps.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Step> step(value: T) {
        scaffold = scaffold.copy(steps = scaffold.steps.orEmpty() + ValueScaffold(value))
    }

    override fun working_directory(value: String) {
        scaffold = scaffold.copy(working_directory = ValueScaffold(value))
    }

    override fun parallelism(value: Int) {
        scaffold = scaffold.copy(parallelism = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun branches(key: String, value: String) {
        scaffold = scaffold.copy(branches = scaffold.branches.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun branches(pair: Pair<String, String>) {
        scaffold = scaffold.copy(branches = scaffold.branches.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun branches(branches: Map<String, String>) {
        scaffold = scaffold.copy(branches = scaffold.branches.orEmpty() + branches.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class JobScaffold(
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
        val builder = DockerExecutorScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class DockerExecutorScaffoldBuilder(internal var scaffold: DockerExecutorScaffold = DockerExecutorScaffold()) : DockerExecutorBuilder {
    override fun image(value: String) {
        scaffold = scaffold.copy(image = ValueScaffold(value))
    }

    override fun entrypoint(value: String) {
        scaffold = scaffold.copy(entrypoint = scaffold.entrypoint.orEmpty() + ValueScaffold(value))
    }

    override fun entrypoint(entrypoint: List<String>) {
        scaffold = scaffold.copy(entrypoint = scaffold.entrypoint.orEmpty() + entrypoint.map { ValueScaffold(it) })
    }

    override fun command(value: String) {
        scaffold = scaffold.copy(command = scaffold.command.orEmpty() + ValueScaffold(value))
    }

    override fun command(command: List<String>) {
        scaffold = scaffold.copy(command = scaffold.command.orEmpty() + command.map { ValueScaffold(it) })
    }

    override fun user(value: String) {
        scaffold = scaffold.copy(user = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun auth(body: AuthBuilder.() -> Unit) {
        scaffold = scaffold.copy(auth = AuthScaffolder(AuthSpec(body)).createScaffold())
    }

    override fun auth(spec: AuthSpec) {
        scaffold = scaffold.copy(auth = AuthScaffolder(spec).createScaffold())
    }

    override fun auth(ref: AuthRef) {
        scaffold = scaffold.copy(auth = RefScaffold(ref.key))
    }

    override fun auth(value: Auth) {
        scaffold = scaffold.copy(auth = ValueScaffold(value))
    }

    override fun aws_auth(body: AwsAuthBuilder.() -> Unit) {
        scaffold = scaffold.copy(aws_auth = AwsAuthScaffolder(AwsAuthSpec(body)).createScaffold())
    }

    override fun aws_auth(spec: AwsAuthSpec) {
        scaffold = scaffold.copy(aws_auth = AwsAuthScaffolder(spec).createScaffold())
    }

    override fun aws_auth(ref: AwsAuthRef) {
        scaffold = scaffold.copy(aws_auth = RefScaffold(ref.key))
    }

    override fun aws_auth(value: AwsAuth) {
        scaffold = scaffold.copy(aws_auth = ValueScaffold(value))
    }
}

internal data class DockerExecutorScaffold(
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
        val builder = AuthScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class AuthScaffoldBuilder(internal var scaffold: AuthScaffold = AuthScaffold()) : AuthBuilder {
    override fun username(value: String) {
        scaffold = scaffold.copy(username = ValueScaffold(value))
    }

    override fun password(value: String) {
        scaffold = scaffold.copy(password = ValueScaffold(value))
    }
}

internal data class AuthScaffold(var username: Scaffold<String>? = null, var password: Scaffold<String>? = null) : Scaffold<Auth> {
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
        val builder = AwsAuthScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class AwsAuthScaffoldBuilder(internal var scaffold: AwsAuthScaffold = AwsAuthScaffold()) : AwsAuthBuilder {
    override fun aws_access_key_id(value: String) {
        scaffold = scaffold.copy(aws_access_key_id = ValueScaffold(value))
    }

    override fun aws_secret_access_key(value: String) {
        scaffold = scaffold.copy(aws_secret_access_key = ValueScaffold(value))
    }
}

internal data class AwsAuthScaffold(var aws_access_key_id: Scaffold<String>? = null, var aws_secret_access_key: Scaffold<String>? = null) : Scaffold<AwsAuth> {
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
        val builder = MachineExecutorScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class MachineExecutorScaffoldBuilder(internal var scaffold: MachineExecutorScaffold = MachineExecutorScaffold()) : MachineExecutorBuilder {
    override fun enabled(value: Boolean) {
        scaffold = scaffold.copy(enabled = ValueScaffold(value))
    }

    override fun image(value: String) {
        scaffold = scaffold.copy(image = ValueScaffold(value))
    }
}

internal data class MachineExecutorScaffold(var enabled: Scaffold<Boolean>? = null, var image: Scaffold<String>? = null) : Scaffold<MachineExecutor> {
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
        val builder = MacosExecutorScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class MacosExecutorScaffoldBuilder(internal var scaffold: MacosExecutorScaffold = MacosExecutorScaffold()) : MacosExecutorBuilder {
    override fun xcode(value: String) {
        scaffold = scaffold.copy(xcode = ValueScaffold(value))
    }
}

internal data class MacosExecutorScaffold(var xcode: Scaffold<String>? = null) : Scaffold<MacosExecutor> {
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
        val builder = RunStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class CheckoutStepScaffolder(internal val spec: CheckoutStepSpec) : Scaffolder<CheckoutStep> {
    override fun createScaffold(): Scaffold<CheckoutStep> {
        val builder = CheckoutStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class SetupRemoteDockerStepScaffolder(internal val spec: SetupRemoteDockerStepSpec) : Scaffolder<SetupRemoteDockerStep> {
    override fun createScaffold(): Scaffold<SetupRemoteDockerStep> {
        val builder = SetupRemoteDockerStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class SaveCacheStepScaffolder(internal val spec: SaveCacheStepSpec) : Scaffolder<SaveCacheStep> {
    override fun createScaffold(): Scaffold<SaveCacheStep> {
        val builder = SaveCacheStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class RestoreCacheStepScaffolder(internal val spec: RestoreCacheStepSpec) : Scaffolder<RestoreCacheStep> {
    override fun createScaffold(): Scaffold<RestoreCacheStep> {
        val builder = RestoreCacheStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class DeployStepScaffolder(internal val spec: DeployStepSpec) : Scaffolder<DeployStep> {
    override fun createScaffold(): Scaffold<DeployStep> {
        val builder = DeployStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class StoreArtifactsStepScaffolder(internal val spec: StoreArtifactsStepSpec) : Scaffolder<StoreArtifactsStep> {
    override fun createScaffold(): Scaffold<StoreArtifactsStep> {
        val builder = StoreArtifactsStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class StoreTestResultsStepScaffolder(internal val spec: StoreTestResultsStepSpec) : Scaffolder<StoreTestResultsStep> {
    override fun createScaffold(): Scaffold<StoreTestResultsStep> {
        val builder = StoreTestResultsStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class PersistToWorkspaceStepScaffolder(internal val spec: PersistToWorkspaceStepSpec) : Scaffolder<PersistToWorkspaceStep> {
    override fun createScaffold(): Scaffold<PersistToWorkspaceStep> {
        val builder = PersistToWorkspaceStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class AttachWorkspaceStepScaffolder(internal val spec: AttachWorkspaceStepSpec) : Scaffolder<AttachWorkspaceStep> {
    override fun createScaffold(): Scaffold<AttachWorkspaceStep> {
        val builder = AttachWorkspaceStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class AddSshKeysStepScaffolder(internal val spec: AddSshKeysStepSpec) : Scaffolder<AddSshKeysStep> {
    override fun createScaffold(): Scaffold<AddSshKeysStep> {
        val builder = AddSshKeysStepScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class RunStepScaffoldBuilder(internal var scaffold: RunStepScaffold = RunStepScaffold()) : RunStepBuilder {
    override fun run(body: RunBuilder.() -> Unit) {
        scaffold = scaffold.copy(run = RunScaffolder(RunSpec(body)).createScaffold())
    }

    override fun run(spec: RunSpec) {
        scaffold = scaffold.copy(run = RunScaffolder(spec).createScaffold())
    }

    override fun run(ref: RunRef) {
        scaffold = scaffold.copy(run = RefScaffold(ref.key))
    }

    override fun run(value: Run) {
        scaffold = scaffold.copy(run = ValueScaffold(value))
    }
}

@DslBuilder
internal class CheckoutStepScaffoldBuilder(internal var scaffold: CheckoutStepScaffold = CheckoutStepScaffold()) : CheckoutStepBuilder {
    override fun checkout(body: CheckoutBuilder.() -> Unit) {
        scaffold = scaffold.copy(checkout = CheckoutScaffolder(CheckoutSpec(body)).createScaffold())
    }

    override fun checkout(spec: CheckoutSpec) {
        scaffold = scaffold.copy(checkout = CheckoutScaffolder(spec).createScaffold())
    }

    override fun checkout(ref: CheckoutRef) {
        scaffold = scaffold.copy(checkout = RefScaffold(ref.key))
    }

    override fun checkout(value: Checkout) {
        scaffold = scaffold.copy(checkout = ValueScaffold(value))
    }
}

@DslBuilder
internal class SetupRemoteDockerStepScaffoldBuilder(internal var scaffold: SetupRemoteDockerStepScaffold = SetupRemoteDockerStepScaffold()) : SetupRemoteDockerStepBuilder {
    override fun setup_remote_docker(body: SetupRemoteDockerBuilder.() -> Unit) {
        scaffold = scaffold.copy(setup_remote_docker = SetupRemoteDockerScaffolder(SetupRemoteDockerSpec(body)).createScaffold())
    }

    override fun setup_remote_docker(spec: SetupRemoteDockerSpec) {
        scaffold = scaffold.copy(setup_remote_docker = SetupRemoteDockerScaffolder(spec).createScaffold())
    }

    override fun setup_remote_docker(ref: SetupRemoteDockerRef) {
        scaffold = scaffold.copy(setup_remote_docker = RefScaffold(ref.key))
    }

    override fun setup_remote_docker(value: SetupRemoteDocker) {
        scaffold = scaffold.copy(setup_remote_docker = ValueScaffold(value))
    }
}

@DslBuilder
internal class SaveCacheStepScaffoldBuilder(internal var scaffold: SaveCacheStepScaffold = SaveCacheStepScaffold()) : SaveCacheStepBuilder {
    override fun save_cache(body: SaveCacheBuilder.() -> Unit) {
        scaffold = scaffold.copy(save_cache = SaveCacheScaffolder(SaveCacheSpec(body)).createScaffold())
    }

    override fun save_cache(spec: SaveCacheSpec) {
        scaffold = scaffold.copy(save_cache = SaveCacheScaffolder(spec).createScaffold())
    }

    override fun save_cache(ref: SaveCacheRef) {
        scaffold = scaffold.copy(save_cache = RefScaffold(ref.key))
    }

    override fun save_cache(value: SaveCache) {
        scaffold = scaffold.copy(save_cache = ValueScaffold(value))
    }
}

@DslBuilder
internal class RestoreCacheStepScaffoldBuilder(internal var scaffold: RestoreCacheStepScaffold = RestoreCacheStepScaffold()) : RestoreCacheStepBuilder {
    override fun restore_cache(body: RestoreCacheBuilder.() -> Unit) {
        scaffold = scaffold.copy(restore_cache = RestoreCacheScaffolder(RestoreCacheSpec(body)).createScaffold())
    }

    override fun restore_cache(spec: RestoreCacheSpec) {
        scaffold = scaffold.copy(restore_cache = RestoreCacheScaffolder(spec).createScaffold())
    }

    override fun restore_cache(ref: RestoreCacheRef) {
        scaffold = scaffold.copy(restore_cache = RefScaffold(ref.key))
    }

    override fun restore_cache(value: RestoreCache) {
        scaffold = scaffold.copy(restore_cache = ValueScaffold(value))
    }
}

@DslBuilder
internal class DeployStepScaffoldBuilder(internal var scaffold: DeployStepScaffold = DeployStepScaffold()) : DeployStepBuilder {
    override fun deploy(body: RunBuilder.() -> Unit) {
        scaffold = scaffold.copy(deploy = RunScaffolder(RunSpec(body)).createScaffold())
    }

    override fun deploy(spec: RunSpec) {
        scaffold = scaffold.copy(deploy = RunScaffolder(spec).createScaffold())
    }

    override fun deploy(ref: RunRef) {
        scaffold = scaffold.copy(deploy = RefScaffold(ref.key))
    }

    override fun deploy(value: Run) {
        scaffold = scaffold.copy(deploy = ValueScaffold(value))
    }
}

@DslBuilder
internal class StoreArtifactsStepScaffoldBuilder(internal var scaffold: StoreArtifactsStepScaffold = StoreArtifactsStepScaffold()) : StoreArtifactsStepBuilder {
    override fun store_artifacts(body: StoreArtifactsBuilder.() -> Unit) {
        scaffold = scaffold.copy(store_artifacts = StoreArtifactsScaffolder(StoreArtifactsSpec(body)).createScaffold())
    }

    override fun store_artifacts(spec: StoreArtifactsSpec) {
        scaffold = scaffold.copy(store_artifacts = StoreArtifactsScaffolder(spec).createScaffold())
    }

    override fun store_artifacts(ref: StoreArtifactsRef) {
        scaffold = scaffold.copy(store_artifacts = RefScaffold(ref.key))
    }

    override fun store_artifacts(value: StoreArtifacts) {
        scaffold = scaffold.copy(store_artifacts = ValueScaffold(value))
    }
}

@DslBuilder
internal class StoreTestResultsStepScaffoldBuilder(internal var scaffold: StoreTestResultsStepScaffold = StoreTestResultsStepScaffold()) : StoreTestResultsStepBuilder {
    override fun store_test_results(body: StoreTestResultsBuilder.() -> Unit) {
        scaffold = scaffold.copy(store_test_results = StoreTestResultsScaffolder(StoreTestResultsSpec(body)).createScaffold())
    }

    override fun store_test_results(spec: StoreTestResultsSpec) {
        scaffold = scaffold.copy(store_test_results = StoreTestResultsScaffolder(spec).createScaffold())
    }

    override fun store_test_results(ref: StoreTestResultsRef) {
        scaffold = scaffold.copy(store_test_results = RefScaffold(ref.key))
    }

    override fun store_test_results(value: StoreTestResults) {
        scaffold = scaffold.copy(store_test_results = ValueScaffold(value))
    }
}

@DslBuilder
internal class PersistToWorkspaceStepScaffoldBuilder(internal var scaffold: PersistToWorkspaceStepScaffold = PersistToWorkspaceStepScaffold()) : PersistToWorkspaceStepBuilder {
    override fun persist_to_workspace(body: PersistToWorkspaceBuilder.() -> Unit) {
        scaffold = scaffold.copy(persist_to_workspace = PersistToWorkspaceScaffolder(PersistToWorkspaceSpec(body)).createScaffold())
    }

    override fun persist_to_workspace(spec: PersistToWorkspaceSpec) {
        scaffold = scaffold.copy(persist_to_workspace = PersistToWorkspaceScaffolder(spec).createScaffold())
    }

    override fun persist_to_workspace(ref: PersistToWorkspaceRef) {
        scaffold = scaffold.copy(persist_to_workspace = RefScaffold(ref.key))
    }

    override fun persist_to_workspace(value: PersistToWorkspace) {
        scaffold = scaffold.copy(persist_to_workspace = ValueScaffold(value))
    }
}

@DslBuilder
internal class AttachWorkspaceStepScaffoldBuilder(internal var scaffold: AttachWorkspaceStepScaffold = AttachWorkspaceStepScaffold()) : AttachWorkspaceStepBuilder {
    override fun attach_workspace(body: AttachWorkspaceBuilder.() -> Unit) {
        scaffold = scaffold.copy(attach_workspace = AttachWorkspaceScaffolder(AttachWorkspaceSpec(body)).createScaffold())
    }

    override fun attach_workspace(spec: AttachWorkspaceSpec) {
        scaffold = scaffold.copy(attach_workspace = AttachWorkspaceScaffolder(spec).createScaffold())
    }

    override fun attach_workspace(ref: AttachWorkspaceRef) {
        scaffold = scaffold.copy(attach_workspace = RefScaffold(ref.key))
    }

    override fun attach_workspace(value: AttachWorkspace) {
        scaffold = scaffold.copy(attach_workspace = ValueScaffold(value))
    }
}

@DslBuilder
internal class AddSshKeysStepScaffoldBuilder(internal var scaffold: AddSshKeysStepScaffold = AddSshKeysStepScaffold()) : AddSshKeysStepBuilder {
    override fun add_ssh_keys(body: AddSshKeysBuilder.() -> Unit) {
        scaffold = scaffold.copy(add_ssh_keys = AddSshKeysScaffolder(AddSshKeysSpec(body)).createScaffold())
    }

    override fun add_ssh_keys(spec: AddSshKeysSpec) {
        scaffold = scaffold.copy(add_ssh_keys = AddSshKeysScaffolder(spec).createScaffold())
    }

    override fun add_ssh_keys(ref: AddSshKeysRef) {
        scaffold = scaffold.copy(add_ssh_keys = RefScaffold(ref.key))
    }

    override fun add_ssh_keys(value: AddSshKeys) {
        scaffold = scaffold.copy(add_ssh_keys = ValueScaffold(value))
    }
}

internal sealed class StepScaffold

internal data class RunStepScaffold(var run: Scaffold<Run>? = null) : StepScaffold(),
        Scaffold<RunStep> {
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

internal data class CheckoutStepScaffold(var checkout: Scaffold<Checkout>? = null) : StepScaffold(),
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

internal data class SetupRemoteDockerStepScaffold(var setup_remote_docker: Scaffold<SetupRemoteDocker>? = null) : StepScaffold(),
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

internal data class SaveCacheStepScaffold(var save_cache: Scaffold<SaveCache>? = null) : StepScaffold(),
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

internal data class RestoreCacheStepScaffold(var restore_cache: Scaffold<RestoreCache>? = null) : StepScaffold(),
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

internal data class DeployStepScaffold(var deploy: Scaffold<Run>? = null) : StepScaffold(),
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

internal data class StoreArtifactsStepScaffold(var store_artifacts: Scaffold<StoreArtifacts>? = null) : StepScaffold(),
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

internal data class StoreTestResultsStepScaffold(var store_test_results: Scaffold<StoreTestResults>? = null) : StepScaffold(),
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

internal data class PersistToWorkspaceStepScaffold(var persist_to_workspace: Scaffold<PersistToWorkspace>? = null) : StepScaffold(),
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

internal data class AttachWorkspaceStepScaffold(var attach_workspace: Scaffold<AttachWorkspace>? = null) : StepScaffold(),
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

internal data class AddSshKeysStepScaffold(var add_ssh_keys: Scaffold<AddSshKeys>? = null) : StepScaffold(),
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
        val builder = RunScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class RunScaffoldBuilder(internal var scaffold: RunScaffold = RunScaffold()) : RunBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun command(value: String) {
        scaffold = scaffold.copy(command = ValueScaffold(value))
    }

    override fun shell(value: String) {
        scaffold = scaffold.copy(shell = ValueScaffold(value))
    }

    override fun environment(key: String, value: String) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        scaffold = scaffold.copy(environment = scaffold.environment.orEmpty() + environment.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }

    override fun background(value: Boolean) {
        scaffold = scaffold.copy(background = ValueScaffold(value))
    }

    override fun working_directory(value: String) {
        scaffold = scaffold.copy(working_directory = ValueScaffold(value))
    }

    override fun no_output_timeout(value: String) {
        scaffold = scaffold.copy(no_output_timeout = ValueScaffold(value))
    }

    override fun `when`(value: When) {
        scaffold = scaffold.copy(`when` = ValueScaffold(value))
    }
}

internal data class RunScaffold(
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
        val builder = CheckoutScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class CheckoutScaffoldBuilder(internal var scaffold: CheckoutScaffold = CheckoutScaffold()) : CheckoutBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }
}

internal data class CheckoutScaffold(var path: Scaffold<String>? = null) : Scaffold<Checkout> {
    override suspend fun resolve(registry: Registry): Checkout {
        val value = Checkout(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

class SetupRemoteDockerScaffolder(internal val spec: SetupRemoteDockerSpec) : Scaffolder<SetupRemoteDocker> {
    override fun createScaffold(): Scaffold<SetupRemoteDocker> {
        val builder = SetupRemoteDockerScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SetupRemoteDockerScaffoldBuilder(internal var scaffold: SetupRemoteDockerScaffold = SetupRemoteDockerScaffold()) : SetupRemoteDockerBuilder {
    override fun docker_layer_caching(value: Boolean) {
        scaffold = scaffold.copy(docker_layer_caching = ValueScaffold(value))
    }

    override fun version(value: String) {
        scaffold = scaffold.copy(version = ValueScaffold(value))
    }
}

internal data class SetupRemoteDockerScaffold(var docker_layer_caching: Scaffold<Boolean>? = null, var version: Scaffold<String>? = null) : Scaffold<SetupRemoteDocker> {
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
        val builder = SaveCacheScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SaveCacheScaffoldBuilder(internal var scaffold: SaveCacheScaffold = SaveCacheScaffold()) : SaveCacheBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(paths = scaffold.paths.orEmpty() + ValueScaffold(value))
    }

    override fun paths(paths: List<String>) {
        scaffold = scaffold.copy(paths = scaffold.paths.orEmpty() + paths.map { ValueScaffold(it) })
    }

    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun `when`(value: When) {
        scaffold = scaffold.copy(`when` = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }
}

internal data class SaveCacheScaffold(
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
        val builder = RestoreCacheScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class RestoreCacheScaffoldBuilder(internal var scaffold: RestoreCacheScaffold = RestoreCacheScaffold()) : RestoreCacheBuilder {
    override fun key(value: String) {
        scaffold = scaffold.copy(keys = scaffold.keys.orEmpty() + ValueScaffold(value))
    }

    override fun keys(keys: List<String>) {
        scaffold = scaffold.copy(keys = scaffold.keys.orEmpty() + keys.map { ValueScaffold(it) })
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }
}

internal data class RestoreCacheScaffold(var keys: List<Scaffold<String>>? = null, var name: Scaffold<String>? = null) : Scaffold<RestoreCache> {
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
        val builder = StoreArtifactsScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class StoreArtifactsScaffoldBuilder(internal var scaffold: StoreArtifactsScaffold = StoreArtifactsScaffold()) : StoreArtifactsBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }

    override fun destination(value: String) {
        scaffold = scaffold.copy(destination = ValueScaffold(value))
    }
}

internal data class StoreArtifactsScaffold(var path: Scaffold<String>? = null, var destination: Scaffold<String>? = null) : Scaffold<StoreArtifacts> {
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
        val builder = StoreTestResultsScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class StoreTestResultsScaffoldBuilder(internal var scaffold: StoreTestResultsScaffold = StoreTestResultsScaffold()) : StoreTestResultsBuilder {
    override fun path(value: String) {
        scaffold = scaffold.copy(path = ValueScaffold(value))
    }
}

internal data class StoreTestResultsScaffold(var path: Scaffold<String>? = null) : Scaffold<StoreTestResults> {
    override suspend fun resolve(registry: Registry): StoreTestResults {
        val value = StoreTestResults(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

class PersistToWorkspaceScaffolder(internal val spec: PersistToWorkspaceSpec) : Scaffolder<PersistToWorkspace> {
    override fun createScaffold(): Scaffold<PersistToWorkspace> {
        val builder = PersistToWorkspaceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class PersistToWorkspaceScaffoldBuilder(internal var scaffold: PersistToWorkspaceScaffold = PersistToWorkspaceScaffold()) : PersistToWorkspaceBuilder {
    override fun root(value: String) {
        scaffold = scaffold.copy(root = ValueScaffold(value))
    }

    override fun path(value: String) {
        scaffold = scaffold.copy(paths = scaffold.paths.orEmpty() + ValueScaffold(value))
    }

    override fun paths(paths: List<String>) {
        scaffold = scaffold.copy(paths = scaffold.paths.orEmpty() + paths.map { ValueScaffold(it) })
    }
}

internal data class PersistToWorkspaceScaffold(var root: Scaffold<String>? = null, var paths: List<Scaffold<String>>? = null) : Scaffold<PersistToWorkspace> {
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
        val builder = AttachWorkspaceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class AttachWorkspaceScaffoldBuilder(internal var scaffold: AttachWorkspaceScaffold = AttachWorkspaceScaffold()) : AttachWorkspaceBuilder {
    override fun at(value: String) {
        scaffold = scaffold.copy(at = ValueScaffold(value))
    }
}

internal data class AttachWorkspaceScaffold(var at: Scaffold<String>? = null) : Scaffold<AttachWorkspace> {
    override suspend fun resolve(registry: Registry): AttachWorkspace {
        val value = AttachWorkspace(
            at?.let{ it.resolve(registry) }
        )
        return value
    }
}

class AddSshKeysScaffolder(internal val spec: AddSshKeysSpec) : Scaffolder<AddSshKeys> {
    override fun createScaffold(): Scaffold<AddSshKeys> {
        val builder = AddSshKeysScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class AddSshKeysScaffoldBuilder(internal var scaffold: AddSshKeysScaffold = AddSshKeysScaffold()) : AddSshKeysBuilder {
    override fun fingerprint(value: String) {
        scaffold = scaffold.copy(fingerprints = scaffold.fingerprints.orEmpty() + ValueScaffold(value))
    }

    override fun fingerprints(fingerprints: List<String>) {
        scaffold = scaffold.copy(fingerprints = scaffold.fingerprints.orEmpty() + fingerprints.map { ValueScaffold(it) })
    }
}

internal data class AddSshKeysScaffold(var fingerprints: List<Scaffold<String>>? = null) : Scaffold<AddSshKeys> {
    override suspend fun resolve(registry: Registry): AddSshKeys {
        val value = AddSshKeys(
            fingerprints?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class WorkflowScaffolder(internal val spec: WorkflowSpec) : Scaffolder<Workflow> {
    override fun createScaffold(): Scaffold<Workflow> {
        val builder = WorkflowScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowScaffoldBuilder(internal var scaffold: WorkflowScaffold = WorkflowScaffold()) : WorkflowBuilder {
    override fun trigger(body: WorkflowTriggerBuilder.() -> Unit) {
        scaffold = scaffold.copy(triggers = scaffold.triggers.orEmpty() + WorkflowTriggerScaffolder(WorkflowTriggerSpec(body)).createScaffold())
    }

    override fun trigger(spec: WorkflowTriggerSpec) {
        scaffold = scaffold.copy(triggers = scaffold.triggers.orEmpty() + WorkflowTriggerScaffolder(spec).createScaffold())
    }

    override fun trigger(ref: WorkflowTriggerRef) {
        scaffold = scaffold.copy(triggers = scaffold.triggers.orEmpty() + RefScaffold(ref.key))
    }

    override fun trigger(value: WorkflowTrigger) {
        scaffold = scaffold.copy(triggers = scaffold.triggers.orEmpty() + ValueScaffold(value))
    }

    override fun triggers(triggers: List<WorkflowTrigger>) {
        scaffold = scaffold.copy(triggers = scaffold.triggers.orEmpty() + triggers.map { ValueScaffold(it) })
    }

    override fun jobs(key: String, body: WorkflowJobBuilder.() -> Unit) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),WorkflowJobScaffolder(WorkflowJobSpec(body)).createScaffold())))
    }

    override fun jobs(key: String, spec: WorkflowJobSpec) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),WorkflowJobScaffolder(spec).createScaffold())))
    }

    override fun jobs(key: String, ref: WorkflowJobRef) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),RefScaffold(ref.key))))
    }

    override fun jobs(key: String, value: WorkflowJob) {
        scaffold = scaffold.copy(jobs = scaffold.jobs.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(ValueScaffold(key),ValueScaffold(value))))
    }
}

internal data class WorkflowScaffold(var triggers: List<Scaffold<WorkflowTrigger>>? = null, var jobs: List<Map<Scaffold<String>, Scaffold<WorkflowJob>>>? = null) : Scaffold<Workflow> {
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
        val builder = WorkflowTriggerScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowTriggerScaffoldBuilder(internal var scaffold: WorkflowTriggerScaffold = WorkflowTriggerScaffold()) : WorkflowTriggerBuilder {
    override fun schedule(body: WorkflowTriggerScheduleBuilder.() -> Unit) {
        scaffold = scaffold.copy(schedule = WorkflowTriggerScheduleScaffolder(WorkflowTriggerScheduleSpec(body)).createScaffold())
    }

    override fun schedule(spec: WorkflowTriggerScheduleSpec) {
        scaffold = scaffold.copy(schedule = WorkflowTriggerScheduleScaffolder(spec).createScaffold())
    }

    override fun schedule(ref: WorkflowTriggerScheduleRef) {
        scaffold = scaffold.copy(schedule = RefScaffold(ref.key))
    }

    override fun schedule(value: WorkflowTriggerSchedule) {
        scaffold = scaffold.copy(schedule = ValueScaffold(value))
    }
}

internal data class WorkflowTriggerScaffold(var schedule: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTrigger> {
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
        val builder = WorkflowTriggerScheduleScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleScaffoldBuilder(internal var scaffold: WorkflowTriggerScheduleScaffold = WorkflowTriggerScheduleScaffold()) : WorkflowTriggerScheduleBuilder {
    override fun cron(value: String) {
        scaffold = scaffold.copy(cron = ValueScaffold(value))
    }

    override fun filters(body: WorkflowTriggerScheduleBuilder.() -> Unit) {
        scaffold = scaffold.copy(filters = WorkflowTriggerScheduleScaffolder(WorkflowTriggerScheduleSpec(body)).createScaffold())
    }

    override fun filters(spec: WorkflowTriggerScheduleSpec) {
        scaffold = scaffold.copy(filters = WorkflowTriggerScheduleScaffolder(spec).createScaffold())
    }

    override fun filters(ref: WorkflowTriggerScheduleRef) {
        scaffold = scaffold.copy(filters = RefScaffold(ref.key))
    }

    override fun filters(value: WorkflowTriggerSchedule) {
        scaffold = scaffold.copy(filters = ValueScaffold(value))
    }
}

internal data class WorkflowTriggerScheduleScaffold(var cron: Scaffold<String>? = null, var filters: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTriggerSchedule> {
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
        val builder = WorkflowTriggerScheduleFiltersScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowTriggerScheduleFiltersScaffoldBuilder(internal var scaffold: WorkflowTriggerScheduleFiltersScaffold = WorkflowTriggerScheduleFiltersScaffold()) : WorkflowTriggerScheduleFiltersBuilder {
    override fun branches(body: FilterBuilder.() -> Unit) {
        scaffold = scaffold.copy(branches = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun branches(spec: FilterSpec) {
        scaffold = scaffold.copy(branches = FilterScaffolder(spec).createScaffold())
    }

    override fun branches(ref: FilterRef) {
        scaffold = scaffold.copy(branches = RefScaffold(ref.key))
    }

    override fun branches(value: Filter) {
        scaffold = scaffold.copy(branches = ValueScaffold(value))
    }
}

internal data class WorkflowTriggerScheduleFiltersScaffold(var branches: Scaffold<Filter>? = null) : Scaffold<WorkflowTriggerScheduleFilters> {
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
        val builder = WorkflowJobScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowJobScaffoldBuilder(internal var scaffold: WorkflowJobScaffold = WorkflowJobScaffold()) : WorkflowJobBuilder {
    override fun require(value: String) {
        scaffold = scaffold.copy(requires = scaffold.requires.orEmpty() + ValueScaffold(value))
    }

    override fun requires(requires: List<String>) {
        scaffold = scaffold.copy(requires = scaffold.requires.orEmpty() + requires.map { ValueScaffold(it) })
    }

    override fun context(value: String) {
        scaffold = scaffold.copy(context = ValueScaffold(value))
    }

    override fun type(value: WorkflowJobType) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun filters(body: WorkflowJobFilterBuilder.() -> Unit) {
        scaffold = scaffold.copy(filters = WorkflowJobFilterScaffolder(WorkflowJobFilterSpec(body)).createScaffold())
    }

    override fun filters(spec: WorkflowJobFilterSpec) {
        scaffold = scaffold.copy(filters = WorkflowJobFilterScaffolder(spec).createScaffold())
    }

    override fun filters(ref: WorkflowJobFilterRef) {
        scaffold = scaffold.copy(filters = RefScaffold(ref.key))
    }

    override fun filters(value: WorkflowJobFilter) {
        scaffold = scaffold.copy(filters = ValueScaffold(value))
    }
}

internal data class WorkflowJobScaffold(
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
        val builder = WorkflowJobFilterScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkflowJobFilterScaffoldBuilder(internal var scaffold: WorkflowJobFilterScaffold = WorkflowJobFilterScaffold()) : WorkflowJobFilterBuilder {
    override fun branches(body: FilterBuilder.() -> Unit) {
        scaffold = scaffold.copy(branches = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun branches(spec: FilterSpec) {
        scaffold = scaffold.copy(branches = FilterScaffolder(spec).createScaffold())
    }

    override fun branches(ref: FilterRef) {
        scaffold = scaffold.copy(branches = RefScaffold(ref.key))
    }

    override fun branches(value: Filter) {
        scaffold = scaffold.copy(branches = ValueScaffold(value))
    }

    override fun tags(body: FilterBuilder.() -> Unit) {
        scaffold = scaffold.copy(tags = FilterScaffolder(FilterSpec(body)).createScaffold())
    }

    override fun tags(spec: FilterSpec) {
        scaffold = scaffold.copy(tags = FilterScaffolder(spec).createScaffold())
    }

    override fun tags(ref: FilterRef) {
        scaffold = scaffold.copy(tags = RefScaffold(ref.key))
    }

    override fun tags(value: Filter) {
        scaffold = scaffold.copy(tags = ValueScaffold(value))
    }
}

internal data class WorkflowJobFilterScaffold(var branches: Scaffold<Filter>? = null, var tags: Scaffold<Filter>? = null) : Scaffold<WorkflowJobFilter> {
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
        val builder = FilterScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class FilterScaffoldBuilder(internal var scaffold: FilterScaffold = FilterScaffold()) : FilterBuilder {
    override fun only(value: String) {
        scaffold = scaffold.copy(only = scaffold.only.orEmpty() + ValueScaffold(value))
    }

    override fun only(only: List<String>) {
        scaffold = scaffold.copy(only = scaffold.only.orEmpty() + only.map { ValueScaffold(it) })
    }

    override fun ignore(value: String) {
        scaffold = scaffold.copy(ignore = scaffold.ignore.orEmpty() + ValueScaffold(value))
    }

    override fun ignore(ignore: List<String>) {
        scaffold = scaffold.copy(ignore = scaffold.ignore.orEmpty() + ignore.map { ValueScaffold(it) })
    }
}

internal data class FilterScaffold(var only: List<Scaffold<String>>? = null, var ignore: List<Scaffold<String>>? = null) : Scaffold<Filter> {
    override suspend fun resolve(registry: Registry): Filter {
        val value = Filter(
            only?.let{ it.map { it.resolve(registry) } },
            ignore?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}
