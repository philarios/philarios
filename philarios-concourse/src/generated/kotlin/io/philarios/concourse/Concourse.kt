package io.philarios.concourse

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

data class Concourse(val teams: List<Team>)

class ConcourseSpec<in C>(internal val body: ConcourseBuilder<C>.() -> Unit)

@DslBuilder
interface ConcourseBuilder<out C> {
    val context: C

    fun team(body: TeamBuilder<C>.() -> Unit)

    fun team(spec: TeamSpec<C>)

    fun team(ref: TeamRef)

    fun team(value: Team)

    fun teams(teams: List<Team>)

    fun include(body: ConcourseBuilder<C>.() -> Unit)

    fun include(spec: ConcourseSpec<C>)

    fun <C2> include(context: C2, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ConcourseSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>)
}

class ConcourseRef(internal val key: String)

internal data class ConcourseShell(var teams: List<Scaffold<Team>>? = null) : Scaffold<Concourse> {
    override suspend fun resolve(registry: Registry): Concourse {
        coroutineScope {
            teams?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Concourse(
            teams.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class ConcourseShellBuilder<out C>(override val context: C, internal var shell: ConcourseShell = ConcourseShell()) : ConcourseBuilder<C> {
    override fun team(body: TeamBuilder<C>.() -> Unit) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(TeamSpec<C>(body)).createScaffold(context))
    }

    override fun team(spec: TeamSpec<C>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + TeamScaffolder<C>(spec).createScaffold(context))
    }

    override fun team(ref: TeamRef) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Deferred(ref.key))
    }

    override fun team(value: Team) {
        shell = shell.copy(teams = shell.teams.orEmpty() + Wrapper(value))
    }

    override fun teams(teams: List<Team>) {
        shell = shell.copy(teams = shell.teams.orEmpty() + teams.map { Wrapper(it) })
    }

    override fun include(body: ConcourseBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ConcourseSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ConcourseBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ConcourseSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ConcourseBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ConcourseSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ConcourseShellBuilder<C2> = ConcourseShellBuilder(context, shell)

    private fun <C2> merge(other: ConcourseShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class ConcourseScaffolder<in C>(internal val spec: ConcourseSpec<C>) : Scaffolder<C, Concourse> {
    override fun createScaffold(context: C): Scaffold<Concourse> {
        val builder = ConcourseShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
