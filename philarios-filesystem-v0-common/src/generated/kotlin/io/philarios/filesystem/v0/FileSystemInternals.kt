package io.philarios.filesystem.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
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

class DirectoryRef(key: String) : Scaffold<Directory> by io.philarios.core.v0.RegistryRef(io.philarios.filesystem.v0.Directory::class, key)

class FileRef(key: String) : Scaffold<File> by io.philarios.core.v0.RegistryRef(io.philarios.filesystem.v0.File::class, key)

open class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit) : Spec<C, Directory> {
    override fun connect(context: C): Scaffold<Directory> {
        val builder = DirectoryBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit) : Spec<C, File> {
    override fun connect(context: C): Scaffold<File> {
        val builder = FileBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
