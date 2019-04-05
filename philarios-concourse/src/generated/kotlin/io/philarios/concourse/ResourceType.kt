package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Any
import kotlin.Boolean
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class ResourceType(
        val name: String,
        val type: String,
        val source: Map<String, Any>,
        val privileged: Boolean?,
        val params: Map<String, Any>,
        val tags: List<String>
)

class ResourceTypeSpec<in C>(internal val body: ResourceTypeBuilder<C>.() -> Unit)

@DslBuilder
interface ResourceTypeBuilder<out C> {
    val context: C

    fun name(value: String)

    fun type(value: String)

    fun source(key: String, value: Any)

    fun source(pair: Pair<String, Any>)

    fun source(source: Map<String, Any>)

    fun privileged(value: Boolean)

    fun params(key: String, value: Any)

    fun params(pair: Pair<String, Any>)

    fun params(params: Map<String, Any>)

    fun tag(value: String)

    fun tags(tags: List<String>)

    fun include(body: ResourceTypeBuilder<C>.() -> Unit)

    fun include(spec: ResourceTypeSpec<C>)

    fun <C2> include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResourceTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>)
}

class ResourceTypeRef(internal val key: String)

internal data class ResourceTypeShell(
        var name: Scaffold<String>? = null,
        var type: Scaffold<String>? = null,
        var source: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var privileged: Scaffold<Boolean>? = null,
        var params: Map<Scaffold<String>, Scaffold<Any>>? = null,
        var tags: List<Scaffold<String>>? = null
) : Scaffold<ResourceType> {
    override suspend fun resolve(registry: Registry): ResourceType {
        checkNotNull(name) { "ResourceType is missing the name property" }
        checkNotNull(type) { "ResourceType is missing the type property" }
        val value = ResourceType(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) },
            source.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            privileged?.let{ it.resolve(registry) },
            params.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            tags.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class ResourceTypeShellBuilder<out C>(override val context: C, internal var shell: ResourceTypeShell = ResourceTypeShell()) : ResourceTypeBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun type(value: String) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun source(key: String, value: Any) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun source(pair: Pair<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun source(source: Map<String, Any>) {
        shell = shell.copy(source = shell.source.orEmpty() + source.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun privileged(value: Boolean) {
        shell = shell.copy(privileged = Wrapper(value))
    }

    override fun params(key: String, value: Any) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun params(pair: Pair<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun params(params: Map<String, Any>) {
        shell = shell.copy(params = shell.params.orEmpty() + params.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun tag(value: String) {
        shell = shell.copy(tags = shell.tags.orEmpty() + Wrapper(value))
    }

    override fun tags(tags: List<String>) {
        shell = shell.copy(tags = shell.tags.orEmpty() + tags.map { Wrapper(it) })
    }

    override fun include(body: ResourceTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ResourceTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ResourceTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ResourceTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ResourceTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ResourceTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResourceTypeShellBuilder<C2> = ResourceTypeShellBuilder(context, shell)

    private fun <C2> merge(other: ResourceTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ResourceTypeScaffolder<in C>(internal val spec: ResourceTypeSpec<C>) : Scaffolder<C, ResourceType> {
    override fun createScaffold(context: C): Scaffold<ResourceType> {
        val builder = ResourceTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
