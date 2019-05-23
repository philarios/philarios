// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.terraform

import io.philarios.core.DslBuilder
import io.philarios.core.RefScaffold
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TerraformScaffolder(internal val spec: TerraformSpec) : Scaffolder<Terraform> {
    override fun createScaffold(): Scaffold<Terraform> {
        val builder = TerraformScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TerraformScaffoldBuilder(internal var scaffold: TerraformScaffold = TerraformScaffold()) : TerraformBuilder {
    override fun resource(body: ResourceBuilder.() -> Unit) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ResourceScaffolder(ResourceSpec(body)).createScaffold())
    }

    override fun resource(spec: ResourceSpec) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ResourceScaffolder(spec).createScaffold())
    }

    override fun resource(ref: ResourceRef) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + RefScaffold(ref.key))
    }

    override fun resource(value: Resource) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + ValueScaffold(value))
    }

    override fun resources(resources: List<Resource>) {
        scaffold = scaffold.copy(resources = scaffold.resources.orEmpty() + resources.map { ValueScaffold(it) })
    }

    override fun dataSource(body: DataSourceBuilder.() -> Unit) {
        scaffold = scaffold.copy(dataSources = scaffold.dataSources.orEmpty() + DataSourceScaffolder(DataSourceSpec(body)).createScaffold())
    }

    override fun dataSource(spec: DataSourceSpec) {
        scaffold = scaffold.copy(dataSources = scaffold.dataSources.orEmpty() + DataSourceScaffolder(spec).createScaffold())
    }

    override fun dataSource(ref: DataSourceRef) {
        scaffold = scaffold.copy(dataSources = scaffold.dataSources.orEmpty() + RefScaffold(ref.key))
    }

    override fun dataSource(value: DataSource) {
        scaffold = scaffold.copy(dataSources = scaffold.dataSources.orEmpty() + ValueScaffold(value))
    }

    override fun dataSources(dataSources: List<DataSource>) {
        scaffold = scaffold.copy(dataSources = scaffold.dataSources.orEmpty() + dataSources.map { ValueScaffold(it) })
    }

    override fun provider(body: ProviderBuilder.() -> Unit) {
        scaffold = scaffold.copy(providers = scaffold.providers.orEmpty() + ProviderScaffolder(ProviderSpec(body)).createScaffold())
    }

    override fun provider(spec: ProviderSpec) {
        scaffold = scaffold.copy(providers = scaffold.providers.orEmpty() + ProviderScaffolder(spec).createScaffold())
    }

    override fun provider(ref: ProviderRef) {
        scaffold = scaffold.copy(providers = scaffold.providers.orEmpty() + RefScaffold(ref.key))
    }

    override fun provider(value: Provider) {
        scaffold = scaffold.copy(providers = scaffold.providers.orEmpty() + ValueScaffold(value))
    }

    override fun providers(providers: List<Provider>) {
        scaffold = scaffold.copy(providers = scaffold.providers.orEmpty() + providers.map { ValueScaffold(it) })
    }

    override fun variable(body: VariableBuilder.() -> Unit) {
        scaffold = scaffold.copy(variables = scaffold.variables.orEmpty() + VariableScaffolder(VariableSpec(body)).createScaffold())
    }

    override fun variable(spec: VariableSpec) {
        scaffold = scaffold.copy(variables = scaffold.variables.orEmpty() + VariableScaffolder(spec).createScaffold())
    }

    override fun variable(ref: VariableRef) {
        scaffold = scaffold.copy(variables = scaffold.variables.orEmpty() + RefScaffold(ref.key))
    }

    override fun variable(value: Variable) {
        scaffold = scaffold.copy(variables = scaffold.variables.orEmpty() + ValueScaffold(value))
    }

    override fun variables(variables: List<Variable>) {
        scaffold = scaffold.copy(variables = scaffold.variables.orEmpty() + variables.map { ValueScaffold(it) })
    }

    override fun output(body: OutputBuilder.() -> Unit) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + OutputScaffolder(OutputSpec(body)).createScaffold())
    }

    override fun output(spec: OutputSpec) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + OutputScaffolder(spec).createScaffold())
    }

    override fun output(ref: OutputRef) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + RefScaffold(ref.key))
    }

    override fun output(value: Output) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + ValueScaffold(value))
    }

    override fun outputs(outputs: List<Output>) {
        scaffold = scaffold.copy(outputs = scaffold.outputs.orEmpty() + outputs.map { ValueScaffold(it) })
    }
}

internal data class TerraformScaffold(
        var resources: List<Scaffold<Resource>>? = null,
        var dataSources: List<Scaffold<DataSource>>? = null,
        var providers: List<Scaffold<Provider>>? = null,
        var variables: List<Scaffold<Variable>>? = null,
        var outputs: List<Scaffold<Output>>? = null
) : Scaffold<Terraform> {
    override suspend fun resolve(registry: Registry): Terraform {
        coroutineScope {
            resources?.let{ it.forEach { launch { it.resolve(registry) } } }
            dataSources?.let{ it.forEach { launch { it.resolve(registry) } } }
            providers?.let{ it.forEach { launch { it.resolve(registry) } } }
            variables?.let{ it.forEach { launch { it.resolve(registry) } } }
            outputs?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Terraform(
            resources.orEmpty().let{ it.map { it.resolve(registry) } },
            dataSources.orEmpty().let{ it.map { it.resolve(registry) } },
            providers.orEmpty().let{ it.map { it.resolve(registry) } },
            variables.orEmpty().let{ it.map { it.resolve(registry) } },
            outputs.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ResourceScaffolder(internal val spec: ResourceSpec) : Scaffolder<Resource> {
    override fun createScaffold(): Scaffold<Resource> {
        val builder = ResourceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ResourceScaffoldBuilder(internal var scaffold: ResourceScaffold = ResourceScaffold()) : ResourceBuilder {
    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class ResourceScaffold(
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

class DataSourceScaffolder(internal val spec: DataSourceSpec) : Scaffolder<DataSource> {
    override fun createScaffold(): Scaffold<DataSource> {
        val builder = DataSourceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class DataSourceScaffoldBuilder(internal var scaffold: DataSourceScaffold = DataSourceScaffold()) : DataSourceBuilder {
    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class DataSourceScaffold(
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

class ProviderScaffolder(internal val spec: ProviderSpec) : Scaffolder<Provider> {
    override fun createScaffold(): Scaffold<Provider> {
        val builder = ProviderScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ProviderScaffoldBuilder(internal var scaffold: ProviderScaffold = ProviderScaffold()) : ProviderBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        scaffold = scaffold.copy(config = scaffold.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
    }
}

internal data class ProviderScaffold(var name: Scaffold<String>? = null, var config: Map<Scaffold<String>, Scaffold<Any>>? = null) : Scaffold<Provider> {
    override suspend fun resolve(registry: Registry): Provider {
        checkNotNull(name) { "Provider is missing the name property" }
        val value = Provider(
            name!!.let{ it.resolve(registry) },
            config.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

class VariableScaffolder(internal val spec: VariableSpec) : Scaffolder<Variable> {
    override fun createScaffold(): Scaffold<Variable> {
        val builder = VariableScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class VariableScaffoldBuilder(internal var scaffold: VariableScaffold = VariableScaffold()) : VariableBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun type(value: String) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }

    override fun default(value: Any) {
        scaffold = scaffold.copy(default = ValueScaffold(value))
    }
}

internal data class VariableScaffold(
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

class OutputScaffolder(internal val spec: OutputSpec) : Scaffolder<Output> {
    override fun createScaffold(): Scaffold<Output> {
        val builder = OutputScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class OutputScaffoldBuilder(internal var scaffold: OutputScaffold = OutputScaffold()) : OutputBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun value(value: Any) {
        scaffold = scaffold.copy(value = ValueScaffold(value))
    }
}

internal data class OutputScaffold(var name: Scaffold<String>? = null, var value: Scaffold<Any>? = null) : Scaffold<Output> {
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
