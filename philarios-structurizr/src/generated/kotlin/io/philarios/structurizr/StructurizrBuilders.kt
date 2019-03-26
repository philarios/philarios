package io.philarios.structurizr

import io.philarios.core.DslBuilder
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
interface WorkspaceBuilder<out C> {
    val context: C

    fun name(value: String)

    fun description(value: String)

    fun include(body: WorkspaceBuilder<C>.() -> Unit)

    fun include(spec: WorkspaceSpec<C>)

    fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: WorkspaceSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>)
}
