package io.philarios.schema.gateways.writers

import com.squareup.kotlinpoet.FileSpec

interface FileSpecWriter {

    fun writeFileSpecs(fileSpecs: List<FileSpec>)

}