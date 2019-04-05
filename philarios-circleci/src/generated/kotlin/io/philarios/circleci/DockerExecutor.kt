package io.philarios.circleci

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class DockerExecutor(
        val image: String?,
        val entrypoint: List<String>?,
        val command: List<String>?,
        val user: String?,
        val environment: Map<String, String>?,
        val auth: Auth?,
        val aws_auth: AwsAuth?
)

class DockerExecutorSpec<in C>(internal val body: DockerExecutorBuilder<C>.() -> Unit)

@DslBuilder
interface DockerExecutorBuilder<out C> {
    val context: C

    fun image(value: String)

    fun entrypoint(value: String)

    fun entrypoint(entrypoint: List<String>)

    fun command(value: String)

    fun command(command: List<String>)

    fun user(value: String)

    fun environment(key: String, value: String)

    fun environment(pair: Pair<String, String>)

    fun environment(environment: Map<String, String>)

    fun auth(body: AuthBuilder<C>.() -> Unit)

    fun auth(spec: AuthSpec<C>)

    fun auth(ref: AuthRef)

    fun auth(value: Auth)

    fun aws_auth(body: AwsAuthBuilder<C>.() -> Unit)

    fun aws_auth(spec: AwsAuthSpec<C>)

    fun aws_auth(ref: AwsAuthRef)

    fun aws_auth(value: AwsAuth)

    fun include(body: DockerExecutorBuilder<C>.() -> Unit)

    fun include(spec: DockerExecutorSpec<C>)

    fun <C2> include(context: C2, body: DockerExecutorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DockerExecutorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DockerExecutorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DockerExecutorSpec<C2>)
}

class DockerExecutorRef(internal val key: String)

internal data class DockerExecutorShell(
        var image: Scaffold<String>? = null,
        var entrypoint: List<Scaffold<String>>? = null,
        var command: List<Scaffold<String>>? = null,
        var user: Scaffold<String>? = null,
        var environment: Map<Scaffold<String>, Scaffold<String>>? = null,
        var auth: Scaffold<Auth>? = null,
        var aws_auth: Scaffold<AwsAuth>? = null
) : Scaffold<DockerExecutor> {
    override suspend fun resolve(registry: Registry): DockerExecutor {
        coroutineScope {
            auth?.let{ launch { it.resolve(registry) } }
            aws_auth?.let{ launch { it.resolve(registry) } }
        }
        val value = DockerExecutor(
            image?.let{ it.resolve(registry) },
            entrypoint?.let{ it.map { it.resolve(registry) } },
            command?.let{ it.map { it.resolve(registry) } },
            user?.let{ it.resolve(registry) },
            environment?.let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            auth?.let{ it.resolve(registry) },
            aws_auth?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class DockerExecutorShellBuilder<out C>(override val context: C, internal var shell: DockerExecutorShell = DockerExecutorShell()) : DockerExecutorBuilder<C> {
    override fun image(value: String) {
        shell = shell.copy(image = Wrapper(value))
    }

    override fun entrypoint(value: String) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + Wrapper(value))
    }

    override fun entrypoint(entrypoint: List<String>) {
        shell = shell.copy(entrypoint = shell.entrypoint.orEmpty() + entrypoint.map { Wrapper(it) })
    }

    override fun command(value: String) {
        shell = shell.copy(command = shell.command.orEmpty() + Wrapper(value))
    }

    override fun command(command: List<String>) {
        shell = shell.copy(command = shell.command.orEmpty() + command.map { Wrapper(it) })
    }

    override fun user(value: String) {
        shell = shell.copy(user = Wrapper(value))
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

    override fun auth(body: AuthBuilder<C>.() -> Unit) {
        shell = shell.copy(auth = AuthScaffolder<C>(AuthSpec<C>(body)).createScaffold(context))
    }

    override fun auth(spec: AuthSpec<C>) {
        shell = shell.copy(auth = AuthScaffolder<C>(spec).createScaffold(context))
    }

    override fun auth(ref: AuthRef) {
        shell = shell.copy(auth = Deferred(ref.key))
    }

    override fun auth(value: Auth) {
        shell = shell.copy(auth = Wrapper(value))
    }

    override fun aws_auth(body: AwsAuthBuilder<C>.() -> Unit) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder<C>(AwsAuthSpec<C>(body)).createScaffold(context))
    }

    override fun aws_auth(spec: AwsAuthSpec<C>) {
        shell = shell.copy(aws_auth = AwsAuthScaffolder<C>(spec).createScaffold(context))
    }

    override fun aws_auth(ref: AwsAuthRef) {
        shell = shell.copy(aws_auth = Deferred(ref.key))
    }

    override fun aws_auth(value: AwsAuth) {
        shell = shell.copy(aws_auth = Wrapper(value))
    }

    override fun include(body: DockerExecutorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DockerExecutorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DockerExecutorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DockerExecutorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DockerExecutorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DockerExecutorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DockerExecutorShellBuilder<C2> = DockerExecutorShellBuilder(context, shell)

    private fun <C2> merge(other: DockerExecutorShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class DockerExecutorScaffolder<in C>(internal val spec: DockerExecutorSpec<C>) : Scaffolder<C, DockerExecutor> {
    override fun createScaffold(context: C): Scaffold<DockerExecutor> {
        val builder = DockerExecutorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
