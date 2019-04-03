package io.philarios.structurizr.sugar

import io.philarios.structurizr.*

fun <C, T : Any> ModelBuilder<C>.person(id: T, body: PersonBuilder<C>.() -> Unit = {}) {
    person {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> ModelBuilder<C>.softwareSystem(id: T, body: SoftwareSystemBuilder<C>.() -> Unit = {}) {
    softwareSystem {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> PersonBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> PersonBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun <C, T : Any> SoftwareSystemBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> SoftwareSystemBuilder<C>.container(id: T, body: ContainerBuilder<C>.() -> Unit = {}) {
    container {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> SoftwareSystemBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun <C, T : Any> ContainerBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> ContainerBuilder<C>.component(id: T, body: ComponentBuilder<C>.() -> Unit = {}) {
    component {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> ContainerBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun <C, T : Any> ComponentBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> ComponentBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun <C, T : Any> RelationshipBuilder<C>.destinationId(id: T) {
    destinationId(id.hierarchicalId())
}

internal fun <T : Any> T.hierarchicalId(): String {
    val packageName = javaClass.`package`.name
    val className = javaClass.canonicalName
    val prefix = className.removePrefix("$packageName.")
    val name = this.toString().toLowerCase().capitalize()
    return "$prefix.$name"
}
