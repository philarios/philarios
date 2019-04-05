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

data class Styles(val elements: List<ElementStyle>?, val relationships: List<RelationshipStyle>?)

class StylesSpec<in C>(internal val body: StylesBuilder<C>.() -> Unit)

@DslBuilder
interface StylesBuilder<out C> {
    val context: C

    fun element(body: ElementStyleBuilder<C>.() -> Unit)

    fun element(spec: ElementStyleSpec<C>)

    fun element(ref: ElementStyleRef)

    fun element(value: ElementStyle)

    fun elements(elements: List<ElementStyle>)

    fun relationship(body: RelationshipStyleBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipStyleSpec<C>)

    fun relationship(ref: RelationshipStyleRef)

    fun relationship(value: RelationshipStyle)

    fun relationships(relationships: List<RelationshipStyle>)

    fun include(body: StylesBuilder<C>.() -> Unit)

    fun include(spec: StylesSpec<C>)

    fun <C2> include(context: C2, body: StylesBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StylesSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StylesBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StylesSpec<C2>)
}

class StylesRef(internal val key: String)

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

class StylesScaffolder<in C>(internal val spec: StylesSpec<C>) : Scaffolder<C, Styles> {
    override fun createScaffold(context: C): Scaffold<Styles> {
        val builder = StylesShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
