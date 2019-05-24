package io.philarios.schema.usecases

import io.philarios.schema.Schema
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.SchemaSpec
import io.philarios.schema.entities.codegen.fileSpecs
import io.philarios.schema.gateways.writers.DirectoryFileSpecWriter
import io.philarios.schema.gateways.writers.FileSpecWriter
import io.philarios.util.registry.emptyRegistry

suspend fun generateCode(schemaSpec: SchemaSpec) = GenerateCode()(schemaSpec)

fun generateCode(schema: Schema) = GenerateCode()(schema)

class GenerateCode(
        private val fileSpecWriter: FileSpecWriter = DirectoryFileSpecWriter()
) {

    suspend operator fun invoke(schemaSpec: SchemaSpec) {
        SchemaScaffolder(schemaSpec)
                .createScaffold()
                .resolve(emptyRegistry())
                .let { invoke(it) }
    }

    operator fun invoke(schema: Schema) {
        fileSpecWriter.writeFileSpecs(schema.fileSpecs())
    }

}
