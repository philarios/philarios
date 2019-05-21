package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <T : Any> ComponentBuilder.id(id: T) {
    id(id.hierarchicalId())
}

fun <T : Any> ComponentBuilder.relationshipFrom(sourceId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        sourceId(sourceId)
        apply(body)
    }
}

fun <T : Any> ComponentBuilder.relationshipTo(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

