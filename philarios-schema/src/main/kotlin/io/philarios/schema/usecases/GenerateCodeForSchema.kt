package io.philarios.schema.usecases

import io.philarios.schema.Schema
import io.philarios.schema.gateways.codegen.fileSpecs
import java.io.File

suspend fun generateCodeForSchema(
        schema: Schema,
        outputDirectory: String = "./src/generated/kotlin"
) {
    val directory = File(outputDirectory)
    schema.fileSpecs.forEach { it.writeTo(directory) }
}
