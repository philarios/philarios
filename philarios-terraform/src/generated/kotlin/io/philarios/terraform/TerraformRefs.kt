package io.philarios.terraform

import io.philarios.core.Scaffold
import kotlin.String

class ConfigurationRef(key: String) : Scaffold<Configuration> by io.philarios.core.RegistryRef(io.philarios.terraform.Configuration::class, key)

class ResourceRef(key: String) : Scaffold<Resource> by io.philarios.core.RegistryRef(io.philarios.terraform.Resource::class, key)

class DataSourceRef(key: String) : Scaffold<DataSource> by io.philarios.core.RegistryRef(io.philarios.terraform.DataSource::class, key)

class ProviderRef(key: String) : Scaffold<Provider> by io.philarios.core.RegistryRef(io.philarios.terraform.Provider::class, key)

class VariableRef(key: String) : Scaffold<Variable> by io.philarios.core.RegistryRef(io.philarios.terraform.Variable::class, key)

class OutputRef(key: String) : Scaffold<Output> by io.philarios.core.RegistryRef(io.philarios.terraform.Output::class, key)
