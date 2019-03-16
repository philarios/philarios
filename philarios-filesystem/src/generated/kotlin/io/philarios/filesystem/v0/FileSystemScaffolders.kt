package io.philarios.filesystem.v0

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class EntryScaffolder<in C, out T : Entry>(internal val spec: EntrySpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is DirectorySpec<C> -> DirectoryScaffolder(spec).createScaffold(context)
            is FileSpec<C> -> FileScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class DirectoryScaffolder<in C>(internal val spec: DirectorySpec<C>) : Scaffolder<C, Directory> {
    override fun createScaffold(context: C): Scaffold<Directory> {
        val builder = DirectoryShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class FileScaffolder<in C>(internal val spec: FileSpec<C>) : Scaffolder<C, File> {
    override fun createScaffold(context: C): Scaffold<File> {
        val builder = FileShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
