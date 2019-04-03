package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentBuilder
import io.philarios.structurizr.ContainerBuilder
import io.philarios.structurizr.RelationshipBuilder

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

