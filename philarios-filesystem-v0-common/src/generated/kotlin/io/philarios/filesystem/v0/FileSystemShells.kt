package io.philarios.filesystem.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

sealed class EntryShell

data class DirectoryShell(var name: String? = null, var entries: List<Scaffold<Entry>> = emptyList()) : EntryShell(),
        Scaffold<Directory> {
    override suspend fun resolve(registry: Registry): Directory {
        coroutineScope {
        	entries.forEach { launch { it.resolve(registry) } }
        }
        val value = Directory(name!!,entries.map { it.resolve(registry) })
        return value
    }
}

data class FileShell(var name: String? = null) : EntryShell(), Scaffold<File> {
    override suspend fun resolve(registry: Registry): File {
        val value = File(name!!)
        return value
    }
}
