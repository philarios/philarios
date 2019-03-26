package io.philarios.structurizr

import com.structurizr.api.StructurizrClient
import io.philarios.core.emptyContext
import io.philarios.core.map

suspend fun main() {
    val workspace = emptyContext()
            .map(WorkspaceScaffolder(workspace))
            .map(OutputTranslator())
            .value

    val apiKey = System.getenv("STRUCTURIZR_API_KEY")
    val apiSecret = System.getenv("STRUCTURIZR_API_SECRET")

    val client = StructurizrClient(apiKey, apiSecret)
    client.putWorkspace(43493, workspace)
}

val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")

    model {
        people {
            name("Admin")
            description("An admin user of the system")
            location(Location.Internal)
        }
    }
}