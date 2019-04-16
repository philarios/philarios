package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <T : Any> ComponentBuilder.id(id: T) {
    id(id.hierarchicalId())
}

fun <T : Any> ComponentBuilder.relationship(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

