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

data class StoreArtifacts(val path: String?, val destination: String?)

class StoreArtifactsSpec<in C>(internal val body: StoreArtifactsBuilder<C>.() -> Unit)

@DslBuilder
interface StoreArtifactsBuilder<out C> {
    val context: C

    fun path(value: String)

    fun destination(value: String)

    fun include(body: StoreArtifactsBuilder<C>.() -> Unit)

    fun include(spec: StoreArtifactsSpec<C>)

    fun <C2> include(context: C2, body: StoreArtifactsBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreArtifactsSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsSpec<C2>)
}

class StoreArtifactsRef(internal val key: String)

internal data class StoreArtifactsShell(var path: Scaffold<String>? = null, var destination: Scaffold<String>? = null) : Scaffold<StoreArtifacts> {
    override suspend fun resolve(registry: Registry): StoreArtifacts {
        val value = StoreArtifacts(
            path?.let{ it.resolve(registry) },
            destination?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class StoreArtifactsShellBuilder<out C>(override val context: C, internal var shell: StoreArtifactsShell = StoreArtifactsShell()) : StoreArtifactsBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun destination(value: String) {
        shell = shell.copy(destination = Wrapper(value))
    }

    override fun include(body: StoreArtifactsBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreArtifactsSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreArtifactsBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreArtifactsSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreArtifactsBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreArtifactsSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreArtifactsShellBuilder<C2> = StoreArtifactsShellBuilder(context, shell)

    private fun <C2> merge(other: StoreArtifactsShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class StoreArtifactsScaffolder<in C>(internal val spec: StoreArtifactsSpec<C>) : Scaffolder<C, StoreArtifacts> {
    override fun createScaffold(context: C): Scaffold<StoreArtifacts> {
        val builder = StoreArtifactsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
