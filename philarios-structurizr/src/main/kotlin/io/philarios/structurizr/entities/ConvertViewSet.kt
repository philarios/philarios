package io.philarios.structurizr.entities

import io.philarios.structurizr.*
import com.structurizr.view.Branding as SBranding
import com.structurizr.view.ViewSet as SViewSet
import com.structurizr.view.Configuration as SConfiguration
import com.structurizr.view.Font as SFont
import com.structurizr.view.Styles as SStyles
import com.structurizr.view.ElementStyle as SElementStyle
import com.structurizr.view.RelationshipStyle as SRelationshipStyle
import com.structurizr.view.Shape as SShape
import com.structurizr.view.Border as SBorder
import com.structurizr.view.Routing as SRouting
import com.structurizr.view.Terminology as STerminology

fun ViewSet.convert(viewSet: SViewSet) = viewSet.also {
    configuration?.let { configuration -> configuration.convert(it.configuration) }
}

private fun Configuration.convert(configuration: SConfiguration) = configuration.also {
    branding?.let { branding -> branding.convert(it.branding) }
    styles?.let { styles -> styles.convert(it.styles) }
    terminology?.let { terminology -> terminology.convert(it.terminology) }
}

private fun Branding.convert(branding: SBranding) = branding.also {
    logo?.let { logo -> it.logo = logo }
    font?.let { font -> it.font = font.convert() }
}

private fun Font.convert() = SFont(name, url)

private fun Styles.convert(styles: SStyles) = styles.also {
    elements?.forEach { element -> it.add(element.convert()) }
    relationships?.forEach { relationship -> it.add(relationship.convert()) }
}

private fun ElementStyle.convert() = SElementStyle(
        tag, width, height, background, color, fontSize, shape?.convert()
).also {
    border?.let { border -> it.border = border.convert() }
    opacity?.let { opacity -> it.opacity = opacity }
    metadata?.let { metadata -> it.metadata = metadata }
    description?.let { description -> it.description = description }
}

private fun Shape.convert() = when (this) {
    Shape.Box -> SShape.Box
    Shape.RoundedBox -> SShape.RoundedBox
    Shape.Circle -> SShape.Circle
    Shape.Ellipse -> SShape.Ellipse
    Shape.Hexagon -> SShape.Hexagon
    Shape.Cylinder -> SShape.Cylinder
    Shape.Pipe -> SShape.Pipe
    Shape.Person -> SShape.Person
    Shape.Robot -> SShape.Robot
    Shape.Folder -> SShape.Folder
    Shape.WebBrowser -> SShape.WebBrowser
    Shape.MobileDevicePortrait -> SShape.MobileDevicePortrait
    Shape.MobileDeviceLandscape -> SShape.MobileDeviceLandscape
}

private fun Border.convert() = when (this) {
    Border.Solid -> SBorder.Solid
    Border.Dashed -> SBorder.Dashed
}

private fun RelationshipStyle.convert() = SRelationshipStyle(
        tag, thickness, color, dashed, routing.convert(), fontSize, width, position
).also {
    opacity?.let { opacity -> it.opacity = opacity }
}

private fun Routing.convert() = when (this) {
    Routing.Direct -> SRouting.Direct
    Routing.Orthogonal -> SRouting.Orthogonal
}

private fun Terminology.convert(terminology: STerminology) = terminology.also {
    enterprise?.let { enterprise -> it.enterprise = enterprise }
    person?.let { person -> it.person = person }
    softwareSystem?.let { softwareSystem -> it.softwareSystem = softwareSystem }
    container?.let { container -> it.container = container }
    component?.let { component -> it.component = component }
    code?.let { code -> it.code = code }
    deploymentNode?.let { deploymentNode -> it.deploymentNode = deploymentNode }
}