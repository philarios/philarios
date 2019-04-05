package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class TaskResource(
        val type: String,
        val source: Map<String, Any>,
        val params: Map<String, Any>,
        val version: Map<String, String>
)

class TaskResourceSpec<in C>(internal val body: TaskResourceBuilder<C>.() -> Unit)

@DslBuilder
interface TaskResourceBuilder<out C> {
    val context: C

    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun version(key: String, value: String)

    fun version(pair: Pair<String, String>)

    fun version(version: Map<String, String>)

    fun include(body: TaskResourceBuilder<C>.() -> Unit)

    fun include(spec: TaskResourceSpec<C>)

    fun <C2> include(context: C2, body: TaskResourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskResourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskResourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskResourceSpec<C2>)
}

class TaskResourceRef(internal val key: String)

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

@DslBuilder
internal class TaskResourceShellBuilder<out C>(override val context: C, internal var shell: TaskResourceShell = TaskResourceShell()) : TaskResourceBuilder<C> {
    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
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

    override fun version(key: String, value: String) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun version(pair: Pair<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun version(version: Map<String, String>) {
        shell = shell.copy(version = shell.version.orEmpty() + version.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun include(body: TaskResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskResourceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskResourceShellBuilder<C2> = TaskResourceShellBuilder(context, shell)

    private fun <C2> merge(other: TaskResourceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskResourceScaffolder<in C>(internal val spec: TaskResourceSpec<C>) : Scaffolder<C, TaskResource> {
    override fun createScaffold(context: C): Scaffold<TaskResource> {
        val builder = TaskResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
