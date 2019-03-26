package io.philarios.structurizr

import com.structurizr.api.StructurizrClient
import io.philarios.core.emptyContext
import io.philarios.core.map

suspend fun main() {
    upload(workspace)
}

private suspend fun upload(workspaceSpec: WorkspaceSpec<Any?>) {
    val workspace = emptyContext()
            .map(WorkspaceScaffolder(workspaceSpec))
            .map(OutputTranslator())
            .value

    val apiKey = System.getenv("STRUCTURIZR_API_KEY")
    val apiSecret = System.getenv("STRUCTURIZR_API_SECRET")

    val client = StructurizrClient(apiKey, apiSecret)
    client.putWorkspace(43493, workspace)
}
