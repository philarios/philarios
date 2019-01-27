package io.philarios.filesystem.v0

import io.philarios.core.Scaffold
import io.philarios.core.Spec

class DirectorySpec<in C>(internal val body: DirectoryBuilder<C>.() -> Unit) : Spec<C, Directory> {
    override fun connect(context: C): Scaffold<Directory> {
        val builder = DirectoryShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

class FileSpec<in C>(internal val body: FileBuilder<C>.() -> Unit) : Spec<C, File> {
    override fun connect(context: C): Scaffold<File> {
        val builder = FileShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
