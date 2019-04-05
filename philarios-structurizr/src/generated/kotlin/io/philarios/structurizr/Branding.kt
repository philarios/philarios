package io.philarios.structurizr

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Branding(val logo: String?, val font: Font?)

class BrandingSpec<in C>(internal val body: BrandingBuilder<C>.() -> Unit)

@DslBuilder
interface BrandingBuilder<out C> {
    val context: C

    fun logo(value: String)

    fun font(body: FontBuilder<C>.() -> Unit)

    fun font(spec: FontSpec<C>)

    fun font(ref: FontRef)

    fun font(value: Font)

    fun include(body: BrandingBuilder<C>.() -> Unit)

    fun include(spec: BrandingSpec<C>)

    fun <C2> include(context: C2, body: BrandingBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: BrandingSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: BrandingBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: BrandingSpec<C2>)
}

class BrandingRef(internal val key: String)

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

class BrandingScaffolder<in C>(internal val spec: BrandingSpec<C>) : Scaffolder<C, Branding> {
    override fun createScaffold(context: C): Scaffold<Branding> {
        val builder = BrandingShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
