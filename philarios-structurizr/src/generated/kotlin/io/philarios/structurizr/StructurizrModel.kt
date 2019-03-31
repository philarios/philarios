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
        val id: String,
        val name: String,
        val description: String,
        val location: Location?
)

data class SoftwareSystem(
        val id: String,
        val name: String,
        val description: String,
        val location: Location?,
        val containers: List<Container>
)

data class Container(
        val id: String,
        val name: String,
        val description: String,
        val technology: String,
        val components: List<Component>
)

data class Component(
        val id: String,
        val name: String,
        val description: String,
        val technology: String,
        val relationships: List<Relationship>
)

data class Relationship(
        val destinationId: String,
        val description: String,
        val technology: String,
        val interactionStyle: InteractionStyle
)

enum class Location {
    Internal,

    External,

    Unspecified
}

enum class InteractionStyle {
    Synchronous,

    Asynchronous
}
