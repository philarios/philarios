package io.philarios.structurizr.entities

import com.structurizr.model.StaticStructureElement
import io.philarios.structurizr.*
import com.structurizr.Workspace as SWorkspace
import com.structurizr.model.Component as SComponent
import com.structurizr.model.Container as SContainer
import com.structurizr.model.InteractionStyle as SInteractionStyle
import com.structurizr.model.Location as SLocation
import com.structurizr.model.Model as SModel
import com.structurizr.model.Person as SPerson
import com.structurizr.model.SoftwareSystem as SSoftwareSystem

fun Workspace.convert(): SWorkspace {
    val addedWorkspace = SWorkspace(name, description)
    val map = addWorkspace(mapOf(), this, addedWorkspace)
    connectWorkspace(map, this)
    return addedWorkspace
}

private fun addWorkspace(
        map: Map<String, StaticStructureElement>,
        workspace: Workspace,
        addedWorkspace: SWorkspace
) = map + workspace.model?.let { model ->
    addModel(map, model, addedWorkspace.model)
}.orEmpty()

private fun addModel(
        map: Map<String, StaticStructureElement>,
        model: Model,
        addedModel: SModel
) = map + model.people.fold(emptyMap<String, StaticStructureElement>()) { map, person ->
    addPerson(map, person, addedModel.addPerson(
            person.location?.convert() ?: SLocation.Unspecified,
            person.name,
            person.description
    ))
} + model.softwareSystems.fold(emptyMap<String, StaticStructureElement>()) { map, softwareSystem ->
    addSoftwareSystem(map, softwareSystem, addedModel.addSoftwareSystem(
            softwareSystem.location?.convert() ?: SLocation.Unspecified,
            softwareSystem.name,
            softwareSystem.description
    ))
}

private fun addPerson(
        map: Map<String, StaticStructureElement>,
        person: Person,
        addedPerson: SPerson
) = map + (person.id to addedPerson)

private fun addSoftwareSystem(
        map: Map<String, StaticStructureElement>,
        softwareSystem: SoftwareSystem,
        addedSoftwareSystem: SSoftwareSystem
) = map + (softwareSystem.id to addedSoftwareSystem) +
        softwareSystem.containers.fold(emptyMap<String, StaticStructureElement>()) { map, container ->
            addContainer(map, container, addedSoftwareSystem.addContainer(
                    container.name,
                    container.description,
                    container.technology
            ))
        }

private fun addContainer(
        map: Map<String, StaticStructureElement>,
        container: Container,
        addedContainer: SContainer
) = map + (container.id to addedContainer) +
        container.components.fold(emptyMap<String, StaticStructureElement>()) { map, component ->
            addComponent(map, component, addedContainer.addComponent(
                    component.name,
                    component.description,
                    component.technology
            ))
        }

private fun addComponent(
        map: Map<String, StaticStructureElement>,
        component: Component,
        addedComponent: SComponent
) = map + (component.id to addedComponent)

private fun connectWorkspace(
        map: Map<String, StaticStructureElement>,
        workspace: Workspace
) {
    workspace.model?.let { model ->
        connectModel(map, model)
    }
}

private fun connectModel(
        map: Map<String, StaticStructureElement>,
        model: Model
) {
    model.people.forEach { person ->
        connectPerson(map, person)
    }
    model.softwareSystems.forEach { softwareSystem ->
        connectSoftwareSystem(map, softwareSystem)
    }
}

private fun connectPerson(
        map: Map<String, StaticStructureElement>,
        person: Person
) {
    connectStaticStructureElements(map, person.id, person.relationships)
}

private fun connectSoftwareSystem(
        map: Map<String, StaticStructureElement>,
        softwareSystem: SoftwareSystem
) {
    connectStaticStructureElements(map, softwareSystem.id, softwareSystem.relationships)
    softwareSystem.containers.forEach { container ->
        connectContainer(map, container)
    }
}

private fun connectContainer(
        map: Map<String, StaticStructureElement>,
        container: Container
) {
    connectStaticStructureElements(map, container.id, container.relationships)
    container.components.forEach { component ->
        connectComponent(map, component)
    }
}

private fun connectComponent(
        map: Map<String, StaticStructureElement>,
        component: Component
) {
    connectStaticStructureElements(map, component.id, component.relationships)
}

private fun connectStaticStructureElements(
        map: Map<String, StaticStructureElement>,
        id: String,
        relationships: List<Relationship>
) {
    map[id]?.let { source ->
        relationships.forEach { relationship ->
            map[relationship.destinationId]?.let { destination ->
                source.uses(
                        destination,
                        relationship.description,
                        relationship.technology,
                        relationship.interactionStyle.convert()
                )
            }
        }
    }
}

private fun Location.convert() = when (this) {
    Location.Internal -> SLocation.Internal
    Location.External -> SLocation.External
    Location.Unspecified -> SLocation.Unspecified
}

private fun InteractionStyle.convert() = when (this) {
    InteractionStyle.Synchronous -> SInteractionStyle.Synchronous
    InteractionStyle.Asynchronous -> SInteractionStyle.Asynchronous
}
