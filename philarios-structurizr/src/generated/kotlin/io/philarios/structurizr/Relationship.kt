package io.philarios.structurizr

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

data class Relationship(
        val destinationId: String,
        val description: String?,
        val technologies: List<String>,
        val interactionStyle: InteractionStyle?,
        val tags: List<String>
)

class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit)

@DslBuilder
interface RelationshipBuilder<out C> {
    val context: C

    fun destinationId(value: String)

    fun description(value: String)

    fun technology(value: String)

    fun technologies(technologies: List<String>)

    fun interactionStyle(value: InteractionStyle)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: RelationshipBuilder<C>.() -> Unit)

    fun include(spec: RelationshipSpec<C>)

    fun <C2> include(context: C2, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RelationshipSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>)
}

class RelationshipRef(internal val key: String)

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

class RelationshipScaffolder<in C>(internal val spec: RelationshipSpec<C>) : Scaffolder<C, Relationship> {
    override fun createScaffold(context: C): Scaffold<Relationship> {
        val builder = RelationshipShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
