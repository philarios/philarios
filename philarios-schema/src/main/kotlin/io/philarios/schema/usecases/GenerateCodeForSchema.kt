package io.philarios.schema.usecases

import io.philarios.core.emptyContext
import io.philarios.core.map
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.SchemaSpec
import io.philarios.schema.gateways.codegen.fileSpecs
import java.io.File

suspend fun generateCodeForSchema(
        schemaSpec: SchemaSpec<Any?>,
        outputDirectory: String = "./src/generated/kotlin"
) {
    emptyContext()
            .map(SchemaScaffolder(schemaSpec))
            .map { schema ->
                val directory = File(outputDirectory)
                schema.fileSpecs.forEach { it.writeTo(directory) }
            }
}
