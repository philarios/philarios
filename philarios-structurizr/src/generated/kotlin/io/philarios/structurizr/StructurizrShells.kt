package io.philarios.structurizr

import io.philarios.core.Scaffold
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class WorkspaceShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        checkNotNull(description) { "Workspace is missing the description property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ModelShell(var people: List<Scaffold<Person>>? = null, var softwareSystems: List<Scaffold<SoftwareSystem>>? = null) : Scaffold<Model> {
    override suspend fun resolve(registry: Registry): Model {
        coroutineScope {
            people?.let{ it.forEach { launch { it.resolve(registry) } } }
            softwareSystems?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Model(
            people.orEmpty().let{ it.map { it.resolve(registry) } },
            softwareSystems.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class PersonShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Person> {
    override suspend fun resolve(registry: Registry): Person {
        checkNotNull(id) { "Person is missing the id property" }
        checkNotNull(name) { "Person is missing the name property" }
        checkNotNull(description) { "Person is missing the description property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Person(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class SoftwareSystemShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var containers: List<Scaffold<Container>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<SoftwareSystem> {
    override suspend fun resolve(registry: Registry): SoftwareSystem {
        checkNotNull(id) { "SoftwareSystem is missing the id property" }
        checkNotNull(name) { "SoftwareSystem is missing the name property" }
        checkNotNull(description) { "SoftwareSystem is missing the description property" }
        coroutineScope {
            containers?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = SoftwareSystem(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            containers.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ContainerShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var components: List<Scaffold<Component>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Container> {
    override suspend fun resolve(registry: Registry): Container {
        checkNotNull(id) { "Container is missing the id property" }
        checkNotNull(name) { "Container is missing the name property" }
        checkNotNull(description) { "Container is missing the description property" }
        checkNotNull(technology) { "Container is missing the technology property" }
        coroutineScope {
            components?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Container(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            components.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ComponentShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Component> {
    override suspend fun resolve(registry: Registry): Component {
        checkNotNull(id) { "Component is missing the id property" }
        checkNotNull(name) { "Component is missing the name property" }
        checkNotNull(description) { "Component is missing the description property" }
        checkNotNull(technology) { "Component is missing the technology property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Component(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class RelationshipShell(
        var destinationId: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var interactionStyle: Scaffold<InteractionStyle>? = null
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        checkNotNull(destinationId) { "Relationship is missing the destinationId property" }
        checkNotNull(description) { "Relationship is missing the description property" }
        checkNotNull(technology) { "Relationship is missing the technology property" }
        checkNotNull(interactionStyle) { "Relationship is missing the interactionStyle property" }
        val value = Relationship(
            destinationId!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            interactionStyle!!.let{ it.resolve(registry) }
        )
        return value
    }
}
