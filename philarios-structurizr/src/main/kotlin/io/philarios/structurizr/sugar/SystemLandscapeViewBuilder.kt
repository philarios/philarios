package io.philarios.structurizr.sugar

import io.philarios.structurizr.SystemLandscapeViewBuilder

fun <T : Any> SystemLandscapeViewBuilder.id(id: T) {
    key("SystemLandscapeView::${id.hierarchicalId()}")
}

fun <T : Any> SystemLandscapeViewBuilder.person(id: T) {
    person(id.hierarchicalId())
}

fun <T : Any> SystemLandscapeViewBuilder.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <T : Any> SystemLandscapeViewBuilder.people(ids: Array<T>) {
    people(ids.toList())
}

fun <T : Any> SystemLandscapeViewBuilder.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <T : Any> SystemLandscapeViewBuilder.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <T : Any> SystemLandscapeViewBuilder.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}
