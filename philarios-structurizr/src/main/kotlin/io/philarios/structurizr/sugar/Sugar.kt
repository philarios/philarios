package io.philarios.structurizr.sugar

import io.philarios.structurizr.*

fun <C> PersonBuilder<C>.id(id: Any) {
    id(id.hierarchicalId())
}

fun <C> SoftwareSystemBuilder<C>.id(id: Any) {
    id(id.hierarchicalId())
}

fun <C> ContainerBuilder<C>.id(id: Any) {
    id(id.hierarchicalId())
}

fun <C> ComponentBuilder<C>.id(id: Any) {
    id(id.hierarchicalId())
}

fun <C> RelationshipBuilder<C>.destinationId(id: Any) {
    destinationId(id.hierarchicalId())
}

internal fun <T : Any> T.hierarchicalId(): String {
    val packageName = javaClass.`package`.name
    val className = javaClass.canonicalName
    val prefix = className.removePrefix("$packageName.")
    val name = this.toString().toLowerCase().capitalize()
    return "$prefix.$name"
}
