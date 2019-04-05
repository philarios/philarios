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

data class Model(val people: List<Person>, val softwareSystems: List<SoftwareSystem>)

class ModelSpec<in C>(internal val body: ModelBuilder<C>.() -> Unit)

@DslBuilder
interface ModelBuilder<out C> {
    val context: C

    fun person(body: PersonBuilder<C>.() -> Unit)

    fun person(spec: PersonSpec<C>)

    fun person(ref: PersonRef)

    fun person(value: Person)

    fun people(people: List<Person>)

    fun softwareSystem(body: SoftwareSystemBuilder<C>.() -> Unit)

    fun softwareSystem(spec: SoftwareSystemSpec<C>)

    fun softwareSystem(ref: SoftwareSystemRef)

    fun softwareSystem(value: SoftwareSystem)

    fun softwareSystems(softwareSystems: List<SoftwareSystem>)

    fun include(body: ModelBuilder<C>.() -> Unit)

    fun include(spec: ModelSpec<C>)

    fun <C2> include(context: C2, body: ModelBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ModelSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ModelBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ModelSpec<C2>)
}

class ModelRef(internal val key: String)

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

class ModelScaffolder<in C>(internal val spec: ModelSpec<C>) : Scaffolder<C, Model> {
    override fun createScaffold(context: C): Scaffold<Model> {
        val builder = ModelShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
