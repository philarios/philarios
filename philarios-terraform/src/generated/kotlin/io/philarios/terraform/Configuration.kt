package io.philarios.terraform

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Configuration(
        val resources: List<Resource>,
        val dataSources: List<DataSource>,
        val providers: List<Provider>,
        val variables: List<Variable>,
        val outputs: List<Output>
)

class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit)

@DslBuilder
interface ConfigurationBuilder<out C> {
    val context: C

    fun resource(body: ResourceBuilder<C>.() -> Unit)

    fun resource(spec: ResourceSpec<C>)

    fun resource(ref: ResourceRef)

    fun resource(value: Resource)

    fun resources(resources: List<Resource>)

    fun dataSource(body: DataSourceBuilder<C>.() -> Unit)

    fun dataSource(spec: DataSourceSpec<C>)

    fun dataSource(ref: DataSourceRef)

    fun dataSource(value: DataSource)

    fun dataSources(dataSources: List<DataSource>)

    fun provider(body: ProviderBuilder<C>.() -> Unit)

    fun provider(spec: ProviderSpec<C>)

    fun provider(ref: ProviderRef)

    fun provider(value: Provider)

    fun providers(providers: List<Provider>)

    fun variable(body: VariableBuilder<C>.() -> Unit)

    fun variable(spec: VariableSpec<C>)

    fun variable(ref: VariableRef)

    fun variable(value: Variable)

    fun variables(variables: List<Variable>)

    fun output(body: OutputBuilder<C>.() -> Unit)

    fun output(spec: OutputSpec<C>)

    fun output(ref: OutputRef)

    fun output(value: Output)

    fun outputs(outputs: List<Output>)

    fun include(body: ConfigurationBuilder<C>.() -> Unit)

    fun include(spec: ConfigurationSpec<C>)

    fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>)
}

class ConfigurationRef(internal val key: String)

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

@DslBuilder
internal class ConfigurationShellBuilder<out C>(override val context: C, internal var shell: ConfigurationShell = ConfigurationShell()) : ConfigurationBuilder<C> {
    override fun resource(body: ResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(ResourceSpec<C>(body)).createScaffold(context))
    }

    override fun resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Deferred(ref.key))
    }

    override fun resource(value: Resource) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(value))
    }

    override fun resources(resources: List<Resource>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    override fun dataSource(body: DataSourceBuilder<C>.() -> Unit) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + DataSourceScaffolder<C>(DataSourceSpec<C>(body)).createScaffold(context))
    }

    override fun dataSource(spec: DataSourceSpec<C>) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + DataSourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun dataSource(ref: DataSourceRef) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + Deferred(ref.key))
    }

    override fun dataSource(value: DataSource) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + Wrapper(value))
    }

    override fun dataSources(dataSources: List<DataSource>) {
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + dataSources.map { Wrapper(it) })
    }

    override fun provider(body: ProviderBuilder<C>.() -> Unit) {
        shell = shell.copy(providers = shell.providers.orEmpty() + ProviderScaffolder<C>(ProviderSpec<C>(body)).createScaffold(context))
    }

    override fun provider(spec: ProviderSpec<C>) {
        shell = shell.copy(providers = shell.providers.orEmpty() + ProviderScaffolder<C>(spec).createScaffold(context))
    }

    override fun provider(ref: ProviderRef) {
        shell = shell.copy(providers = shell.providers.orEmpty() + Deferred(ref.key))
    }

    override fun provider(value: Provider) {
        shell = shell.copy(providers = shell.providers.orEmpty() + Wrapper(value))
    }

    override fun providers(providers: List<Provider>) {
        shell = shell.copy(providers = shell.providers.orEmpty() + providers.map { Wrapper(it) })
    }

    override fun variable(body: VariableBuilder<C>.() -> Unit) {
        shell = shell.copy(variables = shell.variables.orEmpty() + VariableScaffolder<C>(VariableSpec<C>(body)).createScaffold(context))
    }

    override fun variable(spec: VariableSpec<C>) {
        shell = shell.copy(variables = shell.variables.orEmpty() + VariableScaffolder<C>(spec).createScaffold(context))
    }

    override fun variable(ref: VariableRef) {
        shell = shell.copy(variables = shell.variables.orEmpty() + Deferred(ref.key))
    }

    override fun variable(value: Variable) {
        shell = shell.copy(variables = shell.variables.orEmpty() + Wrapper(value))
    }

    override fun variables(variables: List<Variable>) {
        shell = shell.copy(variables = shell.variables.orEmpty() + variables.map { Wrapper(it) })
    }

    override fun output(body: OutputBuilder<C>.() -> Unit) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + OutputScaffolder<C>(OutputSpec<C>(body)).createScaffold(context))
    }

    override fun output(spec: OutputSpec<C>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + OutputScaffolder<C>(spec).createScaffold(context))
    }

    override fun output(ref: OutputRef) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Deferred(ref.key))
    }

    override fun output(value: Output) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + Wrapper(value))
    }

    override fun outputs(outputs: List<Output>) {
        shell = shell.copy(outputs = shell.outputs.orEmpty() + outputs.map { Wrapper(it) })
    }

    override fun include(body: ConfigurationBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ConfigurationSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ConfigurationSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConfigurationShellBuilder<C2> = ConfigurationShellBuilder(context, shell)

    private fun <C2> merge(other: ConfigurationShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ConfigurationScaffolder<in C>(internal val spec: ConfigurationSpec<C>) : Scaffolder<C, Configuration> {
    override fun createScaffold(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
