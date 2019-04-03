package io.philarios.structurizr.sugar

import io.philarios.structurizr.ContainerViewBuilder

fun <C, T : Any> ContainerViewBuilder<C>.id(id: T) {
    softwareSystemId(id.hierarchicalId())
    key("ContainerView::${id.hierarchicalId()}")
}

fun <C, T : Any> ContainerViewBuilder<C>.person(id: T) {
    person(id.hierarchicalId())
}

fun <C, T : Any> ContainerViewBuilder<C>.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ContainerViewBuilder<C>.people(ids: Array<T>) {
    people(ids.toList())
}

fun <C, T : Any> ContainerViewBuilder<C>.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <C, T : Any> ContainerViewBuilder<C>.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ContainerViewBuilder<C>.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}

fun <C, T : Any> ContainerViewBuilder<C>.container(id: T) {
    container(id.hierarchicalId())
}

fun <C, T : Any> ContainerViewBuilder<C>.containers(ids: Iterable<T>) {
    containers(ids.map(Any::hierarchicalId))
}

fun <C, T : Any> ContainerViewBuilder<C>.containers(ids: Array<T>) {
    containers(ids.toList())
}
