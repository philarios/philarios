package io.philarios.filesystem

import io.philarios.util.registry.Registry
import io.philarios.core.Scaffold
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal sealed class EntryShell

internal data class DirectoryShell(var name: Scaffold<String>? = null, var entries: List<Scaffold<Entry>>? = null) : EntryShell(),
        Scaffold<Directory> {
    override suspend fun resolve(registry: Registry): Directory {
        checkNotNull(name) { "Directory is missing the name property" }
        coroutineScope {
            entries?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Directory(
            name!!.let{ it.resolve(registry) },
            entries.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class FileShell(var name: Scaffold<String>? = null, var content: Scaffold<String>? = null) : EntryShell(),
        Scaffold<File> {
    override suspend fun resolve(registry: Registry): File {
        checkNotNull(name) { "File is missing the name property" }
        checkNotNull(content) { "File is missing the content property" }
        val value = File(
            name!!.let{ it.resolve(registry) },
            content!!.let{ it.resolve(registry) }
        )
        return value
    }
}
