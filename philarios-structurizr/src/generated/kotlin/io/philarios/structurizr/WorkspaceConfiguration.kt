package io.philarios.structurizr

import io.philarios.core.Deferred
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

data class WorkspaceConfiguration(val users: List<User>)

class WorkspaceConfigurationSpec<in C>(internal val body: WorkspaceConfigurationBuilder<C>.() -> Unit)

@DslBuilder
interface WorkspaceConfigurationBuilder<out C> {
    val context: C

    fun user(body: UserBuilder<C>.() -> Unit)

    fun user(spec: UserSpec<C>)

    fun user(ref: UserRef)

    fun user(value: User)

    fun users(users: List<User>)

    fun include(body: WorkspaceConfigurationBuilder<C>.() -> Unit)

    fun include(spec: WorkspaceConfigurationSpec<C>)

    fun <C2> include(context: C2, body: WorkspaceConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkspaceConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceConfigurationSpec<C2>)
}

class WorkspaceConfigurationRef(internal val key: String)

internal data class WorkspaceConfigurationShell(var users: List<Scaffold<User>>? = null) : Scaffold<WorkspaceConfiguration> {
    override suspend fun resolve(registry: Registry): WorkspaceConfiguration {
        coroutineScope {
            users?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = WorkspaceConfiguration(
            users.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class WorkspaceConfigurationShellBuilder<out C>(override val context: C, internal var shell: WorkspaceConfigurationShell = WorkspaceConfigurationShell()) : WorkspaceConfigurationBuilder<C> {
    override fun user(body: UserBuilder<C>.() -> Unit) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder<C>(UserSpec<C>(body)).createScaffold(context))
    }

    override fun user(spec: UserSpec<C>) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder<C>(spec).createScaffold(context))
    }

    override fun user(ref: UserRef) {
        shell = shell.copy(users = shell.users.orEmpty() + Deferred(ref.key))
    }

    override fun user(value: User) {
        shell = shell.copy(users = shell.users.orEmpty() + Wrapper(value))
    }

    override fun users(users: List<User>) {
        shell = shell.copy(users = shell.users.orEmpty() + users.map { Wrapper(it) })
    }

    override fun include(body: WorkspaceConfigurationBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkspaceConfigurationSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkspaceConfigurationBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkspaceConfigurationSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceConfigurationBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceConfigurationSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkspaceConfigurationShellBuilder<C2> = WorkspaceConfigurationShellBuilder(context, shell)

    private fun <C2> merge(other: WorkspaceConfigurationShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkspaceConfigurationScaffolder<in C>(internal val spec: WorkspaceConfigurationSpec<C>) : Scaffolder<C, WorkspaceConfiguration> {
    override fun createScaffold(context: C): Scaffold<WorkspaceConfiguration> {
        val builder = WorkspaceConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
