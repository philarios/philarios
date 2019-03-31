package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
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
    override fun people(body: PersonBuilder<C>.() -> Unit) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(PersonSpec<C>(body)).createScaffold(context))
    }

    override fun people(spec: PersonSpec<C>) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(spec).createScaffold(context))
    }

    override fun people(ref: PersonRef) {
        shell = shell.copy(people = shell.people.orEmpty() + ref)
    }

    override fun people(value: Person) {
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
        shell = shell.copy(technology = Wrapper(value))
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
        shell = shell.copy(technology = Wrapper(value))
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
        shell = shell.copy(technology = Wrapper(value))
    }

    override fun interactionStyle(value: InteractionStyle) {
        shell = shell.copy(interactionStyle = Wrapper(value))
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
