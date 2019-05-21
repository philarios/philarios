package io.philarios.structurizr.sugar

import io.philarios.structurizr.ContainerBuilder
import io.philarios.structurizr.Location
import io.philarios.structurizr.RelationshipBuilder
import io.philarios.structurizr.SoftwareSystemBuilder

fun <T : Any> SoftwareSystemBuilder.id(id: T) {
    id(id.hierarchicalId())
}

fun <T : Any> SoftwareSystemBuilder.container(id: T, body: ContainerBuilder.() -> Unit = {}) {
    container {
        id(id)
        apply(body)
    }
}

fun <T : Any> SoftwareSystemBuilder.relationshipFrom(sourceId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        sourceId(sourceId)
        apply(body)
    }
}

fun <T : Any> SoftwareSystemBuilder.relationshipTo(destinationId: T, body: RelationshipBuilder.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

fun SoftwareSystemBuilder.internal() {
    location(Location.Internal)
    tag("Internal")
}

fun SoftwareSystemBuilder.external() {
    location(Location.External)
    tag("External")
}

