package io.philarios.jsonschema.usecases

import io.philarios.core.resolve
import io.philarios.jsonschema.entities.JsonSchemaObject
import io.philarios.jsonschema.entities.jsonSchemaSchema
import io.philarios.jsonschema.gateways.reader.readJsonSchema
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.gateways.writers.DirectoryFileSpecWriter
import io.philarios.schema.usecases.GenerateCode as SchemaGenerateCode

suspend fun generateCode(inputResource: String, pkg: String, name: String) = GenerateCode()(inputResource, pkg, name)

class GenerateCode(
        private val schemaGenerateCode: SchemaGenerateCode = SchemaGenerateCode(DirectoryFileSpecWriter())
) {
    suspend operator fun invoke(inputResource: String, pkg: String, name: String) {
        val inputStream = ClassLoader.getSystemResourceAsStream(inputResource)
        val jsonSchema = readJsonSchema(inputStream) as JsonSchemaObject

        jsonSchemaSchema(pkg, name, jsonSchema)
                .let { SchemaScaffolder(it).createScaffold().resolve() }
                .let { schemaGenerateCode(it) }
    }
}