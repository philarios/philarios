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

data class SystemContextView(
        val softwareSystemId: String,
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?
)

class SystemContextViewSpec<in C>(internal val body: SystemContextViewBuilder<C>.() -> Unit)

@DslBuilder
interface SystemContextViewBuilder<out C> {
    val context: C

    fun softwareSystemId(value: String)

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun include(body: SystemContextViewBuilder<C>.() -> Unit)

    fun include(spec: SystemContextViewSpec<C>)

    fun <C2> include(context: C2, body: SystemContextViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SystemContextViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SystemContextViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SystemContextViewSpec<C2>)
}

class SystemContextViewRef(internal val key: String)

internal data class SystemContextViewShell(
        var softwareSystemId: Scaffold<String>? = null,
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null
) : Scaffold<SystemContextView> {
    override suspend fun resolve(registry: Registry): SystemContextView {
        checkNotNull(softwareSystemId) { "SystemContextView is missing the softwareSystemId property" }
        checkNotNull(key) { "SystemContextView is missing the key property" }
        val value = SystemContextView(
            softwareSystemId!!.let{ it.resolve(registry) },
            key!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            title?.let{ it.resolve(registry) },
            paperSize?.let{ it.resolve(registry) },
            softwareSystems?.let{ it.map { it.resolve(registry) } },
            people?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class SystemContextViewShellBuilder<out C>(override val context: C, internal var shell: SystemContextViewShell = SystemContextViewShell()) : SystemContextViewBuilder<C> {
    override fun softwareSystemId(value: String) {
        shell = shell.copy(softwareSystemId = Wrapper(value))
    }

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

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + Wrapper(value))
    }

    override fun softwareSystems(softwareSystems: List<String>) {
        shell = shell.copy(softwareSystems = shell.softwareSystems.orEmpty() + softwareSystems.map { Wrapper(it) })
    }

    override fun person(value: String) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<String>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun include(body: SystemContextViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SystemContextViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SystemContextViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SystemContextViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SystemContextViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SystemContextViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SystemContextViewShellBuilder<C2> = SystemContextViewShellBuilder(context, shell)

    private fun <C2> merge(other: SystemContextViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class SystemContextViewScaffolder<in C>(internal val spec: SystemContextViewSpec<C>) : Scaffolder<C, SystemContextView> {
    override fun createScaffold(context: C): Scaffold<SystemContextView> {
        val builder = SystemContextViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
