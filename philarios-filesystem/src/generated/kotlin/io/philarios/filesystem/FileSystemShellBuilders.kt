package io.philarios.filesystem

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
internal class DirectoryShellBuilder<out C>(override val context: C, internal var shell: DirectoryShell = DirectoryShell()) : DirectoryBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Entry> entry(spec: EntrySpec<C, T>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + EntryScaffolder<C, Entry>(spec).createScaffold(context))
    }

    override fun <T : Entry> entry(ref: EntryRef<T>) {
        shell = shell.copy(entries = shell.entries.orEmpty() + ref)
    }

    override fun <T : Entry> entry(value: T) {
        shell = shell.copy(entries = shell.entries.orEmpty() + Wrapper(value))
    }

    override fun include(body: DirectoryBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DirectorySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DirectoryBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DirectorySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DirectoryBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DirectorySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DirectoryShellBuilder<C2> = DirectoryShellBuilder(context, shell)

    private fun <C2> merge(other: DirectoryShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class FileShellBuilder<out C>(override val context: C, internal var shell: FileShell = FileShell()) : FileBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun content(value: String) {
        shell = shell.copy(content = Wrapper(value))
    }

    override fun include(body: FileBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FileSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FileBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FileSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FileBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FileSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FileShellBuilder<C2> = FileShellBuilder(context, shell)

    private fun <C2> merge(other: FileShellBuilder<C2>) {
        this.shell = other.shell
    }
}
