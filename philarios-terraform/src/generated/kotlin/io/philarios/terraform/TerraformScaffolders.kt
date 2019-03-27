package io.philarios.terraform

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class ConfigurationScaffolder<in C>(internal val spec: ConfigurationSpec<C>) : Scaffolder<C, Configuration> {
    override fun createScaffold(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ResourceScaffolder<in C>(internal val spec: ResourceSpec<C>) : Scaffolder<C, Resource> {
    override fun createScaffold(context: C): Scaffold<Resource> {
        val builder = ResourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DataSourceScaffolder<in C>(internal val spec: DataSourceSpec<C>) : Scaffolder<C, DataSource> {
    override fun createScaffold(context: C): Scaffold<DataSource> {
        val builder = DataSourceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ProviderScaffolder<in C>(internal val spec: ProviderSpec<C>) : Scaffolder<C, Provider> {
    override fun createScaffold(context: C): Scaffold<Provider> {
        val builder = ProviderShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class VariableScaffolder<in C>(internal val spec: VariableSpec<C>) : Scaffolder<C, Variable> {
    override fun createScaffold(context: C): Scaffold<Variable> {
        val builder = VariableShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class OutputScaffolder<in C>(internal val spec: OutputSpec<C>) : Scaffolder<C, Output> {
    override fun createScaffold(context: C): Scaffold<Output> {
        val builder = OutputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
