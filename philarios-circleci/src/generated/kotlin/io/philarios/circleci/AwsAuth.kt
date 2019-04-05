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

data class AwsAuth(val aws_access_key_id: String?, val aws_secret_access_key: String?)

class AwsAuthSpec<in C>(internal val body: AwsAuthBuilder<C>.() -> Unit)

@DslBuilder
interface AwsAuthBuilder<out C> {
    val context: C

    fun aws_access_key_id(value: String)

    fun aws_secret_access_key(value: String)

    fun include(body: AwsAuthBuilder<C>.() -> Unit)

    fun include(spec: AwsAuthSpec<C>)

    fun <C2> include(context: C2, body: AwsAuthBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AwsAuthSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AwsAuthBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AwsAuthSpec<C2>)
}

class AwsAuthRef(internal val key: String)

internal data class AwsAuthShell(var aws_access_key_id: Scaffold<String>? = null, var aws_secret_access_key: Scaffold<String>? = null) : Scaffold<AwsAuth> {
    override suspend fun resolve(registry: Registry): AwsAuth {
        val value = AwsAuth(
            aws_access_key_id?.let{ it.resolve(registry) },
            aws_secret_access_key?.let{ it.resolve(registry) }
        )
        return value
    }
}

@DslBuilder
internal class AwsAuthShellBuilder<out C>(override val context: C, internal var shell: AwsAuthShell = AwsAuthShell()) : AwsAuthBuilder<C> {
    override fun aws_access_key_id(value: String) {
        shell = shell.copy(aws_access_key_id = Wrapper(value))
    }

    override fun aws_secret_access_key(value: String) {
        shell = shell.copy(aws_secret_access_key = Wrapper(value))
    }

    override fun include(body: AwsAuthBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AwsAuthSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AwsAuthBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AwsAuthSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AwsAuthBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AwsAuthSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AwsAuthShellBuilder<C2> = AwsAuthShellBuilder(context, shell)

    private fun <C2> merge(other: AwsAuthShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class AwsAuthScaffolder<in C>(internal val spec: AwsAuthSpec<C>) : Scaffolder<C, AwsAuth> {
    override fun createScaffold(context: C): Scaffold<AwsAuth> {
        val builder = AwsAuthShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
