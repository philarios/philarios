package io.philarios.terraform

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Provider(val name: String, val config: Map<String, Any>)

class ProviderSpec<in C>(internal val body: ProviderBuilder<C>.() -> Unit)

@DslBuilder
interface ProviderBuilder<out C> {
    val context: C

    fun name(value: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)

    fun include(body: ProviderBuilder<C>.() -> Unit)

    fun include(spec: ProviderSpec<C>)

    fun <C2> include(context: C2, body: ProviderBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ProviderSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ProviderBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ProviderSpec<C2>)
}

class ProviderRef(internal val key: String)

internal data class ProviderShell(var name: Scaffold<String>? = null, var config: Map<Scaffold<String>, Scaffold<Any>>? = null) : Scaffold<Provider> {
    override suspend fun resolve(registry: Registry): Provider {
        checkNotNull(name) { "Provider is missing the name property" }
        val value = Provider(
            name!!.let{ it.resolve(registry) },
            config.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

@DslBuilder
internal class ProviderShellBuilder<out C>(override val context: C, internal var shell: ProviderShell = ProviderShell()) : ProviderBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun config(key: String, value: Any) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + config.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun include(body: ProviderBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ProviderSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ProviderBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ProviderSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ProviderBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ProviderSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ProviderShellBuilder<C2> = ProviderShellBuilder(context, shell)

    private fun <C2> merge(other: ProviderShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ProviderScaffolder<in C>(internal val spec: ProviderSpec<C>) : Scaffolder<C, Provider> {
    override fun createScaffold(context: C): Scaffold<Provider> {
        val builder = ProviderShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
