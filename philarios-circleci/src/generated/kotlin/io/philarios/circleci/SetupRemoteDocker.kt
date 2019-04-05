package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class SetupRemoteDocker(val docker_layer_caching: Boolean?, val version: String?)

class SetupRemoteDockerSpec<in C>(internal val body: SetupRemoteDockerBuilder<C>.() -> Unit)

@DslBuilder
interface SetupRemoteDockerBuilder<out C> {
    val context: C

    fun docker_layer_caching(value: Boolean)

    fun version(value: String)

    fun include(body: SetupRemoteDockerBuilder<C>.() -> Unit)

    fun include(spec: SetupRemoteDockerSpec<C>)

    fun <C2> include(context: C2, body: SetupRemoteDockerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SetupRemoteDockerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerSpec<C2>)
}

class SetupRemoteDockerRef(internal val key: String)

internal data class SetupRemoteDockerShell(var docker_layer_caching: Scaffold<Boolean>? = null, var version: Scaffold<String>? = null) : Scaffold<SetupRemoteDocker> {
    override suspend fun resolve(registry: Registry): SetupRemoteDocker {
        val value = SetupRemoteDocker(
            docker_layer_caching?.let{ it.resolve(registry) },
            version?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class SetupRemoteDockerShellBuilder<out C>(override val context: C, internal var shell: SetupRemoteDockerShell = SetupRemoteDockerShell()) : SetupRemoteDockerBuilder<C> {
    override fun docker_layer_caching(value: Boolean) {
        shell = shell.copy(docker_layer_caching = Wrapper(value))
    }

    override fun version(value: String) {
        shell = shell.copy(version = Wrapper(value))
    }

    override fun include(body: SetupRemoteDockerBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SetupRemoteDockerSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SetupRemoteDockerBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SetupRemoteDockerSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SetupRemoteDockerBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SetupRemoteDockerSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SetupRemoteDockerShellBuilder<C2> = SetupRemoteDockerShellBuilder(context, shell)

    private fun <C2> merge(other: SetupRemoteDockerShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class SetupRemoteDockerScaffolder<in C>(internal val spec: SetupRemoteDockerSpec<C>) : Scaffolder<C, SetupRemoteDocker> {
    override fun createScaffold(context: C): Scaffold<SetupRemoteDocker> {
        val builder = SetupRemoteDockerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
