package io.philarios.concourse

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class ConcourseScaffolder<in C>(internal val spec: ConcourseSpec<C>) : Scaffolder<C, Concourse> {
    override fun createScaffold(context: C): Scaffold<Concourse> {
        val builder = ConcourseShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TeamScaffolder<in C>(internal val spec: TeamSpec<C>) : Scaffolder<C, Team> {
    override fun createScaffold(context: C): Scaffold<Team> {
        val builder = TeamShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PipelineScaffolder<in C>(internal val spec: PipelineSpec<C>) : Scaffolder<C, Pipeline> {
    override fun createScaffold(context: C): Scaffold<Pipeline> {
        val builder = PipelineShellBuilder<C>(context)
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

class StepScaffolder<in C, out T : Step>(internal val spec: StepSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is GetSpec<C> -> GetScaffolder(spec).createScaffold(context)
            is PutSpec<C> -> PutScaffolder(spec).createScaffold(context)
            is TaskSpec<C> -> TaskScaffolder(spec).createScaffold(context)
            is AggregateSpec<C> -> AggregateScaffolder(spec).createScaffold(context)
            is DoSpec<C> -> DoScaffolder(spec).createScaffold(context)
            is TrySpec<C> -> TryScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class GetScaffolder<in C>(internal val spec: GetSpec<C>) : Scaffolder<C, Get> {
    override fun createScaffold(context: C): Scaffold<Get> {
        val builder = GetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class PutScaffolder<in C>(internal val spec: PutSpec<C>) : Scaffolder<C, Put> {
    override fun createScaffold(context: C): Scaffold<Put> {
        val builder = PutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskScaffolder<in C>(internal val spec: TaskSpec<C>) : Scaffolder<C, Task> {
    override fun createScaffold(context: C): Scaffold<Task> {
        val builder = TaskShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AggregateScaffolder<in C>(internal val spec: AggregateSpec<C>) : Scaffolder<C, Aggregate> {
    override fun createScaffold(context: C): Scaffold<Aggregate> {
        val builder = AggregateShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoScaffolder<in C>(internal val spec: DoSpec<C>) : Scaffolder<C, Do> {
    override fun createScaffold(context: C): Scaffold<Do> {
        val builder = DoShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TryScaffolder<in C>(internal val spec: TrySpec<C>) : Scaffolder<C, Try> {
    override fun createScaffold(context: C): Scaffold<Try> {
        val builder = TryShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskConfigScaffolder<in C>(internal val spec: TaskConfigSpec<C>) : Scaffolder<C, TaskConfig> {
    override fun createScaffold(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskResourceScaffolder<in C>(internal val spec: TaskResourceSpec<C>) : Scaffolder<C, TaskResource> {
    override fun createScaffold(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskInputScaffolder<in C>(internal val spec: TaskInputSpec<C>) : Scaffolder<C, TaskInput> {
    override fun createScaffold(context: C): Scaffold<TaskInput> {
        val builder = TaskInputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskOutputScaffolder<in C>(internal val spec: TaskOutputSpec<C>) : Scaffolder<C, TaskOutput> {
    override fun createScaffold(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskCacheScaffolder<in C>(internal val spec: TaskCacheSpec<C>) : Scaffolder<C, TaskCache> {
    override fun createScaffold(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TaskRunConfigScaffolder<in C>(internal val spec: TaskRunConfigSpec<C>) : Scaffolder<C, TaskRunConfig> {
    override fun createScaffold(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ResourceScaffolder<in C>(internal val spec: ResourceSpec<C>) : Scaffolder<C, Resource> {
    override fun createScaffold(context: C): Scaffold<Resource> {
        val builder = ResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ResourceTypeScaffolder<in C>(internal val spec: ResourceTypeSpec<C>) : Scaffolder<C, ResourceType> {
    override fun createScaffold(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class GroupScaffolder<in C>(internal val spec: GroupSpec<C>) : Scaffolder<C, Group> {
    override fun createScaffold(context: C): Scaffold<Group> {
        val builder = GroupShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
