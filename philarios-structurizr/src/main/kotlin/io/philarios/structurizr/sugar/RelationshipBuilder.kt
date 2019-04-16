package io.philarios.structurizr.sugar

import io.philarios.structurizr.InteractionStyle
import io.philarios.structurizr.RelationshipBuilder

fun <T : Any> RelationshipBuilder.destinationId(id: T) {
    destinationId(id.hierarchicalId())
}

fun  RelationshipBuilder.synchronous() {
    interactionStyle(InteractionStyle.Synchronous)
}

fun  RelationshipBuilder.asynchronous() {
    interactionStyle(InteractionStyle.Asynchronous)
}
