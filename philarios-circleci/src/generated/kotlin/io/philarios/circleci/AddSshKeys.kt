package io.philarios.circleci

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

data class AddSshKeys(val fingerprints: List<String>?)

class AddSshKeysSpec<in C>(internal val body: AddSshKeysBuilder<C>.() -> Unit)

@DslBuilder
interface AddSshKeysBuilder<out C> {
    val context: C

    fun fingerprint(value: String)

    fun fingerprints(fingerprints: List<String>)

    fun include(body: AddSshKeysBuilder<C>.() -> Unit)

    fun include(spec: AddSshKeysSpec<C>)

    fun <C2> include(context: C2, body: AddSshKeysBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AddSshKeysSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysSpec<C2>)
}

class AddSshKeysRef(internal val key: String)

internal data class AddSshKeysShell(var fingerprints: List<Scaffold<String>>? = null) : Scaffold<AddSshKeys> {
    override suspend fun resolve(registry: Registry): AddSshKeys {
        val value = AddSshKeys(
            fingerprints?.let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

@DslBuilder
internal class AddSshKeysShellBuilder<out C>(override val context: C, internal var shell: AddSshKeysShell = AddSshKeysShell()) : AddSshKeysBuilder<C> {
    override fun fingerprint(value: String) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + Wrapper(value))
    }

    override fun fingerprints(fingerprints: List<String>) {
        shell = shell.copy(fingerprints = shell.fingerprints.orEmpty() + fingerprints.map { Wrapper(it) })
    }

    override fun include(body: AddSshKeysBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AddSshKeysSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AddSshKeysBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AddSshKeysSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AddSshKeysBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AddSshKeysSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AddSshKeysShellBuilder<C2> = AddSshKeysShellBuilder(context, shell)

    private fun <C2> merge(other: AddSshKeysShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class AddSshKeysScaffolder<in C>(internal val spec: AddSshKeysSpec<C>) : Scaffolder<C, AddSshKeys> {
    override fun createScaffold(context: C): Scaffold<AddSshKeys> {
        val builder = AddSshKeysShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
