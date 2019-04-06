// The model of your schema written as pure Kotlin classes.
//
// Because the model expresses the high-level domain, nothing in this file will depend on the generator or on any
// of the other files. This is done to ensure that you could potentially take this file and reuse the classes
// without having a dependency on the specs or materialization process.
//
// If you feel like something is preventing you from separating the model classes from the specific specs, builders,
// or materialization, please feel free to open an issue in the project's repository.
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
