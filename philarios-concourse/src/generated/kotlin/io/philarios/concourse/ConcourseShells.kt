package io.philarios.concourse

import io.philarios.core.Scaffold
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class ConcourseShell(var teams: List<Scaffold<Team>>? = null) : Scaffold<Concourse> {
    override suspend fun resolve(registry: Registry): Concourse {
        coroutineScope {
            teams?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Concourse(
            teams.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class TeamShell(var name: Scaffold<String>? = null, var pipelines: List<Scaffold<Pipeline>>? = null) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        checkNotNull(name) { "Team is missing the name property" }
        coroutineScope {
            pipelines?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Team(
            name!!.let{ it.resolve(registry) },
            pipelines.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class PipelineShell(
        var name: Scaffold<String>? = null,
        var jobs: List<Scaffold<Job>>? = null,
        var resources: List<Scaffold<Resource>>? = null,
        var resource_types: List<Scaffold<ResourceType>>? = null,
        var groups: List<Scaffold<Group>>? = null
) : Scaffold<Pipeline> {
    override suspend fun resolve(registry: Registry): Pipeline {
        checkNotNull(name) { "Pipeline is missing the name property" }
        coroutineScope {
            jobs?.let{ it.forEach { launch { it.resolve(registry) } } }
            resources?.let{ it.forEach { launch { it.resolve(registry) } } }
            resource_types?.let{ it.forEach { launch { it.resolve(registry) } } }
            groups?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Pipeline(
            name!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { it.resolve(registry) } },
            resources.orEmpty().let{ it.map { it.resolve(registry) } },
            resource_types.orEmpty().let{ it.map { it.resolve(registry) } },
            groups.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class JobShell(
        var name: Scaffold<String>? = null,
        var plan: List<Scaffold<Step>>? = null,
        var serial: Scaffold<Boolean>? = null,
        var build_logs_to_retain: Scaffold<Int>? = null,
        var serial_groups: List<Scaffold<String>>? = null,
        var max_in_flight: Scaffold<Int>? = null,
        var public: Scaffold<Boolean>? = null,
        var disable_manual_trigger: Scaffold<Boolean>? = null,
        var interruptible: Scaffold<Boolean>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null
) : Scaffold<Job> {
    override suspend fun resolve(registry: Registry): Job {
        checkNotNull(name) { "Job is missing the name property" }
        coroutineScope {
            plan?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Job(
            name!!.let{ it.resolve(registry) },
            plan.orEmpty().let{ it.map { it.resolve(registry) } },
            serial?.let{ it.resolve(registry) },
            build_logs_to_retain?.let{ it.resolve(registry) },
            serial_groups.orEmpty().let{ it.map { it.resolve(registry) } },
            max_in_flight?.let{ it.resolve(registry) },
            public?.let{ it.resolve(registry) },
            disable_manual_trigger?.let{ it.resolve(registry) },
            interruptible?.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal sealed class StepShell

internal data class GetShell(
        var get: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var version: Scaffold<String>? = null,
        var passed: List<Scaffold<String>>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var trigger: Scaffold<Boolean>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Get> {
    override suspend fun resolve(registry: Registry): Get {
        checkNotNull(get) { "Get is missing the get property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Get(
            get!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            version?.let{ it.resolve(registry) },
            passed.orEmpty().let{ it.map { it.resolve(registry) } },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            trigger?.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class PutShell(
        var put: Scaffold<String>? = null,
        var resource: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var get_params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Put> {
    override suspend fun resolve(registry: Registry): Put {
        checkNotNull(put) { "Put is missing the put property" }
        coroutineScope {
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Put(
            put!!.let{ it.resolve(registry) },
            resource?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            get_params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskShell(
        var task: Scaffold<String>? = null,
        var config: Scaffold<TaskConfig>? = null,
        var file: Scaffold<String>? = null,
        var privileged: Scaffold<String>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var image: Scaffold<String>? = null,
        var input_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var output_mapping: Map<Scaffold<String>, Scaffold<String>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Task> {
    override suspend fun resolve(registry: Registry): Task {
        checkNotNull(task) { "Task is missing the task property" }
        checkNotNull(config) { "Task is missing the config property" }
        coroutineScope {
            config?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Task(
            task!!.let{ it.resolve(registry) },
            config!!.let{ it.resolve(registry) },
            file?.let{ it.resolve(registry) },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            image?.let{ it.resolve(registry) },
            input_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            output_mapping.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AggregateShell(
        var aggregate: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Aggregate> {
    override suspend fun resolve(registry: Registry): Aggregate {
        coroutineScope {
            aggregate?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Aggregate(
            aggregate.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DoShell(
        var `do`: List<Scaffold<Step>>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Do> {
    override suspend fun resolve(registry: Registry): Do {
        coroutineScope {
            `do`?.let{ it.forEach { launch { it.resolve(registry) } } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Do(
            `do`.orEmpty().let{ it.map { it.resolve(registry) } },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TryShell(
        var `try`: Scaffold<Step>? = null,
        var on_success: Scaffold<Step>? = null,
        var on_failure: Scaffold<Step>? = null,
        var on_abort: Scaffold<Step>? = null,
        var ensure: Scaffold<Step>? = null,
        var tags: List<Scaffold<String>>? = null,
        var timeout: Scaffold<String>? = null,
        var attempts: Scaffold<Int>? = null
) : StepShell(), Scaffold<Try> {
    override suspend fun resolve(registry: Registry): Try {
        checkNotNull(`try`) { "Try is missing the try property" }
        coroutineScope {
            `try`?.let{ launch { it.resolve(registry) } }
            on_success?.let{ launch { it.resolve(registry) } }
            on_failure?.let{ launch { it.resolve(registry) } }
            on_abort?.let{ launch { it.resolve(registry) } }
            ensure?.let{ launch { it.resolve(registry) } }
        }
        val value = Try(
            `try`!!.let{ it.resolve(registry) },
            on_success?.let{ it.resolve(registry) },
            on_failure?.let{ it.resolve(registry) },
            on_abort?.let{ it.resolve(registry) },
            ensure?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            timeout?.let{ it.resolve(registry) },
            attempts?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskConfigShell(
        var platform: Scaffold<String>? = null,
        var image_resource: Scaffold<TaskResource>? = null,
        var rootfs_uri: Scaffold<String>? = null,
        var inputs: List<Scaffold<TaskInput>>? = null,
        var outputs: List<Scaffold<TaskOutput>>? = null,
        var caches: List<Scaffold<TaskCache>>? = null,
        var run: Scaffold<TaskRunConfig>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null
) : Scaffold<TaskConfig> {
    override suspend fun resolve(registry: Registry): TaskConfig {
        checkNotNull(platform) { "TaskConfig is missing the platform property" }
        checkNotNull(image_resource) { "TaskConfig is missing the image_resource property" }
        coroutineScope {
            image_resource?.let{ launch { it.resolve(registry) } }
            inputs?.let{ it.forEach { launch { it.resolve(registry) } } }
            outputs?.let{ it.forEach { launch { it.resolve(registry) } } }
            caches?.let{ it.forEach { launch { it.resolve(registry) } } }
            run?.let{ launch { it.resolve(registry) } }
        }
        val value = TaskConfig(
            platform!!.let{ it.resolve(registry) },
            image_resource!!.let{ it.resolve(registry) },
            rootfs_uri?.let{ it.resolve(registry) },
            inputs.orEmpty().let{ it.map { it.resolve(registry) } },
            outputs.orEmpty().let{ it.map { it.resolve(registry) } },
            caches.orEmpty().let{ it.map { it.resolve(registry) } },
            run?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

internal data class TaskResourceShell(
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var version: Map<Scaffold<String>, Scaffold<String>>? = null
) : Scaffold<TaskResource> {
    override suspend fun resolve(registry: Registry): TaskResource {
        checkNotNull(type) { "TaskResource is missing the type property" }
        val value = TaskResource(
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            version.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

internal data class TaskInputShell(
        var name: Scaffold<String>? = null,
        var path: Scaffold<String>? = null,
        var optional: Scaffold<Boolean>? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        checkNotNull(name) { "TaskInput is missing the name property" }
        val value = TaskInput(
            name!!.let{ it.resolve(registry) },
            path?.let{ it.resolve(registry) },
            optional?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskOutputShell(var name: Scaffold<String>? = null, var path: Scaffold<String>? = null) : Scaffold<TaskOutput> {
    override suspend fun resolve(registry: Registry): TaskOutput {
        checkNotNull(name) { "TaskOutput is missing the name property" }
        val value = TaskOutput(
            name!!.let{ it.resolve(registry) },
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskCacheShell(var path: Scaffold<String>? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        checkNotNull(path) { "TaskCache is missing the path property" }
        val value = TaskCache(
            path!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TaskRunConfigShell(
        var path: Scaffold<String>? = null,
        var args: List<Scaffold<String>>? = null,
        var dir: Scaffold<String>? = null,
        var user: Scaffold<String>? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        checkNotNull(path) { "TaskRunConfig is missing the path property" }
        val value = TaskRunConfig(
            path!!.let{ it.resolve(registry) },
            args.orEmpty().let{ it.map { it.resolve(registry) } },
            dir?.let{ it.resolve(registry) },
            user?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ResourceShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var check_every: Scaffold<String>? = null,
        var tags: List<Scaffold<String>>? = null,
        var webhook_token: Scaffold<String>? = null
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        checkNotNull(name) { "Resource is missing the name property" }
        checkNotNull(type) { "Resource is missing the type property" }
        val value = Resource(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            check_every?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } },
            webhook_token?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ResourceTypeShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var privileged: Scaffold<Boolean>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<ResourceType> {
    override suspend fun resolve(registry: Registry): ResourceType {
        checkNotNull(name) { "ResourceType is missing the name property" }
        checkNotNull(type) { "ResourceType is missing the type property" }
        val value = ResourceType(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class GroupShell(
        var name: Scaffold<String>? = null,
        var jobs: List<Scaffold<String>>? = null,
        var resources: List<Scaffold<String>>? = null
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        checkNotNull(name) { "Group is missing the name property" }
        val value = Group(
            name!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { it.resolve(registry) } },
            resources.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}
