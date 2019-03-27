package io.philarios.concourse

class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit)

class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit)

class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit)

class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit)

sealed class StepSpec<in C, out T : Step>

class GetSpec<in C>(internal val body: GetBuilder<C>.() -> Unit) : StepSpec<C, Get>()

class PutSpec<in C>(internal val body: PutBuilder<C>.() -> Unit) : StepSpec<C, Put>()

class TaskSpec<in C>(internal val body: TaskBuilder<C>.() -> Unit) : StepSpec<C, Task>()

class AggregateSpec<in C>(internal val body: AggregateBuilder<C>.() -> Unit) : StepSpec<C, Aggregate>()

class DoSpec<in C>(internal val body: DoBuilder<C>.() -> Unit) : StepSpec<C, Do>()

class TrySpec<in C>(internal val body: TryBuilder<C>.() -> Unit) : StepSpec<C, Try>()

class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit)

class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit)

class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit)

class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit)

class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit)

class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit)

class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit)

class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit)

class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit)
