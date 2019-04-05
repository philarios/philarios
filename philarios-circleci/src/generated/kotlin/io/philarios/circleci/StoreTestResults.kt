package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class StoreTestResults(val path: String?)

class StoreTestResultsSpec<in C>(internal val body: StoreTestResultsBuilder<C>.() -> Unit)

@DslBuilder
interface StoreTestResultsBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: StoreTestResultsBuilder<C>.() -> Unit)

    fun include(spec: StoreTestResultsSpec<C>)

    fun <C2> include(context: C2, body: StoreTestResultsBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StoreTestResultsSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsSpec<C2>)
}

class StoreTestResultsRef(internal val key: String)

internal data class StoreTestResultsShell(var path: Scaffold<String>? = null) : Scaffold<StoreTestResults> {
    override suspend fun resolve(registry: Registry): StoreTestResults {
        val value = StoreTestResults(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class StoreTestResultsShellBuilder<out C>(override val context: C, internal var shell: StoreTestResultsShell = StoreTestResultsShell()) : StoreTestResultsBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: StoreTestResultsBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StoreTestResultsSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StoreTestResultsBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StoreTestResultsSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StoreTestResultsBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StoreTestResultsSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StoreTestResultsShellBuilder<C2> = StoreTestResultsShellBuilder(context, shell)

    private fun <C2> merge(other: StoreTestResultsShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class StoreTestResultsScaffolder<in C>(internal val spec: StoreTestResultsSpec<C>) : Scaffolder<C, StoreTestResults> {
    override fun createScaffold(context: C): Scaffold<StoreTestResults> {
        val builder = StoreTestResultsShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
