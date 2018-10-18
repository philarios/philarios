package io.philarios.terraform.v0

import io.philarios.core.v0.Scaffold
import kotlin.String

class ConfigurationRef(key: String) : Scaffold<Configuration> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.Configuration::class, key)

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.Resource::class, key)

class DataSourceRef(key: String) : Scaffold<DataSource> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.DataSource::class, key)

class ProviderRef(key: String) : Scaffold<Provider> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.Provider::class, key)

class VariableRef(key: String) : Scaffold<Variable> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.Variable::class, key)

class OutputRef(key: String) : Scaffold<Output> by io.philarios.core.v0.RegistryRef(io.philarios.terraform.v0.Output::class, key)
