package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentViewBuilder

fun <T : Any> ComponentViewBuilder.id(id: T) {
    containerId(id.hierarchicalId())
    key("ComponentView::${id.hierarchicalId()}")
}

fun <T : Any> ComponentViewBuilder.person(id: T) {
    person(id.hierarchicalId())
}

fun <T : Any> ComponentViewBuilder.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <T : Any> ComponentViewBuilder.people(ids: Array<T>) {
    people(ids.toList())
}

fun <T : Any> ComponentViewBuilder.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <T : Any> ComponentViewBuilder.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <T : Any> ComponentViewBuilder.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}

fun <T : Any> ComponentViewBuilder.container(id: T) {
    container(id.hierarchicalId())
}

fun <T : Any> ComponentViewBuilder.containers(ids: Iterable<T>) {
    containers(ids.map(Any::hierarchicalId))
}

fun <T : Any> ComponentViewBuilder.containers(ids: Array<T>) {
    containers(ids.toList())
}

fun <T : Any> ComponentViewBuilder.component(id: T) {
    component(id.hierarchicalId())
}

fun <T : Any> ComponentViewBuilder.components(ids: Iterable<T>) {
    components(ids.map(Any::hierarchicalId))
}

fun <T : Any> ComponentViewBuilder.components(ids: Array<T>) {
    components(ids.toList())
}
