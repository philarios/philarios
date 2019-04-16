package io.philarios.structurizr.sugar

import io.philarios.structurizr.ContainerViewBuilder

fun <T : Any> ContainerViewBuilder.id(id: T) {
    softwareSystemId(id.hierarchicalId())
    key("ContainerView::${id.hierarchicalId()}")
}

fun <T : Any> ContainerViewBuilder.person(id: T) {
    person(id.hierarchicalId())
}

fun <T : Any> ContainerViewBuilder.people(ids: Iterable<T>) {
    people(ids.map(Any::hierarchicalId))
}

fun <T : Any> ContainerViewBuilder.people(ids: Array<T>) {
    people(ids.toList())
}

fun <T : Any> ContainerViewBuilder.softwareSystem(id: T) {
    softwareSystem(id.hierarchicalId())
}

fun <T : Any> ContainerViewBuilder.softwareSystems(ids: Iterable<T>) {
    softwareSystems(ids.map(Any::hierarchicalId))
}

fun <T : Any> ContainerViewBuilder.softwareSystems(ids: Array<T>) {
    softwareSystems(ids.toList())
}

fun <T : Any> ContainerViewBuilder.container(id: T) {
    container(id.hierarchicalId())
}

fun <T : Any> ContainerViewBuilder.containers(ids: Iterable<T>) {
    containers(ids.map(Any::hierarchicalId))
}

fun <T : Any> ContainerViewBuilder.containers(ids: Array<T>) {
    containers(ids.toList())
}
