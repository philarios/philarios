package io.philarios.concourse.v0

import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec

class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit) : Spec<C, Concourse> {
    override fun connect(context: C): Scaffold<Concourse> {
        val builder = ConcourseShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit) : Spec<C, Team> {
    override fun connect(context: C): Scaffold<Team> {
        val builder = TeamShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit) : Spec<C, Pipeline> {
    override fun connect(context: C): Scaffold<Pipeline> {
        val builder = PipelineShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit) : Spec<C, Job> {
    override fun connect(context: C): Scaffold<Job> {
        val builder = JobShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit) : Spec<C, Get> {
    override fun connect(context: C): Scaffold<Get> {
        val builder = GetShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit) : Spec<C, Put> {
    override fun connect(context: C): Scaffold<Put> {
        val builder = PutShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit) : Spec<C, Task> {
    override fun connect(context: C): Scaffold<Task> {
        val builder = TaskShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit) : Spec<C, Aggregate> {
    override fun connect(context: C): Scaffold<Aggregate> {
        val builder = AggregateShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit) : Spec<C, Do> {
    override fun connect(context: C): Scaffold<Do> {
        val builder = DoShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit) : Spec<C, Try> {
    override fun connect(context: C): Scaffold<Try> {
        val builder = TryShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit) : Spec<C, TaskConfig> {
    override fun connect(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit) : Spec<C, TaskResource> {
    override fun connect(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit) : Spec<C, TaskInput> {
    override fun connect(context: C): Scaffold<TaskInput> {
        val builder = TaskInputShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit) : Spec<C, TaskOutput> {
    override fun connect(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit) : Spec<C, TaskCache> {
    override fun connect(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit) : Spec<C, TaskRunConfig> {
    override fun connect(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit) : Spec<C, Resource> {
    override fun connect(context: C): Scaffold<Resource> {
        val builder = ResourceShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit) : Spec<C, ResourceType> {
    override fun connect(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit) : Spec<C, Group> {
    override fun connect(context: C): Scaffold<Group> {
        val builder = GroupShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
