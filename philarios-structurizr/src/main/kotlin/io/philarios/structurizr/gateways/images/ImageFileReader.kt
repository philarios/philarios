package io.philarios.structurizr.gateways.images

import org.apache.commons.codec.binary.Base64

object ImageFileReader {

    fun readAsDataImage(resource: String) =
            "data:image/png;base64,${readAsBase64(resource)}"

    fun readAsBase64(resource: String) =
            ImageFileReader::class.java.classLoader
                    .getResourceAsStream(resource)
                    .readBytes()
                    .let { Base64.encodeBase64(it) }
                    .let { String(it) }

}