package io.philarios.schema.gateways.writers

import com.squareup.kotlinpoet.FileSpec
import java.io.File

class DirectoryFileSpecWriter(
        private val directory: File = File("./src/generated/kotlin")
) : FileSpecWriter {

    override fun writeFileSpecs(fileSpecs: List<FileSpec>) {
        fileSpecs.forEach { it.writeTo(directory) }
    }

}