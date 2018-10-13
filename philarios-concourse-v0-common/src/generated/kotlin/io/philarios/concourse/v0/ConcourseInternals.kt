package io.philarios.concourse.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class ConcourseShell(var teams: List<Scaffold<Team>> = emptyList()) : Scaffold<Concourse> {
    override suspend fun resolve(registry: Registry): Concourse {
        coroutineScope {
        	teams.forEach { launch { it.resolve(registry) } }
        }
        val value = Concourse(teams.map { it.resolve(registry) })
        return value
    }
}

class ConcourseRef(key: String) : Scaffold<Concourse> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Concourse::class, key)

open class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit) : Spec<C, Concourse> {
    override fun connect(context: C): Scaffold<Concourse> {
        val builder = ConcourseBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TeamShell(var name: String? = null, var pipelines: List<Scaffold<Pipeline>> = emptyList()) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        coroutineScope {
        	pipelines.forEach { launch { it.resolve(registry) } }
        }
        val value = Team(name!!,pipelines.map { it.resolve(registry) })
        return value
    }
}

class TeamRef(key: String) : Scaffold<Team> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Team::class, key)

open class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit) : Spec<C, Team> {
    override fun connect(context: C): Scaffold<Team> {
        val builder = TeamBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class PipelineShell(
        var name: String? = null,
        var jobs: List<Scaffold<Job>> = emptyList(),
        var resources: List<Scaffold<Resource>> = emptyList(),
        var resource_types: List<Scaffold<ResourceType>> = emptyList(),
        var groups: List<Scaffold<Group>> = emptyList()
) : Scaffold<Pipeline> {
    override suspend fun resolve(registry: Registry): Pipeline {
        coroutineScope {
        	jobs.forEach { launch { it.resolve(registry) } }
        	resources.forEach { launch { it.resolve(registry) } }
        	resource_types.forEach { launch { it.resolve(registry) } }
        	groups.forEach { launch { it.resolve(registry) } }
        }
        val value = Pipeline(name!!,jobs.map { it.resolve(registry) },resources.map { it.resolve(registry) },resource_types.map { it.resolve(registry) },groups.map { it.resolve(registry) })
        return value
    }
}

class PipelineRef(key: String) : Scaffold<Pipeline> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Pipeline::class, key)

open class PipelineSpec<in C>(internal val body: PipelineBuilder<C>.() -> Unit) : Spec<C, Pipeline> {
    override fun connect(context: C): Scaffold<Pipeline> {
        val builder = PipelineBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class JobShell(
        var name: String? = null,
        var plan: List<Scaffold<Step>> = emptyList(),
        var serial: Boolean? = null,
        var build_logs_to_retain: Int? = null,
        var serial_groups: List<String> = emptyList(),
        var max_in_flight: Int? = null,
        var public: Boolean? = null,
        var disable_manual_trigger: Boolean? = null,
        var interruptible: Boolean? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null
) : Scaffold<Job> {
    override suspend fun resolve(registry: Registry): Job {
        coroutineScope {
        	plan.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Job(name!!,plan.map { it.resolve(registry) },serial,build_logs_to_retain,serial_groups,max_in_flight,public,disable_manual_trigger,interruptible,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry))
        return value
    }
}

class JobRef(key: String) : Scaffold<Job> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Job::class, key)

open class JobSpec<in C>(internal val body: JobBuilder<C>.() -> Unit) : Spec<C, Job> {
    override fun connect(context: C): Scaffold<Job> {
        val builder = JobBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

sealed class StepShell

data class GetShell(
        var get: String? = null,
        var resource: String? = null,
        var version: String? = null,
        var passed: List<String> = emptyList(),
        var params: Map<String, Any>? = emptyMap(),
        var trigger: Boolean? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Get> {
    override suspend fun resolve(registry: Registry): Get {
        coroutineScope {
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Get(get!!,resource,version,passed,params!!,trigger,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class PutShell(
        var put: String? = null,
        var resource: String? = null,
        var params: Map<String, Any>? = emptyMap(),
        var get_params: Map<String, Any>? = emptyMap(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Put> {
    override suspend fun resolve(registry: Registry): Put {
        coroutineScope {
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Put(put!!,resource,params!!,get_params!!,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class TaskShell(
        var task: String? = null,
        var config: Scaffold<TaskConfig>? = null,
        var file: String? = null,
        var privileged: String? = null,
        var params: Map<String, Any>? = emptyMap(),
        var image: String? = null,
        var input_mapping: Map<String, String>? = emptyMap(),
        var output_mapping: Map<String, String>? = emptyMap(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Task> {
    override suspend fun resolve(registry: Registry): Task {
        coroutineScope {
        	launch { config!!.resolve(registry) }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Task(task!!,config!!.resolve(registry),file,privileged,params!!,image,input_mapping!!,output_mapping!!,on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class AggregateShell(
        var aggregate: List<Scaffold<Step>> = emptyList(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Aggregate> {
    override suspend fun resolve(registry: Registry): Aggregate {
        coroutineScope {
        	aggregate.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Aggregate(aggregate.map { it.resolve(registry) },on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class DoShell(
        var doIt: List<Scaffold<Step>> = emptyList(),
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Do> {
    override suspend fun resolve(registry: Registry): Do {
        coroutineScope {
        	doIt.forEach { launch { it.resolve(registry) } }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Do(doIt.map { it.resolve(registry) },on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

data class TryShell(
        var tryIt: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<String> = emptyList(),
        var timeout: String? = null,
        var attempts: Int? = null
) : StepShell(), Scaffold<Try> {
    override suspend fun resolve(registry: Registry): Try {
        coroutineScope {
        	launch { tryIt!!.resolve(registry) }
        	launch { on_success?.resolve(registry) }
        	launch { on_failure?.resolve(registry) }
        	launch { on_abort?.resolve(registry) }
        	launch { ensure?.resolve(registry) }
        }
        val value = Try(tryIt!!.resolve(registry),on_success?.resolve(registry),on_failure?.resolve(registry),on_abort?.resolve(registry),ensure?.resolve(registry),tags,timeout,attempts)
        return value
    }
}

class GetRef(key: String) : Scaffold<Get> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Get::class, key)

class PutRef(key: String) : Scaffold<Put> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Put::class, key)

class TaskRef(key: String) : Scaffold<Task> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Task::class, key)

class AggregateRef(key: String) : Scaffold<Aggregate> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Aggregate::class, key)

class DoRef(key: String) : Scaffold<Do> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Do::class, key)

class TryRef(key: String) : Scaffold<Try> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Try::class, key)

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

data class TaskConfigShell(
        var platform: String? = null,
        var image_resource: Scaffold<TaskResource>? = null,
        var rootfs_uri: String? = null,
        var inputs: List<Scaffold<TaskInput>> = emptyList(),
        var outputs: List<Scaffold<TaskOutput>> = emptyList(),
        var caches: List<Scaffold<TaskCache>> = emptyList(),
        var run: Scaffold<TaskRunConfig>? = null,
        var params: Map<String, Any>? = emptyMap()
) : Scaffold<TaskConfig> {
    override suspend fun resolve(registry: Registry): TaskConfig {
        coroutineScope {
        	launch { image_resource!!.resolve(registry) }
        	inputs.forEach { launch { it.resolve(registry) } }
        	outputs.forEach { launch { it.resolve(registry) } }
        	caches.forEach { launch { it.resolve(registry) } }
        	launch { run?.resolve(registry) }
        }
        val value = TaskConfig(platform!!,image_resource!!.resolve(registry),rootfs_uri,inputs.map { it.resolve(registry) },outputs.map { it.resolve(registry) },caches.map { it.resolve(registry) },run?.resolve(registry),params!!)
        return value
    }
}

class TaskConfigRef(key: String) : Scaffold<TaskConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskConfig::class, key)

open class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit) : Spec<C, TaskConfig> {
    override fun connect(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TaskResourceShell(
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var params: Map<String, Any>? = emptyMap(),
        var version: Map<String, String>? = emptyMap()
) : Scaffold<TaskResource> {
    override suspend fun resolve(registry: Registry): TaskResource {
        val value = TaskResource(type!!,source!!,params!!,version!!)
        return value
    }
}

class TaskResourceRef(key: String) : Scaffold<TaskResource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskResource::class, key)

open class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit) : Spec<C, TaskResource> {
    override fun connect(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TaskInputShell(
        var name: String? = null,
        var path: String? = null,
        var optional: Boolean? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        val value = TaskInput(name!!,path,optional)
        return value
    }
}

class TaskInputRef(key: String) : Scaffold<TaskInput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskInput::class, key)

open class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit) : Spec<C, TaskInput> {
    override fun connect(context: C): Scaffold<TaskInput> {
        val builder = TaskInputBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TaskOutputShell(var name: String? = null, var path: String? = null) : Scaffold<TaskOutput> {
    override suspend fun resolve(registry: Registry): TaskOutput {
        val value = TaskOutput(name!!,path)
        return value
    }
}

class TaskOutputRef(key: String) : Scaffold<TaskOutput> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskOutput::class, key)

open class TaskOutputSpec<in C>(internal val body: TaskOutputBuilder<C>.() -> Unit) : Spec<C, TaskOutput> {
    override fun connect(context: C): Scaffold<TaskOutput> {
        val builder = TaskOutputBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TaskCacheShell(var path: String? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        val value = TaskCache(path!!)
        return value
    }
}

class TaskCacheRef(key: String) : Scaffold<TaskCache> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskCache::class, key)

open class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit) : Spec<C, TaskCache> {
    override fun connect(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class TaskRunConfigShell(
        var path: String? = null,
        var args: List<String> = emptyList(),
        var dir: String? = null,
        var user: String? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        val value = TaskRunConfig(path!!,args,dir,user)
        return value
    }
}

class TaskRunConfigRef(key: String) : Scaffold<TaskRunConfig> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.TaskRunConfig::class, key)

open class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit) : Spec<C, TaskRunConfig> {
    override fun connect(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class ResourceShell(
        var name: String? = null,
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var check_every: String? = null,
        var tags: List<String> = emptyList(),
        var webhook_token: String? = null
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        val value = Resource(name!!,type!!,source!!,check_every,tags,webhook_token)
        return value
    }
}

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Resource::class, key)

open class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit) : Spec<C, Resource> {
    override fun connect(context: C): Scaffold<Resource> {
        val builder = ResourceBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class ResourceTypeShell(
        var name: String? = null,
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var privileged: Boolean? = null,
        var params: Map<String, Any>? = emptyMap(),
        var tags: List<String> = emptyList()
) : Scaffold<ResourceType> {
    override suspend fun resolve(registry: Registry): ResourceType {
        val value = ResourceType(name!!,type!!,source!!,privileged,params!!,tags)
        return value
    }
}

class ResourceTypeRef(key: String) : Scaffold<ResourceType> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.ResourceType::class, key)

open class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit) : Spec<C, ResourceType> {
    override fun connect(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class GroupShell(
        var name: String? = null,
        var jobs: List<String> = emptyList(),
        var resources: List<String> = emptyList()
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        val value = Group(name!!,jobs,resources)
        return value
    }
}

class GroupRef(key: String) : Scaffold<Group> by io.philarios.core.v0.RegistryRef(io.philarios.concourse.v0.Group::class, key)

open class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit) : Spec<C, Group> {
    override fun connect(context: C): Scaffold<Group> {
        val builder = GroupBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
