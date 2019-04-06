package io.philarios.structurizr.entities

import io.philarios.structurizr.Workspace
import io.philarios.structurizr.WorkspaceConfiguration
import com.structurizr.Workspace as SWorkspace
import com.structurizr.configuration.Role as SRole
import com.structurizr.configuration.WorkspaceConfiguration as SWorkspaceConfiguration
import com.structurizr.model.Component as SComponent
import com.structurizr.model.Container as SContainer
import com.structurizr.model.InteractionStyle as SInteractionStyle
import com.structurizr.model.Location as SLocation
import com.structurizr.model.Model as SModel
import com.structurizr.model.Person as SPerson
import com.structurizr.model.SoftwareSystem as SSoftwareSystem
import com.structurizr.view.Border as SBorder
import com.structurizr.view.Branding as SBranding
import com.structurizr.view.Configuration as SConfiguration
import com.structurizr.view.ElementStyle as SElementStyle
import com.structurizr.view.Font as SFont
import com.structurizr.view.RelationshipStyle as SRelationshipStyle
import com.structurizr.view.Routing as SRouting
import com.structurizr.view.Shape as SShape
import com.structurizr.view.Styles as SStyles
import com.structurizr.view.Terminology as STerminology
import com.structurizr.view.ViewSet as SViewSet

fun Workspace.convert() = SWorkspace(name, description).also {
    val elementMap = model?.convert(it.model)
    viewSet?.convert(it.views, elementMap.orEmpty())
    configuration?.convert(it.configuration)
}

private fun WorkspaceConfiguration.convert(configuration: SWorkspaceConfiguration) {
    users.forEach { configuration.addUser(it.username, it.role.convert()) }
}

private fun Role.convert() = when (this) {
    Role.ReadWrite -> SRole.ReadWrite
    Role.ReadOnly -> SRole.ReadOnly
}
