package io.philarios.terraform

import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

data class Configuration(
        val resources: List<Resource>,
        val dataSources: List<DataSource>,
        val providers: List<Provider>,
        val variables: List<Variable>,
        val outputs: List<Output>
)

data class Resource(
        val type: String,
        val name: String,
        val config: Map<String, Any>
)

data class DataSource(
        val type: String,
        val name: String,
        val config: Map<String, Any>
)

data class Provider(val name: String, val config: Map<String, Any>)

data class Variable(
        val name: String,
        val type: String,
        val default: Any
)

data class Output(val name: String, val value: Any)
