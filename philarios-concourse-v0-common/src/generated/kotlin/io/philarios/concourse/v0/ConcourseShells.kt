package io.philarios.concourse.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
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

data class TeamShell(var name: String? = null, var pipelines: List<Scaffold<Pipeline>> = emptyList()) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        checkNotNull(name) { "Team is missing the name property" }
        coroutineScope {
        	pipelines.forEach { launch { it.resolve(registry) } }
        }
        val value = Team(name!!,pipelines.map { it.resolve(registry) })
        return value
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
        checkNotNull(name) { "Pipeline is missing the name property" }
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
        checkNotNull(name) { "Job is missing the name property" }
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
        checkNotNull(get) { "Get is missing the get property" }
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
        checkNotNull(put) { "Put is missing the put property" }
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
        checkNotNull(task) { "Task is missing the task property" }
        checkNotNull(config) { "Task is missing the config property" }
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
        checkNotNull(tryIt) { "Try is missing the tryIt property" }
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
        checkNotNull(platform) { "TaskConfig is missing the platform property" }
        checkNotNull(image_resource) { "TaskConfig is missing the image_resource property" }
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

data class TaskResourceShell(
        var type: String? = null,
        var source: Map<String, Any>? = emptyMap(),
        var params: Map<String, Any>? = emptyMap(),
        var version: Map<String, String>? = emptyMap()
) : Scaffold<TaskResource> {
    override suspend fun resolve(registry: Registry): TaskResource {
        checkNotNull(type) { "TaskResource is missing the type property" }
        val value = TaskResource(type!!,source!!,params!!,version!!)
        return value
    }
}

data class TaskInputShell(
        var name: String? = null,
        var path: String? = null,
        var optional: Boolean? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        checkNotNull(name) { "TaskInput is missing the name property" }
        val value = TaskInput(name!!,path,optional)
        return value
    }
}

data class TaskOutputShell(var name: String? = null, var path: String? = null) : Scaffold<TaskOutput> {
    override suspend fun resolve(registry: Registry): TaskOutput {
        checkNotNull(name) { "TaskOutput is missing the name property" }
        val value = TaskOutput(name!!,path)
        return value
    }
}

data class TaskCacheShell(var path: String? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        checkNotNull(path) { "TaskCache is missing the path property" }
        val value = TaskCache(path!!)
        return value
    }
}

data class TaskRunConfigShell(
        var path: String? = null,
        var args: List<String> = emptyList(),
        var dir: String? = null,
        var user: String? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        checkNotNull(path) { "TaskRunConfig is missing the path property" }
        val value = TaskRunConfig(path!!,args,dir,user)
        return value
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
        checkNotNull(name) { "Resource is missing the name property" }
        checkNotNull(type) { "Resource is missing the type property" }
        val value = Resource(name!!,type!!,source!!,check_every,tags,webhook_token)
        return value
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
        checkNotNull(name) { "ResourceType is missing the name property" }
        checkNotNull(type) { "ResourceType is missing the type property" }
        val value = ResourceType(name!!,type!!,source!!,privileged,params!!,tags)
        return value
    }
}

data class GroupShell(
        var name: String? = null,
        var jobs: List<String> = emptyList(),
        var resources: List<String> = emptyList()
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        checkNotNull(name) { "Group is missing the name property" }
        val value = Group(name!!,jobs,resources)
        return value
    }
}
