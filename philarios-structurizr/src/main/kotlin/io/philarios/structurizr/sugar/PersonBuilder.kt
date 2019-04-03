package io.philarios.structurizr.sugar

import io.philarios.structurizr.PersonBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <C, T : Any> PersonBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> PersonBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

