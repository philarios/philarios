package io.philarios.structurizr

import io.philarios.core.Registry
import io.philarios.core.Scaffold
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
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null
) : Scaffold<Person> {
    override suspend fun resolve(registry: Registry): Person {
        checkNotNull(name) { "Person is missing the name property" }
        checkNotNull(description) { "Person is missing the description property" }
        val value = Person(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SoftwareSystemShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var containers: List<Scaffold<Container>>? = null
) : Scaffold<SoftwareSystem> {
    override suspend fun resolve(registry: Registry): SoftwareSystem {
        checkNotNull(name) { "SoftwareSystem is missing the name property" }
        checkNotNull(description) { "SoftwareSystem is missing the description property" }
        coroutineScope {
            containers?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = SoftwareSystem(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            containers.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ContainerShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var components: List<Scaffold<Component>>? = null
) : Scaffold<Container> {
    override suspend fun resolve(registry: Registry): Container {
        checkNotNull(name) { "Container is missing the name property" }
        checkNotNull(description) { "Container is missing the description property" }
        checkNotNull(technology) { "Container is missing the technology property" }
        coroutineScope {
            components?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Container(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            components.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ComponentShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null
) : Scaffold<Component> {
    override suspend fun resolve(registry: Registry): Component {
        checkNotNull(name) { "Component is missing the name property" }
        checkNotNull(description) { "Component is missing the description property" }
        checkNotNull(technology) { "Component is missing the technology property" }
        val value = Component(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) }
        )
        return value
    }
}
