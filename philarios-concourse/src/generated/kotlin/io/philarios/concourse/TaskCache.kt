package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class TaskCache(val path: String)

class TaskCacheSpec<in C>(internal val body: TaskCacheBuilder<C>.() -> Unit)

@DslBuilder
interface TaskCacheBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: TaskCacheBuilder<C>.() -> Unit)

    fun include(spec: TaskCacheSpec<C>)

    fun <C2> include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>)
}

class TaskCacheRef(internal val key: String)

internal data class TaskCacheShell(var path: Scaffold<String>? = null) : Scaffold<TaskCache> {
    override suspend fun resolve(registry: Registry): TaskCache {
        checkNotNull(path) { "TaskCache is missing the path property" }
        val value = TaskCache(
            path!!.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class TaskCacheShellBuilder<out C>(override val context: C, internal var shell: TaskCacheShell = TaskCacheShell()) : TaskCacheBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: TaskCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskCacheShellBuilder<C2> = TaskCacheShellBuilder(context, shell)

    private fun <C2> merge(other: TaskCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskCacheScaffolder<in C>(internal val spec: TaskCacheSpec<C>) : Scaffolder<C, TaskCache> {
    override fun createScaffold(context: C): Scaffold<TaskCache> {
        val builder = TaskCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
