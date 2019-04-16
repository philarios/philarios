package io.philarios.structurizr.sugar

import io.philarios.structurizr.SystemContextViewBuilder

fun <T : Any> SystemContextViewBuilder.id(id: T) {
    softwareSystemId(id.hierarchicalId())
    key("SystemContextView::${id.hierarchicalId()}")
}

fun <T : Any> SystemContextViewBuilder.person(id: T) {
    person(id.hierarchicalId())
}

fun <T : Any> SystemContextViewBuilder.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <T : Any> SystemContextViewBuilder.people(ids: Array<T>) {
    people(ids.toList())
}

fun <T : Any> SystemContextViewBuilder.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <T : Any> SystemContextViewBuilder.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <T : Any> SystemContextViewBuilder.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}
