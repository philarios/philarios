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

fun <T : Any> ContainerBuilder.relationshipFrom(sourceId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        sourceId(sourceId)
        apply(body)
    }
}

fun <T : Any> ContainerBuilder.relationshipTo(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

