package io.philarios.schema.usecases

import io.philarios.core.emptyContext
import io.philarios.core.map
import io.philarios.schema.Schema
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.SchemaSpec
import io.philarios.schema.entities.codegen.fileSpecs
import io.philarios.schema.gateways.writers.DirectoryFileSpecWriter
import io.philarios.schema.gateways.writers.FileSpecWriter
import java.io.File

suspend fun generateCode(
        schemaSpec: SchemaSpec<Any?>,
        fileSpecWriter: FileSpecWriter = DirectoryFileSpecWriter()
) {
    emptyContext()
            .map(SchemaScaffolder(schemaSpec))
            .map { generateCode(it, fileSpecWriter) }
}

fun generateCode(
        schema: Schema,
        fileSpecWriter: FileSpecWriter
) {
    fileSpecWriter.writeFileSpecs(schema.fileSpecs)
}
