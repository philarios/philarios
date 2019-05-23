// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.structurizr

import io.philarios.core.RefScaffold
import io.philarios.core.DslBuilder
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
        val builder = WorkspaceShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkspaceShellBuilder(internal var shell: WorkspaceShell = WorkspaceShell()) : WorkspaceBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun model(body: ModelBuilder.() -> Unit) {
        shell = shell.copy(model = ModelScaffolder(ModelSpec(body)).createScaffold())
    }

    override fun model(spec: ModelSpec) {
        shell = shell.copy(model = ModelScaffolder(spec).createScaffold())
    }

    override fun model(ref: ModelRef) {
        shell = shell.copy(model = RefScaffold(ref.key))
    }

    override fun model(value: Model) {
        shell = shell.copy(model = ValueScaffold(value))
    }

    override fun viewSet(body: ViewSetBuilder.() -> Unit) {
        shell = shell.copy(viewSet = ViewSetScaffolder(ViewSetSpec(body)).createScaffold())
    }

    override fun viewSet(spec: ViewSetSpec) {
        shell = shell.copy(viewSet = ViewSetScaffolder(spec).createScaffold())
    }

    override fun viewSet(ref: ViewSetRef) {
        shell = shell.copy(viewSet = RefScaffold(ref.key))
    }

    override fun viewSet(value: ViewSet) {
        shell = shell.copy(viewSet = ValueScaffold(value))
    }

    override fun configuration(body: WorkspaceConfigurationBuilder.() -> Unit) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder(WorkspaceConfigurationSpec(body)).createScaffold())
    }

    override fun configuration(spec: WorkspaceConfigurationSpec) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder(spec).createScaffold())
    }

    override fun configuration(ref: WorkspaceConfigurationRef) {
        shell = shell.copy(configuration = RefScaffold(ref.key))
    }

    override fun configuration(value: WorkspaceConfiguration) {
        shell = shell.copy(configuration = ValueScaffold(value))
    }

    override fun documentation(body: DocumentationBuilder.() -> Unit) {
        shell = shell.copy(documentation = DocumentationScaffolder(DocumentationSpec(body)).createScaffold())
    }

    override fun documentation(spec: DocumentationSpec) {
        shell = shell.copy(documentation = DocumentationScaffolder(spec).createScaffold())
    }

    override fun documentation(ref: DocumentationRef) {
        shell = shell.copy(documentation = RefScaffold(ref.key))
    }

    override fun documentation(value: Documentation) {
        shell = shell.copy(documentation = ValueScaffold(value))
    }
}

internal data class WorkspaceShell(
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
        val builder = ModelShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ModelShellBuilder(internal var shell: ModelShell = ModelShell()) : ModelBuilder {
    override fun person(body: PersonBuilder.() -> Unit) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder(PersonSpec(body)).createScaffold())
    }

    override fun person(spec: PersonSpec) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder(spec).createScaffold())
    }

    override fun person(ref: PersonRef) {
        shell = shell.copy(people = shell.people.orEmpty() + RefScaffold(ref.key))
    }

    override fun person(value: Person) {
        shell = shell.copy(people = shell.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<Person>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun softwareSystem(body: SoftwareSystemBuilder.() -> Unit) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + SoftwareSystemScaffolder(SoftwareSystemSpec(body)).createScaffold())
    }

    override fun softwareSystem(spec: SoftwareSystemSpec) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + SoftwareSystemScaffolder(spec).createScaffold())
    }

    override fun softwareSystem(ref: SoftwareSystemRef) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + RefScaffold(ref.key))
    }

    override fun softwareSystem(value: SoftwareSystem) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<SoftwareSystem>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
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

class PersonScaffolder(internal val spec: PersonSpec) : Scaffolder<Person> {
    override fun createScaffold(): Scaffold<Person> {
        val builder = PersonShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class PersonShellBuilder(internal var shell: PersonShell = PersonShell()) : PersonBuilder {
    override fun id(value: String) {
        shell = shell.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun location(value: Location) {
        shell = shell.copy(location = ValueScaffold(value))
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class PersonShell(
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
        val builder = SoftwareSystemShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SoftwareSystemShellBuilder(internal var shell: SoftwareSystemShell = SoftwareSystemShell()) : SoftwareSystemBuilder {
    override fun id(value: String) {
        shell = shell.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun location(value: Location) {
        shell = shell.copy(location = ValueScaffold(value))
    }

    override fun container(body: ContainerBuilder.() -> Unit) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ContainerScaffolder(ContainerSpec(body)).createScaffold())
    }

    override fun container(spec: ContainerSpec) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ContainerScaffolder(spec).createScaffold())
    }

    override fun container(ref: ContainerRef) {
        shell = shell.copy(containers = shell.containers.orEmpty() + RefScaffold(ref.key))
    }

    override fun container(value: Container) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<Container>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class SoftwareSystemShell(
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
        val builder = ContainerShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ContainerShellBuilder(internal var shell: ContainerShell = ContainerShell()) : ContainerBuilder {
    override fun id(value: String) {
        shell = shell.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun component(body: ComponentBuilder.() -> Unit) {
        shell = shell.copy(components = shell.components.orEmpty() + ComponentScaffolder(ComponentSpec(body)).createScaffold())
    }

    override fun component(spec: ComponentSpec) {
        shell = shell.copy(components = shell.components.orEmpty() + ComponentScaffolder(spec).createScaffold())
    }

    override fun component(ref: ComponentRef) {
        shell = shell.copy(components = shell.components.orEmpty() + RefScaffold(ref.key))
    }

    override fun component(value: Component) {
        shell = shell.copy(components = shell.components.orEmpty() + ValueScaffold(value))
    }

    override fun components(components: List<Component>) {
        shell = shell.copy(components = shell.components.orEmpty() + components.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class ContainerShell(
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
        val builder = ComponentShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ComponentShellBuilder(internal var shell: ComponentShell = ComponentShell()) : ComponentBuilder {
    override fun id(value: String) {
        shell = shell.copy(id = ValueScaffold(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipBuilder.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(RelationshipSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipSpec) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class ComponentShell(
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
        val builder = RelationshipShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RelationshipShellBuilder(internal var shell: RelationshipShell = RelationshipShell()) : RelationshipBuilder {
    override fun sourceId(value: String) {
        shell = shell.copy(sourceId = ValueScaffold(value))
    }

    override fun destinationId(value: String) {
        shell = shell.copy(destinationId = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + ValueScaffold(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { ValueScaffold(it) })
    }

    override fun interactionStyle(value: InteractionStyle) {
        shell = shell.copy(interactionStyle = ValueScaffold(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + ValueScaffold(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { ValueScaffold(it) })
    }
}

internal data class RelationshipShell(
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
        val builder = ViewSetShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ViewSetShellBuilder(internal var shell: ViewSetShell = ViewSetShell()) : ViewSetBuilder {
    override fun systemLandscapeView(body: SystemLandscapeViewBuilder.() -> Unit) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder(SystemLandscapeViewSpec(body)).createScaffold())
    }

    override fun systemLandscapeView(spec: SystemLandscapeViewSpec) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder(spec).createScaffold())
    }

    override fun systemLandscapeView(ref: SystemLandscapeViewRef) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun systemLandscapeView(value: SystemLandscapeView) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + ValueScaffold(value))
    }

    override fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + systemLandscapeViews.map { ValueScaffold(it) })
    }

    override fun systemContextView(body: SystemContextViewBuilder.() -> Unit) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder(SystemContextViewSpec(body)).createScaffold())
    }

    override fun systemContextView(spec: SystemContextViewSpec) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder(spec).createScaffold())
    }

    override fun systemContextView(ref: SystemContextViewRef) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun systemContextView(value: SystemContextView) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + ValueScaffold(value))
    }

    override fun systemContextViews(systemContextViews: List<SystemContextView>) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + systemContextViews.map { ValueScaffold(it) })
    }

    override fun containerView(body: ContainerViewBuilder.() -> Unit) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder(ContainerViewSpec(body)).createScaffold())
    }

    override fun containerView(spec: ContainerViewSpec) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder(spec).createScaffold())
    }

    override fun containerView(ref: ContainerViewRef) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun containerView(value: ContainerView) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ValueScaffold(value))
    }

    override fun containerViews(containerViews: List<ContainerView>) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + containerViews.map { ValueScaffold(it) })
    }

    override fun componentView(body: ComponentViewBuilder.() -> Unit) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder(ComponentViewSpec(body)).createScaffold())
    }

    override fun componentView(spec: ComponentViewSpec) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder(spec).createScaffold())
    }

    override fun componentView(ref: ComponentViewRef) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun componentView(value: ComponentView) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ValueScaffold(value))
    }

    override fun componentViews(componentViews: List<ComponentView>) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + componentViews.map { ValueScaffold(it) })
    }

    override fun dynamicView(body: DynamicViewBuilder.() -> Unit) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder(DynamicViewSpec(body)).createScaffold())
    }

    override fun dynamicView(spec: DynamicViewSpec) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder(spec).createScaffold())
    }

    override fun dynamicView(ref: DynamicViewRef) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + RefScaffold(ref.key))
    }

    override fun dynamicView(value: DynamicView) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + ValueScaffold(value))
    }

    override fun dynamicViews(dynamicViews: List<DynamicView>) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + dynamicViews.map { ValueScaffold(it) })
    }

    override fun configuration(body: ConfigurationBuilder.() -> Unit) {
        shell = shell.copy(configuration = ConfigurationScaffolder(ConfigurationSpec(body)).createScaffold())
    }

    override fun configuration(spec: ConfigurationSpec) {
        shell = shell.copy(configuration = ConfigurationScaffolder(spec).createScaffold())
    }

    override fun configuration(ref: ConfigurationRef) {
        shell = shell.copy(configuration = RefScaffold(ref.key))
    }

    override fun configuration(value: Configuration) {
        shell = shell.copy(configuration = ValueScaffold(value))
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

class SystemLandscapeViewScaffolder(internal val spec: SystemLandscapeViewSpec) : Scaffolder<SystemLandscapeView> {
    override fun createScaffold(): Scaffold<SystemLandscapeView> {
        val builder = SystemLandscapeViewShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SystemLandscapeViewShellBuilder(internal var shell: SystemLandscapeViewShell = SystemLandscapeViewShell()) : SystemLandscapeViewBuilder {
    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { ValueScaffold(it) })
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
        val builder = SystemContextViewShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SystemContextViewShellBuilder(internal var shell: SystemContextViewShell = SystemContextViewShell()) : SystemContextViewBuilder {
    override fun softwareSystemId(value: String) {
        shell = shell.copy(softwareSystemId = ValueScaffold(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { ValueScaffold(it) })
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
        val builder = ContainerViewShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ContainerViewShellBuilder(internal var shell: ContainerViewShell = ContainerViewShell()) : ContainerViewBuilder {
    override fun softwareSystemId(value: String) {
        shell = shell.copy(softwareSystemId = ValueScaffold(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun container(value: String) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<String>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { ValueScaffold(it) })
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
        val builder = ComponentViewShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ComponentViewShellBuilder(internal var shell: ComponentViewShell = ComponentViewShell()) : ComponentViewBuilder {
    override fun containerId(value: String) {
        shell = shell.copy(containerId = ValueScaffold(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ValueScaffold(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { ValueScaffold(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + ValueScaffold(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { ValueScaffold(it) })
    }

    override fun container(value: String) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ValueScaffold(value))
    }

    override fun containers(containers: List<String>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { ValueScaffold(it) })
    }

    override fun component(value: String) {
        shell = shell.copy(components = shell.components.orEmpty() + ValueScaffold(value))
    }

    override fun components(components: List<String>) {
        shell = shell.copy(components = shell.components.orEmpty() + components.map { ValueScaffold(it) })
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
        val builder = DynamicViewShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DynamicViewShellBuilder(internal var shell: DynamicViewShell = DynamicViewShell()) : DynamicViewBuilder {
    override fun key(value: String) {
        shell = shell.copy(key = ValueScaffold(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = ValueScaffold(value))
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
        val builder = ConfigurationShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ConfigurationShellBuilder(internal var shell: ConfigurationShell = ConfigurationShell()) : ConfigurationBuilder {
    override fun branding(body: BrandingBuilder.() -> Unit) {
        shell = shell.copy(branding = BrandingScaffolder(BrandingSpec(body)).createScaffold())
    }

    override fun branding(spec: BrandingSpec) {
        shell = shell.copy(branding = BrandingScaffolder(spec).createScaffold())
    }

    override fun branding(ref: BrandingRef) {
        shell = shell.copy(branding = RefScaffold(ref.key))
    }

    override fun branding(value: Branding) {
        shell = shell.copy(branding = ValueScaffold(value))
    }

    override fun styles(body: StylesBuilder.() -> Unit) {
        shell = shell.copy(styles = StylesScaffolder(StylesSpec(body)).createScaffold())
    }

    override fun styles(spec: StylesSpec) {
        shell = shell.copy(styles = StylesScaffolder(spec).createScaffold())
    }

    override fun styles(ref: StylesRef) {
        shell = shell.copy(styles = RefScaffold(ref.key))
    }

    override fun styles(value: Styles) {
        shell = shell.copy(styles = ValueScaffold(value))
    }

    override fun terminology(body: TerminologyBuilder.() -> Unit) {
        shell = shell.copy(terminology = TerminologyScaffolder(TerminologySpec(body)).createScaffold())
    }

    override fun terminology(spec: TerminologySpec) {
        shell = shell.copy(terminology = TerminologyScaffolder(spec).createScaffold())
    }

    override fun terminology(ref: TerminologyRef) {
        shell = shell.copy(terminology = RefScaffold(ref.key))
    }

    override fun terminology(value: Terminology) {
        shell = shell.copy(terminology = ValueScaffold(value))
    }

    override fun viewSortOrder(value: ViewSortOrder) {
        shell = shell.copy(viewSortOrder = ValueScaffold(value))
    }
}

internal data class ConfigurationShell(
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
        val builder = BrandingShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class BrandingShellBuilder(internal var shell: BrandingShell = BrandingShell()) : BrandingBuilder {
    override fun logo(value: String) {
        shell = shell.copy(logo = ValueScaffold(value))
    }

    override fun font(body: FontBuilder.() -> Unit) {
        shell = shell.copy(font = FontScaffolder(FontSpec(body)).createScaffold())
    }

    override fun font(spec: FontSpec) {
        shell = shell.copy(font = FontScaffolder(spec).createScaffold())
    }

    override fun font(ref: FontRef) {
        shell = shell.copy(font = RefScaffold(ref.key))
    }

    override fun font(value: Font) {
        shell = shell.copy(font = ValueScaffold(value))
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

class FontScaffolder(internal val spec: FontSpec) : Scaffolder<Font> {
    override fun createScaffold(): Scaffold<Font> {
        val builder = FontShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class FontShellBuilder(internal var shell: FontShell = FontShell()) : FontBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun url(value: String) {
        shell = shell.copy(url = ValueScaffold(value))
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

class StylesScaffolder(internal val spec: StylesSpec) : Scaffolder<Styles> {
    override fun createScaffold(): Scaffold<Styles> {
        val builder = StylesShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class StylesShellBuilder(internal var shell: StylesShell = StylesShell()) : StylesBuilder {
    override fun element(body: ElementStyleBuilder.() -> Unit) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ElementStyleScaffolder(ElementStyleSpec(body)).createScaffold())
    }

    override fun element(spec: ElementStyleSpec) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ElementStyleScaffolder(spec).createScaffold())
    }

    override fun element(ref: ElementStyleRef) {
        shell = shell.copy(elements = shell.elements.orEmpty() + RefScaffold(ref.key))
    }

    override fun element(value: ElementStyle) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ValueScaffold(value))
    }

    override fun elements(elements: List<ElementStyle>) {
        shell = shell.copy(elements = shell.elements.orEmpty() + elements.map { ValueScaffold(it) })
    }

    override fun relationship(body: RelationshipStyleBuilder.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipStyleScaffolder(RelationshipStyleSpec(body)).createScaffold())
    }

    override fun relationship(spec: RelationshipStyleSpec) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipStyleScaffolder(spec).createScaffold())
    }

    override fun relationship(ref: RelationshipStyleRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RefScaffold(ref.key))
    }

    override fun relationship(value: RelationshipStyle) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ValueScaffold(value))
    }

    override fun relationships(relationships: List<RelationshipStyle>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { ValueScaffold(it) })
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

class ElementStyleScaffolder(internal val spec: ElementStyleSpec) : Scaffolder<ElementStyle> {
    override fun createScaffold(): Scaffold<ElementStyle> {
        val builder = ElementStyleShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class ElementStyleShellBuilder(internal var shell: ElementStyleShell = ElementStyleShell()) : ElementStyleBuilder {
    override fun tag(value: String) {
        shell = shell.copy(tag = ValueScaffold(value))
    }

    override fun width(value: Int) {
        shell = shell.copy(width = ValueScaffold(value))
    }

    override fun height(value: Int) {
        shell = shell.copy(height = ValueScaffold(value))
    }

    override fun background(value: String) {
        shell = shell.copy(background = ValueScaffold(value))
    }

    override fun color(value: String) {
        shell = shell.copy(color = ValueScaffold(value))
    }

    override fun fontSize(value: Int) {
        shell = shell.copy(fontSize = ValueScaffold(value))
    }

    override fun shape(value: Shape) {
        shell = shell.copy(shape = ValueScaffold(value))
    }

    override fun icon(value: String) {
        shell = shell.copy(icon = ValueScaffold(value))
    }

    override fun border(value: Border) {
        shell = shell.copy(border = ValueScaffold(value))
    }

    override fun opacity(value: Int) {
        shell = shell.copy(opacity = ValueScaffold(value))
    }

    override fun metadata(value: Boolean) {
        shell = shell.copy(metadata = ValueScaffold(value))
    }

    override fun description(value: Boolean) {
        shell = shell.copy(description = ValueScaffold(value))
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
        val builder = RelationshipStyleShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class RelationshipStyleShellBuilder(internal var shell: RelationshipStyleShell = RelationshipStyleShell()) : RelationshipStyleBuilder {
    override fun tag(value: String) {
        shell = shell.copy(tag = ValueScaffold(value))
    }

    override fun thickness(value: Int) {
        shell = shell.copy(thickness = ValueScaffold(value))
    }

    override fun color(value: String) {
        shell = shell.copy(color = ValueScaffold(value))
    }

    override fun fontSize(value: Int) {
        shell = shell.copy(fontSize = ValueScaffold(value))
    }

    override fun width(value: Int) {
        shell = shell.copy(width = ValueScaffold(value))
    }

    override fun dashed(value: Boolean) {
        shell = shell.copy(dashed = ValueScaffold(value))
    }

    override fun routing(value: Routing) {
        shell = shell.copy(routing = ValueScaffold(value))
    }

    override fun position(value: Int) {
        shell = shell.copy(position = ValueScaffold(value))
    }

    override fun opacity(value: Int) {
        shell = shell.copy(opacity = ValueScaffold(value))
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
        val builder = TerminologyShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class TerminologyShellBuilder(internal var shell: TerminologyShell = TerminologyShell()) : TerminologyBuilder {
    override fun enterprise(value: String) {
        shell = shell.copy(enterprise = ValueScaffold(value))
    }

    override fun person(value: String) {
        shell = shell.copy(person = ValueScaffold(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystem = ValueScaffold(value))
    }

    override fun container(value: String) {
        shell = shell.copy(container = ValueScaffold(value))
    }

    override fun component(value: String) {
        shell = shell.copy(component = ValueScaffold(value))
    }

    override fun code(value: String) {
        shell = shell.copy(code = ValueScaffold(value))
    }

    override fun deploymentNode(value: String) {
        shell = shell.copy(deploymentNode = ValueScaffold(value))
    }

    override fun relationship(value: String) {
        shell = shell.copy(relationship = ValueScaffold(value))
    }
}

internal data class TerminologyShell(
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
        val builder = WorkspaceConfigurationShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkspaceConfigurationShellBuilder(internal var shell: WorkspaceConfigurationShell = WorkspaceConfigurationShell()) : WorkspaceConfigurationBuilder {
    override fun user(body: UserBuilder.() -> Unit) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder(UserSpec(body)).createScaffold())
    }

    override fun user(spec: UserSpec) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder(spec).createScaffold())
    }

    override fun user(ref: UserRef) {
        shell = shell.copy(users = shell.users.orEmpty() + RefScaffold(ref.key))
    }

    override fun user(value: User) {
        shell = shell.copy(users = shell.users.orEmpty() + ValueScaffold(value))
    }

    override fun users(users: List<User>) {
        shell = shell.copy(users = shell.users.orEmpty() + users.map { ValueScaffold(it) })
    }
}

internal data class WorkspaceConfigurationShell(var users: List<Scaffold<User>>? = null) : Scaffold<WorkspaceConfiguration> {
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
        val builder = UserShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class UserShellBuilder(internal var shell: UserShell = UserShell()) : UserBuilder {
    override fun username(value: String) {
        shell = shell.copy(username = ValueScaffold(value))
    }

    override fun role(value: Role) {
        shell = shell.copy(role = ValueScaffold(value))
    }
}

internal data class UserShell(var username: Scaffold<String>? = null, var role: Scaffold<Role>? = null) : Scaffold<User> {
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
        val builder = DocumentationShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DocumentationShellBuilder(internal var shell: DocumentationShell = DocumentationShell()) : DocumentationBuilder {
    override fun decision(body: DecisionBuilder.() -> Unit) {
        shell = shell.copy(decisions = shell.decisions.orEmpty() + DecisionScaffolder(DecisionSpec(body)).createScaffold())
    }

    override fun decision(spec: DecisionSpec) {
        shell = shell.copy(decisions = shell.decisions.orEmpty() + DecisionScaffolder(spec).createScaffold())
    }

    override fun decision(ref: DecisionRef) {
        shell = shell.copy(decisions = shell.decisions.orEmpty() + RefScaffold(ref.key))
    }

    override fun decision(value: Decision) {
        shell = shell.copy(decisions = shell.decisions.orEmpty() + ValueScaffold(value))
    }

    override fun decisions(decisions: List<Decision>) {
        shell = shell.copy(decisions = shell.decisions.orEmpty() + decisions.map { ValueScaffold(it) })
    }
}

internal data class DocumentationShell(var decisions: List<Scaffold<Decision>>? = null) : Scaffold<Documentation> {
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
        val builder = DecisionShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DecisionShellBuilder(internal var shell: DecisionShell = DecisionShell()) : DecisionBuilder {
    override fun elementId(value: String) {
        shell = shell.copy(elementId = ValueScaffold(value))
    }

    override fun id(value: String) {
        shell = shell.copy(id = ValueScaffold(value))
    }

    override fun date(value: String) {
        shell = shell.copy(date = ValueScaffold(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = ValueScaffold(value))
    }

    override fun status(value: DecisionStatus) {
        shell = shell.copy(status = ValueScaffold(value))
    }

    override fun content(value: String) {
        shell = shell.copy(content = ValueScaffold(value))
    }

    override fun format(value: Format) {
        shell = shell.copy(format = ValueScaffold(value))
    }
}

internal data class DecisionShell(
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
