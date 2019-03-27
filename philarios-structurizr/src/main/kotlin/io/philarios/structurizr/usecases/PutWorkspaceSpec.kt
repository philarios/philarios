package io.philarios.structurizr.usecases

import io.philarios.core.emptyContext
import io.philarios.core.map
import io.philarios.structurizr.Workspace
import io.philarios.structurizr.WorkspaceScaffolder
import io.philarios.structurizr.WorkspaceSpec
import io.philarios.structurizr.entities.convert
import io.philarios.structurizr.gateways.upload.StructurizrUploader

suspend fun putWorkspaceSpec(workspaceSpec: WorkspaceSpec<Any?>) {
    val workspace = emptyContext()
            .map(WorkspaceScaffolder(workspaceSpec))
            .map(Workspace::convert)
            .value
    val uploader = StructurizrUploader()
    uploader.upload(workspace)
}
