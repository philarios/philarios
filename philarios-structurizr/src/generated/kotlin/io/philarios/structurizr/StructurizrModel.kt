package io.philarios.structurizr

import kotlin.String
import kotlin.collections.List

data class Workspace(
        val name: String,
        val description: String,
        val model: Model?
)

data class Model(val people: List<Person>, val softwareSystems: List<SoftwareSystem>)

data class Person(
        val name: String,
        val description: String,
        val location: Location?
)

data class SoftwareSystem(
        val name: String,
        val description: String,
        val location: Location?,
        val containers: List<Container>
)

data class Container(
        val name: String,
        val description: String,
        val technology: String
)

enum class Location {
    Internal,

    External,

    Unspecified
}
