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

data class Person(
        val id: String,
        val name: String,
        val description: String?,
        val location: Location?,
        val relationships: List<Relationship>,
        val tags: List<String>
)

class PersonSpec<in C>(internal val body: PersonBuilder<C>.() -> Unit)

@DslBuilder
interface PersonBuilder<out C> {
    val context: C

    fun id(value: String)

    fun name(value: String)

    fun description(value: String)

    fun location(value: Location)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: PersonBuilder<C>.() -> Unit)

    fun include(spec: PersonSpec<C>)

    fun <C2> include(context: C2, body: PersonBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: PersonSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: PersonBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: PersonSpec<C2>)
}

class PersonRef(internal val key: String)

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

class PersonScaffolder<in C>(internal val spec: PersonSpec<C>) : Scaffolder<C, Person> {
    override fun createScaffold(context: C): Scaffold<Person> {
        val builder = PersonShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
