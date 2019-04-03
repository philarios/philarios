package io.philarios.structurizr

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

data class Workspace(
        val name: String,
        val description: String?,
        val model: Model?,
        val viewSet: ViewSet?
)

data class Model(val people: List<Person>, val softwareSystems: List<SoftwareSystem>)

data class Person(
        val id: String,
        val name: String,
        val description: String?,
        val location: Location?,
        val relationships: List<Relationship>
)

data class SoftwareSystem(
        val id: String,
        val name: String,
        val description: String?,
        val location: Location?,
        val containers: List<Container>,
        val relationships: List<Relationship>
)

data class Container(
        val id: String,
        val name: String,
        val description: String?,
        val technology: String?,
        val components: List<Component>,
        val relationships: List<Relationship>
)

data class Component(
        val id: String,
        val name: String,
        val description: String?,
        val technology: String?,
        val relationships: List<Relationship>
)

data class Relationship(
        val destinationId: String,
        val description: String?,
        val technology: String?,
        val interactionStyle: InteractionStyle?
)

enum class Location {
    Internal,

    External,

    Unspecified
}

enum class InteractionStyle {
    Synchronous,

    Asynchronous
}

data class ViewSet(
        val systemLandscapeViews: List<SystemLandscapeView>?,
        val systemContextViews: List<SystemContextView>?,
        val containerViews: List<ContainerView>?,
        val componentViews: List<ComponentView>?,
        val dynamicViews: List<DynamicView>?,
        val configuration: Configuration?
)

data class SystemLandscapeView(
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?
)

data class SystemContextView(
        val softwareSystemId: String,
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?
)

data class ContainerView(
        val softwareSystemId: String,
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?,
        val containers: List<String>?
)

data class ComponentView(
        val containerId: String,
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?,
        val containers: List<String>?,
        val components: List<String>?
)

data class DynamicView(
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?
)

enum class PaperSize {
    A6_Portrait,

    A6_Landscape,

    A5_Portrait,

    A5_Landscape,

    A4_Portrait,

    A4_Landscape,

    A3_Portrait,

    A3_Landscape,

    A2_Portrait,

    A2_Landscape,

    Letter_Portrait,

    Letter_Landscape,

    Legal_Portrait,

    Legal_Landscape,

    Slide_4_3,

    Slide_16_9
}

data class Configuration(
        val branding: Branding?,
        val styles: Styles?,
        val terminology: Terminology?
)

data class Branding(val logo: String?, val font: Font?)

data class Font(val name: String, val url: String?)

data class Styles(val elements: List<ElementStyle>?, val relationships: List<RelationshipStyle>?)

data class ElementStyle(
        val tag: String,
        val width: Int?,
        val height: Int?,
        val background: String?,
        val color: String?,
        val fontSize: Int?,
        val shape: Shape?,
        val border: Border?,
        val opacity: Int?,
        val metadata: Boolean?,
        val description: Boolean?
)

enum class Shape {
    Box,

    RoundedBox,

    Circle,

    Ellipse,

    Hexagon,

    Cylinder,

    Pipe,

    Person,

    Robot,

    Folder,

    WebBrowser,

    MobileDevicePortrait,

    MobileDeviceLandscape
}

enum class Border {
    Solid,

    Dashed
}

data class RelationshipStyle(
        val tag: String,
        val thickness: Int?,
        val color: String?,
        val fontSize: Int?,
        val width: Int?,
        val dashed: Boolean?,
        val routing: Routing?,
        val position: Int?,
        val opacity: Int?
)

enum class Routing {
    Direct,

    Orthogonal
}

data class Terminology(
        val enterprise: String?,
        val person: String?,
        val softwareSystem: String?,
        val container: String?,
        val component: String?,
        val code: String?,
        val deploymentNode: String?
)
