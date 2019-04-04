package io.philarios.structurizr.sugar

import io.philarios.structurizr.ElementStyleBuilder
import io.philarios.structurizr.gateways.images.ImageFileReader

fun <C> ElementStyleBuilder<C>.iconFromResource(resource: String) {
    val dataImage = ImageFileReader.readAsDataImage(resource)
    icon(dataImage)
}