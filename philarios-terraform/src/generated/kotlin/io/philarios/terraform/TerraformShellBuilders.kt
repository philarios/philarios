package io.philarios.terraform

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.Any
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

@DslBuilder
internal class ConfigurationShellBuilder<out C>(override val context: C, internal var shell: ConfigurationShell = ConfigurationShell()) : ConfigurationBuilder<C> {
    override fun resource(body: ResourceBuilder<C>.() -> Unit) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(ResourceSpec<C>(body)).createScaffold(context))
    }

    override fun resource(spec: ResourceSpec<C>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ResourceScaffolder<C>(spec).createScaffold(context))
    }

    override fun resource(ref: ResourceRef) {
        shell = shell.copy(resources = shell.resources.orEmpty() + ref)
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
        shell = shell.copy(dataSources = shell.dataSources.orEmpty() + ref)
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
        shell = shell.copy(providers = shell.providers.orEmpty() + ref)
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
        shell = shell.copy(variables = shell.variables.orEmpty() + ref)
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
        shell = shell.copy(outputs = shell.outputs.orEmpty() + ref)
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

@DslBuilder
internal class ResourceShellBuilder<out C>(override val context: C, internal var shell: ResourceShell = ResourceShell()) : ResourceBuilder<C> {
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

    override fun include(body: ResourceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ResourceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ResourceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ResourceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ResourceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceShellBuilder<C2> = ResourceShellBuilder(context, shell)

    private fun <C2> merge(other: ResourceShellBuilder<C2>) {
        this.shell = other.shell
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

@DslBuilder
internal class VariableShellBuilder<out C>(override val context: C, internal var shell: VariableShell = VariableShell()) : VariableBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun default(value: Any) {
        shell = shell.copy(default = Wrapper(value))
    }

    override fun include(body: VariableBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: VariableSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: VariableBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: VariableSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: VariableBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: VariableSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): VariableShellBuilder<C2> = VariableShellBuilder(context, shell)

    private fun <C2> merge(other: VariableShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class OutputShellBuilder<out C>(override val context: C, internal var shell: OutputShell = OutputShell()) : OutputBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun value(value: Any) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: OutputBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: OutputSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: OutputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: OutputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: OutputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: OutputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): OutputShellBuilder<C2> = OutputShellBuilder(context, shell)

    private fun <C2> merge(other: OutputShellBuilder<C2>) {
        this.shell = other.shell
    }
}
