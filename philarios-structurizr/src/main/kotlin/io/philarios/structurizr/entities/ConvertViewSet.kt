package io.philarios.structurizr.entities

import io.philarios.structurizr.*
import com.structurizr.model.Component as SComponent
import com.structurizr.model.Container as SContainer
import com.structurizr.model.Person as SPerson
import com.structurizr.model.SoftwareSystem as SSoftwareSystem
import com.structurizr.view.Border as SBorder
import com.structurizr.view.Branding as SBranding
import com.structurizr.view.ComponentView as SComponentView
import com.structurizr.view.Configuration as SConfiguration
import com.structurizr.view.ContainerView as SContainerView
import com.structurizr.view.ElementStyle as SElementStyle
import com.structurizr.view.Font as SFont
import com.structurizr.view.PaperSize as SPaperSize
import com.structurizr.view.RelationshipStyle as SRelationshipStyle
import com.structurizr.view.Routing as SRouting
import com.structurizr.view.Shape as SShape
import com.structurizr.view.Styles as SStyles
import com.structurizr.view.SystemContextView as SSystemContextView
import com.structurizr.view.SystemLandscapeView as SSystemLandscapeView
import com.structurizr.view.Terminology as STerminology
import com.structurizr.view.ViewSet as SViewSet

fun ViewSet.convert(viewSet: SViewSet, elementMap: ElementMap) = viewSet.also {
    systemLandscapeViews?.forEach { systemLandscapeView -> systemLandscapeView.convert(it, elementMap) }
    systemContextViews?.forEach { systemContextView -> systemContextView.convert(it, elementMap) }
    containerViews?.forEach { containerView -> containerView.convert(it, elementMap) }
    componentViews?.forEach { componentView -> componentView.convert(it, elementMap) }
    configuration?.let { configuration -> configuration.convert(it.configuration) }
}

private fun SystemLandscapeView.convert(viewSet: SViewSet, elementMap: ElementMap) =
        viewSet.createSystemLandscapeView(key, description).also {
            title?.let { title -> it.title = title }
            paperSize?.let { paperSize -> it.paperSize = paperSize.convert() }
            softwareSystems?.forEach { softwareSystems -> it.add(elementMap[softwareSystems] as SSoftwareSystem) }
            people?.forEach { person -> it.add(elementMap[person] as SPerson) }
        }

private fun SystemContextView.convert(viewSet: SViewSet, elementMap: ElementMap) =
        viewSet.createSystemContextView(elementMap[softwareSystemId] as SSoftwareSystem, key, description).also {
            title?.let { title -> it.title = title }
            paperSize?.let { paperSize -> it.paperSize = paperSize.convert() }
            softwareSystems?.forEach { softwareSystems -> it.add(elementMap[softwareSystems] as SSoftwareSystem) }
            people?.forEach { person -> it.add(elementMap[person] as SPerson) }
        }

private fun ContainerView.convert(viewSet: SViewSet, elementMap: ElementMap) =
        viewSet.createContainerView(elementMap[softwareSystemId] as SSoftwareSystem, key, description).also {
            title?.let { title -> it.title = title }
            paperSize?.let { paperSize -> it.paperSize = paperSize.convert() }
            softwareSystems?.forEach { softwareSystems -> it.add(elementMap[softwareSystems] as SSoftwareSystem) }
            people?.forEach { person -> it.add(elementMap[person] as SPerson) }
            containers?.forEach { container -> it.add(elementMap[container] as SContainer) }
        }

private fun ComponentView.convert(viewSet: SViewSet, elementMap: ElementMap) =
        viewSet.createComponentView(elementMap[containerId] as SContainer, key, description).also {
            title?.let { title -> it.title = title }
            paperSize?.let { paperSize -> it.paperSize = paperSize.convert() }
            softwareSystems?.forEach { softwareSystems -> it.add(elementMap[softwareSystems] as SSoftwareSystem) }
            people?.forEach { person -> it.add(elementMap[person] as SPerson) }
            containers?.forEach { container -> it.add(elementMap[container] as SContainer) }
            components?.forEach { component -> it.add(elementMap[component] as SComponent) }
        }

private fun PaperSize.convert() = when (this) {
    PaperSize.A6_Portrait -> SPaperSize.A6_Portrait
    PaperSize.A6_Landscape -> SPaperSize.A6_Landscape
    PaperSize.A5_Portrait -> SPaperSize.A5_Portrait
    PaperSize.A5_Landscape -> SPaperSize.A5_Landscape
    PaperSize.A4_Portrait -> SPaperSize.A4_Portrait
    PaperSize.A4_Landscape -> SPaperSize.A4_Landscape
    PaperSize.A3_Portrait -> SPaperSize.A3_Portrait
    PaperSize.A3_Landscape -> SPaperSize.A3_Landscape
    PaperSize.A2_Portrait -> SPaperSize.A2_Portrait
    PaperSize.A2_Landscape -> SPaperSize.A2_Landscape
    PaperSize.Letter_Portrait -> SPaperSize.Letter_Portrait
    PaperSize.Letter_Landscape -> SPaperSize.Letter_Landscape
    PaperSize.Legal_Portrait -> SPaperSize.Legal_Portrait
    PaperSize.Legal_Landscape -> SPaperSize.Legal_Landscape
    PaperSize.Slide_4_3 -> SPaperSize.Slide_4_3
    PaperSize.Slide_16_9 -> SPaperSize.Slide_16_9
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