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

data class Terminology(
        val enterprise: String?,
        val person: String?,
        val softwareSystem: String?,
        val container: String?,
        val component: String?,
        val code: String?,
        val deploymentNode: String?,
        val relationship: String?
)

class TerminologySpec<in C>(internal val body: TerminologyBuilder<C>.() -> Unit)

@DslBuilder
interface TerminologyBuilder<out C> {
    val context: C

    fun enterprise(value: String)

    fun person(value: String)

    fun softwareSystem(value: String)

    fun container(value: String)

    fun component(value: String)

    fun code(value: String)

    fun deploymentNode(value: String)

    fun relationship(value: String)

    fun include(body: TerminologyBuilder<C>.() -> Unit)

    fun include(spec: TerminologySpec<C>)

    fun <C2> include(context: C2, body: TerminologyBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TerminologySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TerminologyBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TerminologySpec<C2>)
}

class TerminologyRef(internal val key: String)

internal data class TerminologyShell(
        var enterprise: Scaffold<String>? = null,
        var person: Scaffold<String>? = null,
        var softwareSystem: Scaffold<String>? = null,
        var container: Scaffold<String>? = null,
        var component: Scaffold<String>? = null,
        var code: Scaffold<String>? = null,
        var deploymentNode: Scaffold<String>? = null,
        var relationship: Scaffold<String>? = null
) : Scaffold<Terminology> {
    override suspend fun resolve(registry: Registry): Terminology {
        val value = Terminology(
            enterprise?.let{ it.resolve(registry) },
            person?.let{ it.resolve(registry) },
            softwareSystem?.let{ it.resolve(registry) },
            container?.let{ it.resolve(registry) },
            component?.let{ it.resolve(registry) },
            code?.let{ it.resolve(registry) },
            deploymentNode?.let{ it.resolve(registry) },
            relationship?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class TerminologyShellBuilder<out C>(override val context: C, internal var shell: TerminologyShell = TerminologyShell()) : TerminologyBuilder<C> {
    override fun enterprise(value: String) {
        shell = shell.copy(enterprise = Wrapper(value))
    }

    override fun person(value: String) {
        shell = shell.copy(person = Wrapper(value))
    }

    override fun softwareSystem(value: String) {
        shell = shell.copy(softwareSystem = Wrapper(value))
    }

    override fun container(value: String) {
        shell = shell.copy(container = Wrapper(value))
    }

    override fun component(value: String) {
        shell = shell.copy(component = Wrapper(value))
    }

    override fun code(value: String) {
        shell = shell.copy(code = Wrapper(value))
    }

    override fun deploymentNode(value: String) {
        shell = shell.copy(deploymentNode = Wrapper(value))
    }

    override fun relationship(value: String) {
        shell = shell.copy(relationship = Wrapper(value))
    }

    override fun include(body: TerminologyBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TerminologySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TerminologyBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TerminologySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TerminologyBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TerminologySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TerminologyShellBuilder<C2> = TerminologyShellBuilder(context, shell)

    private fun <C2> merge(other: TerminologyShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TerminologyScaffolder<in C>(internal val spec: TerminologySpec<C>) : Scaffolder<C, Terminology> {
    override fun createScaffold(context: C): Scaffold<Terminology> {
        val builder = TerminologyShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
