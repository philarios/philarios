package io.philarios.filesystem.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal sealed class EntryShell

internal data class DirectoryShell(var name: String? = null, var entries: List<Scaffold<Entry>> = emptyList()) : EntryShell(),
        Scaffold<Directory> {
    override suspend fun resolve(registry: Registry): Directory {
        checkNotNull(name) { "Directory is missing the name property" }
        coroutineScope {
        	entries.forEach { launch { it.resolve(registry) } }
        }
        val value = Directory(name!!,entries.map { it.resolve(registry) })
        return value
    }
}

internal data class FileShell(var name: String? = null) : EntryShell(), Scaffold<File> {
    override suspend fun resolve(registry: Registry): File {
        checkNotNull(name) { "File is missing the name property" }
        val value = File(name!!)
        return value
    }
}
