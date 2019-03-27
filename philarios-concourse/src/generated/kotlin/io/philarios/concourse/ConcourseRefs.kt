package io.philarios.concourse

import io.philarios.core.Scaffold
import kotlin.String

class ConcourseRef(key: String) : Scaffold<Concourse> by io.philarios.core.RegistryRef(io.philarios.concourse.Concourse::class, key)

class TeamRef(key: String) : Scaffold<Team> by io.philarios.core.RegistryRef(io.philarios.concourse.Team::class, key)

class PipelineRef(key: String) : Scaffold<Pipeline> by io.philarios.core.RegistryRef(io.philarios.concourse.Pipeline::class, key)

class JobRef(key: String) : Scaffold<Job> by io.philarios.core.RegistryRef(io.philarios.concourse.Job::class, key)

sealed class StepRef<T : Step> : Scaffold<T>

class GetRef(key: String) : StepRef<Get>(),
        Scaffold<Get> by io.philarios.core.RegistryRef(io.philarios.concourse.Get::class, key)

class PutRef(key: String) : StepRef<Put>(),
        Scaffold<Put> by io.philarios.core.RegistryRef(io.philarios.concourse.Put::class, key)

class TaskRef(key: String) : StepRef<Task>(),
        Scaffold<Task> by io.philarios.core.RegistryRef(io.philarios.concourse.Task::class, key)

class AggregateRef(key: String) : StepRef<Aggregate>(),
        Scaffold<Aggregate> by io.philarios.core.RegistryRef(io.philarios.concourse.Aggregate::class, key)

class DoRef(key: String) : StepRef<Do>(),
        Scaffold<Do> by io.philarios.core.RegistryRef(io.philarios.concourse.Do::class, key)

class TryRef(key: String) : StepRef<Try>(),
        Scaffold<Try> by io.philarios.core.RegistryRef(io.philarios.concourse.Try::class, key)

class TaskConfigRef(key: String) : Scaffold<TaskConfig> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskConfig::class, key)

class TaskResourceRef(key: String) : Scaffold<TaskResource> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskResource::class, key)

class TaskInputRef(key: String) : Scaffold<TaskInput> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskInput::class, key)

class TaskOutputRef(key: String) : Scaffold<TaskOutput> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskOutput::class, key)

class TaskCacheRef(key: String) : Scaffold<TaskCache> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskCache::class, key)

class TaskRunConfigRef(key: String) : Scaffold<TaskRunConfig> by io.philarios.core.RegistryRef(io.philarios.concourse.TaskRunConfig::class, key)

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.RegistryRef(io.philarios.concourse.Resource::class, key)

class ResourceTypeRef(key: String) : Scaffold<ResourceType> by io.philarios.core.RegistryRef(io.philarios.concourse.ResourceType::class, key)

class GroupRef(key: String) : Scaffold<Group> by io.philarios.core.RegistryRef(io.philarios.concourse.Group::class, key)
