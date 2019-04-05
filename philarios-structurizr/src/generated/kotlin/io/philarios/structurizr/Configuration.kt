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

data class Configuration(
        val branding: Branding?,
        val styles: Styles?,
        val terminology: Terminology?,
        val viewSortOrder: ViewSortOrder?
)

class ConfigurationSpec<in C>(internal val body: ConfigurationBuilder<C>.() -> Unit)

@DslBuilder
interface ConfigurationBuilder<out C> {
    val context: C

    fun branding(body: BrandingBuilder<C>.() -> Unit)

    fun branding(spec: BrandingSpec<C>)

    fun branding(ref: BrandingRef)

    fun branding(value: Branding)

    fun styles(body: StylesBuilder<C>.() -> Unit)

    fun styles(spec: StylesSpec<C>)

    fun styles(ref: StylesRef)

    fun styles(value: Styles)

    fun terminology(body: TerminologyBuilder<C>.() -> Unit)

    fun terminology(spec: TerminologySpec<C>)

    fun terminology(ref: TerminologyRef)

    fun terminology(value: Terminology)

    fun viewSortOrder(value: ViewSortOrder)

    fun include(body: ConfigurationBuilder<C>.() -> Unit)

    fun include(spec: ConfigurationSpec<C>)

    fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConfigurationSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>)
}

class ConfigurationRef(internal val key: String)

internal data class ConfigurationShell(
        var branding: Scaffold<Branding>? = null,
        var styles: Scaffold<Styles>? = null,
        var terminology: Scaffold<Terminology>? = null,
        var viewSortOrder: Scaffold<ViewSortOrder>? = null
) : Scaffold<Configuration> {
    override suspend fun resolve(registry: Registry): Configuration {
        coroutineScope {
            branding?.let{ launch { it.resolve(registry) } }
            styles?.let{ launch { it.resolve(registry) } }
            terminology?.let{ launch { it.resolve(registry) } }
        }
        val value = Configuration(
            branding?.let{ it.resolve(registry) },
            styles?.let{ it.resolve(registry) },
            terminology?.let{ it.resolve(registry) },
            viewSortOrder?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class ConfigurationShellBuilder<out C>(override val context: C, internal var shell: ConfigurationShell = ConfigurationShell()) : ConfigurationBuilder<C> {
    override fun branding(body: BrandingBuilder<C>.() -> Unit) {
        shell = shell.copy(branding = BrandingScaffolder<C>(BrandingSpec<C>(body)).createScaffold(context))
    }

    override fun branding(spec: BrandingSpec<C>) {
        shell = shell.copy(branding = BrandingScaffolder<C>(spec).createScaffold(context))
    }

    override fun branding(ref: BrandingRef) {
        shell = shell.copy(branding = Deferred(ref.key))
    }

    override fun branding(value: Branding) {
        shell = shell.copy(branding = Wrapper(value))
    }

    override fun styles(body: StylesBuilder<C>.() -> Unit) {
        shell = shell.copy(styles = StylesScaffolder<C>(StylesSpec<C>(body)).createScaffold(context))
    }

    override fun styles(spec: StylesSpec<C>) {
        shell = shell.copy(styles = StylesScaffolder<C>(spec).createScaffold(context))
    }

    override fun styles(ref: StylesRef) {
        shell = shell.copy(styles = Deferred(ref.key))
    }

    override fun styles(value: Styles) {
        shell = shell.copy(styles = Wrapper(value))
    }

    override fun terminology(body: TerminologyBuilder<C>.() -> Unit) {
        shell = shell.copy(terminology = TerminologyScaffolder<C>(TerminologySpec<C>(body)).createScaffold(context))
    }

    override fun terminology(spec: TerminologySpec<C>) {
        shell = shell.copy(terminology = TerminologyScaffolder<C>(spec).createScaffold(context))
    }

    override fun terminology(ref: TerminologyRef) {
        shell = shell.copy(terminology = Deferred(ref.key))
    }

    override fun terminology(value: Terminology) {
        shell = shell.copy(terminology = Wrapper(value))
    }

    override fun viewSortOrder(value: ViewSortOrder) {
        shell = shell.copy(viewSortOrder = Wrapper(value))
    }

    override fun include(body: ConfigurationBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ConfigurationSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ConfigurationBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ConfigurationSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ConfigurationBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ConfigurationSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConfigurationShellBuilder<C2> = ConfigurationShellBuilder(context, shell)

    private fun <C2> merge(other: ConfigurationShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ConfigurationScaffolder<in C>(internal val spec: ConfigurationSpec<C>) : Scaffolder<C, Configuration> {
    override fun createScaffold(context: C): Scaffold<Configuration> {
        val builder = ConfigurationShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
