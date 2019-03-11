package io.philarios.circleci

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class CircleCIScaffolder<in C>(internal val spec: CircleCISpec<C>) : Scaffolder<C, CircleCI> {
    override fun createScaffold(context: C): Scaffold<CircleCI> {
        val builder = CircleCIShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class JobScaffolder<in C>(internal val spec: JobSpec<C>) : Scaffolder<C, Job> {
    override fun createScaffold(context: C): Scaffold<Job> {
        val builder = JobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DockerExecutorScaffolder<in C>(internal val spec: DockerExecutorSpec<C>) : Scaffolder<C, DockerExecutor> {
    override fun createScaffold(context: C): Scaffold<DockerExecutor> {
        val builder = DockerExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AuthScaffolder<in C>(internal val spec: AuthSpec<C>) : Scaffolder<C, Auth> {
    override fun createScaffold(context: C): Scaffold<Auth> {
        val builder = AuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AwsAuthScaffolder<in C>(internal val spec: AwsAuthSpec<C>) : Scaffolder<C, AwsAuth> {
    override fun createScaffold(context: C): Scaffold<AwsAuth> {
        val builder = AwsAuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class MachineExecutorScaffolder<in C>(internal val spec: MachineExecutorSpec<C>) : Scaffolder<C, MachineExecutor> {
    override fun createScaffold(context: C): Scaffold<MachineExecutor> {
        val builder = MachineExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class MacosExecutorScaffolder<in C>(internal val spec: MacosExecutorSpec<C>) : Scaffolder<C, MacosExecutor> {
    override fun createScaffold(context: C): Scaffold<MacosExecutor> {
        val builder = MacosExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class RunScaffolder<in C>(internal val spec: RunSpec<C>) : Scaffolder<C, Run> {
    override fun createScaffold(context: C): Scaffold<Run> {
        val builder = RunShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class CheckoutScaffolder<in C>(internal val spec: CheckoutSpec<C>) : Scaffolder<C, Checkout> {
    override fun createScaffold(context: C): Scaffold<Checkout> {
        val builder = CheckoutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SetupRemoteDockerScaffolder<in C>(internal val spec: SetupRemoteDockerSpec<C>) : Scaffolder<C, SetupRemoteDocker> {
    override fun createScaffold(context: C): Scaffold<SetupRemoteDocker> {
        val builder = SetupRemoteDockerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class SaveCacheScaffolder<in C>(internal val spec: SaveCacheSpec<C>) : Scaffolder<C, SaveCache> {
    override fun createScaffold(context: C): Scaffold<SaveCache> {
        val builder = SaveCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RestoreCacheScaffolder<in C>(internal val spec: RestoreCacheSpec<C>) : Scaffolder<C, RestoreCache> {
    override fun createScaffold(context: C): Scaffold<RestoreCache> {
        val builder = RestoreCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreArtifactsScaffolder<in C>(internal val spec: StoreArtifactsSpec<C>) : Scaffolder<C, StoreArtifacts> {
    override fun createScaffold(context: C): Scaffold<StoreArtifacts> {
        val builder = StoreArtifactsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StoreTestResultsScaffolder<in C>(internal val spec: StoreTestResultsSpec<C>) : Scaffolder<C, StoreTestResults> {
    override fun createScaffold(context: C): Scaffold<StoreTestResults> {
        val builder = StoreTestResultsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PersistToWorkspaceScaffolder<in C>(internal val spec: PersistToWorkspaceSpec<C>) : Scaffolder<C, PersistToWorkspace> {
    override fun createScaffold(context: C): Scaffold<PersistToWorkspace> {
        val builder = PersistToWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AttachWorkspaceScaffolder<in C>(internal val spec: AttachWorkspaceSpec<C>) : Scaffolder<C, AttachWorkspace> {
    override fun createScaffold(context: C): Scaffold<AttachWorkspace> {
        val builder = AttachWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AddSshKeysScaffolder<in C>(internal val spec: AddSshKeysSpec<C>) : Scaffolder<C, AddSshKeys> {
    override fun createScaffold(context: C): Scaffold<AddSshKeys> {
        val builder = AddSshKeysShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowScaffolder<in C>(internal val spec: WorkflowSpec<C>) : Scaffolder<C, Workflow> {
    override fun createScaffold(context: C): Scaffold<Workflow> {
        val builder = WorkflowShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowTriggerScaffolder<in C>(internal val spec: WorkflowTriggerSpec<C>) : Scaffolder<C, WorkflowTrigger> {
    override fun createScaffold(context: C): Scaffold<WorkflowTrigger> {
        val builder = WorkflowTriggerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowTriggerScheduleScaffolder<in C>(internal val spec: WorkflowTriggerScheduleSpec<C>) : Scaffolder<C, WorkflowTriggerSchedule> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerSchedule> {
        val builder = WorkflowTriggerScheduleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowTriggerScheduleFiltersScaffolder<in C>(internal val spec: WorkflowTriggerScheduleFiltersSpec<C>) : Scaffolder<C, WorkflowTriggerScheduleFilters> {
    override fun createScaffold(context: C): Scaffold<WorkflowTriggerScheduleFilters> {
        val builder = WorkflowTriggerScheduleFiltersShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowJobScaffolder<in C>(internal val spec: WorkflowJobSpec<C>) : Scaffolder<C, WorkflowJob> {
    override fun createScaffold(context: C): Scaffold<WorkflowJob> {
        val builder = WorkflowJobShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class WorkflowJobFilterScaffolder<in C>(internal val spec: WorkflowJobFilterSpec<C>) : Scaffolder<C, WorkflowJobFilter> {
    override fun createScaffold(context: C): Scaffold<WorkflowJobFilter> {
        val builder = WorkflowJobFilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class FilterScaffolder<in C>(internal val spec: FilterSpec<C>) : Scaffolder<C, Filter> {
    override fun createScaffold(context: C): Scaffold<Filter> {
        val builder = FilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
