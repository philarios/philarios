package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class User(val username: String, val role: Role)

class UserSpec<in C>(internal val body: UserBuilder<C>.() -> Unit)

@DslBuilder
interface UserBuilder<out C> {
    val context: C

    fun username(value: String)

    fun role(value: Role)

    fun include(body: UserBuilder<C>.() -> Unit)

    fun include(spec: UserSpec<C>)

    fun <C2> include(context: C2, body: UserBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: UserSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: UserBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: UserSpec<C2>)
}

class UserRef(internal val key: String)

internal data class UserShell(var username: Scaffold<String>? = null, var role: Scaffold<Role>? = null) : Scaffold<User> {
    override suspend fun resolve(registry: Registry): User {
        checkNotNull(username) { "User is missing the username property" }
        checkNotNull(role) { "User is missing the role property" }
        val value = User(
            username!!.let{ it.resolve(registry) },
            role!!.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class UserShellBuilder<out C>(override val context: C, internal var shell: UserShell = UserShell()) : UserBuilder<C> {
    override fun username(value: String) {
        shell = shell.copy(username = Wrapper(value))
    }

    override fun role(value: Role) {
        shell = shell.copy(role = Wrapper(value))
    }

    override fun include(body: UserBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: UserSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: UserBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: UserSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: UserBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: UserSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): UserShellBuilder<C2> = UserShellBuilder(context, shell)

    private fun <C2> merge(other: UserShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class UserScaffolder<in C>(internal val spec: UserSpec<C>) : Scaffolder<C, User> {
    override fun createScaffold(context: C): Scaffold<User> {
        val builder = UserShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
