package io.philarios.structurizr.gateways.upload

import com.structurizr.Workspace
import com.structurizr.api.StructurizrClient

class StructurizrUploader {

    val workspaceId = System.getenv("STRUCTURIZR_WORKSPACE_ID").toLong()
    val apiKey = System.getenv("STRUCTURIZR_API_KEY")
    val apiSecret = System.getenv("STRUCTURIZR_API_SECRET")

    val client = StructurizrClient(apiKey, apiSecret)

    fun upload(workspace: Workspace) {
        client.putWorkspace(workspaceId, workspace)
    }

}
