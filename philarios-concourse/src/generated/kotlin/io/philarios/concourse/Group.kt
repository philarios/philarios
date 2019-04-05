package io.philarios.concourse

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

data class Group(
        val name: String,
        val jobs: List<String>,
        val resources: List<String>
)

class GroupSpec<in C>(internal val body: GroupBuilder<C>.() -> Unit)

@DslBuilder
interface GroupBuilder<out C> {
    val context: C

    fun name(value: String)

    fun job(value: String)

    fun jobs(jobs: List<String>)

    fun resource(value: String)

    fun resources(resources: List<String>)

    fun include(body: GroupBuilder<C>.() -> Unit)

    fun include(spec: GroupSpec<C>)

    fun <C2> include(context: C2, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: GroupSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>)
}

class GroupRef(internal val key: String)

internal data class GroupShell(
        var name: Scaffold<String>? = null,
        var jobs: List<Scaffold<String>>? = null,
        var resources: List<Scaffold<String>>? = null
) : Scaffold<Group> {
    override suspend fun resolve(registry: Registry): Group {
        checkNotNull(name) { "Group is missing the name property" }
        val value = Group(
            name!!.let{ it.resolve(registry) },
            jobs.orEmpty().let{ it.map { it.resolve(registry) } },
            resources.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class GroupShellBuilder<out C>(override val context: C, internal var shell: GroupShell = GroupShell()) : GroupBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun job(value: String) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + Wrapper(value))
    }

    override fun jobs(jobs: List<String>) {
        shell = shell.copy(jobs = shell.jobs.orEmpty() + jobs.map { Wrapper(it) })
    }

    override fun resource(value: String) {
        shell = shell.copy(resources = shell.resources.orEmpty() + Wrapper(value))
    }

    override fun resources(resources: List<String>) {
        shell = shell.copy(resources = shell.resources.orEmpty() + resources.map { Wrapper(it) })
    }

    override fun include(body: GroupBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: GroupSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: GroupBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: GroupSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: GroupBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: GroupSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): GroupShellBuilder<C2> = GroupShellBuilder(context, shell)

    private fun <C2> merge(other: GroupShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class GroupScaffolder<in C>(internal val spec: GroupSpec<C>) : Scaffolder<C, Group> {
    override fun createScaffold(context: C): Scaffold<Group> {
        val builder = GroupShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
