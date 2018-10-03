package io.philarios.filesystem.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Translator
import kotlinx.coroutines.experimental.launch
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry()

data class File(val name: String) : Entry()

sealed class EntryShell

data class DirectoryShell(var name: String? = null, var entries: List<Scaffold<Entry>> = emptyList()) : EntryShell(),
        Scaffold<Directory> {
    override suspend fun resolve(registry: Registry): Directory {
        kotlinx.coroutines.experimental.coroutineScope {
        	entries!!.forEach { launch { it.resolve(registry) } }
        }
        val value = Directory(name!!,entries!!.map { it.resolve(registry) })
        return value
    }
}

data class FileShell(var name: String? = null) : EntryShell(), Scaffold<File> {
    override suspend fun resolve(registry: Registry): File {
        val value = File(name!!)
        return value
    }
}

class DirectoryRef(key: String) : Scaffold<Directory> by io.philarios.core.v0.RegistryRef(io.philarios.filesystem.v0.Directory::class, key)

class FileRef(key: String) : Scaffold<File> by io.philarios.core.v0.RegistryRef(io.philarios.filesystem.v0.File::class, key)

class DirectoryTemplate<in C>(private val spec: DirectorySpec<C>, private val context: C) : Builder<Directory> {
    constructor(body: DirectoryBuilder<C>.() -> Unit, context: C) : this(io.philarios.filesystem.v0.DirectorySpec<C>(body), context)

    override fun scaffold(): Scaffold<Directory> {
        val builder = DirectoryBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class FileTemplate<in C>(private val spec: FileSpec<C>, private val context: C) : Builder<File> {
    constructor(body: FileBuilder<C>.() -> Unit, context: C) : this(io.philarios.filesystem.v0.FileSpec<C>(body), context)

    override fun scaffold(): Scaffold<File> {
        val builder = FileBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit)

open class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit)

@DslBuilder
class DirectoryBuilder<out C>(val context: C, internal var shell: DirectoryShell = DirectoryShell()) {
    fun <C> DirectoryBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> DirectoryBuilder<C>.entry(spec: DirectorySpec<C>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + DirectoryTemplate<C>(spec, context).scaffold())
    }

    fun <C> DirectoryBuilder<C>.entry(ref: DirectoryRef) {
        shell = shell.copy(entries = shell.entries.orEmpty() + ref)
    }

    fun <C> DirectoryBuilder<C>.entry(spec: FileSpec<C>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + FileTemplate<C>(spec, context).scaffold())
    }

    fun <C> DirectoryBuilder<C>.entry(ref: FileRef) {
        shell = shell.copy(entries = shell.entries.orEmpty() + ref)
    }

    fun <C> DirectoryBuilder<C>.include(body: DirectoryBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DirectoryBuilder<C>.include(spec: DirectorySpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DirectoryBuilder<C>.include(context: C2, body: DirectoryBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> DirectoryBuilder<C>.include(context: C2, spec: DirectorySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> DirectoryBuilder<C>.includeForEach(context: Iterable<C2>, body: DirectoryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DirectoryBuilder<C>.includeForEach(context: Iterable<C2>, spec: DirectorySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DirectoryBuilder<C2> = DirectoryBuilder(context, shell)

    private fun <C2> merge(other: DirectoryBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class FileBuilder<out C>(val context: C, internal var shell: FileShell = FileShell()) {
    fun <C> FileBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> FileBuilder<C>.include(body: FileBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> FileBuilder<C>.include(spec: FileSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> FileBuilder<C>.include(context: C2, body: FileBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> FileBuilder<C>.include(context: C2, spec: FileSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> FileBuilder<C>.includeForEach(context: Iterable<C2>, body: FileBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> FileBuilder<C>.includeForEach(context: Iterable<C2>, spec: FileSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FileBuilder<C2> = FileBuilder(context, shell)

    private fun <C2> merge(other: FileBuilder<C2>) {
        this.shell = other.shell
    }
}

open class DirectoryTranslator<in C>(private val spec: DirectorySpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Directory> {
    constructor(body: DirectoryBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.filesystem.v0.DirectorySpec<C>(body), registry)

    override suspend fun translate(context: C): Directory {
        val builder = DirectoryTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class FileTranslator<in C>(private val spec: FileSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, File> {
    constructor(body: FileBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.filesystem.v0.FileSpec<C>(body), registry)

    override suspend fun translate(context: C): File {
        val builder = FileTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}
