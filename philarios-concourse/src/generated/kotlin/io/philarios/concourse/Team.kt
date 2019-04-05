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

data class Team(val name: String, val pipelines: List<Pipeline>)

class TeamSpec<in C>(internal val body: TeamBuilder<C>.() -> Unit)

@DslBuilder
interface TeamBuilder<out C> {
    val context: C

    fun name(value: String)

    fun pipeline(body: PipelineBuilder<C>.() -> Unit)

    fun pipeline(spec: PipelineSpec<C>)

    fun pipeline(ref: PipelineRef)

    fun pipeline(value: Pipeline)

    fun pipelines(pipelines: List<Pipeline>)

    fun include(body: TeamBuilder<C>.() -> Unit)

    fun include(spec: TeamSpec<C>)

    fun <C2> include(context: C2, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TeamSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>)
}

class TeamRef(internal val key: String)

internal data class TeamShell(var name: Scaffold<String>? = null, var pipelines: List<Scaffold<Pipeline>>? = null) : Scaffold<Team> {
    override suspend fun resolve(registry: Registry): Team {
        checkNotNull(name) { "Team is missing the name property" }
        coroutineScope {
            pipelines?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Team(
            name!!.let{ it.resolve(registry) },
            pipelines.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class TeamShellBuilder<out C>(override val context: C, internal var shell: TeamShell = TeamShell()) : TeamBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun pipeline(body: PipelineBuilder<C>.() -> Unit) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder<C>(PipelineSpec<C>(body)).createScaffold(context))
    }

    override fun pipeline(spec: PipelineSpec<C>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + PipelineScaffolder<C>(spec).createScaffold(context))
    }

    override fun pipeline(ref: PipelineRef) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Deferred(ref.key))
    }

    override fun pipeline(value: Pipeline) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + Wrapper(value))
    }

    override fun pipelines(pipelines: List<Pipeline>) {
        shell = shell.copy(pipelines = shell.pipelines.orEmpty() + pipelines.map { Wrapper(it) })
    }

    override fun include(body: TeamBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TeamSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TeamBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TeamSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TeamBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TeamSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TeamShellBuilder<C2> = TeamShellBuilder(context, shell)

    private fun <C2> merge(other: TeamShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TeamScaffolder<in C>(internal val spec: TeamSpec<C>) : Scaffolder<C, Team> {
    override fun createScaffold(context: C): Scaffold<Team> {
        val builder = TeamShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
