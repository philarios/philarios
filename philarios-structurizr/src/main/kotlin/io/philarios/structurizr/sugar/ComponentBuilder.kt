package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <C, T : Any> ComponentBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> ComponentBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

