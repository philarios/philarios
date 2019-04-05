package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class RestoreCache(val keys: List<String>?, val name: String?)

class RestoreCacheSpec<in C>(internal val body: RestoreCacheBuilder<C>.() -> Unit)

@DslBuilder
interface RestoreCacheBuilder<out C> {
    val context: C

    fun key(value: String)

    fun keys(keys: List<String>)

    fun name(value: String)

    fun include(body: RestoreCacheBuilder<C>.() -> Unit)

    fun include(spec: RestoreCacheSpec<C>)

    fun <C2> include(context: C2, body: RestoreCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RestoreCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheSpec<C2>)
}

class RestoreCacheRef(internal val key: String)

internal data class RestoreCacheShell(var keys: List<Scaffold<String>>? = null, var name: Scaffold<String>? = null) : Scaffold<RestoreCache> {
    override suspend fun resolve(registry: Registry): RestoreCache {
        val value = RestoreCache(
            keys?.let{ it.map { it.resolve(registry) } },
            name?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class RestoreCacheShellBuilder<out C>(override val context: C, internal var shell: RestoreCacheShell = RestoreCacheShell()) : RestoreCacheBuilder<C> {
    override fun key(value: String) {
        shell = shell.copy(keys = shell.keys.orEmpty() + Wrapper(value))
    }

    override fun keys(keys: List<String>) {
        shell = shell.copy(keys = shell.keys.orEmpty() + keys.map { Wrapper(it) })
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun include(body: RestoreCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RestoreCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RestoreCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RestoreCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RestoreCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RestoreCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RestoreCacheShellBuilder<C2> = RestoreCacheShellBuilder(context, shell)

    private fun <C2> merge(other: RestoreCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class RestoreCacheScaffolder<in C>(internal val spec: RestoreCacheSpec<C>) : Scaffolder<C, RestoreCache> {
    override fun createScaffold(context: C): Scaffold<RestoreCache> {
        val builder = RestoreCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
