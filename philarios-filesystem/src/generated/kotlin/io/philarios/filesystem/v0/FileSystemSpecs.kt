package io.philarios.filesystem.v0

sealed class EntrySpec<in C, out T : Entry>

class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit) : EntrySpec<C, Directory>()

class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit) : EntrySpec<C, File>()
