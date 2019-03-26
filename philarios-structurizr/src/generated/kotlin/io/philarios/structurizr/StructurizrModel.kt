package io.philarios.structurizr

import kotlin.String
import kotlin.collections.List

data class Workspace(
        val name: String,
        val description: String,
        val model: Model?
)

data class Model(val people: List<Person>)

data class Person(
        val name: String,
        val description: String,
        val location: Location?
)

enum class Location {
    Internal,

    External,

    Unspecified
}
