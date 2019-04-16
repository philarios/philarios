// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.terraform

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

class TerraformSpec(override val body: TerraformBuilder.() -> Unit) : Spec<TerraformBuilder>

@DslBuilder
interface TerraformBuilder : Builder<TerraformSpec, TerraformBuilder> {
    fun resource(body: ResourceBuilder.() -> Unit)

    fun resource(spec: ResourceSpec)

    fun resource(ref: ResourceRef)

    fun resource(value: Resource)

    fun resources(resources: List<Resource>)

    fun dataSource(body: DataSourceBuilder.() -> Unit)

    fun dataSource(spec: DataSourceSpec)

    fun dataSource(ref: DataSourceRef)

    fun dataSource(value: DataSource)

    fun dataSources(dataSources: List<DataSource>)

    fun provider(body: ProviderBuilder.() -> Unit)

    fun provider(spec: ProviderSpec)

    fun provider(ref: ProviderRef)

    fun provider(value: Provider)

    fun providers(providers: List<Provider>)

    fun variable(body: VariableBuilder.() -> Unit)

    fun variable(spec: VariableSpec)

    fun variable(ref: VariableRef)

    fun variable(value: Variable)

    fun variables(variables: List<Variable>)

    fun output(body: OutputBuilder.() -> Unit)

    fun output(spec: OutputSpec)

    fun output(ref: OutputRef)

    fun output(value: Output)

    fun outputs(outputs: List<Output>)
}

class TerraformRef(internal val key: String)

class ResourceSpec(override val body: ResourceBuilder.() -> Unit) : Spec<ResourceBuilder>

@DslBuilder
interface ResourceBuilder : Builder<ResourceSpec, ResourceBuilder> {
    fun type(value: String)

    fun name(value: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)
}

class ResourceRef(internal val key: String)

class DataSourceSpec(override val body: DataSourceBuilder.() -> Unit) : Spec<DataSourceBuilder>

@DslBuilder
interface DataSourceBuilder : Builder<DataSourceSpec, DataSourceBuilder> {
    fun type(value: String)

    fun name(value: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)
}

class DataSourceRef(internal val key: String)

class ProviderSpec(override val body: ProviderBuilder.() -> Unit) : Spec<ProviderBuilder>

@DslBuilder
interface ProviderBuilder : Builder<ProviderSpec, ProviderBuilder> {
    fun name(value: String)

    fun config(key: String, value: Any)

    fun config(pair: Pair<String, Any>)

    fun config(config: Map<String, Any>)
}

class ProviderRef(internal val key: String)

class VariableSpec(override val body: VariableBuilder.() -> Unit) : Spec<VariableBuilder>

@DslBuilder
interface VariableBuilder : Builder<VariableSpec, VariableBuilder> {
    fun name(value: String)

    fun type(value: String)

    fun default(value: Any)
}

class VariableRef(internal val key: String)

class OutputSpec(override val body: OutputBuilder.() -> Unit) : Spec<OutputBuilder>

@DslBuilder
interface OutputBuilder : Builder<OutputSpec, OutputBuilder> {
    fun name(value: String)

    fun value(value: Any)
}

class OutputRef(internal val key: String)
