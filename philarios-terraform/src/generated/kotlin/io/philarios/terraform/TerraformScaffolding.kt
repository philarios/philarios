// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.terraform

import io.philarios.core.RefScaffold
import io.philarios.core.DslBuilder
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
        val builder = TerraformShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TerraformShellBuilder(internal var shell: TerraformShell = TerraformShell()) : TerraformBuilder {
    override fun resource(body: ResourceBuilder.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder(ResourceSpec(body)).createScaffold())
    }

    override fun resource(spec: ResourceSpec) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder(spec).createScaffold())
    }

    override fun resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + RefScaffold(ref.key))
    }

    override fun resource(value: Resource) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ValueScaffold(value))
    }

    override fun resources(resources: List<Resource>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { ValueScaffold(it) })
    }

    override fun dataSource(body: DataSourceBuilder.() -> Unit) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + DataSourceScaffolder(DataSourceSpec(body)).createScaffold())
    }

    override fun dataSource(spec: DataSourceSpec) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + DataSourceScaffolder(spec).createScaffold())
    }

    override fun dataSource(ref: DataSourceRef) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + RefScaffold(ref.key))
    }

    override fun dataSource(value: DataSource) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + ValueScaffold(value))
    }

    override fun dataSources(dataSources: List<DataSource>) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + dataSources.map { ValueScaffold(it) })
    }

    override fun provider(body: ProviderBuilder.() -> Unit) {
        shell = shell.copy(providers = shell.providers.orEmpty() + ProviderScaffolder(ProviderSpec(body)).createScaffold())
    }

    override fun provider(spec: ProviderSpec) {
        shell = shell.copy(providers = shell.providers.orEmpty() + ProviderScaffolder(spec).createScaffold())
    }

    override fun provider(ref: ProviderRef) {
        shell = shell.copy(providers = shell.providers.orEmpty() + RefScaffold(ref.key))
    }

    override fun provider(value: Provider) {
        shell = shell.copy(providers = shell.providers.orEmpty() + ValueScaffold(value))
    }

    override fun providers(providers: List<Provider>) {
        shell = shell.copy(providers = shell.providers.orEmpty() + providers.map { ValueScaffold(it) })
    }

    override fun variable(body: VariableBuilder.() -> Unit) {
        shell = shell.copy(variables = shell.variables.orEmpty() + VariableScaffolder(VariableSpec(body)).createScaffold())
    }

    override fun variable(spec: VariableSpec) {
        shell = shell.copy(variables = shell.variables.orEmpty() + VariableScaffolder(spec).createScaffold())
    }

    override fun variable(ref: VariableRef) {
        shell = shell.copy(variables = shell.variables.orEmpty() + RefScaffold(ref.key))
    }

    override fun variable(value: Variable) {
        shell = shell.copy(variables = shell.variables.orEmpty() + ValueScaffold(value))
    }

    override fun variables(variables: List<Variable>) {
        shell = shell.copy(variables = shell.variables.orEmpty() + variables.map { ValueScaffold(it) })
    }

    override fun output(body: OutputBuilder.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + OutputScaffolder(OutputSpec(body)).createScaffold())
    }

    override fun output(spec: OutputSpec) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + OutputScaffolder(spec).createScaffold())
    }

    override fun output(ref: OutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + RefScaffold(ref.key))
    }

    override fun output(value: Output) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + ValueScaffold(value))
    }

    override fun outputs(outputs: List<Output>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { ValueScaffold(it) })
    }
}

internal data class TerraformShell(
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
        val builder = ResourceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ResourceShellBuilder(internal var shell: ResourceShell = ResourceShell()) : ResourceBuilder {
    override fun type(value: String) {
        shell = shell.copy(type = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
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

class DataSourceScaffolder(internal val spec: DataSourceSpec) : Scaffolder<DataSource> {
    override fun createScaffold(): Scaffold<DataSource> {
        val builder = DataSourceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DataSourceShellBuilder(internal var shell: DataSourceShell = DataSourceShell()) : DataSourceBuilder {
    override fun type(value: String) {
        shell = shell.copy(type = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
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

class ProviderScaffolder(internal val spec: ProviderSpec) : Scaffolder<Provider> {
    override fun createScaffold(): Scaffold<Provider> {
        val builder = ProviderShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ProviderShellBuilder(internal var shell: ProviderShell = ProviderShell()) : ProviderBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun config(key: String, value: Any) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(key),ValueScaffold(value)))
    }

    override fun config(pair: Pair<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + Pair(ValueScaffold(pair.first), ValueScaffold(pair.second)))
    }

    override fun config(config: Map<String, Any>) {
        shell = shell.copy(config = shell.config.orEmpty() + config.map { Pair(ValueScaffold(it.key), ValueScaffold(it.value)) })
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

class VariableScaffolder(internal val spec: VariableSpec) : Scaffolder<Variable> {
    override fun createScaffold(): Scaffold<Variable> {
        val builder = VariableShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class VariableShellBuilder(internal var shell: VariableShell = VariableShell()) : VariableBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = ValueScaffold(value))
    }

    override fun default(value: Any) {
        shell = shell.copy(default = ValueScaffold(value))
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

class OutputScaffolder(internal val spec: OutputSpec) : Scaffolder<Output> {
    override fun createScaffold(): Scaffold<Output> {
        val builder = OutputShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class OutputShellBuilder(internal var shell: OutputShell = OutputShell()) : OutputBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun value(value: Any) {
        shell = shell.copy(value = ValueScaffold(value))
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
