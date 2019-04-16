// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.circleci

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.Boolean
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

class CircleCISpec(override val body: CircleCIBuilder.() -> Unit) : Spec<CircleCIBuilder>

@DslBuilder
interface CircleCIBuilder : Builder<CircleCISpec, CircleCIBuilder> {
    fun version(value: String)

    fun jobs(key: String, body: JobBuilder.() -> Unit)

    fun jobs(key: String, spec: JobSpec)

    fun jobs(key: String, ref: JobRef)

    fun jobs(key: String, value: Job)

    fun workflows(key: String, body: WorkflowBuilder.() -> Unit)

    fun workflows(key: String, spec: WorkflowSpec)

    fun workflows(key: String, ref: WorkflowRef)

    fun workflows(key: String, value: Workflow)
}

class CircleCIRef(internal val key: String)

class JobSpec(override val body: JobBuilder.() -> Unit) : Spec<JobBuilder>

@DslBuilder
interface JobBuilder : Builder<JobSpec, JobBuilder> {
    fun docker(body: DockerExecutorBuilder.() -> Unit)

    fun docker(spec: DockerExecutorSpec)

    fun docker(ref: DockerExecutorRef)

    fun docker(value: DockerExecutor)

    fun docker(docker: List<DockerExecutor>)

    fun resource_class(value: ResourceClass)

    fun machine(body: MachineExecutorBuilder.() -> Unit)

    fun machine(spec: MachineExecutorSpec)

    fun machine(ref: MachineExecutorRef)

    fun machine(value: MachineExecutor)

    fun macos(body: MacosExecutorBuilder.() -> Unit)

    fun macos(spec: MacosExecutorSpec)

    fun macos(ref: MacosExecutorRef)

    fun macos(value: MacosExecutor)

    fun shell(value: String)

    fun <T : Step> step(spec: StepSpec<T>)

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
}

class JobRef(internal val key: String)

class DockerExecutorSpec(override val body: DockerExecutorBuilder.() -> Unit) : Spec<DockerExecutorBuilder>

@DslBuilder
interface DockerExecutorBuilder : Builder<DockerExecutorSpec, DockerExecutorBuilder> {
    fun image(value: String)

    fun entrypoint(value: String)

    fun entrypoint(entrypoint: List<String>)

    fun command(value: String)

    fun command(command: List<String>)

    fun user(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun auth(body: AuthBuilder.() -> Unit)

    fun auth(spec: AuthSpec)

    fun auth(ref: AuthRef)

    fun auth(value: Auth)

    fun aws_auth(body: AwsAuthBuilder.() -> Unit)

    fun aws_auth(spec: AwsAuthSpec)

    fun aws_auth(ref: AwsAuthRef)

    fun aws_auth(value: AwsAuth)
}

class DockerExecutorRef(internal val key: String)

class AuthSpec(override val body: AuthBuilder.() -> Unit) : Spec<AuthBuilder>

@DslBuilder
interface AuthBuilder : Builder<AuthSpec, AuthBuilder> {
    fun username(value: String)

    fun password(value: String)
}

class AuthRef(internal val key: String)

class AwsAuthSpec(override val body: AwsAuthBuilder.() -> Unit) : Spec<AwsAuthBuilder>

@DslBuilder
interface AwsAuthBuilder : Builder<AwsAuthSpec, AwsAuthBuilder> {
    fun aws_access_key_id(value: String)

    fun aws_secret_access_key(value: String)
}

class AwsAuthRef(internal val key: String)

class MachineExecutorSpec(override val body: MachineExecutorBuilder.() -> Unit) : Spec<MachineExecutorBuilder>

@DslBuilder
interface MachineExecutorBuilder : Builder<MachineExecutorSpec, MachineExecutorBuilder> {
    fun enabled(value: Boolean)

    fun image(value: String)
}

class MachineExecutorRef(internal val key: String)

class MacosExecutorSpec(override val body: MacosExecutorBuilder.() -> Unit) : Spec<MacosExecutorBuilder>

@DslBuilder
interface MacosExecutorBuilder : Builder<MacosExecutorSpec, MacosExecutorBuilder> {
    fun xcode(value: String)
}

class MacosExecutorRef(internal val key: String)

sealed class StepSpec<out T : Step>

class RunStepSpec(override val body: RunStepBuilder.() -> Unit) : StepSpec<RunStep>(),
        Spec<RunStepBuilder>

class CheckoutStepSpec(override val body: CheckoutStepBuilder.() -> Unit) : StepSpec<CheckoutStep>(),
        Spec<CheckoutStepBuilder>

class SetupRemoteDockerStepSpec(override val body: SetupRemoteDockerStepBuilder.() -> Unit) : StepSpec<SetupRemoteDockerStep>(),
        Spec<SetupRemoteDockerStepBuilder>

class SaveCacheStepSpec(override val body: SaveCacheStepBuilder.() -> Unit) : StepSpec<SaveCacheStep>(),
        Spec<SaveCacheStepBuilder>

class RestoreCacheStepSpec(override val body: RestoreCacheStepBuilder.() -> Unit) : StepSpec<RestoreCacheStep>(),
        Spec<RestoreCacheStepBuilder>

class DeployStepSpec(override val body: DeployStepBuilder.() -> Unit) : StepSpec<DeployStep>(),
        Spec<DeployStepBuilder>

class StoreArtifactsStepSpec(override val body: StoreArtifactsStepBuilder.() -> Unit) : StepSpec<StoreArtifactsStep>(),
        Spec<StoreArtifactsStepBuilder>

class StoreTestResultsStepSpec(override val body: StoreTestResultsStepBuilder.() -> Unit) : StepSpec<StoreTestResultsStep>(),
        Spec<StoreTestResultsStepBuilder>

class PersistToWorkspaceStepSpec(override val body: PersistToWorkspaceStepBuilder.() -> Unit) : StepSpec<PersistToWorkspaceStep>(),
        Spec<PersistToWorkspaceStepBuilder>

class AttachWorkspaceStepSpec(override val body: AttachWorkspaceStepBuilder.() -> Unit) : StepSpec<AttachWorkspaceStep>(),
        Spec<AttachWorkspaceStepBuilder>

class AddSshKeysStepSpec(override val body: AddSshKeysStepBuilder.() -> Unit) : StepSpec<AddSshKeysStep>(),
        Spec<AddSshKeysStepBuilder>

@DslBuilder
interface RunStepBuilder : Builder<RunStepSpec, RunStepBuilder> {
    fun run(body: RunBuilder.() -> Unit)

    fun run(spec: RunSpec)

    fun run(ref: RunRef)

    fun run(value: Run)
}

@DslBuilder
interface CheckoutStepBuilder : Builder<CheckoutStepSpec, CheckoutStepBuilder> {
    fun checkout(body: CheckoutBuilder.() -> Unit)

    fun checkout(spec: CheckoutSpec)

    fun checkout(ref: CheckoutRef)

    fun checkout(value: Checkout)
}

@DslBuilder
interface SetupRemoteDockerStepBuilder : Builder<SetupRemoteDockerStepSpec, SetupRemoteDockerStepBuilder> {
    fun setup_remote_docker(body: SetupRemoteDockerBuilder.() -> Unit)

    fun setup_remote_docker(spec: SetupRemoteDockerSpec)

    fun setup_remote_docker(ref: SetupRemoteDockerRef)

    fun setup_remote_docker(value: SetupRemoteDocker)
}

@DslBuilder
interface SaveCacheStepBuilder : Builder<SaveCacheStepSpec, SaveCacheStepBuilder> {
    fun save_cache(body: SaveCacheBuilder.() -> Unit)

    fun save_cache(spec: SaveCacheSpec)

    fun save_cache(ref: SaveCacheRef)

    fun save_cache(value: SaveCache)
}

@DslBuilder
interface RestoreCacheStepBuilder : Builder<RestoreCacheStepSpec, RestoreCacheStepBuilder> {
    fun restore_cache(body: RestoreCacheBuilder.() -> Unit)

    fun restore_cache(spec: RestoreCacheSpec)

    fun restore_cache(ref: RestoreCacheRef)

    fun restore_cache(value: RestoreCache)
}

@DslBuilder
interface DeployStepBuilder : Builder<DeployStepSpec, DeployStepBuilder> {
    fun deploy(body: RunBuilder.() -> Unit)

    fun deploy(spec: RunSpec)

    fun deploy(ref: RunRef)

    fun deploy(value: Run)
}

@DslBuilder
interface StoreArtifactsStepBuilder : Builder<StoreArtifactsStepSpec, StoreArtifactsStepBuilder> {
    fun store_artifacts(body: StoreArtifactsBuilder.() -> Unit)

    fun store_artifacts(spec: StoreArtifactsSpec)

    fun store_artifacts(ref: StoreArtifactsRef)

    fun store_artifacts(value: StoreArtifacts)
}

@DslBuilder
interface StoreTestResultsStepBuilder : Builder<StoreTestResultsStepSpec, StoreTestResultsStepBuilder> {
    fun store_test_results(body: StoreTestResultsBuilder.() -> Unit)

    fun store_test_results(spec: StoreTestResultsSpec)

    fun store_test_results(ref: StoreTestResultsRef)

    fun store_test_results(value: StoreTestResults)
}

@DslBuilder
interface PersistToWorkspaceStepBuilder : Builder<PersistToWorkspaceStepSpec, PersistToWorkspaceStepBuilder> {
    fun persist_to_workspace(body: PersistToWorkspaceBuilder.() -> Unit)

    fun persist_to_workspace(spec: PersistToWorkspaceSpec)

    fun persist_to_workspace(ref: PersistToWorkspaceRef)

    fun persist_to_workspace(value: PersistToWorkspace)
}

@DslBuilder
interface AttachWorkspaceStepBuilder : Builder<AttachWorkspaceStepSpec, AttachWorkspaceStepBuilder> {
    fun attach_workspace(body: AttachWorkspaceBuilder.() -> Unit)

    fun attach_workspace(spec: AttachWorkspaceSpec)

    fun attach_workspace(ref: AttachWorkspaceRef)

    fun attach_workspace(value: AttachWorkspace)
}

@DslBuilder
interface AddSshKeysStepBuilder : Builder<AddSshKeysStepSpec, AddSshKeysStepBuilder> {
    fun add_ssh_keys(body: AddSshKeysBuilder.() -> Unit)

    fun add_ssh_keys(spec: AddSshKeysSpec)

    fun add_ssh_keys(ref: AddSshKeysRef)

    fun add_ssh_keys(value: AddSshKeys)
}

sealed class StepRef<T : Step> {
    internal abstract val key: String
}

class RunStepRef(override val key: String) : StepRef<RunStep>()

class CheckoutStepRef(override val key: String) : StepRef<CheckoutStep>()

class SetupRemoteDockerStepRef(override val key: String) : StepRef<SetupRemoteDockerStep>()

class SaveCacheStepRef(override val key: String) : StepRef<SaveCacheStep>()

class RestoreCacheStepRef(override val key: String) : StepRef<RestoreCacheStep>()

class DeployStepRef(override val key: String) : StepRef<DeployStep>()

class StoreArtifactsStepRef(override val key: String) : StepRef<StoreArtifactsStep>()

class StoreTestResultsStepRef(override val key: String) : StepRef<StoreTestResultsStep>()

class PersistToWorkspaceStepRef(override val key: String) : StepRef<PersistToWorkspaceStep>()

class AttachWorkspaceStepRef(override val key: String) : StepRef<AttachWorkspaceStep>()

class AddSshKeysStepRef(override val key: String) : StepRef<AddSshKeysStep>()

class RunSpec(override val body: RunBuilder.() -> Unit) : Spec<RunBuilder>

@DslBuilder
interface RunBuilder : Builder<RunSpec, RunBuilder> {
    fun name(value: String)

    fun command(value: String)

    fun shell(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun background(value: Boolean)

    fun working_directory(value: String)

    fun no_output_timeout(value: String)

    fun `when`(value: When)
}

class RunRef(internal val key: String)

class CheckoutSpec(override val body: CheckoutBuilder.() -> Unit) : Spec<CheckoutBuilder>

@DslBuilder
interface CheckoutBuilder : Builder<CheckoutSpec, CheckoutBuilder> {
    fun path(value: String)
}

class CheckoutRef(internal val key: String)

class SetupRemoteDockerSpec(override val body: SetupRemoteDockerBuilder.() -> Unit) : Spec<SetupRemoteDockerBuilder>

@DslBuilder
interface SetupRemoteDockerBuilder : Builder<SetupRemoteDockerSpec, SetupRemoteDockerBuilder> {
    fun docker_layer_caching(value: Boolean)

    fun version(value: String)
}

class SetupRemoteDockerRef(internal val key: String)

class SaveCacheSpec(override val body: SaveCacheBuilder.() -> Unit) : Spec<SaveCacheBuilder>

@DslBuilder
interface SaveCacheBuilder : Builder<SaveCacheSpec, SaveCacheBuilder> {
    fun path(value: String)

    fun paths(paths: List<String>)

    fun key(value: String)

    fun `when`(value: When)

    fun name(value: String)
}

class SaveCacheRef(internal val key: String)

class RestoreCacheSpec(override val body: RestoreCacheBuilder.() -> Unit) : Spec<RestoreCacheBuilder>

@DslBuilder
interface RestoreCacheBuilder : Builder<RestoreCacheSpec, RestoreCacheBuilder> {
    fun key(value: String)

    fun keys(keys: List<String>)

    fun name(value: String)
}

class RestoreCacheRef(internal val key: String)

class StoreArtifactsSpec(override val body: StoreArtifactsBuilder.() -> Unit) : Spec<StoreArtifactsBuilder>

@DslBuilder
interface StoreArtifactsBuilder : Builder<StoreArtifactsSpec, StoreArtifactsBuilder> {
    fun path(value: String)

    fun destination(value: String)
}

class StoreArtifactsRef(internal val key: String)

class StoreTestResultsSpec(override val body: StoreTestResultsBuilder.() -> Unit) : Spec<StoreTestResultsBuilder>

@DslBuilder
interface StoreTestResultsBuilder : Builder<StoreTestResultsSpec, StoreTestResultsBuilder> {
    fun path(value: String)
}

class StoreTestResultsRef(internal val key: String)

class PersistToWorkspaceSpec(override val body: PersistToWorkspaceBuilder.() -> Unit) : Spec<PersistToWorkspaceBuilder>

@DslBuilder
interface PersistToWorkspaceBuilder : Builder<PersistToWorkspaceSpec, PersistToWorkspaceBuilder> {
    fun root(value: String)

    fun path(value: String)

    fun paths(paths: List<String>)
}

class PersistToWorkspaceRef(internal val key: String)

class AttachWorkspaceSpec(override val body: AttachWorkspaceBuilder.() -> Unit) : Spec<AttachWorkspaceBuilder>

@DslBuilder
interface AttachWorkspaceBuilder : Builder<AttachWorkspaceSpec, AttachWorkspaceBuilder> {
    fun at(value: String)
}

class AttachWorkspaceRef(internal val key: String)

class AddSshKeysSpec(override val body: AddSshKeysBuilder.() -> Unit) : Spec<AddSshKeysBuilder>

@DslBuilder
interface AddSshKeysBuilder : Builder<AddSshKeysSpec, AddSshKeysBuilder> {
    fun fingerprint(value: String)

    fun fingerprints(fingerprints: List<String>)
}

class AddSshKeysRef(internal val key: String)

class WorkflowSpec(override val body: WorkflowBuilder.() -> Unit) : Spec<WorkflowBuilder>

@DslBuilder
interface WorkflowBuilder : Builder<WorkflowSpec, WorkflowBuilder> {
    fun trigger(body: WorkflowTriggerBuilder.() -> Unit)

    fun trigger(spec: WorkflowTriggerSpec)

    fun trigger(ref: WorkflowTriggerRef)

    fun trigger(value: WorkflowTrigger)

    fun triggers(triggers: List<WorkflowTrigger>)

    fun jobs(key: String, body: WorkflowJobBuilder.() -> Unit)

    fun jobs(key: String, spec: WorkflowJobSpec)

    fun jobs(key: String, ref: WorkflowJobRef)

    fun jobs(key: String, value: WorkflowJob)
}

class WorkflowRef(internal val key: String)

class WorkflowTriggerSpec(override val body: WorkflowTriggerBuilder.() -> Unit) : Spec<WorkflowTriggerBuilder>

@DslBuilder
interface WorkflowTriggerBuilder : Builder<WorkflowTriggerSpec, WorkflowTriggerBuilder> {
    fun schedule(body: WorkflowTriggerScheduleBuilder.() -> Unit)

    fun schedule(spec: WorkflowTriggerScheduleSpec)

    fun schedule(ref: WorkflowTriggerScheduleRef)

    fun schedule(value: WorkflowTriggerSchedule)
}

class WorkflowTriggerRef(internal val key: String)

class WorkflowTriggerScheduleSpec(override val body: WorkflowTriggerScheduleBuilder.() -> Unit) : Spec<WorkflowTriggerScheduleBuilder>

@DslBuilder
interface WorkflowTriggerScheduleBuilder : Builder<WorkflowTriggerScheduleSpec, WorkflowTriggerScheduleBuilder> {
    fun cron(value: String)

    fun filters(body: WorkflowTriggerScheduleBuilder.() -> Unit)

    fun filters(spec: WorkflowTriggerScheduleSpec)

    fun filters(ref: WorkflowTriggerScheduleRef)

    fun filters(value: WorkflowTriggerSchedule)
}

class WorkflowTriggerScheduleRef(internal val key: String)

class WorkflowTriggerScheduleFiltersSpec(override val body: WorkflowTriggerScheduleFiltersBuilder.() -> Unit) : Spec<WorkflowTriggerScheduleFiltersBuilder>

@DslBuilder
interface WorkflowTriggerScheduleFiltersBuilder : Builder<WorkflowTriggerScheduleFiltersSpec, WorkflowTriggerScheduleFiltersBuilder> {
    fun branches(body: FilterBuilder.() -> Unit)

    fun branches(spec: FilterSpec)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)
}

class WorkflowTriggerScheduleFiltersRef(internal val key: String)

class WorkflowJobSpec(override val body: WorkflowJobBuilder.() -> Unit) : Spec<WorkflowJobBuilder>

@DslBuilder
interface WorkflowJobBuilder : Builder<WorkflowJobSpec, WorkflowJobBuilder> {
    fun require(value: String)

    fun requires(requires: List<String>)

    fun context(value: String)

    fun type(value: WorkflowJobType)

    fun filters(body: WorkflowJobFilterBuilder.() -> Unit)

    fun filters(spec: WorkflowJobFilterSpec)

    fun filters(ref: WorkflowJobFilterRef)

    fun filters(value: WorkflowJobFilter)
}

class WorkflowJobRef(internal val key: String)

class WorkflowJobFilterSpec(override val body: WorkflowJobFilterBuilder.() -> Unit) : Spec<WorkflowJobFilterBuilder>

@DslBuilder
interface WorkflowJobFilterBuilder : Builder<WorkflowJobFilterSpec, WorkflowJobFilterBuilder> {
    fun branches(body: FilterBuilder.() -> Unit)

    fun branches(spec: FilterSpec)

    fun branches(ref: FilterRef)

    fun branches(value: Filter)

    fun tags(body: FilterBuilder.() -> Unit)

    fun tags(spec: FilterSpec)

    fun tags(ref: FilterRef)

    fun tags(value: Filter)
}

class WorkflowJobFilterRef(internal val key: String)

class FilterSpec(override val body: FilterBuilder.() -> Unit) : Spec<FilterBuilder>

@DslBuilder
interface FilterBuilder : Builder<FilterSpec, FilterBuilder> {
    fun only(value: String)

    fun only(only: List<String>)

    fun ignore(value: String)

    fun ignore(ignore: List<String>)
}

class FilterRef(internal val key: String)
