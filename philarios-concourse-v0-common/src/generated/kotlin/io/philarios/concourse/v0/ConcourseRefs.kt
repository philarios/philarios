package io.philarios.concourse.v0

import io.philarios.core.v0.Scaffold
import kotlin.String

class ConcourseRef(key: String) : Scaffold<Concourse> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Concourse::class, key)

class TeamRef(key: String) : Scaffold<Team> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Team::class, key)

class PipelineRef(key: String) : Scaffold<Pipeline> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Pipeline::class, key)

class JobRef(key: String) : Scaffold<Job> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Job::class, key)

class GetRef(key: String) : Scaffold<Get> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Get::class, key)

class PutRef(key: String) : Scaffold<Put> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Put::class, key)

class TaskRef(key: String) : Scaffold<Task> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Task::class, key)

class AggregateRef(key: String) : Scaffold<Aggregate> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Aggregate::class, key)

class DoRef(key: String) : Scaffold<Do> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Do::class, key)

class TryRef(key: String) : Scaffold<Try> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Try::class, key)

class TaskConfigRef(key: String) : Scaffold<TaskConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskConfig::class, key)

class TaskResourceRef(key: String) : Scaffold<TaskResource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskResource::class, key)

class TaskInputRef(key: String) : Scaffold<TaskInput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskInput::class, key)

class TaskOutputRef(key: String) : Scaffold<TaskOutput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskOutput::class, key)

class TaskCacheRef(key: String) : Scaffold<TaskCache> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskCache::class, key)

class TaskRunConfigRef(key: String) : Scaffold<TaskRunConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskRunConfig::class, key)

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Resource::class, key)

class ResourceTypeRef(key: String) : Scaffold<ResourceType> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.ResourceType::class, key)

class GroupRef(key: String) : Scaffold<Group> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Group::class, key)
