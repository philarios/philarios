package io.philarios.concourse.v0

import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec

open class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit) : Spec<C, Concourse> {
    override fun connect(context: C): Scaffold<Concourse> {
        val builder = ConcourseBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit) : Spec<C, Team> {
    override fun connect(context: C): Scaffold<Team> {
        val builder = TeamBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit) : Spec<C, Pipeline> {
    override fun connect(context: C): Scaffold<Pipeline> {
        val builder = PipelineBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit) : Spec<C, Job> {
    override fun connect(context: C): Scaffold<Job> {
        val builder = JobBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit) : Spec<C, Get> {
    override fun connect(context: C): Scaffold<Get> {
        val builder = GetBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit) : Spec<C, Put> {
    override fun connect(context: C): Scaffold<Put> {
        val builder = PutBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit) : Spec<C, Task> {
    override fun connect(context: C): Scaffold<Task> {
        val builder = TaskBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit) : Spec<C, Aggregate> {
    override fun connect(context: C): Scaffold<Aggregate> {
        val builder = AggregateBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit) : Spec<C, Do> {
    override fun connect(context: C): Scaffold<Do> {
        val builder = DoBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit) : Spec<C, Try> {
    override fun connect(context: C): Scaffold<Try> {
        val builder = TryBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit) : Spec<C, TaskConfig> {
    override fun connect(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit) : Spec<C, TaskResource> {
    override fun connect(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit) : Spec<C, TaskInput> {
    override fun connect(context: C): Scaffold<TaskInput> {
        val builder = TaskInputBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit) : Spec<C, TaskOutput> {
    override fun connect(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit) : Spec<C, TaskCache> {
    override fun connect(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit) : Spec<C, TaskRunConfig> {
    override fun connect(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit) : Spec<C, Resource> {
    override fun connect(context: C): Scaffold<Resource> {
        val builder = ResourceBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit) : Spec<C, ResourceType> {
    override fun connect(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit) : Spec<C, Group> {
    override fun connect(context: C): Scaffold<Group> {
        val builder = GroupBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
