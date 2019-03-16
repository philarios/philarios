package io.philarios.filesystem

import io.philarios.core.Scaffold
import kotlin.String

sealed class EntryRef<T : Entry> : Scaffold<T>

class DirectoryRef(key: String) : EntryRef<Directory>(),
        Scaffold<Directory> by io.philarios.core.RegistryRef(io.philarios.filesystem.Directory::class, key)

class FileRef(key: String) : EntryRef<File>(),
        Scaffold<File> by io.philarios.core.RegistryRef(io.philarios.filesystem.File::class, key)
