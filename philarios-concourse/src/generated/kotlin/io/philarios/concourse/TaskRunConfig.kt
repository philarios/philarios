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

data class TaskRunConfig(
        val path: String,
        val args: List<String>,
        val dir: String?,
        val user: String?
)

class TaskRunConfigSpec<in C>(internal val body: TaskRunConfigBuilder<C>.() -> Unit)

@DslBuilder
interface TaskRunConfigBuilder<out C> {
    val context: C

    fun path(value: String)

    fun arg(value: String)

    fun args(args: List<String>)

    fun dir(value: String)

    fun user(value: String)

    fun include(body: TaskRunConfigBuilder<C>.() -> Unit)

    fun include(spec: TaskRunConfigSpec<C>)

    fun <C2> include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskRunConfigSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>)
}

class TaskRunConfigRef(internal val key: String)

internal data class TaskRunConfigShell(
        var path: Scaffold<String>? = null,
        var args: List<Scaffold<String>>? = null,
        var dir: Scaffold<String>? = null,
        var user: Scaffold<String>? = null
) : Scaffold<TaskRunConfig> {
    override suspend fun resolve(registry: Registry): TaskRunConfig {
        checkNotNull(path) { "TaskRunConfig is missing the path property" }
        val value = TaskRunConfig(
            path!!.let{ it.resolve(registry) },
            args.orEmpty().let{ it.map { it.resolve(registry) } },
            dir?.let{ it.resolve(registry) },
            user?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class TaskRunConfigShellBuilder<out C>(override val context: C, internal var shell: TaskRunConfigShell = TaskRunConfigShell()) : TaskRunConfigBuilder<C> {
    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun arg(value: String) {
        shell = shell.copy(args = shell.args.orEmpty() + Wrapper(value))
    }

    override fun args(args: List<String>) {
        shell = shell.copy(args = shell.args.orEmpty() + args.map { Wrapper(it) })
    }

    override fun dir(value: String) {
        shell = shell.copy(dir = Wrapper(value))
    }

    override fun user(value: String) {
        shell = shell.copy(user = Wrapper(value))
    }

    override fun include(body: TaskRunConfigBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskRunConfigSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskRunConfigSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskRunConfigBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskRunConfigSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskRunConfigShellBuilder<C2> = TaskRunConfigShellBuilder(context, shell)

    private fun <C2> merge(other: TaskRunConfigShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskRunConfigScaffolder<in C>(internal val spec: TaskRunConfigSpec<C>) : Scaffolder<C, TaskRunConfig> {
    override fun createScaffold(context: C): Scaffold<TaskRunConfig> {
        val builder = TaskRunConfigShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
