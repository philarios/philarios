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

data class SystemLandscapeView(
        val key: String,
        val description: String?,
        val title: String?,
        val paperSize: PaperSize?,
        val softwareSystems: List<String>?,
        val people: List<String>?
)

class SystemLandscapeViewSpec<in C>(internal val body: SystemLandscapeViewBuilder<C>.() -> Unit)

@DslBuilder
interface SystemLandscapeViewBuilder<out C> {
    val context: C

    fun key(value: String)

    fun description(value: String)

    fun title(value: String)

    fun paperSize(value: PaperSize)

    fun softwareSystem(value: String)

    fun softwareSystems(softwareSystems: List<String>)

    fun person(value: String)

    fun people(people: List<String>)

    fun include(body: SystemLandscapeViewBuilder<C>.() -> Unit)

    fun include(spec: SystemLandscapeViewSpec<C>)

    fun <C2> include(context: C2, body: SystemLandscapeViewBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SystemLandscapeViewSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SystemLandscapeViewBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SystemLandscapeViewSpec<C2>)
}

class SystemLandscapeViewRef(internal val key: String)

internal data class SystemLandscapeViewShell(
        var key: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var title: Scaffold<String>? = null,
        var paperSize: Scaffold<PaperSize>? = null,
        var softwareSystems: List<Scaffold<String>>? = null,
        var people: List<Scaffold<String>>? = null
) : Scaffold<SystemLandscapeView> {
    override suspend fun resolve(registry: Registry): SystemLandscapeView {
        checkNotNull(key) { "SystemLandscapeView is missing the key property" }
        val value = SystemLandscapeView(
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
internal class SystemLandscapeViewShellBuilder<out C>(override val context: C, internal var shell: SystemLandscapeViewShell = SystemLandscapeViewShell()) : SystemLandscapeViewBuilder<C> {
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

    override fun include(body: SystemLandscapeViewBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SystemLandscapeViewSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SystemLandscapeViewBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SystemLandscapeViewSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SystemLandscapeViewBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SystemLandscapeViewSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SystemLandscapeViewShellBuilder<C2> = SystemLandscapeViewShellBuilder(context, shell)

    private fun <C2> merge(other: SystemLandscapeViewShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class SystemLandscapeViewScaffolder<in C>(internal val spec: SystemLandscapeViewSpec<C>) : Scaffolder<C, SystemLandscapeView> {
    override fun createScaffold(context: C): Scaffold<SystemLandscapeView> {
        val builder = SystemLandscapeViewShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
