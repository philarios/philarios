package io.philarios.terraform

import io.philarios.core.Scaffold
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class ConfigurationShell(
        var resources: List<Scaffold<Resource>>? = null,
        var dataSources: List<Scaffold<DataSource>>? = null,
        var providers: List<Scaffold<Provider>>? = null,
        var variables: List<Scaffold<Variable>>? = null,
        var outputs: List<Scaffold<Output>>? = null
) : Scaffold<Configuration> {
    override suspend fun resolve(registry: Registry): Configuration {
        coroutineScope {
            resources?.let{ it.forEach { launch { it.resolve(registry) } } }
            dataSources?.let{ it.forEach { launch { it.resolve(registry) } } }
            providers?.let{ it.forEach { launch { it.resolve(registry) } } }
            variables?.let{ it.forEach { launch { it.resolve(registry) } } }
            outputs?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Configuration(
            resources.orEmpty().let{ it.map { it.resolve(registry) } },
            dataSources.orEmpty().let{ it.map { it.resolve(registry) } },
            providers.orEmpty().let{ it.map { it.resolve(registry) } },
            variables.orEmpty().let{ it.map { it.resolve(registry) } },
            outputs.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ResourceShell(
        var type: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var config: Map<Scaffold<String>, Scaffold<Any>>? = null
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        checkNotNull(type) { "Resource is missing the type property" }
        checkNotNull(name) { "Resource is missing the name property" }
        val value = Resource(
            type!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            config.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

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

internal data class VariableShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var default: Scaffold<Any>? = null
) : Scaffold<Variable> {
    override suspend fun resolve(registry: Registry): Variable {
        checkNotNull(name) { "Variable is missing the name property" }
        checkNotNull(type) { "Variable is missing the type property" }
        checkNotNull(default) { "Variable is missing the default property" }
        val value = Variable(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            default!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class OutputShell(var name: Scaffold<String>? = null, var value: Scaffold<Any>? = null) : Scaffold<Output> {
    override suspend fun resolve(registry: Registry): Output {
        checkNotNull(name) { "Output is missing the name property" }
        checkNotNull(value) { "Output is missing the value property" }
        val value = Output(
            name!!.let{ it.resolve(registry) },
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}
