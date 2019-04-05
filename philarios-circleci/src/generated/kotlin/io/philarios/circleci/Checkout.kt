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

data class Checkout(val path: String?)

class CheckoutSpec<in C>(internal val body: CheckoutBuilder<C>.() -> Unit)

@DslBuilder
interface CheckoutBuilder<out C> {
    val context: C

    fun path(value: String)

    fun include(body: CheckoutBuilder<C>.() -> Unit)

    fun include(spec: CheckoutSpec<C>)

    fun <C2> include(context: C2, body: CheckoutBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CheckoutSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutSpec<C2>)
}

class CheckoutRef(internal val key: String)

internal data class CheckoutShell(var path: Scaffold<String>? = null) : Scaffold<Checkout> {
    override suspend fun resolve(registry: Registry): Checkout {
        val value = Checkout(
            path?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class CheckoutShellBuilder<out C>(override val context: C, internal var shell: CheckoutShell = CheckoutShell()) : CheckoutBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun include(body: CheckoutBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CheckoutSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CheckoutBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CheckoutSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CheckoutBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CheckoutSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CheckoutShellBuilder<C2> = CheckoutShellBuilder(context, shell)

    private fun <C2> merge(other: CheckoutShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class CheckoutScaffolder<in C>(internal val spec: CheckoutSpec<C>) : Scaffolder<C, Checkout> {
    override fun createScaffold(context: C): Scaffold<Checkout> {
        val builder = CheckoutShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
