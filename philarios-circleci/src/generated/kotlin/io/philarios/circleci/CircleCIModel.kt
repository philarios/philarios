// The model of your schema written as pure Kotlin classes.
//
// Because the model expresses the high-level domain, nothing in this file will depend on the generator or on any
// of the other files. This is done to ensure that you could potentially take this file and reuse the classes
// without having a dependency on the specs or materialization process.
//
// If you feel like something is preventing you from separating the model classes from the specific specs, builders,
// or materialization, please feel free to open an issue in the project's repository.
package io.philarios.circleci

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

data class CircleCI(
        val version: String?,
        val jobs: Map<String, Job>?,
        val workflows: Map<String, Workflow>?
)

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

data class DockerExecutor(
        val image: String?,
        val entrypoint: List<String>?,
        val command: List<String>?,
        val user: String?,
        val environment: Map<String, String>?,
        val auth: Auth?,
        val aws_auth: AwsAuth?
)

data class Auth(val username: String?, val password: String?)

data class AwsAuth(val aws_access_key_id: String?, val aws_secret_access_key: String?)

enum class ResourceClass {
    small,

    medium,

    `medium+`,

    large,

    xlarge
}

data class MachineExecutor(val enabled: Boolean?, val image: String?)

data class MacosExecutor(val xcode: String?)

sealed class Step

data class RunStep(val run: Run?) : Step()

data class CheckoutStep(val checkout: Checkout?) : Step()

data class SetupRemoteDockerStep(val setup_remote_docker: SetupRemoteDocker?) : Step()

data class SaveCacheStep(val save_cache: SaveCache?) : Step()

data class RestoreCacheStep(val restore_cache: RestoreCache?) : Step()

data class DeployStep(val deploy: Run?) : Step()

data class StoreArtifactsStep(val store_artifacts: StoreArtifacts?) : Step()

data class StoreTestResultsStep(val store_test_results: StoreTestResults?) : Step()

data class PersistToWorkspaceStep(val persist_to_workspace: PersistToWorkspace?) : Step()

data class AttachWorkspaceStep(val attach_workspace: AttachWorkspace?) : Step()

data class AddSshKeysStep(val add_ssh_keys: AddSshKeys?) : Step()

data class Run(
        val name: String?,
        val command: String?,
        val shell: String?,
        val environment: Map<String, String>?,
        val background: Boolean?,
        val working_directory: String?,
        val no_output_timeout: String?,
        val `when`: When?
)

data class Checkout(val path: String?)

data class SetupRemoteDocker(val docker_layer_caching: Boolean?, val version: String?)

data class SaveCache(
        val paths: List<String>?,
        val key: String?,
        val `when`: When?,
        val name: String?
)

data class RestoreCache(val keys: List<String>?, val name: String?)

data class StoreArtifacts(val path: String?, val destination: String?)

data class StoreTestResults(val path: String?)

data class PersistToWorkspace(val root: String?, val paths: List<String>?)

data class AttachWorkspace(val at: String?)

data class AddSshKeys(val fingerprints: List<String>?)

enum class When {
    always,

    on_success,

    on_fail
}

data class Workflow(val triggers: List<WorkflowTrigger>?, val jobs: List<Map<String, WorkflowJob>>?)

data class WorkflowTrigger(val schedule: WorkflowTriggerSchedule?)

data class WorkflowTriggerSchedule(val cron: String?, val filters: WorkflowTriggerSchedule?)

data class WorkflowTriggerScheduleFilters(val branches: Filter?)

data class WorkflowJob(
        val requires: List<String>?,
        val context: String?,
        val type: WorkflowJobType?,
        val filters: WorkflowJobFilter?
)

enum class WorkflowJobType {
    approval
}

data class WorkflowJobFilter(val branches: Filter?, val tags: Filter?)

data class Filter(val only: List<String>?, val ignore: List<String>?)
