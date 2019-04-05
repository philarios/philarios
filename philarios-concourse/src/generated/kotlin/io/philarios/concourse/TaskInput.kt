package io.philarios.concourse

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class TaskInput(
        val name: String,
        val path: String?,
        val optional: Boolean?
)

class TaskInputSpec<in C>(internal val body: TaskInputBuilder<C>.() -> Unit)

@DslBuilder
interface TaskInputBuilder<out C> {
    val context: C

    fun name(value: String)

    fun path(value: String)

    fun optional(value: Boolean)

    fun include(body: TaskInputBuilder<C>.() -> Unit)

    fun include(spec: TaskInputSpec<C>)

    fun <C2> include(context: C2, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TaskInputSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>)
}

class TaskInputRef(internal val key: String)

internal data class TaskInputShell(
        var name: Scaffold<String>? = null,
        var path: Scaffold<String>? = null,
        var optional: Scaffold<Boolean>? = null
) : Scaffold<TaskInput> {
    override suspend fun resolve(registry: Registry): TaskInput {
        checkNotNull(name) { "TaskInput is missing the name property" }
        val value = TaskInput(
            name!!.let{ it.resolve(registry) },
            path?.let{ it.resolve(registry) },
            optional?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class TaskInputShellBuilder<out C>(override val context: C, internal var shell: TaskInputShell = TaskInputShell()) : TaskInputBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun path(value: String) {
        shell = shell.copy(path = Wrapper(value))
    }

    override fun optional(value: Boolean) {
        shell = shell.copy(optional = Wrapper(value))
    }

    override fun include(body: TaskInputBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TaskInputSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TaskInputBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TaskInputSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TaskInputBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TaskInputSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TaskInputShellBuilder<C2> = TaskInputShellBuilder(context, shell)

    private fun <C2> merge(other: TaskInputShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TaskInputScaffolder<in C>(internal val spec: TaskInputSpec<C>) : Scaffolder<C, TaskInput> {
    override fun createScaffold(context: C): Scaffold<TaskInput> {
        val builder = TaskInputShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
