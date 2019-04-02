package io.philarios.structurizr

import io.philarios.core.Scaffold
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class WorkspaceShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null,
        var viewSet: Scaffold<ViewSet>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        checkNotNull(description) { "Workspace is missing the description property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
            viewSet?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) },
            viewSet?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ModelShell(var people: List<Scaffold<Person>>? = null, var softwareSystems: List<Scaffold<SoftwareSystem>>? = null) : Scaffold<Model> {
    override suspend fun resolve(registry: Registry): Model {
        coroutineScope {
            people?.let{ it.forEach { launch { it.resolve(registry) } } }
            softwareSystems?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Model(
            people.orEmpty().let{ it.map { it.resolve(registry) } },
            softwareSystems.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class PersonShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Person> {
    override suspend fun resolve(registry: Registry): Person {
        checkNotNull(id) { "Person is missing the id property" }
        checkNotNull(name) { "Person is missing the name property" }
        checkNotNull(description) { "Person is missing the description property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Person(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class SoftwareSystemShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var containers: List<Scaffold<Container>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<SoftwareSystem> {
    override suspend fun resolve(registry: Registry): SoftwareSystem {
        checkNotNull(id) { "SoftwareSystem is missing the id property" }
        checkNotNull(name) { "SoftwareSystem is missing the name property" }
        checkNotNull(description) { "SoftwareSystem is missing the description property" }
        coroutineScope {
            containers?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = SoftwareSystem(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            containers.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ContainerShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var components: List<Scaffold<Component>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Container> {
    override suspend fun resolve(registry: Registry): Container {
        checkNotNull(id) { "Container is missing the id property" }
        checkNotNull(name) { "Container is missing the name property" }
        checkNotNull(description) { "Container is missing the description property" }
        checkNotNull(technology) { "Container is missing the technology property" }
        coroutineScope {
            components?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Container(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            components.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ComponentShell(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var relationships: List<Scaffold<Relationship>>? = null
) : Scaffold<Component> {
    override suspend fun resolve(registry: Registry): Component {
        checkNotNull(id) { "Component is missing the id property" }
        checkNotNull(name) { "Component is missing the name property" }
        checkNotNull(description) { "Component is missing the description property" }
        checkNotNull(technology) { "Component is missing the technology property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Component(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class RelationshipShell(
        var destinationId: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technology: Scaffold<String>? = null,
        var interactionStyle: Scaffold<InteractionStyle>? = null
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        checkNotNull(destinationId) { "Relationship is missing the destinationId property" }
        checkNotNull(description) { "Relationship is missing the description property" }
        checkNotNull(technology) { "Relationship is missing the technology property" }
        checkNotNull(interactionStyle) { "Relationship is missing the interactionStyle property" }
        val value = Relationship(
            destinationId!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            technology!!.let{ it.resolve(registry) },
            interactionStyle!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ViewSetShell(
        var systemLandscapeViews: List<Scaffold<SystemLandscapeView>>? = null,
        var systemContextViews: List<Scaffold<SystemContextView>>? = null,
        var containerViews: List<Scaffold<ContainerView>>? = null,
        var componentViews: List<Scaffold<ComponentView>>? = null,
        var dynamicViews: List<Scaffold<DynamicView>>? = null,
        var configuration: Scaffold<Configuration>? = null
) : Scaffold<ViewSet> {
    override suspend fun resolve(registry: Registry): ViewSet {
        coroutineScope {
            systemLandscapeViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            systemContextViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            containerViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            componentViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            dynamicViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            configuration?.let{ launch { it.resolve(registry) } }
        }
        val value = ViewSet(
            systemLandscapeViews?.let{ it.map { it.resolve(registry) } },
            systemContextViews?.let{ it.map { it.resolve(registry) } },
            containerViews?.let{ it.map { it.resolve(registry) } },
            componentViews?.let{ it.map { it.resolve(registry) } },
            dynamicViews?.let{ it.map { it.resolve(registry) } },
            configuration?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class SystemLandscapeViewShell(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null
) : Scaffold<SystemLandscapeView> {
    override suspend fun resolve(registry: Registry): SystemLandscapeView {
        checkNotNull(key) { "SystemLandscapeView is missing the key property" }
        checkNotNull(description) { "SystemLandscapeView is missing the description property" }
        val value = SystemLandscapeView(
            key!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class SystemContextViewShell(
        var softwareSystemId: Scaffold<String>? = null,
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null
) : Scaffold<SystemContextView> {
    override suspend fun resolve(registry: Registry): SystemContextView {
        checkNotNull(softwareSystemId) { "SystemContextView is missing the softwareSystemId property" }
        checkNotNull(key) { "SystemContextView is missing the key property" }
        checkNotNull(description) { "SystemContextView is missing the description property" }
        val value = SystemContextView(
            softwareSystemId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ContainerViewShell(
        var softwareSystemId: Scaffold<String>? = null,
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null,
        var containers: List<Scaffold<String>>? = null
) : Scaffold<ContainerView> {
    override suspend fun resolve(registry: Registry): ContainerView {
        checkNotNull(softwareSystemId) { "ContainerView is missing the softwareSystemId property" }
        checkNotNull(key) { "ContainerView is missing the key property" }
        checkNotNull(description) { "ContainerView is missing the description property" }
        val value = ContainerView(
            softwareSystemId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } },
            containers?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ComponentViewShell(
        var containerId: Scaffold<String>? = null,
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null,
        var containers: List<Scaffold<String>>? = null,
        var components: List<Scaffold<String>>? = null
) : Scaffold<ComponentView> {
    override suspend fun resolve(registry: Registry): ComponentView {
        checkNotNull(containerId) { "ComponentView is missing the containerId property" }
        checkNotNull(key) { "ComponentView is missing the key property" }
        checkNotNull(description) { "ComponentView is missing the description property" }
        val value = ComponentView(
            containerId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } },
            containers?.let{ it.map { it.resolve(registry) } },
            components?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class DynamicViewShell(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null
) : Scaffold<DynamicView> {
    override suspend fun resolve(registry: Registry): DynamicView {
        checkNotNull(key) { "DynamicView is missing the key property" }
        checkNotNull(description) { "DynamicView is missing the description property" }
        val value = DynamicView(
            key!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ConfigurationShell(
        var branding: Scaffold<Branding>? = null,
        var styles: Scaffold<Styles>? = null,
        var terminology: Scaffold<Terminology>? = null
) : Scaffold<Configuration> {
    override suspend fun resolve(registry: Registry): Configuration {
        coroutineScope {
            branding?.let{ launch { it.resolve(registry) } }
            styles?.let{ launch { it.resolve(registry) } }
            terminology?.let{ launch { it.resolve(registry) } }
        }
        val value = Configuration(
            branding?.let{ it.resolve(registry) },
            styles?.let{ it.resolve(registry) },
            terminology?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class BrandingShell(var logo: Scaffold<String>? = null, var font: Scaffold<Font>? = null) : Scaffold<Branding> {
    override suspend fun resolve(registry: Registry): Branding {
        coroutineScope {
            font?.let{ launch { it.resolve(registry) } }
        }
        val value = Branding(
            logo?.let{ it.resolve(registry) },
            font?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class FontShell(var name: Scaffold<String>? = null, var url: Scaffold<String>? = null) : Scaffold<Font> {
    override suspend fun resolve(registry: Registry): Font {
        checkNotNull(name) { "Font is missing the name property" }
        val value = Font(
            name!!.let{ it.resolve(registry) },
            url?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StylesShell(var elements: List<Scaffold<ElementStyle>>? = null, var relationships: List<Scaffold<RelationshipStyle>>? = null) : Scaffold<Styles> {
    override suspend fun resolve(registry: Registry): Styles {
        coroutineScope {
            elements?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Styles(
            elements?.let{ it.map { it.resolve(registry) } },
            relationships?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class ElementStyleShell(
        var tag: Scaffold<String>? = null,
        var width: Scaffold<Int>? = null,
        var height: Scaffold<Int>? = null,
        var background: Scaffold<String>? = null,
        var color: Scaffold<String>? = null,
        var fontSize: Scaffold<Int>? = null,
        var shape: Scaffold<Shape>? = null,
        var border: Scaffold<Border>? = null,
        var opacity: Scaffold<Int>? = null,
        var metadata: Scaffold<Boolean>? = null,
        var description: Scaffold<Boolean>? = null
) : Scaffold<ElementStyle> {
    override suspend fun resolve(registry: Registry): ElementStyle {
        checkNotNull(tag) { "ElementStyle is missing the tag property" }
        checkNotNull(width) { "ElementStyle is missing the width property" }
        checkNotNull(height) { "ElementStyle is missing the height property" }
        checkNotNull(background) { "ElementStyle is missing the background property" }
        checkNotNull(color) { "ElementStyle is missing the color property" }
        checkNotNull(fontSize) { "ElementStyle is missing the fontSize property" }
        val value = ElementStyle(
            tag!!.let{ it.resolve(registry) },
            width!!.let{ it.resolve(registry) },
            height!!.let{ it.resolve(registry) },
            background!!.let{ it.resolve(registry) },
            color!!.let{ it.resolve(registry) },
            fontSize!!.let{ it.resolve(registry) },
            shape?.let{ it.resolve(registry) },
            border?.let{ it.resolve(registry) },
            opacity?.let{ it.resolve(registry) },
            metadata?.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class RelationshipStyleShell(
        var tag: Scaffold<String>? = null,
        var thickness: Scaffold<Int>? = null,
        var color: Scaffold<String>? = null,
        var fontSize: Scaffold<Int>? = null,
        var width: Scaffold<Int>? = null,
        var dashed: Scaffold<Boolean>? = null,
        var routing: Scaffold<Routing>? = null,
        var position: Scaffold<Int>? = null,
        var opacity: Scaffold<Int>? = null
) : Scaffold<RelationshipStyle> {
    override suspend fun resolve(registry: Registry): RelationshipStyle {
        checkNotNull(tag) { "RelationshipStyle is missing the tag property" }
        checkNotNull(thickness) { "RelationshipStyle is missing the thickness property" }
        checkNotNull(color) { "RelationshipStyle is missing the color property" }
        checkNotNull(fontSize) { "RelationshipStyle is missing the fontSize property" }
        checkNotNull(width) { "RelationshipStyle is missing the width property" }
        checkNotNull(dashed) { "RelationshipStyle is missing the dashed property" }
        checkNotNull(routing) { "RelationshipStyle is missing the routing property" }
        checkNotNull(position) { "RelationshipStyle is missing the position property" }
        val value = RelationshipStyle(
            tag!!.let{ it.resolve(registry) },
            thickness!!.let{ it.resolve(registry) },
            color!!.let{ it.resolve(registry) },
            fontSize!!.let{ it.resolve(registry) },
            width!!.let{ it.resolve(registry) },
            dashed!!.let{ it.resolve(registry) },
            routing!!.let{ it.resolve(registry) },
            position!!.let{ it.resolve(registry) },
            opacity?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TerminologyShell(
        var enterprise: Scaffold<String>? = null,
        var person: Scaffold<String>? = null,
        var softwareSystem: Scaffold<String>? = null,
        var container: Scaffold<String>? = null,
        var component: Scaffold<String>? = null,
        var code: Scaffold<String>? = null,
        var deploymentNode: Scaffold<String>? = null
) : Scaffold<Terminology> {
    override suspend fun resolve(registry: Registry): Terminology {
        val value = Terminology(
            enterprise?.let{ it.resolve(registry) },
            person?.let{ it.resolve(registry) },
            softwareSystem?.let{ it.resolve(registry) },
            container?.let{ it.resolve(registry) },
            component?.let{ it.resolve(registry) },
            code?.let{ it.resolve(registry) },
            deploymentNode?.let{ it.resolve(registry) }
        )
        return value
    }
}
