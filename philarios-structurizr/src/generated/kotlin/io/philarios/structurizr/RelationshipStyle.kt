package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class RelationshipStyle(
        val tag: String,
        val thickness: Int?,
        val color: String?,
        val fontSize: Int?,
        val width: Int?,
        val dashed: Boolean?,
        val routing: Routing?,
        val position: Int?,
        val opacity: Int?
)

class RelationshipStyleSpec<in C>(internal val body: RelationshipStyleBuilder<C>.() -> Unit)

@DslBuilder
interface RelationshipStyleBuilder<out C> {
    val context: C

    fun tag(value: String)

    fun thickness(value: Int)

    fun color(value: String)

    fun fontSize(value: Int)

    fun width(value: Int)

    fun dashed(value: Boolean)

    fun routing(value: Routing)

    fun position(value: Int)

    fun opacity(value: Int)

    fun include(body: RelationshipStyleBuilder<C>.() -> Unit)

    fun include(spec: RelationshipStyleSpec<C>)

    fun <C2> include(context: C2, body: RelationshipStyleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RelationshipStyleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipStyleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipStyleSpec<C2>)
}

class RelationshipStyleRef(internal val key: String)

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

class RelationshipStyleScaffolder<in C>(internal val spec: RelationshipStyleSpec<C>) : Scaffolder<C, RelationshipStyle> {
    override fun createScaffold(context: C): Scaffold<RelationshipStyle> {
        val builder = RelationshipStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
