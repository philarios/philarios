package io.philarios.structurizr.sugar

import io.philarios.structurizr.Location
import io.philarios.structurizr.PersonBuilder
import io.philarios.structurizr.RelationshipBuilder

fun <T : Any> PersonBuilder.id(id: T) {
    id(id.hierarchicalId())
}

fun <T : Any> PersonBuilder.relationship(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun  PersonBuilder.internal() {
    location(Location.Internal)
}

fun  PersonBuilder.external() {
    location(Location.External)
}

