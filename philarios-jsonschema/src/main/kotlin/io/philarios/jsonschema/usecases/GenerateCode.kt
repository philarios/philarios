package io.philarios.jsonschema.usecases

import io.philarios.core.contextOf
import io.philarios.core.map
import io.philarios.jsonschema.entities.JsonSchemaObject
import io.philarios.jsonschema.entities.jsonSchemaSchema
import io.philarios.jsonschema.gateways.reader.readJsonSchema
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.gateways.writers.DirectoryFileSpecWriter
import io.philarios.schema.gateways.writers.FileSpecWriter
import io.philarios.schema.usecases.generateCode

suspend fun generateCode(
        inputResource: String,
        pkg: String,
        name: String,
        fileSpecWriter: FileSpecWriter = DirectoryFileSpecWriter()
) {
    val inputStream = ClassLoader.getSystemResourceAsStream(inputResource)
    val jsonSchema = readJsonSchema(inputStream) as JsonSchemaObject

    contextOf(jsonSchema)
            .map(SchemaScaffolder(jsonSchemaSchema(pkg, name)))
            .map { generateCode(it, fileSpecWriter) }
}