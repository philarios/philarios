package io.philarios.structurizr

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

fun <C> SystemLandscapeViewBuilder<C>.id(id: Any) {
    key(id.hierarchicalId())
}

fun <C> SystemLandscapeViewBuilder<C>.softwareSystem(id: Any) {
    softwareSystem(id.hierarchicalId())
}

fun <C> SystemLandscapeViewBuilder<C>.person(id: Any) {
    person(id.hierarchicalId())
}

fun <C> SystemContextViewBuilder<C>.id(id: Any) {
    softwareSystemId(id.hierarchicalId())
    key("SystemContextView::${id.hierarchicalId()}")
}

fun <C> SystemContextViewBuilder<C>.softwareSystem(id: Any) {
    softwareSystem(id.hierarchicalId())
}

fun <C> SystemContextViewBuilder<C>.person(id: Any) {
    person(id.hierarchicalId())
}

fun <C> ContainerViewBuilder<C>.id(id: Any) {
    softwareSystemId(id.hierarchicalId())
    key("ContainerView::${id.hierarchicalId()}")
}

fun <C> ContainerViewBuilder<C>.softwareSystem(id: Any) {
    softwareSystem(id.hierarchicalId())
}

fun <C> ContainerViewBuilder<C>.person(id: Any) {
    person(id.hierarchicalId())
}

fun <C> ContainerViewBuilder<C>.container(id: Any) {
    container(id.hierarchicalId())
}

fun <C> ComponentViewBuilder<C>.id(id: Any) {
    containerId(id.hierarchicalId())
    key("ComponentView::${id.hierarchicalId()}")
}

fun <C> ComponentViewBuilder<C>.softwareSystem(id: Any) {
    softwareSystem(id.hierarchicalId())
}

fun <C> ComponentViewBuilder<C>.person(id: Any) {
    person(id.hierarchicalId())
}

fun <C> ComponentViewBuilder<C>.container(id: Any) {
    container(id.hierarchicalId())
}

fun <C> ComponentViewBuilder<C>.component(id: Any) {
    component(id.hierarchicalId())
}

private fun Any.hierarchicalId(): String {
    val packageName = javaClass.`package`.name
    val className = javaClass.canonicalName
    val prefix = className.removePrefix("$packageName.")
    val name = this.toString().toLowerCase().capitalize()
    return "$prefix.$name"
}
