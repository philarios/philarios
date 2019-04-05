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

data class Workspace(
        val name: String,
        val description: String?,
        val model: Model?,
        val viewSet: ViewSet?,
        val configuration: WorkspaceConfiguration?
)

class WorkspaceSpec<in C>(internal val body: WorkspaceBuilder<C>.() -> Unit)

@DslBuilder
interface WorkspaceBuilder<out C> {
    val context: C

    fun name(value: String)

    fun description(value: String)

    fun model(body: ModelBuilder<C>.() -> Unit)

    fun model(spec: ModelSpec<C>)

    fun model(ref: ModelRef)

    fun model(value: Model)

    fun viewSet(body: ViewSetBuilder<C>.() -> Unit)

    fun viewSet(spec: ViewSetSpec<C>)

    fun viewSet(ref: ViewSetRef)

    fun viewSet(value: ViewSet)

    fun configuration(body: WorkspaceConfigurationBuilder<C>.() -> Unit)

    fun configuration(spec: WorkspaceConfigurationSpec<C>)

    fun configuration(ref: WorkspaceConfigurationRef)

    fun configuration(value: WorkspaceConfiguration)

    fun include(body: WorkspaceBuilder<C>.() -> Unit)

    fun include(spec: WorkspaceSpec<C>)

    fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>)
}

class WorkspaceRef(internal val key: String)

internal data class WorkspaceShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null,
        var viewSet: Scaffold<ViewSet>? = null,
        var configuration: Scaffold<WorkspaceConfiguration>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
            viewSet?.let{ launch { it.resolve(registry) } }
            configuration?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description?.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) },
            viewSet?.let{ it.resolve(registry) },
            configuration?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class WorkspaceShellBuilder<out C>(override val context: C, internal var shell: WorkspaceShell = WorkspaceShell()) : WorkspaceBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun model(body: ModelBuilder<C>.() -> Unit) {
        shell = shell.copy(model = ModelScaffolder<C>(ModelSpec<C>(body)).createScaffold(context))
    }

    override fun model(spec: ModelSpec<C>) {
        shell = shell.copy(model = ModelScaffolder<C>(spec).createScaffold(context))
    }

    override fun model(ref: ModelRef) {
        shell = shell.copy(model = Deferred(ref.key))
    }

    override fun model(value: Model) {
        shell = shell.copy(model = Wrapper(value))
    }

    override fun viewSet(body: ViewSetBuilder<C>.() -> Unit) {
        shell = shell.copy(viewSet = ViewSetScaffolder<C>(ViewSetSpec<C>(body)).createScaffold(context))
    }

    override fun viewSet(spec: ViewSetSpec<C>) {
        shell = shell.copy(viewSet = ViewSetScaffolder<C>(spec).createScaffold(context))
    }

    override fun viewSet(ref: ViewSetRef) {
        shell = shell.copy(viewSet = Deferred(ref.key))
    }

    override fun viewSet(value: ViewSet) {
        shell = shell.copy(viewSet = Wrapper(value))
    }

    override fun configuration(body: WorkspaceConfigurationBuilder<C>.() -> Unit) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder<C>(WorkspaceConfigurationSpec<C>(body)).createScaffold(context))
    }

    override fun configuration(spec: WorkspaceConfigurationSpec<C>) {
        shell = shell.copy(configuration = WorkspaceConfigurationScaffolder<C>(spec).createScaffold(context))
    }

    override fun configuration(ref: WorkspaceConfigurationRef) {
        shell = shell.copy(configuration = Deferred(ref.key))
    }

    override fun configuration(value: WorkspaceConfiguration) {
        shell = shell.copy(configuration = Wrapper(value))
    }

    override fun include(body: WorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkspaceShellBuilder<C2> = WorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: WorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class WorkspaceScaffolder<in C>(internal val spec: WorkspaceSpec<C>) : Scaffolder<C, Workspace> {
    override fun createScaffold(context: C): Scaffold<Workspace> {
        val builder = WorkspaceShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
