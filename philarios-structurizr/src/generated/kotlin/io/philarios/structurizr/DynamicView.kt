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

data class DynamicView(
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?
)

class DynamicViewSpec<in C>(internal val body: DynamicViewBuilder<C>.() -> Unit)

@DslBuilder
interface DynamicViewBuilder<out C> {
    val context: C

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun include(body: DynamicViewBuilder<C>.() -> Unit)

    fun include(spec: DynamicViewSpec<C>)

    fun <C2> include(context: C2, body: DynamicViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DynamicViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DynamicViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DynamicViewSpec<C2>)
}

class DynamicViewRef(internal val key: String)

internal data class DynamicViewShell(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null
) : Scaffold<DynamicView> {
    override suspend fun resolve(registry: Registry): DynamicView {
        checkNotNull(key) { "DynamicView is missing the key property" }
        val value = DynamicView(
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class DynamicViewShellBuilder<out C>(override val context: C, internal var shell: DynamicViewShell = DynamicViewShell()) : DynamicViewBuilder<C> {
    override fun key(value: String) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun title(value: String) {
        shell = shell.copy(title = Wrapper(value))
    }

    override fun paperSize(value: PaperSize) {
        shell = shell.copy(paperSize = Wrapper(value))
    }

    override fun include(body: DynamicViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DynamicViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DynamicViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DynamicViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DynamicViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DynamicViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DynamicViewShellBuilder<C2> = DynamicViewShellBuilder(context, shell)

    private fun <C2> merge(other: DynamicViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class DynamicViewScaffolder<in C>(internal val spec: DynamicViewSpec<C>) : Scaffolder<C, DynamicView> {
    override fun createScaffold(context: C): Scaffold<DynamicView> {
        val builder = DynamicViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
