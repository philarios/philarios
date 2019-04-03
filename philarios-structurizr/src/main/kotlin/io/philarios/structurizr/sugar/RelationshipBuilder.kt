package io.philarios.structurizr.sugar

import io.philarios.structurizr.RelationshipBuilder

fun <C, T : Any> RelationshipBuilder<C>.destinationId(id: T) {
    destinationId(id.hierarchicalId())
}
