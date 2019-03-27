package io.philarios.terraform

class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit)

class ResourceSpec<in C>(internal val body: ResourceBuilder<C>.() -> Unit)

class DataSourceSpec<in C>(internal val body: DataSourceBuilder<C>.() -> Unit)

class ProviderSpec<in C>(internal val body: ProviderBuilder<C>.() -> Unit)

class VariableSpec<in C>(internal val body: VariableBuilder<C>.() -> Unit)

class OutputSpec<in C>(internal val body: OutputBuilder<C>.() -> Unit)
