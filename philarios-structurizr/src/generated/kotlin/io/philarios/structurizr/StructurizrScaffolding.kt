package io.philarios.structurizr

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class WorkspaceScaffolder<in C>(internal val spec: WorkspaceSpec<C>) : Scaffolder<C, Workspace> {
    override fun createScaffold(context: C): Scaffold<Workspace> {
        val builder = WorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

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
        shell = shell.copy(model = Deferred(ref.key))
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
        shell = shell.copy(viewSet = Deferred(ref.key))
    }

    override fun viewSet(value: ViewSet) {
        shell = shell.copy(viewSet = Wrapper(value))
    }

    override fun configuration(body: WorkspaceConfigurationBuilder<C>.() -> Unit) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder<C>(WorkspaceConfigurationSpec<C>(body)).createScaffold(context))
    }

    override fun configuration(spec: WorkspaceConfigurationSpec<C>) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder<C>(spec).createScaffold(context))
    }

    override fun configuration(ref: WorkspaceConfigurationRef) {
        shell = shell.copy(configuration = Deferred(ref.key))
    }

    override fun configuration(value: WorkspaceConfiguration) {
        shell = shell.copy(configuration = Wrapper(value))
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

internal data class WorkspaceShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null,
        var viewSet: Scaffold<ViewSet>? = null,
        var configuration: Scaffold<WorkspaceConfiguration>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
            viewSet?.let{ launch { it.resolve(registry) } }
            configuration?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) },
            viewSet?.let{ it.resolve(registry) },
            configuration?.let{ it.resolve(registry) }
        )
        return value
    }
}

class ModelScaffolder<in C>(internal val spec: ModelSpec<C>) : Scaffolder<C, Model> {
    override fun createScaffold(context: C): Scaffold<Model> {
        val builder = ModelShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(people = shell.people.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Deferred(ref.key))
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

class PersonScaffolder<in C>(internal val spec: PersonSpec<C>) : Scaffolder<C, Person> {
    override fun createScaffold(context: C): Scaffold<Person> {
        val builder = PersonShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Deferred(ref.key))
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

class SoftwareSystemScaffolder<in C>(internal val spec: SoftwareSystemSpec<C>) : Scaffolder<C, SoftwareSystem> {
    override fun createScaffold(context: C): Scaffold<SoftwareSystem> {
        val builder = SoftwareSystemShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(containers = shell.containers.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Deferred(ref.key))
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

class ContainerScaffolder<in C>(internal val spec: ContainerSpec<C>) : Scaffolder<C, Container> {
    override fun createScaffold(context: C): Scaffold<Container> {
        val builder = ContainerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(components = shell.components.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Deferred(ref.key))
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

class ComponentScaffolder<in C>(internal val spec: ComponentSpec<C>) : Scaffolder<C, Component> {
    override fun createScaffold(context: C): Scaffold<Component> {
        val builder = ComponentShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Deferred(ref.key))
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

class RelationshipScaffolder<in C>(internal val spec: RelationshipSpec<C>) : Scaffolder<C, Relationship> {
    override fun createScaffold(context: C): Scaffold<Relationship> {
        val builder = RelationshipShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

internal data class RelationshipShell(
        var destinationId: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var technologies: List<Scaffold<String>>? = null,
        var interactionStyle: Scaffold<InteractionStyle>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        checkNotNull(destinationId) { "Relationship is missing the destinationId property" }
        val value = Relationship(
            destinationId!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            technologies.orEmpty().let{ it.map { it.resolve(registry) } },
            interactionStyle?.let{ it.resolve(registry) },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

class ViewSetScaffolder<in C>(internal val spec: ViewSetSpec<C>) : Scaffolder<C, ViewSet> {
    override fun createScaffold(context: C): Scaffold<ViewSet> {
        val builder = ViewSetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(configuration = Deferred(ref.key))
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

class SystemLandscapeViewScaffolder<in C>(internal val spec: SystemLandscapeViewSpec<C>) : Scaffolder<C, SystemLandscapeView> {
    override fun createScaffold(context: C): Scaffold<SystemLandscapeView> {
        val builder = SystemLandscapeViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class SystemContextViewScaffolder<in C>(internal val spec: SystemContextViewSpec<C>) : Scaffolder<C, SystemContextView> {
    override fun createScaffold(context: C): Scaffold<SystemContextView> {
        val builder = SystemContextViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class ContainerViewScaffolder<in C>(internal val spec: ContainerViewSpec<C>) : Scaffolder<C, ContainerView> {
    override fun createScaffold(context: C): Scaffold<ContainerView> {
        val builder = ContainerViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class ComponentViewScaffolder<in C>(internal val spec: ComponentViewSpec<C>) : Scaffolder<C, ComponentView> {
    override fun createScaffold(context: C): Scaffold<ComponentView> {
        val builder = ComponentViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class DynamicViewScaffolder<in C>(internal val spec: DynamicViewSpec<C>) : Scaffolder<C, DynamicView> {
    override fun createScaffold(context: C): Scaffold<DynamicView> {
        val builder = DynamicViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class ConfigurationScaffolder<in C>(internal val spec: ConfigurationSpec<C>) : Scaffolder<C, Configuration> {
    override fun createScaffold(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(branding = Deferred(ref.key))
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
        shell = shell.copy(styles = Deferred(ref.key))
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
        shell = shell.copy(terminology = Deferred(ref.key))
    }

    override fun terminology(value: Terminology) {
        shell = shell.copy(terminology = Wrapper(value))
    }

    override fun viewSortOrder(value: ViewSortOrder) {
        shell = shell.copy(viewSortOrder = Wrapper(value))
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

class BrandingScaffolder<in C>(internal val spec: BrandingSpec<C>) : Scaffolder<C, Branding> {
    override fun createScaffold(context: C): Scaffold<Branding> {
        val builder = BrandingShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(font = Deferred(ref.key))
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

class FontScaffolder<in C>(internal val spec: FontSpec<C>) : Scaffolder<C, Font> {
    override fun createScaffold(context: C): Scaffold<Font> {
        val builder = FontShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class StylesScaffolder<in C>(internal val spec: StylesSpec<C>) : Scaffolder<C, Styles> {
    override fun createScaffold(context: C): Scaffold<Styles> {
        val builder = StylesShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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
        shell = shell.copy(elements = shell.elements.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Deferred(ref.key))
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

class ElementStyleScaffolder<in C>(internal val spec: ElementStyleSpec<C>) : Scaffolder<C, ElementStyle> {
    override fun createScaffold(context: C): Scaffold<ElementStyle> {
        val builder = ElementStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

    override fun icon(value: String) {
        shell = shell.copy(icon = Wrapper(value))
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

class RelationshipStyleScaffolder<in C>(internal val spec: RelationshipStyleSpec<C>) : Scaffolder<C, RelationshipStyle> {
    override fun createScaffold(context: C): Scaffold<RelationshipStyle> {
        val builder = RelationshipStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

class TerminologyScaffolder<in C>(internal val spec: TerminologySpec<C>) : Scaffolder<C, Terminology> {
    override fun createScaffold(context: C): Scaffold<Terminology> {
        val builder = TerminologyShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
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

    override fun relationship(value: String) {
        shell = shell.copy(relationship = Wrapper(value))
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

class WorkspaceConfigurationScaffolder<in C>(internal val spec: WorkspaceConfigurationSpec<C>) : Scaffolder<C, WorkspaceConfiguration> {
    override fun createScaffold(context: C): Scaffold<WorkspaceConfiguration> {
        val builder = WorkspaceConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class WorkspaceConfigurationShellBuilder<out C>(override val context: C, internal var shell: WorkspaceConfigurationShell = WorkspaceConfigurationShell()) : WorkspaceConfigurationBuilder<C> {
    override fun user(body: UserBuilder<C>.() -> Unit) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder<C>(UserSpec<C>(body)).createScaffold(context))
    }

    override fun user(spec: UserSpec<C>) {
        shell = shell.copy(users = shell.users.orEmpty() + UserScaffolder<C>(spec).createScaffold(context))
    }

    override fun user(ref: UserRef) {
        shell = shell.copy(users = shell.users.orEmpty() + Deferred(ref.key))
    }

    override fun user(value: User) {
        shell = shell.copy(users = shell.users.orEmpty() + Wrapper(value))
    }

    override fun users(users: List<User>) {
        shell = shell.copy(users = shell.users.orEmpty() + users.map { Wrapper(it) })
    }

    override fun include(body: WorkspaceConfigurationBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkspaceConfigurationSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkspaceConfigurationBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkspaceConfigurationSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceConfigurationBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceConfigurationSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkspaceConfigurationShellBuilder<C2> = WorkspaceConfigurationShellBuilder(context, shell)

    private fun <C2> merge(other: WorkspaceConfigurationShellBuilder<C2>) {
        this.shell = other.shell
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

class UserScaffolder<in C>(internal val spec: UserSpec<C>) : Scaffolder<C, User> {
    override fun createScaffold(context: C): Scaffold<User> {
        val builder = UserShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class UserShellBuilder<out C>(override val context: C, internal var shell: UserShell = UserShell()) : UserBuilder<C> {
    override fun username(value: String) {
        shell = shell.copy(username = Wrapper(value))
    }

    override fun role(value: Role) {
        shell = shell.copy(role = Wrapper(value))
    }

    override fun include(body: UserBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: UserSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: UserBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: UserSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: UserBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: UserSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): UserShellBuilder<C2> = UserShellBuilder(context, shell)

    private fun <C2> merge(other: UserShellBuilder<C2>) {
        this.shell = other.shell
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
