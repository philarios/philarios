package io.philarios.structurizr.usecases

import io.philarios.core.resolve
import io.philarios.structurizr.WorkspaceScaffolder
import io.philarios.structurizr.WorkspaceSpec
import io.philarios.structurizr.entities.convert
import io.philarios.structurizr.gateways.upload.StructurizrUploader

suspend fun putWorkspaceSpec(workspaceSpec: WorkspaceSpec) {
    val workspace = WorkspaceScaffolder(workspaceSpec)
            .createScaffold()
            .resolve()
            .convert()
    val uploader = StructurizrUploader()
    uploader.upload(workspace)
}
