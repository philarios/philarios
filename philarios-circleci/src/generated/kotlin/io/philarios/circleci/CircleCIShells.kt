package io.philarios.circleci

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class CircleCIShell(
        var version: Scaffold<String>? = null,
        var jobs: Map<Scaffold<String>, Scaffold<Job>>? = null,
        var workflows: Map<Scaffold<String>, Scaffold<Workflow>>? = null
) : Scaffold<CircleCI> {
    override suspend fun resolve(registry: Registry): CircleCI {
        checkNotNull(version) { "CircleCI is missing the version property" }
        coroutineScope {
            jobs?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
            workflows?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
        }
        val value = CircleCI(
            version!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            workflows.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
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
        checkNotNull(resource_class) { "Job is missing the resource_class property" }
        checkNotNull(machine) { "Job is missing the machine property" }
        checkNotNull(macos) { "Job is missing the macos property" }
        checkNotNull(shell) { "Job is missing the shell property" }
        checkNotNull(working_directory) { "Job is missing the working_directory property" }
        checkNotNull(parallelism) { "Job is missing the parallelism property" }
        coroutineScope {
            docker?.let{ it.forEach { launch { it.resolve(registry) } } }
            machine?.let{ launch { it.resolve(registry) } }
            macos?.let{ launch { it.resolve(registry) } }
            steps?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Job(
            docker.orEmpty().let{ it.map { it.resolve(registry) } },
            resource_class!!.let{ it.resolve(registry) },
            machine!!.let{ it.resolve(registry) },
            macos!!.let{ it.resolve(registry) },
            shell!!.let{ it.resolve(registry) },
            steps.orEmpty().let{ it.map { it.resolve(registry) } },
            working_directory!!.let{ it.resolve(registry) },
            parallelism!!.let{ it.resolve(registry) },
            environment.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            branches.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
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
        checkNotNull(image) { "DockerExecutor is missing the image property" }
        checkNotNull(user) { "DockerExecutor is missing the user property" }
        checkNotNull(auth) { "DockerExecutor is missing the auth property" }
        checkNotNull(aws_auth) { "DockerExecutor is missing the aws_auth property" }
        coroutineScope {
            auth?.let{ launch { it.resolve(registry) } }
            aws_auth?.let{ launch { it.resolve(registry) } }
        }
        val value = DockerExecutor(
            image!!.let{ it.resolve(registry) },
            entrypoint.orEmpty().let{ it.map { it.resolve(registry) } },
            command.orEmpty().let{ it.map { it.resolve(registry) } },
            user!!.let{ it.resolve(registry) },
            environment.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            auth!!.let{ it.resolve(registry) },
            aws_auth!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AuthShell(var username: Scaffold<String>? = null, var password: Scaffold<String>? = null) : Scaffold<Auth> {
    override suspend fun resolve(registry: Registry): Auth {
        checkNotNull(username) { "Auth is missing the username property" }
        checkNotNull(password) { "Auth is missing the password property" }
        val value = Auth(
            username!!.let{ it.resolve(registry) },
            password!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AwsAuthShell(var aws_access_key_id: Scaffold<String>? = null, var aws_secret_access_key: Scaffold<String>? = null) : Scaffold<AwsAuth> {
    override suspend fun resolve(registry: Registry): AwsAuth {
        checkNotNull(aws_access_key_id) { "AwsAuth is missing the aws_access_key_id property" }
        checkNotNull(aws_secret_access_key) { "AwsAuth is missing the aws_secret_access_key property" }
        val value = AwsAuth(
            aws_access_key_id!!.let{ it.resolve(registry) },
            aws_secret_access_key!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class MachineExecutorShell(var enabled: Scaffold<Boolean>? = null, var image: Scaffold<String>? = null) : Scaffold<MachineExecutor> {
    override suspend fun resolve(registry: Registry): MachineExecutor {
        checkNotNull(enabled) { "MachineExecutor is missing the enabled property" }
        checkNotNull(image) { "MachineExecutor is missing the image property" }
        val value = MachineExecutor(
            enabled!!.let{ it.resolve(registry) },
            image!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class MacosExecutorShell(var xcode: Scaffold<String>? = null) : Scaffold<MacosExecutor> {
    override suspend fun resolve(registry: Registry): MacosExecutor {
        checkNotNull(xcode) { "MacosExecutor is missing the xcode property" }
        val value = MacosExecutor(
            xcode!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal sealed class StepShell

internal data class RunStepShell(var run: Scaffold<Run>? = null) : StepShell(), Scaffold<RunStep> {
    override suspend fun resolve(registry: Registry): RunStep {
        checkNotNull(run) { "RunStep is missing the run property" }
        coroutineScope {
            run?.let{ launch { it.resolve(registry) } }
        }
        val value = RunStep(
            run!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class CheckoutStepShell(var checkout: Scaffold<Checkout>? = null) : StepShell(),
        Scaffold<CheckoutStep> {
    override suspend fun resolve(registry: Registry): CheckoutStep {
        checkNotNull(checkout) { "CheckoutStep is missing the checkout property" }
        coroutineScope {
            checkout?.let{ launch { it.resolve(registry) } }
        }
        val value = CheckoutStep(
            checkout!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SetupRemoteDockerStepShell(var setup_remote_docker: Scaffold<SetupRemoteDocker>? = null) : StepShell(),
        Scaffold<SetupRemoteDockerStep> {
    override suspend fun resolve(registry: Registry): SetupRemoteDockerStep {
        checkNotNull(setup_remote_docker) { "SetupRemoteDockerStep is missing the setup_remote_docker property" }
        coroutineScope {
            setup_remote_docker?.let{ launch { it.resolve(registry) } }
        }
        val value = SetupRemoteDockerStep(
            setup_remote_docker!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SaveCacheStepShell(var save_cache: Scaffold<SaveCache>? = null) : StepShell(),
        Scaffold<SaveCacheStep> {
    override suspend fun resolve(registry: Registry): SaveCacheStep {
        checkNotNull(save_cache) { "SaveCacheStep is missing the save_cache property" }
        coroutineScope {
            save_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = SaveCacheStep(
            save_cache!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class RestoreCacheStepShell(var restore_cache: Scaffold<RestoreCache>? = null) : StepShell(),
        Scaffold<RestoreCacheStep> {
    override suspend fun resolve(registry: Registry): RestoreCacheStep {
        checkNotNull(restore_cache) { "RestoreCacheStep is missing the restore_cache property" }
        coroutineScope {
            restore_cache?.let{ launch { it.resolve(registry) } }
        }
        val value = RestoreCacheStep(
            restore_cache!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DeployStepShell(var deploy: Scaffold<Run>? = null) : StepShell(),
        Scaffold<DeployStep> {
    override suspend fun resolve(registry: Registry): DeployStep {
        checkNotNull(deploy) { "DeployStep is missing the deploy property" }
        coroutineScope {
            deploy?.let{ launch { it.resolve(registry) } }
        }
        val value = DeployStep(
            deploy!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreArtifactsStepShell(var store_artifacts: Scaffold<StoreArtifacts>? = null) : StepShell(),
        Scaffold<StoreArtifactsStep> {
    override suspend fun resolve(registry: Registry): StoreArtifactsStep {
        checkNotNull(store_artifacts) { "StoreArtifactsStep is missing the store_artifacts property" }
        coroutineScope {
            store_artifacts?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreArtifactsStep(
            store_artifacts!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreTestResultsStepShell(var store_test_results: Scaffold<StoreTestResults>? = null) : StepShell(),
        Scaffold<StoreTestResultsStep> {
    override suspend fun resolve(registry: Registry): StoreTestResultsStep {
        checkNotNull(store_test_results) { "StoreTestResultsStep is missing the store_test_results property" }
        coroutineScope {
            store_test_results?.let{ launch { it.resolve(registry) } }
        }
        val value = StoreTestResultsStep(
            store_test_results!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PersistToWorkspaceStepShell(var persist_to_workspace: Scaffold<PersistToWorkspace>? = null) : StepShell(),
        Scaffold<PersistToWorkspaceStep> {
    override suspend fun resolve(registry: Registry): PersistToWorkspaceStep {
        checkNotNull(persist_to_workspace) { "PersistToWorkspaceStep is missing the persist_to_workspace property" }
        coroutineScope {
            persist_to_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = PersistToWorkspaceStep(
            persist_to_workspace!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AttachWorkspaceStepShell(var attach_workspace: Scaffold<AttachWorkspace>? = null) : StepShell(),
        Scaffold<AttachWorkspaceStep> {
    override suspend fun resolve(registry: Registry): AttachWorkspaceStep {
        checkNotNull(attach_workspace) { "AttachWorkspaceStep is missing the attach_workspace property" }
        coroutineScope {
            attach_workspace?.let{ launch { it.resolve(registry) } }
        }
        val value = AttachWorkspaceStep(
            attach_workspace!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AddSshKeysStepShell(var add_ssh_keys: Scaffold<AddSshKeys>? = null) : StepShell(),
        Scaffold<AddSshKeysStep> {
    override suspend fun resolve(registry: Registry): AddSshKeysStep {
        checkNotNull(add_ssh_keys) { "AddSshKeysStep is missing the add_ssh_keys property" }
        coroutineScope {
            add_ssh_keys?.let{ launch { it.resolve(registry) } }
        }
        val value = AddSshKeysStep(
            add_ssh_keys!!.let{ it.resolve(registry) }
        )
        return value
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
        checkNotNull(name) { "Run is missing the name property" }
        checkNotNull(command) { "Run is missing the command property" }
        checkNotNull(shell) { "Run is missing the shell property" }
        checkNotNull(background) { "Run is missing the background property" }
        checkNotNull(working_directory) { "Run is missing the working_directory property" }
        checkNotNull(no_output_timeout) { "Run is missing the no_output_timeout property" }
        checkNotNull(`when`) { "Run is missing the when property" }
        val value = Run(
            name!!.let{ it.resolve(registry) },
            command!!.let{ it.resolve(registry) },
            shell!!.let{ it.resolve(registry) },
            environment.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            background!!.let{ it.resolve(registry) },
            working_directory!!.let{ it.resolve(registry) },
            no_output_timeout!!.let{ it.resolve(registry) },
            `when`!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class CheckoutShell(var path: Scaffold<String>? = null) : Scaffold<Checkout> {
    override suspend fun resolve(registry: Registry): Checkout {
        checkNotNull(path) { "Checkout is missing the path property" }
        val value = Checkout(
            path!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SetupRemoteDockerShell(var docker_layer_caching: Scaffold<Boolean>? = null, var version: Scaffold<String>? = null) : Scaffold<SetupRemoteDocker> {
    override suspend fun resolve(registry: Registry): SetupRemoteDocker {
        checkNotNull(docker_layer_caching) { "SetupRemoteDocker is missing the docker_layer_caching property" }
        checkNotNull(version) { "SetupRemoteDocker is missing the version property" }
        val value = SetupRemoteDocker(
            docker_layer_caching!!.let{ it.resolve(registry) },
            version!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SaveCacheShell(
        var paths: List<Scaffold<String>>? = null,
        var key: Scaffold<String>? = null,
        var `when`: Scaffold<When>? = null,
        var name: Scaffold<String>? = null
) : Scaffold<SaveCache> {
    override suspend fun resolve(registry: Registry): SaveCache {
        checkNotNull(key) { "SaveCache is missing the key property" }
        checkNotNull(`when`) { "SaveCache is missing the when property" }
        checkNotNull(name) { "SaveCache is missing the name property" }
        val value = SaveCache(
            paths.orEmpty().let{ it.map { it.resolve(registry) } },
            key!!.let{ it.resolve(registry) },
            `when`!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class RestoreCacheShell(var keys: List<Scaffold<String>>? = null, var name: Scaffold<String>? = null) : Scaffold<RestoreCache> {
    override suspend fun resolve(registry: Registry): RestoreCache {
        checkNotNull(name) { "RestoreCache is missing the name property" }
        val value = RestoreCache(
            keys.orEmpty().let{ it.map { it.resolve(registry) } },
            name!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreArtifactsShell(var path: Scaffold<String>? = null, var destination: Scaffold<String>? = null) : Scaffold<StoreArtifacts> {
    override suspend fun resolve(registry: Registry): StoreArtifacts {
        checkNotNull(path) { "StoreArtifacts is missing the path property" }
        checkNotNull(destination) { "StoreArtifacts is missing the destination property" }
        val value = StoreArtifacts(
            path!!.let{ it.resolve(registry) },
            destination!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StoreTestResultsShell(var path: Scaffold<String>? = null) : Scaffold<StoreTestResults> {
    override suspend fun resolve(registry: Registry): StoreTestResults {
        checkNotNull(path) { "StoreTestResults is missing the path property" }
        val value = StoreTestResults(
            path!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PersistToWorkspaceShell(var root: Scaffold<String>? = null, var paths: List<Scaffold<String>>? = null) : Scaffold<PersistToWorkspace> {
    override suspend fun resolve(registry: Registry): PersistToWorkspace {
        checkNotNull(root) { "PersistToWorkspace is missing the root property" }
        val value = PersistToWorkspace(
            root!!.let{ it.resolve(registry) },
            paths.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class AttachWorkspaceShell(var at: Scaffold<String>? = null) : Scaffold<AttachWorkspace> {
    override suspend fun resolve(registry: Registry): AttachWorkspace {
        checkNotNull(at) { "AttachWorkspace is missing the at property" }
        val value = AttachWorkspace(
            at!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AddSshKeysShell(var fingerprints: List<Scaffold<String>>? = null) : Scaffold<AddSshKeys> {
    override suspend fun resolve(registry: Registry): AddSshKeys {
        val value = AddSshKeys(
            fingerprints.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class WorkflowShell(var triggers: List<Scaffold<WorkflowTrigger>>? = null, var jobs: List<Map<Scaffold<String>, Scaffold<WorkflowJob>>>? = null) : Scaffold<Workflow> {
    override suspend fun resolve(registry: Registry): Workflow {
        coroutineScope {
            triggers?.let{ it.forEach { launch { it.resolve(registry) } } }
            jobs?.let{ it.forEach { it.forEach { it.value.let { launch { it.resolve(registry) } } } } }
        }
        val value = Workflow(
            triggers.orEmpty().let{ it.map { it.resolve(registry) } },
            jobs.orEmpty().let{ it.map { it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() } }
        )
        return value
    }
}

internal data class WorkflowTriggerShell(var schedule: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTrigger> {
    override suspend fun resolve(registry: Registry): WorkflowTrigger {
        checkNotNull(schedule) { "WorkflowTrigger is missing the schedule property" }
        coroutineScope {
            schedule?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTrigger(
            schedule!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class WorkflowTriggerScheduleShell(var cron: Scaffold<String>? = null, var filters: Scaffold<WorkflowTriggerSchedule>? = null) : Scaffold<WorkflowTriggerSchedule> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerSchedule {
        checkNotNull(cron) { "WorkflowTriggerSchedule is missing the cron property" }
        checkNotNull(filters) { "WorkflowTriggerSchedule is missing the filters property" }
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerSchedule(
            cron!!.let{ it.resolve(registry) },
            filters!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class WorkflowTriggerScheduleFiltersShell(var branches: Scaffold<Filter>? = null) : Scaffold<WorkflowTriggerScheduleFilters> {
    override suspend fun resolve(registry: Registry): WorkflowTriggerScheduleFilters {
        checkNotNull(branches) { "WorkflowTriggerScheduleFilters is missing the branches property" }
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowTriggerScheduleFilters(
            branches!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class WorkflowJobShell(
        var requires: List<Scaffold<String>>? = null,
        var context: Scaffold<String>? = null,
        var type: Scaffold<WorkflowJobType>? = null,
        var filters: Scaffold<WorkflowJobFilter>? = null
) : Scaffold<WorkflowJob> {
    override suspend fun resolve(registry: Registry): WorkflowJob {
        checkNotNull(context) { "WorkflowJob is missing the context property" }
        checkNotNull(type) { "WorkflowJob is missing the type property" }
        checkNotNull(filters) { "WorkflowJob is missing the filters property" }
        coroutineScope {
            filters?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJob(
            requires.orEmpty().let{ it.map { it.resolve(registry) } },
            context!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            filters!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class WorkflowJobFilterShell(var branches: Scaffold<Filter>? = null, var tags: Scaffold<Filter>? = null) : Scaffold<WorkflowJobFilter> {
    override suspend fun resolve(registry: Registry): WorkflowJobFilter {
        checkNotNull(branches) { "WorkflowJobFilter is missing the branches property" }
        checkNotNull(tags) { "WorkflowJobFilter is missing the tags property" }
        coroutineScope {
            branches?.let{ launch { it.resolve(registry) } }
            tags?.let{ launch { it.resolve(registry) } }
        }
        val value = WorkflowJobFilter(
            branches!!.let{ it.resolve(registry) },
            tags!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class FilterShell(var only: List<Scaffold<String>>? = null, var ignore: List<Scaffold<String>>? = null) : Scaffold<Filter> {
    override suspend fun resolve(registry: Registry): Filter {
        val value = Filter(
            only.orEmpty().let{ it.map { it.resolve(registry) } },
            ignore.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}
