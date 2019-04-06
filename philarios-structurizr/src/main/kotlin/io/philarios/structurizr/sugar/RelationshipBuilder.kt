package io.philarios.structurizr.sugar

import io.philarios.structurizr.InteractionStyle
import io.philarios.structurizr.RelationshipBuilder

fun <C, T : Any> RelationshipBuilder<C>.destinationId(id: T) {
    destinationId(id.hierarchicalId())
}

fun <C> RelationshipBuilder<C>.synchronous() {
    interactionStyle(InteractionStyle.Synchronous)
}

fun <C> RelationshipBuilder<C>.asynchronous() {
    interactionStyle(InteractionStyle.Asynchronous)
}
