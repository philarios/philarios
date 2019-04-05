package io.philarios.structurizr

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Container(
        val id: String,
        val name: String,
        val description: String?,
        val technologies: List<String>,
        val components: List<Component>,
        val relationships: List<Relationship>,
        val tags: List<String>
)

class ContainerSpec<in C>(internal val body: ContainerBuilder<C>.() -> Unit)

@DslBuilder
interface ContainerBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun component(body: ComponentBuilder<C>.() -> Unit)

    fun component(spec: ComponentSpec<C>)

    fun component(ref: ComponentRef)

    fun component(value: Component)

    fun components(components: List<Component>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: ContainerBuilder<C>.() -> Unit)

    fun include(spec: ContainerSpec<C>)

    fun <C2> include(context: C2, body: ContainerBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ContainerSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ContainerBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ContainerSpec<C2>)
}

class ContainerRef(internal val key: String)

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

class ContainerScaffolder<in C>(internal val spec: ContainerSpec<C>) : Scaffolder<C, Container> {
    override fun createScaffold(context: C): Scaffold<Container> {
        val builder = ContainerShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
