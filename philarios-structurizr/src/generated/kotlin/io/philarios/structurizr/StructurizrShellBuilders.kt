package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
internal class WorkspaceShellBuilder<out C>(override val context: C, internal var shell: WorkspaceShell = WorkspaceShell()) : WorkspaceBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun model(body: ModelBuilder<C>.() -> Unit) {
        shell = shell.copy(model = ModelScaffolder<C>(ModelSpec<C>(body)).createScaffold(context))
    }

    override fun model(spec: ModelSpec<C>) {
        shell = shell.copy(model = ModelScaffolder<C>(spec).createScaffold(context))
    }

    override fun model(ref: ModelRef) {
        shell = shell.copy(model = ref)
    }

    override fun model(value: Model) {
        shell = shell.copy(model = Wrapper(value))
    }

    override fun viewSet(body: ViewSetBuilder<C>.() -> Unit) {
        shell = shell.copy(viewSet = ViewSetScaffolder<C>(ViewSetSpec<C>(body)).createScaffold(context))
    }

    override fun viewSet(spec: ViewSetSpec<C>) {
        shell = shell.copy(viewSet = ViewSetScaffolder<C>(spec).createScaffold(context))
    }

    override fun viewSet(ref: ViewSetRef) {
        shell = shell.copy(viewSet = ref)
    }

    override fun viewSet(value: ViewSet) {
        shell = shell.copy(viewSet = Wrapper(value))
    }

    override fun include(body: WorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkspaceShellBuilder<C2> = WorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: WorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ModelShellBuilder<out C>(override val context: C, internal var shell: ModelShell = ModelShell()) : ModelBuilder<C> {
    override fun person(body: PersonBuilder<C>.() -> Unit) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(PersonSpec<C>(body)).createScaffold(context))
    }

    override fun person(spec: PersonSpec<C>) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(spec).createScaffold(context))
    }

    override fun person(ref: PersonRef) {
        shell = shell.copy(people = shell.people.orEmpty() + ref)
    }

    override fun person(value: Person) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<Person>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun softwareSystem(body: SoftwareSystemBuilder<C>.() -> Unit) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + SoftwareSystemScaffolder<C>(SoftwareSystemSpec<C>(body)).createScaffold(context))
    }

    override fun softwareSystem(spec: SoftwareSystemSpec<C>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + SoftwareSystemScaffolder<C>(spec).createScaffold(context))
    }

    override fun softwareSystem(ref: SoftwareSystemRef) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + ref)
    }

    override fun softwareSystem(value: SoftwareSystem) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<SoftwareSystem>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun include(body: ModelBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ModelSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ModelBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ModelSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ModelBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ModelSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ModelShellBuilder<C2> = ModelShellBuilder(context, shell)

    private fun <C2> merge(other: ModelShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PersonShellBuilder<out C>(override val context: C, internal var shell: PersonShell = PersonShell()) : PersonBuilder<C> {
    override fun id(value: String) {
        shell = shell.copy(id = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun location(value: Location) {
        shell = shell.copy(location = Wrapper(value))
    }

    override fun relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(RelationshipSpec<C>(body)).createScaffold(context))
    }

    override fun relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(spec).createScaffold(context))
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: PersonBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersonSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersonBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersonSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersonBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersonSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersonShellBuilder<C2> = PersonShellBuilder(context, shell)

    private fun <C2> merge(other: PersonShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SoftwareSystemShellBuilder<out C>(override val context: C, internal var shell: SoftwareSystemShell = SoftwareSystemShell()) : SoftwareSystemBuilder<C> {
    override fun id(value: String) {
        shell = shell.copy(id = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun location(value: Location) {
        shell = shell.copy(location = Wrapper(value))
    }

    override fun container(body: ContainerBuilder<C>.() -> Unit) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ContainerScaffolder<C>(ContainerSpec<C>(body)).createScaffold(context))
    }

    override fun container(spec: ContainerSpec<C>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ContainerScaffolder<C>(spec).createScaffold(context))
    }

    override fun container(ref: ContainerRef) {
        shell = shell.copy(containers = shell.containers.orEmpty() + ref)
    }

    override fun container(value: Container) {
        shell = shell.copy(containers = shell.containers.orEmpty() + Wrapper(value))
    }

    override fun containers(containers: List<Container>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { Wrapper(it) })
    }

    override fun relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(RelationshipSpec<C>(body)).createScaffold(context))
    }

    override fun relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(spec).createScaffold(context))
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: SoftwareSystemBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SoftwareSystemSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SoftwareSystemBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SoftwareSystemSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SoftwareSystemBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SoftwareSystemSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SoftwareSystemShellBuilder<C2> = SoftwareSystemShellBuilder(context, shell)

    private fun <C2> merge(other: SoftwareSystemShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ContainerShellBuilder<out C>(override val context: C, internal var shell: ContainerShell = ContainerShell()) : ContainerBuilder<C> {
    override fun id(value: String) {
        shell = shell.copy(id = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + Wrapper(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { Wrapper(it) })
    }

    override fun component(body: ComponentBuilder<C>.() -> Unit) {
        shell = shell.copy(components = shell.components.orEmpty() + ComponentScaffolder<C>(ComponentSpec<C>(body)).createScaffold(context))
    }

    override fun component(spec: ComponentSpec<C>) {
        shell = shell.copy(components = shell.components.orEmpty() + ComponentScaffolder<C>(spec).createScaffold(context))
    }

    override fun component(ref: ComponentRef) {
        shell = shell.copy(components = shell.components.orEmpty() + ref)
    }

    override fun component(value: Component) {
        shell = shell.copy(components = shell.components.orEmpty() + Wrapper(value))
    }

    override fun components(components: List<Component>) {
        shell = shell.copy(components = shell.components.orEmpty() + components.map { Wrapper(it) })
    }

    override fun relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(RelationshipSpec<C>(body)).createScaffold(context))
    }

    override fun relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(spec).createScaffold(context))
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: ContainerBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ContainerSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ContainerBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ContainerSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ContainerBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ContainerSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ContainerShellBuilder<C2> = ContainerShellBuilder(context, shell)

    private fun <C2> merge(other: ContainerShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ComponentShellBuilder<out C>(override val context: C, internal var shell: ComponentShell = ComponentShell()) : ComponentBuilder<C> {
    override fun id(value: String) {
        shell = shell.copy(id = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + Wrapper(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { Wrapper(it) })
    }

    override fun relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(RelationshipSpec<C>(body)).createScaffold(context))
    }

    override fun relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipScaffolder<C>(spec).createScaffold(context))
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(value: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(value))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: ComponentBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ComponentSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ComponentBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ComponentSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ComponentBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ComponentSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ComponentShellBuilder<C2> = ComponentShellBuilder(context, shell)

    private fun <C2> merge(other: ComponentShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RelationshipShellBuilder<out C>(override val context: C, internal var shell: RelationshipShell = RelationshipShell()) : RelationshipBuilder<C> {
    override fun destinationId(value: String) {
        shell = shell.copy(destinationId = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun technology(value: String) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + Wrapper(value))
    }

    override fun technologies(technologies: List<String>) {
        shell = shell.copy(technologies = shell.technologies.orEmpty() + technologies.map { Wrapper(it) })
    }

    override fun interactionStyle(value: InteractionStyle) {
        shell = shell.copy(interactionStyle = Wrapper(value))
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: RelationshipBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RelationshipSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RelationshipBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RelationshipSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RelationshipShellBuilder<C2> = RelationshipShellBuilder(context, shell)

    private fun <C2> merge(other: RelationshipShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ViewSetShellBuilder<out C>(override val context: C, internal var shell: ViewSetShell = ViewSetShell()) : ViewSetBuilder<C> {
    override fun systemLandscapeView(body: SystemLandscapeViewBuilder<C>.() -> Unit) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder<C>(SystemLandscapeViewSpec<C>(body)).createScaffold(context))
    }

    override fun systemLandscapeView(spec: SystemLandscapeViewSpec<C>) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun systemLandscapeView(ref: SystemLandscapeViewRef) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + ref)
    }

    override fun systemLandscapeView(value: SystemLandscapeView) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + Wrapper(value))
    }

    override fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + systemLandscapeViews.map { Wrapper(it) })
    }

    override fun systemContextView(body: SystemContextViewBuilder<C>.() -> Unit) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder<C>(SystemContextViewSpec<C>(body)).createScaffold(context))
    }

    override fun systemContextView(spec: SystemContextViewSpec<C>) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun systemContextView(ref: SystemContextViewRef) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + ref)
    }

    override fun systemContextView(value: SystemContextView) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + Wrapper(value))
    }

    override fun systemContextViews(systemContextViews: List<SystemContextView>) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + systemContextViews.map { Wrapper(it) })
    }

    override fun containerView(body: ContainerViewBuilder<C>.() -> Unit) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder<C>(ContainerViewSpec<C>(body)).createScaffold(context))
    }

    override fun containerView(spec: ContainerViewSpec<C>) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun containerView(ref: ContainerViewRef) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ref)
    }

    override fun containerView(value: ContainerView) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + Wrapper(value))
    }

    override fun containerViews(containerViews: List<ContainerView>) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + containerViews.map { Wrapper(it) })
    }

    override fun componentView(body: ComponentViewBuilder<C>.() -> Unit) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder<C>(ComponentViewSpec<C>(body)).createScaffold(context))
    }

    override fun componentView(spec: ComponentViewSpec<C>) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun componentView(ref: ComponentViewRef) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ref)
    }

    override fun componentView(value: ComponentView) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + Wrapper(value))
    }

    override fun componentViews(componentViews: List<ComponentView>) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + componentViews.map { Wrapper(it) })
    }

    override fun dynamicView(body: DynamicViewBuilder<C>.() -> Unit) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder<C>(DynamicViewSpec<C>(body)).createScaffold(context))
    }

    override fun dynamicView(spec: DynamicViewSpec<C>) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun dynamicView(ref: DynamicViewRef) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + ref)
    }

    override fun dynamicView(value: DynamicView) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + Wrapper(value))
    }

    override fun dynamicViews(dynamicViews: List<DynamicView>) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + dynamicViews.map { Wrapper(it) })
    }

    override fun configuration(body: ConfigurationBuilder<C>.() -> Unit) {
        shell = shell.copy(configuration = ConfigurationScaffolder<C>(ConfigurationSpec<C>(body)).createScaffold(context))
    }

    override fun configuration(spec: ConfigurationSpec<C>) {
        shell = shell.copy(configuration = ConfigurationScaffolder<C>(spec).createScaffold(context))
    }

    override fun configuration(ref: ConfigurationRef) {
        shell = shell.copy(configuration = ref)
    }

    override fun configuration(value: Configuration) {
        shell = shell.copy(configuration = Wrapper(value))
    }

    override fun include(body: ViewSetBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ViewSetSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ViewSetBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ViewSetSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ViewSetBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ViewSetSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ViewSetShellBuilder<C2> = ViewSetShellBuilder(context, shell)

    private fun <C2> merge(other: ViewSetShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SystemLandscapeViewShellBuilder<out C>(override val context: C, internal var shell: SystemLandscapeViewShell = SystemLandscapeViewShell()) : SystemLandscapeViewBuilder<C> {
    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun include(body: SystemLandscapeViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SystemLandscapeViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SystemLandscapeViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SystemLandscapeViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SystemLandscapeViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SystemLandscapeViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SystemLandscapeViewShellBuilder<C2> = SystemLandscapeViewShellBuilder(context, shell)

    private fun <C2> merge(other: SystemLandscapeViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SystemContextViewShellBuilder<out C>(override val context: C, internal var shell: SystemContextViewShell = SystemContextViewShell()) : SystemContextViewBuilder<C> {
    override fun softwareSystemId(value: String) {
        shell = shell.copy(softwareSystemId = Wrapper(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun include(body: SystemContextViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SystemContextViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SystemContextViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SystemContextViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SystemContextViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SystemContextViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SystemContextViewShellBuilder<C2> = SystemContextViewShellBuilder(context, shell)

    private fun <C2> merge(other: SystemContextViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ContainerViewShellBuilder<out C>(override val context: C, internal var shell: ContainerViewShell = ContainerViewShell()) : ContainerViewBuilder<C> {
    override fun softwareSystemId(value: String) {
        shell = shell.copy(softwareSystemId = Wrapper(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun container(value: String) {
        shell = shell.copy(containers = shell.containers.orEmpty() + Wrapper(value))
    }

    override fun containers(containers: List<String>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { Wrapper(it) })
    }

    override fun include(body: ContainerViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ContainerViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ContainerViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ContainerViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ContainerViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ContainerViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ContainerViewShellBuilder<C2> = ContainerViewShellBuilder(context, shell)

    private fun <C2> merge(other: ContainerViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ComponentViewShellBuilder<out C>(override val context: C, internal var shell: ComponentViewShell = ComponentViewShell()) : ComponentViewBuilder<C> {
    override fun containerId(value: String) {
        shell = shell.copy(containerId = Wrapper(value))
    }

    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun container(value: String) {
        shell = shell.copy(containers = shell.containers.orEmpty() + Wrapper(value))
    }

    override fun containers(containers: List<String>) {
        shell = shell.copy(containers = shell.containers.orEmpty() + containers.map { Wrapper(it) })
    }

    override fun component(value: String) {
        shell = shell.copy(components = shell.components.orEmpty() + Wrapper(value))
    }

    override fun components(components: List<String>) {
        shell = shell.copy(components = shell.components.orEmpty() + components.map { Wrapper(it) })
    }

    override fun include(body: ComponentViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ComponentViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ComponentViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ComponentViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ComponentViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ComponentViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ComponentViewShellBuilder<C2> = ComponentViewShellBuilder(context, shell)

    private fun <C2> merge(other: ComponentViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DynamicViewShellBuilder<out C>(override val context: C, internal var shell: DynamicViewShell = DynamicViewShell()) : DynamicViewBuilder<C> {
    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun include(body: DynamicViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DynamicViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DynamicViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DynamicViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DynamicViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DynamicViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DynamicViewShellBuilder<C2> = DynamicViewShellBuilder(context, shell)

    private fun <C2> merge(other: DynamicViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ConfigurationShellBuilder<out C>(override val context: C, internal var shell: ConfigurationShell = ConfigurationShell()) : ConfigurationBuilder<C> {
    override fun branding(body: BrandingBuilder<C>.() -> Unit) {
        shell = shell.copy(branding = BrandingScaffolder<C>(BrandingSpec<C>(body)).createScaffold(context))
    }

    override fun branding(spec: BrandingSpec<C>) {
        shell = shell.copy(branding = BrandingScaffolder<C>(spec).createScaffold(context))
    }

    override fun branding(ref: BrandingRef) {
        shell = shell.copy(branding = ref)
    }

    override fun branding(value: Branding) {
        shell = shell.copy(branding = Wrapper(value))
    }

    override fun styles(body: StylesBuilder<C>.() -> Unit) {
        shell = shell.copy(styles = StylesScaffolder<C>(StylesSpec<C>(body)).createScaffold(context))
    }

    override fun styles(spec: StylesSpec<C>) {
        shell = shell.copy(styles = StylesScaffolder<C>(spec).createScaffold(context))
    }

    override fun styles(ref: StylesRef) {
        shell = shell.copy(styles = ref)
    }

    override fun styles(value: Styles) {
        shell = shell.copy(styles = Wrapper(value))
    }

    override fun terminology(body: TerminologyBuilder<C>.() -> Unit) {
        shell = shell.copy(terminology = TerminologyScaffolder<C>(TerminologySpec<C>(body)).createScaffold(context))
    }

    override fun terminology(spec: TerminologySpec<C>) {
        shell = shell.copy(terminology = TerminologyScaffolder<C>(spec).createScaffold(context))
    }

    override fun terminology(ref: TerminologyRef) {
        shell = shell.copy(terminology = ref)
    }

    override fun terminology(value: Terminology) {
        shell = shell.copy(terminology = Wrapper(value))
    }

    override fun include(body: ConfigurationBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ConfigurationSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ConfigurationSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConfigurationShellBuilder<C2> = ConfigurationShellBuilder(context, shell)

    private fun <C2> merge(other: ConfigurationShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class BrandingShellBuilder<out C>(override val context: C, internal var shell: BrandingShell = BrandingShell()) : BrandingBuilder<C> {
    override fun logo(value: String) {
        shell = shell.copy(logo = Wrapper(value))
    }

    override fun font(body: FontBuilder<C>.() -> Unit) {
        shell = shell.copy(font = FontScaffolder<C>(FontSpec<C>(body)).createScaffold(context))
    }

    override fun font(spec: FontSpec<C>) {
        shell = shell.copy(font = FontScaffolder<C>(spec).createScaffold(context))
    }

    override fun font(ref: FontRef) {
        shell = shell.copy(font = ref)
    }

    override fun font(value: Font) {
        shell = shell.copy(font = Wrapper(value))
    }

    override fun include(body: BrandingBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: BrandingSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: BrandingBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: BrandingSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: BrandingBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: BrandingSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): BrandingShellBuilder<C2> = BrandingShellBuilder(context, shell)

    private fun <C2> merge(other: BrandingShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class FontShellBuilder<out C>(override val context: C, internal var shell: FontShell = FontShell()) : FontBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun url(value: String) {
        shell = shell.copy(url = Wrapper(value))
    }

    override fun include(body: FontBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FontSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FontBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FontSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FontBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FontSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FontShellBuilder<C2> = FontShellBuilder(context, shell)

    private fun <C2> merge(other: FontShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StylesShellBuilder<out C>(override val context: C, internal var shell: StylesShell = StylesShell()) : StylesBuilder<C> {
    override fun element(body: ElementStyleBuilder<C>.() -> Unit) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ElementStyleScaffolder<C>(ElementStyleSpec<C>(body)).createScaffold(context))
    }

    override fun element(spec: ElementStyleSpec<C>) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ElementStyleScaffolder<C>(spec).createScaffold(context))
    }

    override fun element(ref: ElementStyleRef) {
        shell = shell.copy(elements = shell.elements.orEmpty() + ref)
    }

    override fun element(value: ElementStyle) {
        shell = shell.copy(elements = shell.elements.orEmpty() + Wrapper(value))
    }

    override fun elements(elements: List<ElementStyle>) {
        shell = shell.copy(elements = shell.elements.orEmpty() + elements.map { Wrapper(it) })
    }

    override fun relationship(body: RelationshipStyleBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipStyleScaffolder<C>(RelationshipStyleSpec<C>(body)).createScaffold(context))
    }

    override fun relationship(spec: RelationshipStyleSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipStyleScaffolder<C>(spec).createScaffold(context))
    }

    override fun relationship(ref: RelationshipStyleRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(value: RelationshipStyle) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(value))
    }

    override fun relationships(relationships: List<RelationshipStyle>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun include(body: StylesBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StylesSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StylesBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StylesSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StylesBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StylesSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StylesShellBuilder<C2> = StylesShellBuilder(context, shell)

    private fun <C2> merge(other: StylesShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ElementStyleShellBuilder<out C>(override val context: C, internal var shell: ElementStyleShell = ElementStyleShell()) : ElementStyleBuilder<C> {
    override fun tag(value: String) {
        shell = shell.copy(tag = Wrapper(value))
    }

    override fun width(value: Int) {
        shell = shell.copy(width = Wrapper(value))
    }

    override fun height(value: Int) {
        shell = shell.copy(height = Wrapper(value))
    }

    override fun background(value: String) {
        shell = shell.copy(background = Wrapper(value))
    }

    override fun color(value: String) {
        shell = shell.copy(color = Wrapper(value))
    }

    override fun fontSize(value: Int) {
        shell = shell.copy(fontSize = Wrapper(value))
    }

    override fun shape(value: Shape) {
        shell = shell.copy(shape = Wrapper(value))
    }

    override fun border(value: Border) {
        shell = shell.copy(border = Wrapper(value))
    }

    override fun opacity(value: Int) {
        shell = shell.copy(opacity = Wrapper(value))
    }

    override fun metadata(value: Boolean) {
        shell = shell.copy(metadata = Wrapper(value))
    }

    override fun description(value: Boolean) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun include(body: ElementStyleBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ElementStyleSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ElementStyleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ElementStyleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ElementStyleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ElementStyleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ElementStyleShellBuilder<C2> = ElementStyleShellBuilder(context, shell)

    private fun <C2> merge(other: ElementStyleShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RelationshipStyleShellBuilder<out C>(override val context: C, internal var shell: RelationshipStyleShell = RelationshipStyleShell()) : RelationshipStyleBuilder<C> {
    override fun tag(value: String) {
        shell = shell.copy(tag = Wrapper(value))
    }

    override fun thickness(value: Int) {
        shell = shell.copy(thickness = Wrapper(value))
    }

    override fun color(value: String) {
        shell = shell.copy(color = Wrapper(value))
    }

    override fun fontSize(value: Int) {
        shell = shell.copy(fontSize = Wrapper(value))
    }

    override fun width(value: Int) {
        shell = shell.copy(width = Wrapper(value))
    }

    override fun dashed(value: Boolean) {
        shell = shell.copy(dashed = Wrapper(value))
    }

    override fun routing(value: Routing) {
        shell = shell.copy(routing = Wrapper(value))
    }

    override fun position(value: Int) {
        shell = shell.copy(position = Wrapper(value))
    }

    override fun opacity(value: Int) {
        shell = shell.copy(opacity = Wrapper(value))
    }

    override fun include(body: RelationshipStyleBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RelationshipStyleSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RelationshipStyleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RelationshipStyleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipStyleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipStyleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RelationshipStyleShellBuilder<C2> = RelationshipStyleShellBuilder(context, shell)

    private fun <C2> merge(other: RelationshipStyleShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TerminologyShellBuilder<out C>(override val context: C, internal var shell: TerminologyShell = TerminologyShell()) : TerminologyBuilder<C> {
    override fun enterprise(value: String) {
        shell = shell.copy(enterprise = Wrapper(value))
    }

    override fun person(value: String) {
        shell = shell.copy(person = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystem = Wrapper(value))
    }

    override fun container(value: String) {
        shell = shell.copy(container = Wrapper(value))
    }

    override fun component(value: String) {
        shell = shell.copy(component = Wrapper(value))
    }

    override fun code(value: String) {
        shell = shell.copy(code = Wrapper(value))
    }

    override fun deploymentNode(value: String) {
        shell = shell.copy(deploymentNode = Wrapper(value))
    }

    override fun include(body: TerminologyBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TerminologySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TerminologyBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TerminologySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TerminologyBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TerminologySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TerminologyShellBuilder<C2> = TerminologyShellBuilder(context, shell)

    private fun <C2> merge(other: TerminologyShellBuilder<C2>) {
        this.shell = other.shell
    }
}
