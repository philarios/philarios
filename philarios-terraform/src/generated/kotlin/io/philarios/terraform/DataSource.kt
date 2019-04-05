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

data class DataSource(
        val type: String,
        val name: String,
        val config: Map<String, Any>
)

class DataSourceSpec<in C>(internal val body: DataSourceBuilder<C>.() -> Unit)

@DslBuilder
interface DataSourceBuilder<out C> {
    val context: C

    fun type(value: String)

    fun name(value: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)

    fun include(body: DataSourceBuilder<C>.() -> Unit)

    fun include(spec: DataSourceSpec<C>)

    fun <C2> include(context: C2, body: DataSourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DataSourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DataSourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DataSourceSpec<C2>)
}

class DataSourceRef(internal val key: String)

internal data class DataSourceShell(
        var type: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var config: Map<Scaffold<String>, Scaffold<Any>>? = null
) : Scaffold<DataSource> {
    override suspend fun resolve(registry: Registry): DataSource {
        checkNotNull(type) { "DataSource is missing the type property" }
        checkNotNull(name) { "DataSource is missing the name property" }
        val value = DataSource(
            type!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            config.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

@DslBuilder
internal class DataSourceShellBuilder<out C>(override val context: C, internal var shell: DataSourceShell = DataSourceShell()) : DataSourceBuilder<C> {
    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

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

    override fun include(body: DataSourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DataSourceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DataSourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DataSourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DataSourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DataSourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DataSourceShellBuilder<C2> = DataSourceShellBuilder(context, shell)

    private fun <C2> merge(other: DataSourceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class DataSourceScaffolder<in C>(internal val spec: DataSourceSpec<C>) : Scaffolder<C, DataSource> {
    override fun createScaffold(context: C): Scaffold<DataSource> {
        val builder = DataSourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
