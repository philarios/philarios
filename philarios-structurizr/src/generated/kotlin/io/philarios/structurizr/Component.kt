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

data class Component(
        val id: String,
        val name: String,
        val description: String?,
        val technologies: List<String>,
        val relationships: List<Relationship>,
        val tags: List<String>
)

class ComponentSpec<in C>(internal val body: ComponentBuilder<C>.() -> Unit)

@DslBuilder
interface ComponentBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: ComponentBuilder<C>.() -> Unit)

    fun include(spec: ComponentSpec<C>)

    fun <C2> include(context: C2, body: ComponentBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ComponentSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ComponentBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ComponentSpec<C2>)
}

class ComponentRef(internal val key: String)

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

class ComponentScaffolder<in C>(internal val spec: ComponentSpec<C>) : Scaffolder<C, Component> {
    override fun createScaffold(context: C): Scaffold<Component> {
        val builder = ComponentShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
