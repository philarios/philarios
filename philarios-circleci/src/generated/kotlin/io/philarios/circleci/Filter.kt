package io.philarios.circleci

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

data class Filter(val only: List<String>?, val ignore: List<String>?)

class FilterSpec<in C>(internal val body: FilterBuilder<C>.() -> Unit)

@DslBuilder
interface FilterBuilder<out C> {
    val context: C

    fun only(value: String)

    fun only(only: List<String>)

    fun ignore(value: String)

    fun ignore(ignore: List<String>)

    fun include(body: FilterBuilder<C>.() -> Unit)

    fun include(spec: FilterSpec<C>)

    fun <C2> include(context: C2, body: FilterBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FilterSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FilterBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FilterSpec<C2>)
}

class FilterRef(internal val key: String)

internal data class FilterShell(var only: List<Scaffold<String>>? = null, var ignore: List<Scaffold<String>>? = null) : Scaffold<Filter> {
    override suspend fun resolve(registry: Registry): Filter {
        val value = Filter(
            only?.let{ it.map { it.resolve(registry) } },
            ignore?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class FilterShellBuilder<out C>(override val context: C, internal var shell: FilterShell = FilterShell()) : FilterBuilder<C> {
    override fun only(value: String) {
        shell = shell.copy(only = shell.only.orEmpty() + Wrapper(value))
    }

    override fun only(only: List<String>) {
        shell = shell.copy(only = shell.only.orEmpty() + only.map { Wrapper(it) })
    }

    override fun ignore(value: String) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + Wrapper(value))
    }

    override fun ignore(ignore: List<String>) {
        shell = shell.copy(ignore = shell.ignore.orEmpty() + ignore.map { Wrapper(it) })
    }

    override fun include(body: FilterBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FilterSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FilterBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FilterSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FilterBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FilterSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FilterShellBuilder<C2> = FilterShellBuilder(context, shell)

    private fun <C2> merge(other: FilterShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class FilterScaffolder<in C>(internal val spec: FilterSpec<C>) : Scaffolder<C, Filter> {
    override fun createScaffold(context: C): Scaffold<Filter> {
        val builder = FilterShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
