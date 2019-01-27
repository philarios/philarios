package io.philarios.filesystem.v0

import io.philarios.core.Scaffold
import kotlin.String

class DirectoryRef(key: String) : Scaffold<Directory> by io.philarios.core.RegistryRef(io.philarios.filesystem.v0.Directory::class, key)

class FileRef(key: String) : Scaffold<File> by io.philarios.core.RegistryRef(io.philarios.filesystem.v0.File::class, key)
