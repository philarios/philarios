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

data class Auth(val username: String?, val password: String?)

class AuthSpec<in C>(internal val body: AuthBuilder<C>.() -> Unit)

@DslBuilder
interface AuthBuilder<out C> {
    val context: C

    fun username(value: String)

    fun password(value: String)

    fun include(body: AuthBuilder<C>.() -> Unit)

    fun include(spec: AuthSpec<C>)

    fun <C2> include(context: C2, body: AuthBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AuthSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AuthBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AuthSpec<C2>)
}

class AuthRef(internal val key: String)

internal data class AuthShell(var username: Scaffold<String>? = null, var password: Scaffold<String>? = null) : Scaffold<Auth> {
    override suspend fun resolve(registry: Registry): Auth {
        val value = Auth(
            username?.let{ it.resolve(registry) },
            password?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class AuthShellBuilder<out C>(override val context: C, internal var shell: AuthShell = AuthShell()) : AuthBuilder<C> {
    override fun username(value: String) {
        shell = shell.copy(username = Wrapper(value))
    }

    override fun password(value: String) {
        shell = shell.copy(password = Wrapper(value))
    }

    override fun include(body: AuthBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AuthSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AuthBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AuthSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AuthBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AuthSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AuthShellBuilder<C2> = AuthShellBuilder(context, shell)

    private fun <C2> merge(other: AuthShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class AuthScaffolder<in C>(internal val spec: AuthSpec<C>) : Scaffolder<C, Auth> {
    override fun createScaffold(context: C): Scaffold<Auth> {
        val builder = AuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
