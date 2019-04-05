package io.philarios.circleci

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Run(
        val name: String?,
        val command: String?,
        val shell: String?,
        val environment: Map<String, String>?,
        val background: Boolean?,
        val working_directory: String?,
        val no_output_timeout: String?,
        val `when`: When?
)

class RunSpec<in C>(internal val body: RunBuilder<C>.() -> Unit)

@DslBuilder
interface RunBuilder<out C> {
    val context: C

    fun name(value: String)

    fun command(value: String)

    fun shell(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun background(value: Boolean)

    fun working_directory(value: String)

    fun no_output_timeout(value: String)

    fun `when`(value: When)

    fun include(body: RunBuilder<C>.() -> Unit)

    fun include(spec: RunSpec<C>)

    fun <C2> include(context: C2, body: RunBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RunSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RunBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RunSpec<C2>)
}

class RunRef(internal val key: String)

internal data class RunShell(
        var name: Scaffold<String>? = null,
        var command: Scaffold<String>? = null,
        var shell: Scaffold<String>? = null,
        var environment: Map<Scaffold<String>, Scaffold<String>>? = null,
        var background: Scaffold<Boolean>? = null,
        var working_directory: Scaffold<String>? = null,
        var no_output_timeout: Scaffold<String>? = null,
        var `when`: Scaffold<When>? = null
) : Scaffold<Run> {
    override suspend fun resolve(registry: Registry): Run {
        val value = Run(
            name?.let{ it.resolve(registry) },
            command?.let{ it.resolve(registry) },
            shell?.let{ it.resolve(registry) },
            environment?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            background?.let{ it.resolve(registry) },
            working_directory?.let{ it.resolve(registry) },
            no_output_timeout?.let{ it.resolve(registry) },
            `when`?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class RunShellBuilder<out C>(override val context: C, internal var shell: RunShell = RunShell()) : RunBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun command(value: String) {
        shell = shell.copy(command = Wrapper(value))
    }

    override fun shell(value: String) {
        shell = shell.copy(shell = Wrapper(value))
    }

    override fun environment(key: String, value: String) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun environment(pair: Pair<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun environment(environment: Map<String, String>) {
        shell = shell.copy(environment = shell.environment.orEmpty() + environment.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun background(value: Boolean) {
        shell = shell.copy(background = Wrapper(value))
    }

    override fun working_directory(value: String) {
        shell = shell.copy(working_directory = Wrapper(value))
    }

    override fun no_output_timeout(value: String) {
        shell = shell.copy(no_output_timeout = Wrapper(value))
    }

    override fun `when`(value: When) {
        shell = shell.copy(`when` = Wrapper(value))
    }

    override fun include(body: RunBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RunSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RunBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RunSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RunBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RunSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RunShellBuilder<C2> = RunShellBuilder(context, shell)

    private fun <C2> merge(other: RunShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class RunScaffolder<in C>(internal val spec: RunSpec<C>) : Scaffolder<C, Run> {
    override fun createScaffold(context: C): Scaffold<Run> {
        val builder = RunShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
