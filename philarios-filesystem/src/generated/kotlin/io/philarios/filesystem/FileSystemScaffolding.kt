// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.filesystem

import io.philarios.core.RefScaffold
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class EntryScaffolder<out T : Entry>(internal val spec: EntrySpec<T>) : Scaffolder<T> {
    override fun createScaffold(): Scaffold<T> {
        val result = when (spec) {
            is DirectorySpec -> DirectoryScaffolder(spec).createScaffold()
            is FileSpec -> FileScaffolder(spec).createScaffold()
        }
        return result as Scaffold<T>
    }
}

class DirectoryScaffolder(internal val spec: DirectorySpec) : Scaffolder<Directory> {
    override fun createScaffold(): Scaffold<Directory> {
        val builder = DirectoryShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class FileScaffolder(internal val spec: FileSpec) : Scaffolder<File> {
    override fun createScaffold(): Scaffold<File> {
        val builder = FileShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class DirectoryShellBuilder(internal var shell: DirectoryShell = DirectoryShell()) : DirectoryBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun <T : Entry> entry(spec: EntrySpec<T>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + EntryScaffolder<Entry>(spec).createScaffold())
    }

    override fun <T : Entry> entry(ref: EntryRef<T>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Entry> entry(value: T) {
        shell = shell.copy(entries = shell.entries.orEmpty() + ValueScaffold(value))
    }
}

@DslBuilder
internal class FileShellBuilder(internal var shell: FileShell = FileShell()) : FileBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = ValueScaffold(value))
    }

    override fun content(value: String) {
        shell = shell.copy(content = ValueScaffold(value))
    }
}

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
