package io.philarios.filesystem

import io.philarios.core.DslBuilder
import kotlin.String
import kotlin.collections.Iterable

sealed class EntrySpec<in C, out T : Entry>

class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit) : EntrySpec<C, Directory>()

class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit) : EntrySpec<C, File>()

@DslBuilder
interface DirectoryBuilder<out C> {
    val context: C

    fun name(value: String)

    fun <T : Entry> entry(spec: EntrySpec<C, T>)

    fun <T : Entry> entry(ref: EntryRef<T>)

    fun <T : Entry> entry(value: T)

    fun include(body: DirectoryBuilder<C>.() -> Unit)

    fun include(spec: DirectorySpec<C>)

    fun <C2> include(context: C2, body: DirectoryBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DirectorySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DirectoryBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DirectorySpec<C2>)
}

@DslBuilder
interface FileBuilder<out C> {
    val context: C

    fun name(value: String)

    fun content(value: String)

    fun include(body: FileBuilder<C>.() -> Unit)

    fun include(spec: FileSpec<C>)

    fun <C2> include(context: C2, body: FileBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FileSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FileBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FileSpec<C2>)
}

sealed class EntryRef<T : Entry> {
    internal abstract val key: String
}

class DirectoryRef(override val key: String) : EntryRef<Directory>()

class FileRef(override val key: String) : EntryRef<File>()
