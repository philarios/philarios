package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentBuilder
import io.philarios.structurizr.ContainerBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <T : Any> ContainerBuilder.id(id: T) {
    id(id.hierarchicalId())
}

fun <T : Any> ContainerBuilder.component(id: T, body: ComponentBuilder.() -> Unit = {}) {
    component {
        id(id)
        apply(body)
    }
}

fun <T : Any> ContainerBuilder.relationship(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

