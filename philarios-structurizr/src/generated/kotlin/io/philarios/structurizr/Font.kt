package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Font(val name: String, val url: String?)

class FontSpec<in C>(internal val body: FontBuilder<C>.() -> Unit)

@DslBuilder
interface FontBuilder<out C> {
    val context: C

    fun name(value: String)

    fun url(value: String)

    fun include(body: FontBuilder<C>.() -> Unit)

    fun include(spec: FontSpec<C>)

    fun <C2> include(context: C2, body: FontBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FontSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FontBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FontSpec<C2>)
}

class FontRef(internal val key: String)

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

class FontScaffolder<in C>(internal val spec: FontSpec<C>) : Scaffolder<C, Font> {
    override fun createScaffold(context: C): Scaffold<Font> {
        val builder = FontShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
