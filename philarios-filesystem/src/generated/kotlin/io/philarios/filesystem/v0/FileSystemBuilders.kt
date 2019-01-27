package io.philarios.filesystem.v0

import io.philarios.core.DslBuilder
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
interface DirectoryBuilder<out C> {
    val context: C

    fun name(name: String)

    fun entry(spec: DirectorySpec<C>)

    fun entry(ref: DirectoryRef)

    fun entry(spec: FileSpec<C>)

    fun entry(ref: FileRef)

    fun include(body: DirectoryBuilder<C>.() -> Unit)

    fun include(spec: DirectorySpec<C>)

    fun <C2> include(context: C2, body: DirectoryBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DirectorySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DirectoryBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DirectorySpec<C2>)
}

@DslBuilder
interface FileBuilder<out C> {
    val context: C

    fun name(name: String)

    fun include(body: FileBuilder<C>.() -> Unit)

    fun include(spec: FileSpec<C>)

    fun <C2> include(context: C2, body: FileBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FileSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FileBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FileSpec<C2>)
}
