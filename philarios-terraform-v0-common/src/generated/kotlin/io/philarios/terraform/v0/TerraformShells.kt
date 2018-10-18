package io.philarios.terraform.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

internal data class ConfigurationShell(
        var resources: List<Scaffold<Resource>> = emptyList(),
        var dataSources: List<Scaffold<DataSource>> = emptyList(),
        var providers: List<Scaffold<Provider>> = emptyList(),
        var variables: List<Scaffold<Variable>> = emptyList(),
        var outputs: List<Scaffold<Output>> = emptyList()
) : Scaffold<Configuration> {
    override suspend fun resolve(registry: Registry): Configuration {
        coroutineScope {
        	resources.forEach { launch { it.resolve(registry) } }
        	dataSources.forEach { launch { it.resolve(registry) } }
        	providers.forEach { launch { it.resolve(registry) } }
        	variables.forEach { launch { it.resolve(registry) } }
        	outputs.forEach { launch { it.resolve(registry) } }
        }
        val value = Configuration(resources.map { it.resolve(registry) },dataSources.map { it.resolve(registry) },providers.map { it.resolve(registry) },variables.map { it.resolve(registry) },outputs.map { it.resolve(registry) })
        return value
    }
}

internal data class ResourceShell(
        var type: String? = null,
        var name: String? = null,
        var config: Map<String, Any>? = emptyMap()
) : Scaffold<Resource> {
    override suspend fun resolve(registry: Registry): Resource {
        checkNotNull(type) { "Resource is missing the type property" }
        checkNotNull(name) { "Resource is missing the name property" }
        val value = Resource(type!!,name!!,config!!)
        return value
    }
}

internal data class DataSourceShell(
        var type: String? = null,
        var name: String? = null,
        var config: Map<String, Any>? = emptyMap()
) : Scaffold<DataSource> {
    override suspend fun resolve(registry: Registry): DataSource {
        checkNotNull(type) { "DataSource is missing the type property" }
        checkNotNull(name) { "DataSource is missing the name property" }
        val value = DataSource(type!!,name!!,config!!)
        return value
    }
}

internal data class ProviderShell(var name: String? = null, var config: Map<String, Any>? = emptyMap()) : Scaffold<Provider> {
    override suspend fun resolve(registry: Registry): Provider {
        checkNotNull(name) { "Provider is missing the name property" }
        val value = Provider(name!!,config!!)
        return value
    }
}

internal data class VariableShell(
        var name: String? = null,
        var type: String? = null,
        var default: Any? = null
) : Scaffold<Variable> {
    override suspend fun resolve(registry: Registry): Variable {
        checkNotNull(name) { "Variable is missing the name property" }
        checkNotNull(type) { "Variable is missing the type property" }
        checkNotNull(default) { "Variable is missing the default property" }
        val value = Variable(name!!,type!!,default!!)
        return value
    }
}

internal data class OutputShell(var name: String? = null, var value: Any? = null) : Scaffold<Output> {
    override suspend fun resolve(registry: Registry): Output {
        checkNotNull(name) { "Output is missing the name property" }
        checkNotNull(value) { "Output is missing the value property" }
        val value = Output(name!!,value!!)
        return value
    }
}
