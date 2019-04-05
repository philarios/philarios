package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class AttachWorkspace(val at: String?)

class AttachWorkspaceSpec<in C>(internal val body: AttachWorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface AttachWorkspaceBuilder<out C> {
    val context: C

    fun at(value: String)

    fun include(body: AttachWorkspaceBuilder<C>.() -> Unit)

    fun include(spec: AttachWorkspaceSpec<C>)

    fun <C2> include(context: C2, body: AttachWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AttachWorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceSpec<C2>)
}

class AttachWorkspaceRef(internal val key: String)

internal data class AttachWorkspaceShell(var at: Scaffold<String>? = null) : Scaffold<AttachWorkspace> {
    override suspend fun resolve(registry: Registry): AttachWorkspace {
        val value = AttachWorkspace(
            at?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class AttachWorkspaceShellBuilder<out C>(override val context: C, internal var shell: AttachWorkspaceShell = AttachWorkspaceShell()) : AttachWorkspaceBuilder<C> {
    override fun at(value: String) {
        shell = shell.copy(at = Wrapper(value))
    }

    override fun include(body: AttachWorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AttachWorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AttachWorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AttachWorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AttachWorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AttachWorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttachWorkspaceShellBuilder<C2> = AttachWorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: AttachWorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class AttachWorkspaceScaffolder<in C>(internal val spec: AttachWorkspaceSpec<C>) : Scaffolder<C, AttachWorkspace> {
    override fun createScaffold(context: C): Scaffold<AttachWorkspace> {
        val builder = AttachWorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
