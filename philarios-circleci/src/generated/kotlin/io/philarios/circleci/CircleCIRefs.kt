package io.philarios.circleci

import io.philarios.core.Scaffold
import kotlin.String

class CircleCIRef(key: String) : Scaffold<CircleCI> by io.philarios.core.RegistryRef(io.philarios.circleci.CircleCI::class, key)

class JobRef(key: String) : Scaffold<Job> by io.philarios.core.RegistryRef(io.philarios.circleci.Job::class, key)

class DockerExecutorRef(key: String) : Scaffold<DockerExecutor> by io.philarios.core.RegistryRef(io.philarios.circleci.DockerExecutor::class, key)

class AuthRef(key: String) : Scaffold<Auth> by io.philarios.core.RegistryRef(io.philarios.circleci.Auth::class, key)

class AwsAuthRef(key: String) : Scaffold<AwsAuth> by io.philarios.core.RegistryRef(io.philarios.circleci.AwsAuth::class, key)

class MachineExecutorRef(key: String) : Scaffold<MachineExecutor> by io.philarios.core.RegistryRef(io.philarios.circleci.MachineExecutor::class, key)

class MacosExecutorRef(key: String) : Scaffold<MacosExecutor> by io.philarios.core.RegistryRef(io.philarios.circleci.MacosExecutor::class, key)

sealed class StepRef<T : Step> : Scaffold<T>

class RunStepRef(key: String) : StepRef<RunStep>(),
        Scaffold<RunStep> by io.philarios.core.RegistryRef(io.philarios.circleci.RunStep::class, key)

class CheckoutStepRef(key: String) : StepRef<CheckoutStep>(),
        Scaffold<CheckoutStep> by io.philarios.core.RegistryRef(io.philarios.circleci.CheckoutStep::class, key)

class SetupRemoteDockerStepRef(key: String) : StepRef<SetupRemoteDockerStep>(),
        Scaffold<SetupRemoteDockerStep> by io.philarios.core.RegistryRef(io.philarios.circleci.SetupRemoteDockerStep::class, key)

class SaveCacheStepRef(key: String) : StepRef<SaveCacheStep>(),
        Scaffold<SaveCacheStep> by io.philarios.core.RegistryRef(io.philarios.circleci.SaveCacheStep::class, key)

class RestoreCacheStepRef(key: String) : StepRef<RestoreCacheStep>(),
        Scaffold<RestoreCacheStep> by io.philarios.core.RegistryRef(io.philarios.circleci.RestoreCacheStep::class, key)

class DeployStepRef(key: String) : StepRef<DeployStep>(),
        Scaffold<DeployStep> by io.philarios.core.RegistryRef(io.philarios.circleci.DeployStep::class, key)

class StoreArtifactsStepRef(key: String) : StepRef<StoreArtifactsStep>(),
        Scaffold<StoreArtifactsStep> by io.philarios.core.RegistryRef(io.philarios.circleci.StoreArtifactsStep::class, key)

class StoreTestResultsStepRef(key: String) : StepRef<StoreTestResultsStep>(),
        Scaffold<StoreTestResultsStep> by io.philarios.core.RegistryRef(io.philarios.circleci.StoreTestResultsStep::class, key)

class PersistToWorkspaceStepRef(key: String) : StepRef<PersistToWorkspaceStep>(),
        Scaffold<PersistToWorkspaceStep> by io.philarios.core.RegistryRef(io.philarios.circleci.PersistToWorkspaceStep::class, key)

class AttachWorkspaceStepRef(key: String) : StepRef<AttachWorkspaceStep>(),
        Scaffold<AttachWorkspaceStep> by io.philarios.core.RegistryRef(io.philarios.circleci.AttachWorkspaceStep::class, key)

class AddSshKeysStepRef(key: String) : StepRef<AddSshKeysStep>(),
        Scaffold<AddSshKeysStep> by io.philarios.core.RegistryRef(io.philarios.circleci.AddSshKeysStep::class, key)

class RunRef(key: String) : Scaffold<Run> by io.philarios.core.RegistryRef(io.philarios.circleci.Run::class, key)

class CheckoutRef(key: String) : Scaffold<Checkout> by io.philarios.core.RegistryRef(io.philarios.circleci.Checkout::class, key)

class SetupRemoteDockerRef(key: String) : Scaffold<SetupRemoteDocker> by io.philarios.core.RegistryRef(io.philarios.circleci.SetupRemoteDocker::class, key)

class SaveCacheRef(key: String) : Scaffold<SaveCache> by io.philarios.core.RegistryRef(io.philarios.circleci.SaveCache::class, key)

class RestoreCacheRef(key: String) : Scaffold<RestoreCache> by io.philarios.core.RegistryRef(io.philarios.circleci.RestoreCache::class, key)

class StoreArtifactsRef(key: String) : Scaffold<StoreArtifacts> by io.philarios.core.RegistryRef(io.philarios.circleci.StoreArtifacts::class, key)

class StoreTestResultsRef(key: String) : Scaffold<StoreTestResults> by io.philarios.core.RegistryRef(io.philarios.circleci.StoreTestResults::class, key)

class PersistToWorkspaceRef(key: String) : Scaffold<PersistToWorkspace> by io.philarios.core.RegistryRef(io.philarios.circleci.PersistToWorkspace::class, key)

class AttachWorkspaceRef(key: String) : Scaffold<AttachWorkspace> by io.philarios.core.RegistryRef(io.philarios.circleci.AttachWorkspace::class, key)

class AddSshKeysRef(key: String) : Scaffold<AddSshKeys> by io.philarios.core.RegistryRef(io.philarios.circleci.AddSshKeys::class, key)

class WorkflowRef(key: String) : Scaffold<Workflow> by io.philarios.core.RegistryRef(io.philarios.circleci.Workflow::class, key)

class WorkflowTriggerRef(key: String) : Scaffold<WorkflowTrigger> by io.philarios.core.RegistryRef(io.philarios.circleci.WorkflowTrigger::class, key)

class WorkflowTriggerScheduleRef(key: String) : Scaffold<WorkflowTriggerSchedule> by io.philarios.core.RegistryRef(io.philarios.circleci.WorkflowTriggerSchedule::class, key)

class WorkflowTriggerScheduleFiltersRef(key: String) : Scaffold<WorkflowTriggerScheduleFilters> by io.philarios.core.RegistryRef(io.philarios.circleci.WorkflowTriggerScheduleFilters::class, key)

class WorkflowJobRef(key: String) : Scaffold<WorkflowJob> by io.philarios.core.RegistryRef(io.philarios.circleci.WorkflowJob::class, key)

class WorkflowJobFilterRef(key: String) : Scaffold<WorkflowJobFilter> by io.philarios.core.RegistryRef(io.philarios.circleci.WorkflowJobFilter::class, key)

class FilterRef(key: String) : Scaffold<Filter> by io.philarios.core.RegistryRef(io.philarios.circleci.Filter::class, key)
