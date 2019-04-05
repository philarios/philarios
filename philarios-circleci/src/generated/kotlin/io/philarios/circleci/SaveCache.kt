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

data class SaveCache(
        val paths: List<String>?,
        val key: String?,
        val `when`: When?,
        val name: String?
)

class SaveCacheSpec<in C>(internal val body: SaveCacheBuilder<C>.() -> Unit)

@DslBuilder
interface SaveCacheBuilder<out C> {
    val context: C

    fun path(value: String)

    fun paths(paths: List<String>)

    fun key(value: String)

    fun `when`(value: When)

    fun name(value: String)

    fun include(body: SaveCacheBuilder<C>.() -> Unit)

    fun include(spec: SaveCacheSpec<C>)

    fun <C2> include(context: C2, body: SaveCacheBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SaveCacheSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheSpec<C2>)
}

class SaveCacheRef(internal val key: String)

internal data class SaveCacheShell(
        var paths: List<Scaffold<String>>? = null,
        var key: Scaffold<String>? = null,
        var `when`: Scaffold<When>? = null,
        var name: Scaffold<String>? = null
) : Scaffold<SaveCache> {
    override suspend fun resolve(registry: Registry): SaveCache {
        val value = SaveCache(
            paths?.let{ it.map { it.resolve(registry) } },
            key?.let{ it.resolve(registry) },
            `when`?.let{ it.resolve(registry) },
            name?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class SaveCacheShellBuilder<out C>(override val context: C, internal var shell: SaveCacheShell = SaveCacheShell()) : SaveCacheBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + Wrapper(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { Wrapper(it) })
    }

    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun include(body: SaveCacheBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SaveCacheSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SaveCacheBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SaveCacheSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SaveCacheBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SaveCacheSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SaveCacheShellBuilder<C2> = SaveCacheShellBuilder(context, shell)

    private fun <C2> merge(other: SaveCacheShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class SaveCacheScaffolder<in C>(internal val spec: SaveCacheSpec<C>) : Scaffolder<C, SaveCache> {
    override fun createScaffold(context: C): Scaffold<SaveCache> {
        val builder = SaveCacheShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
