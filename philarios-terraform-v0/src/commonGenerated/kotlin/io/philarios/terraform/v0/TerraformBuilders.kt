package io.philarios.terraform.v0

import io.philarios.core.v0.DslBuilder
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

@DslBuilder
interface ConfigurationBuilder<out C> {
    val context: C

    fun resource(body: ResourceBuilder<C>.() -> Unit)

    fun resource(spec: ResourceSpec<C>)

    fun resource(ref: ResourceRef)

    fun resource(resource: Resource)

    fun resources(resources: List<Resource>)

    fun dataSource(body: DataSourceBuilder<C>.() -> Unit)

    fun dataSource(spec: DataSourceSpec<C>)

    fun dataSource(ref: DataSourceRef)

    fun dataSource(dataSource: DataSource)

    fun dataSources(dataSources: List<DataSource>)

    fun provider(body: ProviderBuilder<C>.() -> Unit)

    fun provider(spec: ProviderSpec<C>)

    fun provider(ref: ProviderRef)

    fun provider(provider: Provider)

    fun providers(providers: List<Provider>)

    fun variable(body: VariableBuilder<C>.() -> Unit)

    fun variable(spec: VariableSpec<C>)

    fun variable(ref: VariableRef)

    fun variable(variable: Variable)

    fun variables(variables: List<Variable>)

    fun output(body: OutputBuilder<C>.() -> Unit)

    fun output(spec: OutputSpec<C>)

    fun output(ref: OutputRef)

    fun output(output: Output)

    fun outputs(outputs: List<Output>)

    fun include(body: ConfigurationBuilder<C>.() -> Unit)

    fun include(spec: ConfigurationSpec<C>)

    fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>)
}

@DslBuilder
interface ResourceBuilder<out C> {
    val context: C

    fun type(type: String)

    fun name(name: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)

    fun include(body: ResourceBuilder<C>.() -> Unit)

    fun include(spec: ResourceSpec<C>)

    fun <C2> include(context: C2, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>)
}

@DslBuilder
interface DataSourceBuilder<out C> {
    val context: C

    fun type(type: String)

    fun name(name: String)

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

@DslBuilder
interface ProviderBuilder<out C> {
    val context: C

    fun name(name: String)

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

@DslBuilder
interface VariableBuilder<out C> {
    val context: C

    fun name(name: String)

    fun type(type: String)

    fun default(default: Any)

    fun include(body: VariableBuilder<C>.() -> Unit)

    fun include(spec: VariableSpec<C>)

    fun <C2> include(context: C2, body: VariableBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: VariableSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: VariableBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: VariableSpec<C2>)
}

@DslBuilder
interface OutputBuilder<out C> {
    val context: C

    fun name(name: String)

    fun value(value: Any)

    fun include(body: OutputBuilder<C>.() -> Unit)

    fun include(spec: OutputSpec<C>)

    fun <C2> include(context: C2, body: OutputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: OutputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: OutputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: OutputSpec<C2>)
}
