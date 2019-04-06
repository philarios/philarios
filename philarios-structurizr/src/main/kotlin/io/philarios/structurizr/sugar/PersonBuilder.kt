package io.philarios.structurizr.sugar

import io.philarios.structurizr.Location
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

fun <C> PersonBuilder<C>.internal() {
    location(Location.Internal)
}

fun <C> PersonBuilder<C>.external() {
    location(Location.External)
}

