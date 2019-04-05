package io.philarios.concourse

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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

class TaskConfigSpec<in C>(internal val body: TaskConfigBuilder<C>.() -> Unit)

@DslBuilder
interface TaskConfigBuilder<out C> {
    val context: C

    fun platform(value: String)

    fun image_resource(body: TaskResourceBuilder<C>.() -> Unit)

    fun image_resource(spec: TaskResourceSpec<C>)

    fun image_resource(ref: TaskResourceRef)

    fun image_resource(value: TaskResource)

    fun rootfs_uri(value: String)

    fun input(body: TaskInputBuilder<C>.() -> Unit)

    fun input(spec: TaskInputSpec<C>)

    fun input(ref: TaskInputRef)

    fun input(value: TaskInput)

    fun inputs(inputs: List<TaskInput>)

    fun output(body: TaskOutputBuilder<C>.() -> Unit)

    fun output(spec: TaskOutputSpec<C>)

    fun output(ref: TaskOutputRef)

    fun output(value: TaskOutput)

    fun outputs(outputs: List<TaskOutput>)

    fun cache(body: TaskCacheBuilder<C>.() -> Unit)

    fun cache(spec: TaskCacheSpec<C>)

    fun cache(ref: TaskCacheRef)

    fun cache(value: TaskCache)

    fun caches(caches: List<TaskCache>)

    fun run(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun run(spec: TaskRunConfigSpec<C>)

    fun run(ref: TaskRunConfigRef)

    fun run(value: TaskRunConfig)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun include(body: TaskConfigBuilder<C>.() -> Unit)

    fun include(spec: TaskConfigSpec<C>)

    fun <C2> include(context: C2, body: TaskConfigBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskConfigSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskConfigBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskConfigSpec<C2>)
}

class TaskConfigRef(internal val key: String)

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

@DslBuilder
internal class TaskConfigShellBuilder<out C>(override val context: C, internal var shell: TaskConfigShell = TaskConfigShell()) : TaskConfigBuilder<C> {
    override fun platform(value: String) {
        shell = shell.copy(platform = Wrapper(value))
    }

    override fun image_resource(body: TaskResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(image_resource = TaskResourceScaffolder<C>(TaskResourceSpec<C>(body)).createScaffold(context))
    }

    override fun image_resource(spec: TaskResourceSpec<C>) {
        shell = shell.copy(image_resource = TaskResourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun image_resource(ref: TaskResourceRef) {
        shell = shell.copy(image_resource = Deferred(ref.key))
    }

    override fun image_resource(value: TaskResource) {
        shell = shell.copy(image_resource = Wrapper(value))
    }

    override fun rootfs_uri(value: String) {
        shell = shell.copy(rootfs_uri = Wrapper(value))
    }

    override fun input(body: TaskInputBuilder<C>.() -> Unit) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder<C>(TaskInputSpec<C>(body)).createScaffold(context))
    }

    override fun input(spec: TaskInputSpec<C>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + TaskInputScaffolder<C>(spec).createScaffold(context))
    }

    override fun input(ref: TaskInputRef) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Deferred(ref.key))
    }

    override fun input(value: TaskInput) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + Wrapper(value))
    }

    override fun inputs(inputs: List<TaskInput>) {
        shell = shell.copy(inputs = shell.inputs.orEmpty() + inputs.map { Wrapper(it) })
    }

    override fun output(body: TaskOutputBuilder<C>.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder<C>(TaskOutputSpec<C>(body)).createScaffold(context))
    }

    override fun output(spec: TaskOutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + TaskOutputScaffolder<C>(spec).createScaffold(context))
    }

    override fun output(ref: TaskOutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Deferred(ref.key))
    }

    override fun output(value: TaskOutput) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Wrapper(value))
    }

    override fun outputs(outputs: List<TaskOutput>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { Wrapper(it) })
    }

    override fun cache(body: TaskCacheBuilder<C>.() -> Unit) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder<C>(TaskCacheSpec<C>(body)).createScaffold(context))
    }

    override fun cache(spec: TaskCacheSpec<C>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + TaskCacheScaffolder<C>(spec).createScaffold(context))
    }

    override fun cache(ref: TaskCacheRef) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Deferred(ref.key))
    }

    override fun cache(value: TaskCache) {
        shell = shell.copy(caches = shell.caches.orEmpty() + Wrapper(value))
    }

    override fun caches(caches: List<TaskCache>) {
        shell = shell.copy(caches = shell.caches.orEmpty() + caches.map { Wrapper(it) })
    }

    override fun run(body: TaskRunConfigBuilder<C>.() -> Unit) {
        shell = shell.copy(run = TaskRunConfigScaffolder<C>(TaskRunConfigSpec<C>(body)).createScaffold(context))
    }

    override fun run(spec: TaskRunConfigSpec<C>) {
        shell = shell.copy(run = TaskRunConfigScaffolder<C>(spec).createScaffold(context))
    }

    override fun run(ref: TaskRunConfigRef) {
        shell = shell.copy(run = Deferred(ref.key))
    }

    override fun run(value: TaskRunConfig) {
        shell = shell.copy(run = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun include(body: TaskConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskConfigSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskConfigShellBuilder<C2> = TaskConfigShellBuilder(context, shell)

    private fun <C2> merge(other: TaskConfigShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskConfigScaffolder<in C>(internal val spec: TaskConfigSpec<C>) : Scaffolder<C, TaskConfig> {
    override fun createScaffold(context: C): Scaffold<TaskConfig> {
        val builder = TaskConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
