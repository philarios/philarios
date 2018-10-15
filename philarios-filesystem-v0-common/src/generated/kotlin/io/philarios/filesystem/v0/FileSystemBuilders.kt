package io.philarios.filesystem.v0

import io.philarios.core.v0.DslBuilder
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
class DirectoryBuilder<out C>(val context: C, internal var shell: DirectoryShell = DirectoryShell()) {
    fun <C> DirectoryBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> DirectoryBuilder<C>.entry(spec: DirectorySpec<C>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + spec.connect(context))
    }

    fun <C> DirectoryBuilder<C>.entry(ref: DirectoryRef) {
        shell = shell.copy(entries = shell.entries.orEmpty() + ref)
    }

    fun <C> DirectoryBuilder<C>.entry(spec: FileSpec<C>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + spec.connect(context))
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
