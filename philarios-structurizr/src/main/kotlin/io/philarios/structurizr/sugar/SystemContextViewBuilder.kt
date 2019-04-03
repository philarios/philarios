package io.philarios.structurizr.sugar

import io.philarios.structurizr.SystemContextViewBuilder

fun <C, T : Any> SystemContextViewBuilder<C>.id(id: T) {
    softwareSystemId(id.hierarchicalId())
    key("SystemContextView::${id.hierarchicalId()}")
}

fun <C, T : Any> SystemContextViewBuilder<C>.person(id: T) {
    person(id.hierarchicalId())
}

fun <C, T : Any> SystemContextViewBuilder<C>.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> SystemContextViewBuilder<C>.people(ids: Array<T>) {
    people(ids.toList())
}

fun <C, T : Any> SystemContextViewBuilder<C>.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <C, T : Any> SystemContextViewBuilder<C>.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> SystemContextViewBuilder<C>.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}
