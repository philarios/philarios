package io.philarios.filesystem.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry() {
    companion object {
        operator fun <C> invoke(body: DirectoryBuilder<C>.() -> Unit): DirectorySpec<C> = DirectorySpec<C>(body)
    }
}

data class File(val name: String) : Entry() {
    companion object {
        operator fun <C> invoke(body: FileBuilder<C>.() -> Unit): FileSpec<C> = FileSpec<C>(body)
    }
}

@DslBuilder
class DirectoryBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var entries: List<Entry>? = emptyList()
) : Builder<Directory> {
    fun <C> DirectoryBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> DirectoryBuilder<C>.entry(spec: DirectorySpec<C>) {
        this.entries = this.entries.orEmpty() + DirectoryTranslator<C>(spec).translate(context)
    }

    fun <C> DirectoryBuilder<C>.entry(spec: FileSpec<C>) {
        this.entries = this.entries.orEmpty() + FileTranslator<C>(spec).translate(context)
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

    fun <C> DirectoryBuilder<C>.include(body: DirectoryBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DirectoryBuilder<C>.include(spec: DirectorySpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DirectoryBuilder<C>.includeForEach(context: Iterable<C2>, body: DirectoryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DirectoryBuilder<C>.includeForEach(context: Iterable<C2>, spec: DirectorySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DirectoryBuilder<C2> = DirectoryBuilder(context,name,entries)

    private fun <C2> merge(other: DirectoryBuilder<C2>) {
        this.name = other.name
        this.entries = other.entries
    }

    override fun build(): Directory = Directory(name!!,entries!!)
}

@DslBuilder
class FileBuilder<out C>(val context: C, private var name: String? = null) : Builder<File> {
    fun <C> FileBuilder<C>.name(name: String) {
        this.name = name
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

    fun <C> FileBuilder<C>.include(body: FileBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> FileBuilder<C>.include(spec: FileSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> FileBuilder<C>.includeForEach(context: Iterable<C2>, body: FileBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> FileBuilder<C>.includeForEach(context: Iterable<C2>, spec: FileSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FileBuilder<C2> = FileBuilder(context,name)

    private fun <C2> merge(other: FileBuilder<C2>) {
        this.name = other.name
    }

    override fun build(): File = File(name!!)
}

open class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit) : Spec<DirectoryBuilder<C>, Directory> {
    override fun DirectoryBuilder<C>.body() {
        this@DirectorySpec.body.invoke(this)
    }
}

open class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit) : Spec<FileBuilder<C>, File> {
    override fun FileBuilder<C>.body() {
        this@FileSpec.body.invoke(this)
    }
}

open class DirectoryTranslator<in C>(private val spec: DirectorySpec<C>) : Translator<C, Directory> {
    constructor(body: DirectoryBuilder<C>.() -> Unit) : this(io.philarios.filesystem.v0.DirectorySpec<C>(body))

    override fun translate(context: C): Directory {
        val builder = DirectoryBuilder(context)
        val translator = BuilderSpecTranslator<C, DirectoryBuilder<C>, Directory>(builder, spec)
        return translator.translate(context)
    }
}

open class FileTranslator<in C>(private val spec: FileSpec<C>) : Translator<C, File> {
    constructor(body: FileBuilder<C>.() -> Unit) : this(io.philarios.filesystem.v0.FileSpec<C>(body))

    override fun translate(context: C): File {
        val builder = FileBuilder(context)
        val translator = BuilderSpecTranslator<C, FileBuilder<C>, File>(builder, spec)
        return translator.translate(context)
    }
}
