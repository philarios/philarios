package io.philarios.structurizr

import com.structurizr.api.StructurizrClient
import io.philarios.core.emptyContext
import io.philarios.core.map

suspend fun main() {
    val workspace = emptyContext()
            .map(WorkspaceScaffolder(workspace))
            .map(OutputTranslator())
            .value

    val client = StructurizrClient("4b7c1da9-d3d3-4cdb-9dc3-59b63c0d6816", "f00066fc-bf7c-48f3-b4c5-575998fe188b")
    client.putWorkspace(43493, workspace)
}

val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")
}