package io.philarios.circleci

class CircleCISpec<in C>(internal val body: CircleCIBuilder<C>.() -> Unit)

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

class DockerExecutorSpec<in C>(internal val body: DockerExecutorBuilder<C>.() -> Unit)

class AuthSpec<in C>(internal val body: AuthBuilder<C>.() -> Unit)

class AwsAuthSpec<in C>(internal val body: AwsAuthBuilder<C>.() -> Unit)

class MachineExecutorSpec<in C>(internal val body: MachineExecutorBuilder<C>.() -> Unit)

class MacosExecutorSpec<in C>(internal val body: MacosExecutorBuilder<C>.() -> Unit)

sealed class StepSpec<in C, out T : Step>

class RunStepSpec<in C>(internal val body: RunStepBuilder<C>.() -> Unit) : StepSpec<C, RunStep>()

class CheckoutStepSpec<in C>(internal val body: CheckoutStepBuilder<C>.() -> Unit) : StepSpec<C, CheckoutStep>()

class SetupRemoteDockerStepSpec<in C>(internal val body: SetupRemoteDockerStepBuilder<C>.() -> Unit) : StepSpec<C, SetupRemoteDockerStep>()

class SaveCacheStepSpec<in C>(internal val body: SaveCacheStepBuilder<C>.() -> Unit) : StepSpec<C, SaveCacheStep>()

class RestoreCacheStepSpec<in C>(internal val body: RestoreCacheStepBuilder<C>.() -> Unit) : StepSpec<C, RestoreCacheStep>()

class DeployStepSpec<in C>(internal val body: DeployStepBuilder<C>.() -> Unit) : StepSpec<C, DeployStep>()

class StoreArtifactsStepSpec<in C>(internal val body: StoreArtifactsStepBuilder<C>.() -> Unit) : StepSpec<C, StoreArtifactsStep>()

class StoreTestResultsStepSpec<in C>(internal val body: StoreTestResultsStepBuilder<C>.() -> Unit) : StepSpec<C, StoreTestResultsStep>()

class PersistToWorkspaceStepSpec<in C>(internal val body: PersistToWorkspaceStepBuilder<C>.() -> Unit) : StepSpec<C, PersistToWorkspaceStep>()

class AttachWorkspaceStepSpec<in C>(internal val body: AttachWorkspaceStepBuilder<C>.() -> Unit) : StepSpec<C, AttachWorkspaceStep>()

class AddSshKeysStepSpec<in C>(internal val body: AddSshKeysStepBuilder<C>.() -> Unit) : StepSpec<C, AddSshKeysStep>()

class RunSpec<in C>(internal val body: RunBuilder<C>.() -> Unit)

class CheckoutSpec<in C>(internal val body: CheckoutBuilder<C>.() -> Unit)

class SetupRemoteDockerSpec<in C>(internal val body: SetupRemoteDockerBuilder<C>.() -> Unit)

class SaveCacheSpec<in C>(internal val body: SaveCacheBuilder<C>.() -> Unit)

class RestoreCacheSpec<in C>(internal val body: RestoreCacheBuilder<C>.() -> Unit)

class StoreArtifactsSpec<in C>(internal val body: StoreArtifactsBuilder<C>.() -> Unit)

class StoreTestResultsSpec<in C>(internal val body: StoreTestResultsBuilder<C>.() -> Unit)

class PersistToWorkspaceSpec<in C>(internal val body: PersistToWorkspaceBuilder<C>.() -> Unit)

class AttachWorkspaceSpec<in C>(internal val body: AttachWorkspaceBuilder<C>.() -> Unit)

class AddSshKeysSpec<in C>(internal val body: AddSshKeysBuilder<C>.() -> Unit)

class WorkflowSpec<in C>(internal val body: WorkflowBuilder<C>.() -> Unit)

class WorkflowTriggerSpec<in C>(internal val body: WorkflowTriggerBuilder<C>.() -> Unit)

class WorkflowTriggerScheduleSpec<in C>(internal val body: WorkflowTriggerScheduleBuilder<C>.() -> Unit)

class WorkflowTriggerScheduleFiltersSpec<in C>(internal val body: WorkflowTriggerScheduleFiltersBuilder<C>.() -> Unit)

class WorkflowJobSpec<in C>(internal val body: WorkflowJobBuilder<C>.() -> Unit)

class WorkflowJobFilterSpec<in C>(internal val body: WorkflowJobFilterBuilder<C>.() -> Unit)

class FilterSpec<in C>(internal val body: FilterBuilder<C>.() -> Unit)
