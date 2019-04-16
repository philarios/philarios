package io.philarios.structurizr.sugar

import io.philarios.structurizr.ElementStyleBuilder
import io.philarios.structurizr.gateways.images.ImageFileReader

fun  ElementStyleBuilder.iconFromResource(resource: String) {
    val dataImage = ImageFileReader.readAsDataImage(resource)
    icon(dataImage)
}