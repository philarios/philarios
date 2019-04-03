package io.philarios.structurizr.sugar

import io.philarios.structurizr.ContainerBuilder
import io.philarios.structurizr.RelationshipBuilder
import io.philarios.structurizr.SoftwareSystemBuilder

fun <C, T : Any> SoftwareSystemBuilder<C>.id(id: T) {
    id(id.hierarchicalId())
}

fun <C, T : Any> SoftwareSystemBuilder<C>.container(id: T, body: ContainerBuilder<C>.() -> Unit = {}) {
    container {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> SoftwareSystemBuilder<C>.relationship(destinationId: T, body: RelationshipBuilder<C>.() -> Unit = {}) {
    relationship {
        destinationId(destinationId)
        apply(body)
    }
}

