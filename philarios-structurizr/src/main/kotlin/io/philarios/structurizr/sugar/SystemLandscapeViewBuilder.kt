package io.philarios.structurizr.sugar

import io.philarios.structurizr.SystemLandscapeViewBuilder

fun <C, T : Any> SystemLandscapeViewBuilder<C>.id(id: T) {
    key("SystemLandscapeView::${id.hierarchicalId()}")
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.person(id: T) {
    person(id.hierarchicalId())
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.people(ids: Array<T>) {
    people(ids.toList())
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> SystemLandscapeViewBuilder<C>.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}
