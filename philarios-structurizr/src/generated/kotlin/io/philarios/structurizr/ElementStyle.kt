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

data class ElementStyle(
        val tag: String,
        val width: Int?,
        val height: Int?,
        val background: String?,
        val color: String?,
        val fontSize: Int?,
        val shape: Shape?,
        val icon: String?,
        val border: Border?,
        val opacity: Int?,
        val metadata: Boolean?,
        val description: Boolean?
)

class ElementStyleSpec<in C>(internal val body: ElementStyleBuilder<C>.() -> Unit)

@DslBuilder
interface ElementStyleBuilder<out C> {
    val context: C

    fun tag(value: String)

    fun width(value: Int)

    fun height(value: Int)

    fun background(value: String)

    fun color(value: String)

    fun fontSize(value: Int)

    fun shape(value: Shape)

    fun icon(value: String)

    fun border(value: Border)

    fun opacity(value: Int)

    fun metadata(value: Boolean)

    fun description(value: Boolean)

    fun include(body: ElementStyleBuilder<C>.() -> Unit)

    fun include(spec: ElementStyleSpec<C>)

    fun <C2> include(context: C2, body: ElementStyleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ElementStyleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ElementStyleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ElementStyleSpec<C2>)
}

class ElementStyleRef(internal val key: String)

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

class ElementStyleScaffolder<in C>(internal val spec: ElementStyleSpec<C>) : Scaffolder<C, ElementStyle> {
    override fun createScaffold(context: C): Scaffold<ElementStyle> {
        val builder = ElementStyleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
