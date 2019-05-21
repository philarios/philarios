// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.filesystem

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.String

sealed class EntrySpec<out T : Entry>

class DirectorySpec(override val body: DirectoryBuilder.() -> Unit) : EntrySpec<Directory>(),
        Spec<DirectoryBuilder>

class FileSpec(override val body: FileBuilder.() -> Unit) : EntrySpec<File>(), Spec<FileBuilder>

@DslBuilder
interface DirectoryBuilder : Builder<DirectorySpec, DirectoryBuilder> {
    fun name(value: String)

    fun <T : Entry> entry(spec: EntrySpec<T>)

    fun <T : Entry> entry(ref: EntryRef<T>)

    fun <T : Entry> entry(value: T)
}

@DslBuilder
interface FileBuilder : Builder<FileSpec, FileBuilder> {
    fun name(value: String)

    fun content(value: String)
}

sealed class EntryRef<T : Entry> {
    internal abstract val key: String
}

class DirectoryRef(override val key: String) : EntryRef<Directory>()

class FileRef(override val key: String) : EntryRef<File>()

fun directory(body: DirectoryBuilder.() -> Unit): DirectorySpec = DirectorySpec(body)

fun file(body: FileBuilder.() -> Unit): FileSpec = FileSpec(body)
