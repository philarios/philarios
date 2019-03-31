package io.philarios.structurizr.gateways.upload

import com.structurizr.Workspace
import com.structurizr.api.StructurizrClient

class StructurizrUploader {

    private val workspaceId = System.getenv("STRUCTURIZR_WORKSPACE_ID")!!.toLong()
    private val apiKey = System.getenv("STRUCTURIZR_API_KEY")!!
    private val apiSecret = System.getenv("STRUCTURIZR_API_SECRET")!!

    private val client = StructurizrClient(apiKey, apiSecret)

    fun upload(workspace: Workspace) {
        client.putWorkspace(workspaceId, workspace)
    }

}
