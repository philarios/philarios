package io.philarios.concourse.v0

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class Concourse(val teams: List<Team>)

data class Team(val name: String, val pipelines: List<Pipeline>)

data class Pipeline(
        val name: String,
        val jobs: List<Job>,
        val resources: List<Resource>,
        val resource_types: List<ResourceType>,
        val groups: List<Group>
)

data class Job(
        val name: String,
        val plan: List<Step>,
        val serial: Boolean?,
        val build_logs_to_retain: Int?,
        val serial_groups: List<String>,
        val max_in_flight: Int?,
        val public: Boolean?,
        val disable_manual_trigger: Boolean?,
        val interruptible: Boolean?,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?
)

sealed class Step

data class Get(
        val get: String,
        val resource: String?,
        val version: String?,
        val passed: List<String>,
        val params: Map<String, Any>,
        val trigger: Boolean?,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Put(
        val put: String,
        val resource: String?,
        val params: Map<String, Any>,
        val get_params: Map<String, Any>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Task(
        val task: String,
        val config: TaskConfig,
        val file: String?,
        val privileged: String?,
        val params: Map<String, Any>,
        val image: String?,
        val input_mapping: Map<String, String>,
        val output_mapping: Map<String, String>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Aggregate(
        val aggregate: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Do(
        val doIt: List<Step>,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class Try(
        val tryIt: Step,
        val on_success: Step?,
        val on_failure: Step?,
        val on_abort: Step?,
        val ensure: Step?,
        val tags: List<String>,
        val timeout: String?,
        val attempts: Int?
) : Step()

data class TaskConfig(
        val platform: String,
        val image_resource: TaskResource,
        val rootfs_uri: String?,
        val inputs: List<TaskInput>,
        val outputs: List<TaskOutput>,
        val caches: List<TaskCache>,
        val run: TaskRunConfig?,
        val params: Map<String, Any>
)

data class TaskResource(
        val type: String,
        val source: Map<String, Any>,
        val params: Map<String, Any>,
        val version: Map<String, String>
)

data class TaskInput(
        val name: String,
        val path: String?,
        val optional: Boolean?
)

data class TaskOutput(val name: String, val path: String?)

data class TaskCache(val path: String)

data class TaskRunConfig(
        val path: String,
        val args: List<String>,
        val dir: String?,
        val user: String?
)

data class Resource(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val check_every: String?,
        val tags: List<String>,
        val webhook_token: String?
)

data class ResourceType(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val privileged: Boolean?,
        val params: Map<String, Any>,
        val tags: List<String>
)

data class Group(
        val name: String,
        val jobs: List<String>,
        val resources: List<String>
)
