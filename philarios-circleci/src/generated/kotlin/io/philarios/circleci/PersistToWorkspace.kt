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

data class PersistToWorkspace(val root: String?, val paths: List<String>?)

class PersistToWorkspaceSpec<in C>(internal val body: PersistToWorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface PersistToWorkspaceBuilder<out C> {
    val context: C

    fun root(value: String)

    fun path(value: String)

    fun paths(paths: List<String>)

    fun include(body: PersistToWorkspaceBuilder<C>.() -> Unit)

    fun include(spec: PersistToWorkspaceSpec<C>)

    fun <C2> include(context: C2, body: PersistToWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PersistToWorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceSpec<C2>)
}

class PersistToWorkspaceRef(internal val key: String)

internal data class PersistToWorkspaceShell(var root: Scaffold<String>? = null, var paths: List<Scaffold<String>>? = null) : Scaffold<PersistToWorkspace> {
    override suspend fun resolve(registry: Registry): PersistToWorkspace {
        val value = PersistToWorkspace(
            root?.let{ it.resolve(registry) },
            paths?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class PersistToWorkspaceShellBuilder<out C>(override val context: C, internal var shell: PersistToWorkspaceShell = PersistToWorkspaceShell()) : PersistToWorkspaceBuilder<C> {
    override fun root(value: String) {
        shell = shell.copy(root = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(paths = shell.paths.orEmpty() + Wrapper(value))
    }

    override fun paths(paths: List<String>) {
        shell = shell.copy(paths = shell.paths.orEmpty() + paths.map { Wrapper(it) })
    }

    override fun include(body: PersistToWorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersistToWorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersistToWorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersistToWorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersistToWorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersistToWorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersistToWorkspaceShellBuilder<C2> = PersistToWorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: PersistToWorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class PersistToWorkspaceScaffolder<in C>(internal val spec: PersistToWorkspaceSpec<C>) : Scaffolder<C, PersistToWorkspace> {
    override fun createScaffold(context: C): Scaffold<PersistToWorkspace> {
        val builder = PersistToWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
