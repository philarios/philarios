package io.philarios.structurizr.sugar

import io.philarios.structurizr.ComponentViewBuilder

fun <C, T : Any> ComponentViewBuilder<C>.id(id: T) {
    containerId(id.hierarchicalId())
    key("ComponentView::${id.hierarchicalId()}")
}

fun <C, T : Any> ComponentViewBuilder<C>.person(id: T) {
    person(id.hierarchicalId())
}

fun <C, T : Any> ComponentViewBuilder<C>.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ComponentViewBuilder<C>.people(ids: Array<T>) {
    people(ids.toList())
}

fun <C, T : Any> ComponentViewBuilder<C>.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <C, T : Any> ComponentViewBuilder<C>.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ComponentViewBuilder<C>.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}

fun <C, T : Any> ComponentViewBuilder<C>.container(id: T) {
    container(id.hierarchicalId())
}

fun <C, T : Any> ComponentViewBuilder<C>.containers(ids: Iterable<T>) {
    containers(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ComponentViewBuilder<C>.containers(ids: Array<T>) {
    containers(ids.toList())
}

fun <C, T : Any> ComponentViewBuilder<C>.component(id: T) {
    component(id.hierarchicalId())
}

fun <C, T : Any> ComponentViewBuilder<C>.components(ids: Iterable<T>) {
    components(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ComponentViewBuilder<C>.components(ids: Array<T>) {
    components(ids.toList())
}
