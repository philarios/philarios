package io.philarios.terraform.v0

import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec

open class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit) : Spec<C, Configuration> {
    override fun connect(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit) : Spec<C, Resource> {
    override fun connect(context: C): Scaffold<Resource> {
        val builder = ResourceShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class DataSourceSpec<in C>(internal val body: DataSourceBuilder<C>.() -> Unit) : Spec<C, DataSource> {
    override fun connect(context: C): Scaffold<DataSource> {
        val builder = DataSourceShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class ProviderSpec<in C>(internal val body: ProviderBuilder<C>.() -> Unit) : Spec<C, Provider> {
    override fun connect(context: C): Scaffold<Provider> {
        val builder = ProviderShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class VariableSpec<in C>(internal val body: VariableBuilder<C>.() -> Unit) : Spec<C, Variable> {
    override fun connect(context: C): Scaffold<Variable> {
        val builder = VariableShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class OutputSpec<in C>(internal val body: OutputBuilder<C>.() -> Unit) : Spec<C, Output> {
    override fun connect(context: C): Scaffold<Output> {
        val builder = OutputShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
