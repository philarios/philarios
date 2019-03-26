package io.philarios.structurizr

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.String
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class WorkspaceShell(var name: Scaffold<String>? = null, var description: Scaffold<String>? = null) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        checkNotNull(description) { "Workspace is missing the description property" }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) }
        )
        return value
    }
}
