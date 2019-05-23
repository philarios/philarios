// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.RefScaffold
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class WorkspaceScaffolder(internal val spec: WorkspaceSpec) : Scaffolder<Workspace> {
    override fun createScaffold(): Scaffold<Workspace> {
        val builder = WorkspaceScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkspaceScaffoldBuilder(internal var scaffold: WorkspaceScaffold = WorkspaceScaffold()) : WorkspaceBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun model(body: ModelBuilder.() -> Unit) {
        scaffold = scaffold.copy(model = ModelScaffolder(ModelSpec(body)).createScaffold())
    }

    override fun model(spec: ModelSpec) {
        scaffold = scaffold.copy(model = ModelScaffolder(spec).createScaffold())
    }

    override fun model(ref: ModelRef) {
        scaffold = scaffold.copy(model = RefScaffold(ref.key))
    }

    override fun model(value: Model) {
        scaffold = scaffold.copy(model = ValueScaffold(value))
    }

    override fun viewSet(body: ViewSetBuilder.() -> Unit) {
        scaffold = scaffold.copy(viewSet = ViewSetScaffolder(ViewSetSpec(body)).createScaffold())
    }

    override fun viewSet(spec: ViewSetSpec) {
        scaffold = scaffold.copy(viewSet = ViewSetScaffolder(spec).createScaffold())
    }

    override fun viewSet(ref: ViewSetRef) {
        scaffold = scaffold.copy(viewSet = RefScaffold(ref.key))
    }

    override fun viewSet(value: ViewSet) {
        scaffold = scaffold.copy(viewSet = ValueScaffold(value))
    }

    override fun configuration(body: WorkspaceConfigurationBuilder.() -> Unit) {
        scaffold = scaffold.copy(configuration = WorkspaceConfigurationScaffolder(WorkspaceConfigurationSpec(body)).createScaffold())
    }

    override fun configuration(spec: WorkspaceConfigurationSpec) {
        scaffold = scaffold.copy(configuration = WorkspaceConfigurationScaffolder(spec).createScaffold())
    }

    override fun configuration(ref: WorkspaceConfigurationRef) {
        scaffold = scaffold.copy(configuration = RefScaffold(ref.key))
    }

    override fun configuration(value: WorkspaceConfiguration) {
        scaffold = scaffold.copy(configuration = ValueScaffold(value))
    }

    override fun documentation(body: DocumentationBuilder.() -> Unit) {
        scaffold = scaffold.copy(documentation = DocumentationScaffolder(DocumentationSpec(body)).createScaffold())
    }

    override fun documentation(spec: DocumentationSpec) {
        scaffold = scaffold.copy(documentation = DocumentationScaffolder(spec).createScaffold())
    }

    override fun documentation(ref: DocumentationRef) {
        scaffold = scaffold.copy(documentation = RefScaffold(ref.key))
    }

    override fun documentation(value: Documentation) {
        scaffold = scaffold.copy(documentation = ValueScaffold(value))
    }
}

internal data class WorkspaceScaffold(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null,
        var viewSet: Scaffold<ViewSet>? = null,
        var configuration: Scaffold<WorkspaceConfiguration>? = null,
        var documentation: Scaffold<Documentation>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
            viewSet?.let{ launch { it.resolve(registry) } }
            configuration?.let{ launch { it.resolve(registry) } }
            documentation?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) },
            viewSet?.let{ it.resolve(registry) },
            configuration?.let{ it.resolve(registry) },
            documentation?.let{ it.resolve(registry) }
        )
        return value
    }
}

class ModelScaffolder(internal val spec: ModelSpec) : Scaffolder<Model> {
    override fun createScaffold(): Scaffold<Model> {
        val builder = ModelScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ModelScaffoldBuilder(internal var scaffold: ModelScaffold = ModelScaffold()) : ModelBuilder {
    override fun person(body: PersonBuilder.() -> Unit) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + PersonScaffolder(PersonSpec(body)).createScaffold())
    }

    override fun person(spec: PersonSpec) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + PersonScaffolder(spec).createScaffold())
    }

    override fun person(ref: PersonRef) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + RefScaffold(ref.key))
    }

    override fun person(value: Person) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<Person>) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun softwareSystem(body: SoftwareSystemBuilder.() -> Unit) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + SoftwareSystemScaffolder(SoftwareSystemSpec(body)).createScaffold())
    }

    override fun softwareSystem(spec: SoftwareSystemSpec) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + SoftwareSystemScaffolder(spec).createScaffold())
    }

    override fun softwareSystem(ref: SoftwareSystemRef) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + RefScaffold(ref.key))
    }

    override fun softwareSystem(value: SoftwareSystem) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<SoftwareSystem>) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }
}

internal data class ModelScaffold(var people: List<Scaffold<Person>>? = null, var softwareSystems: List<Scaffold<SoftwareSystem>>? = null) : Scaffold<Model> {
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

class PersonScaffolder(internal val spec: PersonSpec) : Scaffolder<Person> {
    override fun createScaffold(): Scaffold<Person> {
        val builder = PersonScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class PersonScaffoldBuilder(internal var scaffold: PersonScaffold = PersonScaffold()) : PersonBuilder {
    override fun id(value: String) {
        scaffold = scaffold.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun location(value: Location) {
        scaffold = scaffold.copy(location = ValueScaffold(value))
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class PersonScaffold(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var relationships: List<Scaffold<Relationship>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<Person> {
    override suspend fun resolve(registry: Registry): Person {
        checkNotNull(id) { "Person is missing the id property" }
        checkNotNull(name) { "Person is missing the name property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Person(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class SoftwareSystemScaffolder(internal val spec: SoftwareSystemSpec) : Scaffolder<SoftwareSystem> {
    override fun createScaffold(): Scaffold<SoftwareSystem> {
        val builder = SoftwareSystemScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SoftwareSystemScaffoldBuilder(internal var scaffold: SoftwareSystemScaffold = SoftwareSystemScaffold()) : SoftwareSystemBuilder {
    override fun id(value: String) {
        scaffold = scaffold.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun location(value: Location) {
        scaffold = scaffold.copy(location = ValueScaffold(value))
    }

    override fun container(body: ContainerBuilder.() -> Unit) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + ContainerScaffolder(ContainerSpec(body)).createScaffold())
    }

    override fun container(spec: ContainerSpec) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + ContainerScaffolder(spec).createScaffold())
    }

    override fun container(ref: ContainerRef) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + RefScaffold(ref.key))
    }

    override fun container(value: Container) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<Container>) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + containers.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class SoftwareSystemScaffold(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null,
        var containers: List<Scaffold<Container>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<SoftwareSystem> {
    override suspend fun resolve(registry: Registry): SoftwareSystem {
        checkNotNull(id) { "SoftwareSystem is missing the id property" }
        checkNotNull(name) { "SoftwareSystem is missing the name property" }
        coroutineScope {
            containers?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = SoftwareSystem(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) },
            containers.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ContainerScaffolder(internal val spec: ContainerSpec) : Scaffolder<Container> {
    override fun createScaffold(): Scaffold<Container> {
        val builder = ContainerScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ContainerScaffoldBuilder(internal var scaffold: ContainerScaffold = ContainerScaffold()) : ContainerBuilder {
    override fun id(value: String) {
        scaffold = scaffold.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun component(body: ComponentBuilder.() -> Unit) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + ComponentScaffolder(ComponentSpec(body)).createScaffold())
    }

    override fun component(spec: ComponentSpec) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + ComponentScaffolder(spec).createScaffold())
    }

    override fun component(ref: ComponentRef) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + RefScaffold(ref.key))
    }

    override fun component(value: Component) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + ValueScaffold(value))
    }

    override fun components(components: List<Component>) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + components.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class ContainerScaffold(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technologies: List<Scaffold<String>>? = null,
        var components: List<Scaffold<Component>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<Container> {
    override suspend fun resolve(registry: Registry): Container {
        checkNotNull(id) { "Container is missing the id property" }
        checkNotNull(name) { "Container is missing the name property" }
        coroutineScope {
            components?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Container(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            technologies.orEmpty().let{ it.map { it.resolve(registry) } },
            components.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ComponentScaffolder(internal val spec: ComponentSpec) : Scaffolder<Component> {
    override fun createScaffold(): Scaffold<Component> {
        val builder = ComponentScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ComponentScaffoldBuilder(internal var scaffold: ComponentScaffold = ComponentScaffold()) : ComponentBuilder {
    override fun id(value: String) {
        scaffold = scaffold.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class ComponentScaffold(
        var id: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technologies: List<Scaffold<String>>? = null,
        var relationships: List<Scaffold<Relationship>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<Component> {
    override suspend fun resolve(registry: Registry): Component {
        checkNotNull(id) { "Component is missing the id property" }
        checkNotNull(name) { "Component is missing the name property" }
        coroutineScope {
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Component(
            id!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            technologies.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class RelationshipScaffolder(internal val spec: RelationshipSpec) : Scaffolder<Relationship> {
    override fun createScaffold(): Scaffold<Relationship> {
        val builder = RelationshipScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class RelationshipScaffoldBuilder(internal var scaffold: RelationshipScaffold = RelationshipScaffold()) : RelationshipBuilder {
    override fun sourceId(value: String) {
        scaffold = scaffold.copy(sourceId = ValueScaffold(value))
    }

    override fun destinationId(value: String) {
        scaffold = scaffold.copy(destinationId = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        scaffold = scaffold.copy(technologies = scaffold.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun interactionStyle(value: InteractionStyle) {
        scaffold = scaffold.copy(interactionStyle = ValueScaffold(value))
    }

    override fun tag(value: String) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        scaffold = scaffold.copy(tags = scaffold.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class RelationshipScaffold(
        var sourceId: Scaffold<String>? = null,
        var destinationId: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technologies: List<Scaffold<String>>? = null,
        var interactionStyle: Scaffold<InteractionStyle>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        val value = Relationship(
            sourceId?.let{ it.resolve(registry) },
            destinationId?.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            technologies.orEmpty().let{ it.map { it.resolve(registry) } },
            interactionStyle?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ViewSetScaffolder(internal val spec: ViewSetSpec) : Scaffolder<ViewSet> {
    override fun createScaffold(): Scaffold<ViewSet> {
        val builder = ViewSetScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ViewSetScaffoldBuilder(internal var scaffold: ViewSetScaffold = ViewSetScaffold()) : ViewSetBuilder {
    override fun systemLandscapeView(body: SystemLandscapeViewBuilder.() -> Unit) {
        scaffold = scaffold.copy(systemLandscapeViews = scaffold.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder(SystemLandscapeViewSpec(body)).createScaffold())
    }

    override fun systemLandscapeView(spec: SystemLandscapeViewSpec) {
        scaffold = scaffold.copy(systemLandscapeViews = scaffold.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder(spec).createScaffold())
    }

    override fun systemLandscapeView(ref: SystemLandscapeViewRef) {
        scaffold = scaffold.copy(systemLandscapeViews = scaffold.systemLandscapeViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun systemLandscapeView(value: SystemLandscapeView) {
        scaffold = scaffold.copy(systemLandscapeViews = scaffold.systemLandscapeViews.orEmpty() + ValueScaffold(value))
    }

    override fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>) {
        scaffold = scaffold.copy(systemLandscapeViews = scaffold.systemLandscapeViews.orEmpty() + systemLandscapeViews.map { ValueScaffold(it) })
    }

    override fun systemContextView(body: SystemContextViewBuilder.() -> Unit) {
        scaffold = scaffold.copy(systemContextViews = scaffold.systemContextViews.orEmpty() + SystemContextViewScaffolder(SystemContextViewSpec(body)).createScaffold())
    }

    override fun systemContextView(spec: SystemContextViewSpec) {
        scaffold = scaffold.copy(systemContextViews = scaffold.systemContextViews.orEmpty() + SystemContextViewScaffolder(spec).createScaffold())
    }

    override fun systemContextView(ref: SystemContextViewRef) {
        scaffold = scaffold.copy(systemContextViews = scaffold.systemContextViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun systemContextView(value: SystemContextView) {
        scaffold = scaffold.copy(systemContextViews = scaffold.systemContextViews.orEmpty() + ValueScaffold(value))
    }

    override fun systemContextViews(systemContextViews: List<SystemContextView>) {
        scaffold = scaffold.copy(systemContextViews = scaffold.systemContextViews.orEmpty() + systemContextViews.map { ValueScaffold(it) })
    }

    override fun containerView(body: ContainerViewBuilder.() -> Unit) {
        scaffold = scaffold.copy(containerViews = scaffold.containerViews.orEmpty() + ContainerViewScaffolder(ContainerViewSpec(body)).createScaffold())
    }

    override fun containerView(spec: ContainerViewSpec) {
        scaffold = scaffold.copy(containerViews = scaffold.containerViews.orEmpty() + ContainerViewScaffolder(spec).createScaffold())
    }

    override fun containerView(ref: ContainerViewRef) {
        scaffold = scaffold.copy(containerViews = scaffold.containerViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun containerView(value: ContainerView) {
        scaffold = scaffold.copy(containerViews = scaffold.containerViews.orEmpty() + ValueScaffold(value))
    }

    override fun containerViews(containerViews: List<ContainerView>) {
        scaffold = scaffold.copy(containerViews = scaffold.containerViews.orEmpty() + containerViews.map { ValueScaffold(it) })
    }

    override fun componentView(body: ComponentViewBuilder.() -> Unit) {
        scaffold = scaffold.copy(componentViews = scaffold.componentViews.orEmpty() + ComponentViewScaffolder(ComponentViewSpec(body)).createScaffold())
    }

    override fun componentView(spec: ComponentViewSpec) {
        scaffold = scaffold.copy(componentViews = scaffold.componentViews.orEmpty() + ComponentViewScaffolder(spec).createScaffold())
    }

    override fun componentView(ref: ComponentViewRef) {
        scaffold = scaffold.copy(componentViews = scaffold.componentViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun componentView(value: ComponentView) {
        scaffold = scaffold.copy(componentViews = scaffold.componentViews.orEmpty() + ValueScaffold(value))
    }

    override fun componentViews(componentViews: List<ComponentView>) {
        scaffold = scaffold.copy(componentViews = scaffold.componentViews.orEmpty() + componentViews.map { ValueScaffold(it) })
    }

    override fun dynamicView(body: DynamicViewBuilder.() -> Unit) {
        scaffold = scaffold.copy(dynamicViews = scaffold.dynamicViews.orEmpty() + DynamicViewScaffolder(DynamicViewSpec(body)).createScaffold())
    }

    override fun dynamicView(spec: DynamicViewSpec) {
        scaffold = scaffold.copy(dynamicViews = scaffold.dynamicViews.orEmpty() + DynamicViewScaffolder(spec).createScaffold())
    }

    override fun dynamicView(ref: DynamicViewRef) {
        scaffold = scaffold.copy(dynamicViews = scaffold.dynamicViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun dynamicView(value: DynamicView) {
        scaffold = scaffold.copy(dynamicViews = scaffold.dynamicViews.orEmpty() + ValueScaffold(value))
    }

    override fun dynamicViews(dynamicViews: List<DynamicView>) {
        scaffold = scaffold.copy(dynamicViews = scaffold.dynamicViews.orEmpty() + dynamicViews.map { ValueScaffold(it) })
    }

    override fun configuration(body: ConfigurationBuilder.() -> Unit) {
        scaffold = scaffold.copy(configuration = ConfigurationScaffolder(ConfigurationSpec(body)).createScaffold())
    }

    override fun configuration(spec: ConfigurationSpec) {
        scaffold = scaffold.copy(configuration = ConfigurationScaffolder(spec).createScaffold())
    }

    override fun configuration(ref: ConfigurationRef) {
        scaffold = scaffold.copy(configuration = RefScaffold(ref.key))
    }

    override fun configuration(value: Configuration) {
        scaffold = scaffold.copy(configuration = ValueScaffold(value))
    }
}

internal data class ViewSetScaffold(
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

class SystemLandscapeViewScaffolder(internal val spec: SystemLandscapeViewSpec) : Scaffolder<SystemLandscapeView> {
    override fun createScaffold(): Scaffold<SystemLandscapeView> {
        val builder = SystemLandscapeViewScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SystemLandscapeViewScaffoldBuilder(internal var scaffold: SystemLandscapeViewScaffold = SystemLandscapeViewScaffold()) : SystemLandscapeViewBuilder {
    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        scaffold = scaffold.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + people.map { ValueScaffold(it) })
    }
}

internal data class SystemLandscapeViewScaffold(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null
) : Scaffold<SystemLandscapeView> {
    override suspend fun resolve(registry: Registry): SystemLandscapeView {
        checkNotNull(key) { "SystemLandscapeView is missing the key property" }
        val value = SystemLandscapeView(
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class SystemContextViewScaffolder(internal val spec: SystemContextViewSpec) : Scaffolder<SystemContextView> {
    override fun createScaffold(): Scaffold<SystemContextView> {
        val builder = SystemContextViewScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SystemContextViewScaffoldBuilder(internal var scaffold: SystemContextViewScaffold = SystemContextViewScaffold()) : SystemContextViewBuilder {
    override fun softwareSystemId(value: String) {
        scaffold = scaffold.copy(softwareSystemId = ValueScaffold(value))
    }

    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        scaffold = scaffold.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + people.map { ValueScaffold(it) })
    }
}

internal data class SystemContextViewScaffold(
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
        val value = SystemContextView(
            softwareSystemId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ContainerViewScaffolder(internal val spec: ContainerViewSpec) : Scaffolder<ContainerView> {
    override fun createScaffold(): Scaffold<ContainerView> {
        val builder = ContainerViewScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ContainerViewScaffoldBuilder(internal var scaffold: ContainerViewScaffold = ContainerViewScaffold()) : ContainerViewBuilder {
    override fun softwareSystemId(value: String) {
        scaffold = scaffold.copy(softwareSystemId = ValueScaffold(value))
    }

    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        scaffold = scaffold.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun container(value: String) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<String>) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + containers.map { ValueScaffold(it) })
    }
}

internal data class ContainerViewScaffold(
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
        val value = ContainerView(
            softwareSystemId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } },
            containers?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ComponentViewScaffolder(internal val spec: ComponentViewSpec) : Scaffolder<ComponentView> {
    override fun createScaffold(): Scaffold<ComponentView> {
        val builder = ComponentViewScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ComponentViewScaffoldBuilder(internal var scaffold: ComponentViewScaffold = ComponentViewScaffold()) : ComponentViewBuilder {
    override fun containerId(value: String) {
        scaffold = scaffold.copy(containerId = ValueScaffold(value))
    }

    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        scaffold = scaffold.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        scaffold = scaffold.copy(softwareSystems = scaffold.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        scaffold = scaffold.copy(people = scaffold.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun container(value: String) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<String>) {
        scaffold = scaffold.copy(containers = scaffold.containers.orEmpty() + containers.map { ValueScaffold(it) })
    }

    override fun component(value: String) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + ValueScaffold(value))
    }

    override fun components(components: List<String>) {
        scaffold = scaffold.copy(components = scaffold.components.orEmpty() + components.map { ValueScaffold(it) })
    }
}

internal data class ComponentViewScaffold(
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
        val value = ComponentView(
            containerId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
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

class DynamicViewScaffolder(internal val spec: DynamicViewSpec) : Scaffolder<DynamicView> {
    override fun createScaffold(): Scaffold<DynamicView> {
        val builder = DynamicViewScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class DynamicViewScaffoldBuilder(internal var scaffold: DynamicViewScaffold = DynamicViewScaffold()) : DynamicViewBuilder {
    override fun key(value: String) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        scaffold = scaffold.copy(paperSize = ValueScaffold(value))
    }
}

internal data class DynamicViewScaffold(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null
) : Scaffold<DynamicView> {
    override suspend fun resolve(registry: Registry): DynamicView {
        checkNotNull(key) { "DynamicView is missing the key property" }
        val value = DynamicView(
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) }
        )
        return value
    }
}

class ConfigurationScaffolder(internal val spec: ConfigurationSpec) : Scaffolder<Configuration> {
    override fun createScaffold(): Scaffold<Configuration> {
        val builder = ConfigurationScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ConfigurationScaffoldBuilder(internal var scaffold: ConfigurationScaffold = ConfigurationScaffold()) : ConfigurationBuilder {
    override fun branding(body: BrandingBuilder.() -> Unit) {
        scaffold = scaffold.copy(branding = BrandingScaffolder(BrandingSpec(body)).createScaffold())
    }

    override fun branding(spec: BrandingSpec) {
        scaffold = scaffold.copy(branding = BrandingScaffolder(spec).createScaffold())
    }

    override fun branding(ref: BrandingRef) {
        scaffold = scaffold.copy(branding = RefScaffold(ref.key))
    }

    override fun branding(value: Branding) {
        scaffold = scaffold.copy(branding = ValueScaffold(value))
    }

    override fun styles(body: StylesBuilder.() -> Unit) {
        scaffold = scaffold.copy(styles = StylesScaffolder(StylesSpec(body)).createScaffold())
    }

    override fun styles(spec: StylesSpec) {
        scaffold = scaffold.copy(styles = StylesScaffolder(spec).createScaffold())
    }

    override fun styles(ref: StylesRef) {
        scaffold = scaffold.copy(styles = RefScaffold(ref.key))
    }

    override fun styles(value: Styles) {
        scaffold = scaffold.copy(styles = ValueScaffold(value))
    }

    override fun terminology(body: TerminologyBuilder.() -> Unit) {
        scaffold = scaffold.copy(terminology = TerminologyScaffolder(TerminologySpec(body)).createScaffold())
    }

    override fun terminology(spec: TerminologySpec) {
        scaffold = scaffold.copy(terminology = TerminologyScaffolder(spec).createScaffold())
    }

    override fun terminology(ref: TerminologyRef) {
        scaffold = scaffold.copy(terminology = RefScaffold(ref.key))
    }

    override fun terminology(value: Terminology) {
        scaffold = scaffold.copy(terminology = ValueScaffold(value))
    }

    override fun viewSortOrder(value: ViewSortOrder) {
        scaffold = scaffold.copy(viewSortOrder = ValueScaffold(value))
    }
}

internal data class ConfigurationScaffold(
        var branding: Scaffold<Branding>? = null,
        var styles: Scaffold<Styles>? = null,
        var terminology: Scaffold<Terminology>? = null,
        var viewSortOrder: Scaffold<ViewSortOrder>? = null
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
            terminology?.let{ it.resolve(registry) },
            viewSortOrder?.let{ it.resolve(registry) }
        )
        return value
    }
}

class BrandingScaffolder(internal val spec: BrandingSpec) : Scaffolder<Branding> {
    override fun createScaffold(): Scaffold<Branding> {
        val builder = BrandingScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class BrandingScaffoldBuilder(internal var scaffold: BrandingScaffold = BrandingScaffold()) : BrandingBuilder {
    override fun logo(value: String) {
        scaffold = scaffold.copy(logo = ValueScaffold(value))
    }

    override fun font(body: FontBuilder.() -> Unit) {
        scaffold = scaffold.copy(font = FontScaffolder(FontSpec(body)).createScaffold())
    }

    override fun font(spec: FontSpec) {
        scaffold = scaffold.copy(font = FontScaffolder(spec).createScaffold())
    }

    override fun font(ref: FontRef) {
        scaffold = scaffold.copy(font = RefScaffold(ref.key))
    }

    override fun font(value: Font) {
        scaffold = scaffold.copy(font = ValueScaffold(value))
    }
}

internal data class BrandingScaffold(var logo: Scaffold<String>? = null, var font: Scaffold<Font>? = null) : Scaffold<Branding> {
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

class FontScaffolder(internal val spec: FontSpec) : Scaffolder<Font> {
    override fun createScaffold(): Scaffold<Font> {
        val builder = FontScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class FontScaffoldBuilder(internal var scaffold: FontScaffold = FontScaffold()) : FontBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun url(value: String) {
        scaffold = scaffold.copy(url = ValueScaffold(value))
    }
}

internal data class FontScaffold(var name: Scaffold<String>? = null, var url: Scaffold<String>? = null) : Scaffold<Font> {
    override suspend fun resolve(registry: Registry): Font {
        checkNotNull(name) { "Font is missing the name property" }
        val value = Font(
            name!!.let{ it.resolve(registry) },
            url?.let{ it.resolve(registry) }
        )
        return value
    }
}

class StylesScaffolder(internal val spec: StylesSpec) : Scaffolder<Styles> {
    override fun createScaffold(): Scaffold<Styles> {
        val builder = StylesScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class StylesScaffoldBuilder(internal var scaffold: StylesScaffold = StylesScaffold()) : StylesBuilder {
    override fun element(body: ElementStyleBuilder.() -> Unit) {
        scaffold = scaffold.copy(elements = scaffold.elements.orEmpty() + ElementStyleScaffolder(ElementStyleSpec(body)).createScaffold())
    }

    override fun element(spec: ElementStyleSpec) {
        scaffold = scaffold.copy(elements = scaffold.elements.orEmpty() + ElementStyleScaffolder(spec).createScaffold())
    }

    override fun element(ref: ElementStyleRef) {
        scaffold = scaffold.copy(elements = scaffold.elements.orEmpty() + RefScaffold(ref.key))
    }

    override fun element(value: ElementStyle) {
        scaffold = scaffold.copy(elements = scaffold.elements.orEmpty() + ValueScaffold(value))
    }

    override fun elements(elements: List<ElementStyle>) {
        scaffold = scaffold.copy(elements = scaffold.elements.orEmpty() + elements.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipStyleBuilder.() -> Unit) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipStyleScaffolder(RelationshipStyleSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipStyleSpec) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RelationshipStyleScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipStyleRef) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: RelationshipStyle) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<RelationshipStyle>) {
        scaffold = scaffold.copy(relationships = scaffold.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }
}

internal data class StylesScaffold(var elements: List<Scaffold<ElementStyle>>? = null, var relationships: List<Scaffold<RelationshipStyle>>? = null) : Scaffold<Styles> {
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

class ElementStyleScaffolder(internal val spec: ElementStyleSpec) : Scaffolder<ElementStyle> {
    override fun createScaffold(): Scaffold<ElementStyle> {
        val builder = ElementStyleScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class ElementStyleScaffoldBuilder(internal var scaffold: ElementStyleScaffold = ElementStyleScaffold()) : ElementStyleBuilder {
    override fun tag(value: String) {
        scaffold = scaffold.copy(tag = ValueScaffold(value))
    }

    override fun width(value: Int) {
        scaffold = scaffold.copy(width = ValueScaffold(value))
    }

    override fun height(value: Int) {
        scaffold = scaffold.copy(height = ValueScaffold(value))
    }

    override fun background(value: String) {
        scaffold = scaffold.copy(background = ValueScaffold(value))
    }

    override fun color(value: String) {
        scaffold = scaffold.copy(color = ValueScaffold(value))
    }

    override fun fontSize(value: Int) {
        scaffold = scaffold.copy(fontSize = ValueScaffold(value))
    }

    override fun shape(value: Shape) {
        scaffold = scaffold.copy(shape = ValueScaffold(value))
    }

    override fun icon(value: String) {
        scaffold = scaffold.copy(icon = ValueScaffold(value))
    }

    override fun border(value: Border) {
        scaffold = scaffold.copy(border = ValueScaffold(value))
    }

    override fun opacity(value: Int) {
        scaffold = scaffold.copy(opacity = ValueScaffold(value))
    }

    override fun metadata(value: Boolean) {
        scaffold = scaffold.copy(metadata = ValueScaffold(value))
    }

    override fun description(value: Boolean) {
        scaffold = scaffold.copy(description = ValueScaffold(value))
    }
}

internal data class ElementStyleScaffold(
        var tag: Scaffold<String>? = null,
        var width: Scaffold<Int>? = null,
        var height: Scaffold<Int>? = null,
        var background: Scaffold<String>? = null,
        var color: Scaffold<String>? = null,
        var fontSize: Scaffold<Int>? = null,
        var shape: Scaffold<Shape>? = null,
        var icon: Scaffold<String>? = null,
        var border: Scaffold<Border>? = null,
        var opacity: Scaffold<Int>? = null,
        var metadata: Scaffold<Boolean>? = null,
        var description: Scaffold<Boolean>? = null
) : Scaffold<ElementStyle> {
    override suspend fun resolve(registry: Registry): ElementStyle {
        checkNotNull(tag) { "ElementStyle is missing the tag property" }
        val value = ElementStyle(
            tag!!.let{ it.resolve(registry) },
            width?.let{ it.resolve(registry) },
            height?.let{ it.resolve(registry) },
            background?.let{ it.resolve(registry) },
            color?.let{ it.resolve(registry) },
            fontSize?.let{ it.resolve(registry) },
            shape?.let{ it.resolve(registry) },
            icon?.let{ it.resolve(registry) },
            border?.let{ it.resolve(registry) },
            opacity?.let{ it.resolve(registry) },
            metadata?.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) }
        )
        return value
    }
}

class RelationshipStyleScaffolder(internal val spec: RelationshipStyleSpec) : Scaffolder<RelationshipStyle> {
    override fun createScaffold(): Scaffold<RelationshipStyle> {
        val builder = RelationshipStyleScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class RelationshipStyleScaffoldBuilder(internal var scaffold: RelationshipStyleScaffold = RelationshipStyleScaffold()) : RelationshipStyleBuilder {
    override fun tag(value: String) {
        scaffold = scaffold.copy(tag = ValueScaffold(value))
    }

    override fun thickness(value: Int) {
        scaffold = scaffold.copy(thickness = ValueScaffold(value))
    }

    override fun color(value: String) {
        scaffold = scaffold.copy(color = ValueScaffold(value))
    }

    override fun fontSize(value: Int) {
        scaffold = scaffold.copy(fontSize = ValueScaffold(value))
    }

    override fun width(value: Int) {
        scaffold = scaffold.copy(width = ValueScaffold(value))
    }

    override fun dashed(value: Boolean) {
        scaffold = scaffold.copy(dashed = ValueScaffold(value))
    }

    override fun routing(value: Routing) {
        scaffold = scaffold.copy(routing = ValueScaffold(value))
    }

    override fun position(value: Int) {
        scaffold = scaffold.copy(position = ValueScaffold(value))
    }

    override fun opacity(value: Int) {
        scaffold = scaffold.copy(opacity = ValueScaffold(value))
    }
}

internal data class RelationshipStyleScaffold(
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
        val value = RelationshipStyle(
            tag!!.let{ it.resolve(registry) },
            thickness?.let{ it.resolve(registry) },
            color?.let{ it.resolve(registry) },
            fontSize?.let{ it.resolve(registry) },
            width?.let{ it.resolve(registry) },
            dashed?.let{ it.resolve(registry) },
            routing?.let{ it.resolve(registry) },
            position?.let{ it.resolve(registry) },
            opacity?.let{ it.resolve(registry) }
        )
        return value
    }
}

class TerminologyScaffolder(internal val spec: TerminologySpec) : Scaffolder<Terminology> {
    override fun createScaffold(): Scaffold<Terminology> {
        val builder = TerminologyScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class TerminologyScaffoldBuilder(internal var scaffold: TerminologyScaffold = TerminologyScaffold()) : TerminologyBuilder {
    override fun enterprise(value: String) {
        scaffold = scaffold.copy(enterprise = ValueScaffold(value))
    }

    override fun person(value: String) {
        scaffold = scaffold.copy(person = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        scaffold = scaffold.copy(softwareSystem = ValueScaffold(value))
    }

    override fun container(value: String) {
        scaffold = scaffold.copy(container = ValueScaffold(value))
    }

    override fun component(value: String) {
        scaffold = scaffold.copy(component = ValueScaffold(value))
    }

    override fun code(value: String) {
        scaffold = scaffold.copy(code = ValueScaffold(value))
    }

    override fun deploymentNode(value: String) {
        scaffold = scaffold.copy(deploymentNode = ValueScaffold(value))
    }

    override fun relationship(value: String) {
        scaffold = scaffold.copy(relationship = ValueScaffold(value))
    }
}

internal data class TerminologyScaffold(
        var enterprise: Scaffold<String>? = null,
        var person: Scaffold<String>? = null,
        var softwareSystem: Scaffold<String>? = null,
        var container: Scaffold<String>? = null,
        var component: Scaffold<String>? = null,
        var code: Scaffold<String>? = null,
        var deploymentNode: Scaffold<String>? = null,
        var relationship: Scaffold<String>? = null
) : Scaffold<Terminology> {
    override suspend fun resolve(registry: Registry): Terminology {
        val value = Terminology(
            enterprise?.let{ it.resolve(registry) },
            person?.let{ it.resolve(registry) },
            softwareSystem?.let{ it.resolve(registry) },
            container?.let{ it.resolve(registry) },
            component?.let{ it.resolve(registry) },
            code?.let{ it.resolve(registry) },
            deploymentNode?.let{ it.resolve(registry) },
            relationship?.let{ it.resolve(registry) }
        )
        return value
    }
}

class WorkspaceConfigurationScaffolder(internal val spec: WorkspaceConfigurationSpec) : Scaffolder<WorkspaceConfiguration> {
    override fun createScaffold(): Scaffold<WorkspaceConfiguration> {
        val builder = WorkspaceConfigurationScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class WorkspaceConfigurationScaffoldBuilder(internal var scaffold: WorkspaceConfigurationScaffold = WorkspaceConfigurationScaffold()) : WorkspaceConfigurationBuilder {
    override fun user(body: UserBuilder.() -> Unit) {
        scaffold = scaffold.copy(users = scaffold.users.orEmpty() + UserScaffolder(UserSpec(body)).createScaffold())
    }

    override fun user(spec: UserSpec) {
        scaffold = scaffold.copy(users = scaffold.users.orEmpty() + UserScaffolder(spec).createScaffold())
    }

    override fun user(ref: UserRef) {
        scaffold = scaffold.copy(users = scaffold.users.orEmpty() + RefScaffold(ref.key))
    }

    override fun user(value: User) {
        scaffold = scaffold.copy(users = scaffold.users.orEmpty() + ValueScaffold(value))
    }

    override fun users(users: List<User>) {
        scaffold = scaffold.copy(users = scaffold.users.orEmpty() + users.map { ValueScaffold(it) })
    }
}

internal data class WorkspaceConfigurationScaffold(var users: List<Scaffold<User>>? = null) : Scaffold<WorkspaceConfiguration> {
    override suspend fun resolve(registry: Registry): WorkspaceConfiguration {
        coroutineScope {
            users?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = WorkspaceConfiguration(
            users.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class UserScaffolder(internal val spec: UserSpec) : Scaffolder<User> {
    override fun createScaffold(): Scaffold<User> {
        val builder = UserScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class UserScaffoldBuilder(internal var scaffold: UserScaffold = UserScaffold()) : UserBuilder {
    override fun username(value: String) {
        scaffold = scaffold.copy(username = ValueScaffold(value))
    }

    override fun role(value: Role) {
        scaffold = scaffold.copy(role = ValueScaffold(value))
    }
}

internal data class UserScaffold(var username: Scaffold<String>? = null, var role: Scaffold<Role>? = null) : Scaffold<User> {
    override suspend fun resolve(registry: Registry): User {
        checkNotNull(username) { "User is missing the username property" }
        checkNotNull(role) { "User is missing the role property" }
        val value = User(
            username!!.let{ it.resolve(registry) },
            role!!.let{ it.resolve(registry) }
        )
        return value
    }
}

class DocumentationScaffolder(internal val spec: DocumentationSpec) : Scaffolder<Documentation> {
    override fun createScaffold(): Scaffold<Documentation> {
        val builder = DocumentationScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class DocumentationScaffoldBuilder(internal var scaffold: DocumentationScaffold = DocumentationScaffold()) : DocumentationBuilder {
    override fun decision(body: DecisionBuilder.() -> Unit) {
        scaffold = scaffold.copy(decisions = scaffold.decisions.orEmpty() + DecisionScaffolder(DecisionSpec(body)).createScaffold())
    }

    override fun decision(spec: DecisionSpec) {
        scaffold = scaffold.copy(decisions = scaffold.decisions.orEmpty() + DecisionScaffolder(spec).createScaffold())
    }

    override fun decision(ref: DecisionRef) {
        scaffold = scaffold.copy(decisions = scaffold.decisions.orEmpty() + RefScaffold(ref.key))
    }

    override fun decision(value: Decision) {
        scaffold = scaffold.copy(decisions = scaffold.decisions.orEmpty() + ValueScaffold(value))
    }

    override fun decisions(decisions: List<Decision>) {
        scaffold = scaffold.copy(decisions = scaffold.decisions.orEmpty() + decisions.map { ValueScaffold(it) })
    }
}

internal data class DocumentationScaffold(var decisions: List<Scaffold<Decision>>? = null) : Scaffold<Documentation> {
    override suspend fun resolve(registry: Registry): Documentation {
        coroutineScope {
            decisions?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Documentation(
            decisions.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class DecisionScaffolder(internal val spec: DecisionSpec) : Scaffolder<Decision> {
    override fun createScaffold(): Scaffold<Decision> {
        val builder = DecisionScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class DecisionScaffoldBuilder(internal var scaffold: DecisionScaffold = DecisionScaffold()) : DecisionBuilder {
    override fun elementId(value: String) {
        scaffold = scaffold.copy(elementId = ValueScaffold(value))
    }

    override fun id(value: String) {
        scaffold = scaffold.copy(id = ValueScaffold(value))
    }

    override fun date(value: String) {
        scaffold = scaffold.copy(date = ValueScaffold(value))
    }

    override fun title(value: String) {
        scaffold = scaffold.copy(title = ValueScaffold(value))
    }

    override fun status(value: DecisionStatus) {
        scaffold = scaffold.copy(status = ValueScaffold(value))
    }

    override fun content(value: String) {
        scaffold = scaffold.copy(content = ValueScaffold(value))
    }

    override fun format(value: Format) {
        scaffold = scaffold.copy(format = ValueScaffold(value))
    }
}

internal data class DecisionScaffold(
        var elementId: Scaffold<String>? = null,
        var id: Scaffold<String>? = null,
        var date: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var status: Scaffold<DecisionStatus>? = null,
        var content: Scaffold<String>? = null,
        var format: Scaffold<Format>? = null
) : Scaffold<Decision> {
    override suspend fun resolve(registry: Registry): Decision {
        checkNotNull(elementId) { "Decision is missing the elementId property" }
        checkNotNull(id) { "Decision is missing the id property" }
        checkNotNull(date) { "Decision is missing the date property" }
        checkNotNull(title) { "Decision is missing the title property" }
        checkNotNull(status) { "Decision is missing the status property" }
        checkNotNull(content) { "Decision is missing the content property" }
        checkNotNull(format) { "Decision is missing the format property" }
        val value = Decision(
            elementId!!.let{ it.resolve(registry) },
            id!!.let{ it.resolve(registry) },
            date!!.let{ it.resolve(registry) },
            title!!.let{ it.resolve(registry) },
            status!!.let{ it.resolve(registry) },
            content!!.let{ it.resolve(registry) },
            format!!.let{ it.resolve(registry) }
        )
        return value
    }
}
