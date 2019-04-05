package io.philarios.structurizr

import io.philarios.core.Deferred
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

data class ViewSet(
        val systemLandscapeViews: List<SystemLandscapeView>?,
        val systemContextViews: List<SystemContextView>?,
        val containerViews: List<ContainerView>?,
        val componentViews: List<ComponentView>?,
        val dynamicViews: List<DynamicView>?,
        val configuration: Configuration?
)

class ViewSetSpec<in C>(internal val body: ViewSetBuilder<C>.() -> Unit)

@DslBuilder
interface ViewSetBuilder<out C> {
    val context: C

    fun systemLandscapeView(body: SystemLandscapeViewBuilder<C>.() -> Unit)

    fun systemLandscapeView(spec: SystemLandscapeViewSpec<C>)

    fun systemLandscapeView(ref: SystemLandscapeViewRef)

    fun systemLandscapeView(value: SystemLandscapeView)

    fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>)

    fun systemContextView(body: SystemContextViewBuilder<C>.() -> Unit)

    fun systemContextView(spec: SystemContextViewSpec<C>)

    fun systemContextView(ref: SystemContextViewRef)

    fun systemContextView(value: SystemContextView)

    fun systemContextViews(systemContextViews: List<SystemContextView>)

    fun containerView(body: ContainerViewBuilder<C>.() -> Unit)

    fun containerView(spec: ContainerViewSpec<C>)

    fun containerView(ref: ContainerViewRef)

    fun containerView(value: ContainerView)

    fun containerViews(containerViews: List<ContainerView>)

    fun componentView(body: ComponentViewBuilder<C>.() -> Unit)

    fun componentView(spec: ComponentViewSpec<C>)

    fun componentView(ref: ComponentViewRef)

    fun componentView(value: ComponentView)

    fun componentViews(componentViews: List<ComponentView>)

    fun dynamicView(body: DynamicViewBuilder<C>.() -> Unit)

    fun dynamicView(spec: DynamicViewSpec<C>)

    fun dynamicView(ref: DynamicViewRef)

    fun dynamicView(value: DynamicView)

    fun dynamicViews(dynamicViews: List<DynamicView>)

    fun configuration(body: ConfigurationBuilder<C>.() -> Unit)

    fun configuration(spec: ConfigurationSpec<C>)

    fun configuration(ref: ConfigurationRef)

    fun configuration(value: Configuration)

    fun include(body: ViewSetBuilder<C>.() -> Unit)

    fun include(spec: ViewSetSpec<C>)

    fun <C2> include(context: C2, body: ViewSetBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ViewSetSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ViewSetBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ViewSetSpec<C2>)
}

class ViewSetRef(internal val key: String)

internal data class ViewSetShell(
        var systemLandscapeViews: List<Scaffold<SystemLandscapeView>>? = null,
        var systemContextViews: List<Scaffold<SystemContextView>>? = null,
        var containerViews: List<Scaffold<ContainerView>>? = null,
        var componentViews: List<Scaffold<ComponentView>>? = null,
        var dynamicViews: List<Scaffold<DynamicView>>? = null,
        var configuration: Scaffold<Configuration>? = null
) : Scaffold<ViewSet> {
    override suspend fun resolve(registry: Registry): ViewSet {
        coroutineScope {
            systemLandscapeViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            systemContextViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            containerViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            componentViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            dynamicViews?.let{ it.forEach { launch { it.resolve(registry) } } }
            configuration?.let{ launch { it.resolve(registry) } }
        }
        val value = ViewSet(
            systemLandscapeViews?.let{ it.map { it.resolve(registry) } },
            systemContextViews?.let{ it.map { it.resolve(registry) } },
            containerViews?.let{ it.map { it.resolve(registry) } },
            componentViews?.let{ it.map { it.resolve(registry) } },
            dynamicViews?.let{ it.map { it.resolve(registry) } },
            configuration?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class ViewSetShellBuilder<out C>(override val context: C, internal var shell: ViewSetShell = ViewSetShell()) : ViewSetBuilder<C> {
    override fun systemLandscapeView(body: SystemLandscapeViewBuilder<C>.() -> Unit) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder<C>(SystemLandscapeViewSpec<C>(body)).createScaffold(context))
    }

    override fun systemLandscapeView(spec: SystemLandscapeViewSpec<C>) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + SystemLandscapeViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun systemLandscapeView(ref: SystemLandscapeViewRef) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + Deferred(ref.key))
    }

    override fun systemLandscapeView(value: SystemLandscapeView) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + Wrapper(value))
    }

    override fun systemLandscapeViews(systemLandscapeViews: List<SystemLandscapeView>) {
        shell = shell.copy(systemLandscapeViews = shell.systemLandscapeViews.orEmpty() + systemLandscapeViews.map { Wrapper(it) })
    }

    override fun systemContextView(body: SystemContextViewBuilder<C>.() -> Unit) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder<C>(SystemContextViewSpec<C>(body)).createScaffold(context))
    }

    override fun systemContextView(spec: SystemContextViewSpec<C>) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + SystemContextViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun systemContextView(ref: SystemContextViewRef) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + Deferred(ref.key))
    }

    override fun systemContextView(value: SystemContextView) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + Wrapper(value))
    }

    override fun systemContextViews(systemContextViews: List<SystemContextView>) {
        shell = shell.copy(systemContextViews = shell.systemContextViews.orEmpty() + systemContextViews.map { Wrapper(it) })
    }

    override fun containerView(body: ContainerViewBuilder<C>.() -> Unit) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder<C>(ContainerViewSpec<C>(body)).createScaffold(context))
    }

    override fun containerView(spec: ContainerViewSpec<C>) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + ContainerViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun containerView(ref: ContainerViewRef) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + Deferred(ref.key))
    }

    override fun containerView(value: ContainerView) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + Wrapper(value))
    }

    override fun containerViews(containerViews: List<ContainerView>) {
        shell = shell.copy(containerViews = shell.containerViews.orEmpty() + containerViews.map { Wrapper(it) })
    }

    override fun componentView(body: ComponentViewBuilder<C>.() -> Unit) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder<C>(ComponentViewSpec<C>(body)).createScaffold(context))
    }

    override fun componentView(spec: ComponentViewSpec<C>) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + ComponentViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun componentView(ref: ComponentViewRef) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + Deferred(ref.key))
    }

    override fun componentView(value: ComponentView) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + Wrapper(value))
    }

    override fun componentViews(componentViews: List<ComponentView>) {
        shell = shell.copy(componentViews = shell.componentViews.orEmpty() + componentViews.map { Wrapper(it) })
    }

    override fun dynamicView(body: DynamicViewBuilder<C>.() -> Unit) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder<C>(DynamicViewSpec<C>(body)).createScaffold(context))
    }

    override fun dynamicView(spec: DynamicViewSpec<C>) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + DynamicViewScaffolder<C>(spec).createScaffold(context))
    }

    override fun dynamicView(ref: DynamicViewRef) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + Deferred(ref.key))
    }

    override fun dynamicView(value: DynamicView) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + Wrapper(value))
    }

    override fun dynamicViews(dynamicViews: List<DynamicView>) {
        shell = shell.copy(dynamicViews = shell.dynamicViews.orEmpty() + dynamicViews.map { Wrapper(it) })
    }

    override fun configuration(body: ConfigurationBuilder<C>.() -> Unit) {
        shell = shell.copy(configuration = ConfigurationScaffolder<C>(ConfigurationSpec<C>(body)).createScaffold(context))
    }

    override fun configuration(spec: ConfigurationSpec<C>) {
        shell = shell.copy(configuration = ConfigurationScaffolder<C>(spec).createScaffold(context))
    }

    override fun configuration(ref: ConfigurationRef) {
        shell = shell.copy(configuration = Deferred(ref.key))
    }

    override fun configuration(value: Configuration) {
        shell = shell.copy(configuration = Wrapper(value))
    }

    override fun include(body: ViewSetBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ViewSetSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ViewSetBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ViewSetSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ViewSetBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ViewSetSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ViewSetShellBuilder<C2> = ViewSetShellBuilder(context, shell)

    private fun <C2> merge(other: ViewSetShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ViewSetScaffolder<in C>(internal val spec: ViewSetSpec<C>) : Scaffolder<C, ViewSet> {
    override fun createScaffold(context: C): Scaffold<ViewSet> {
        val builder = ViewSetShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
