package io.philarios.jsonschema.usecases

import io.philarios.core.contextOf
import io.philarios.core.map
import io.philarios.jsonschema.entities.JsonSchemaObject
import io.philarios.jsonschema.entities.jsonSchemaSchema
import io.philarios.jsonschema.gateways.reader.readJsonSchema
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.usecases.generateCodeForSchema


data class GenerateCodeForJsonSchemaInput(
        val inputResource: String,
        val pkg: String,
        val name: String,
        val outputDirectory: String = "./src/generated/kotlin"
)

suspend fun generateCodeForJsonSchema(input: GenerateCodeForJsonSchemaInput) {
    val inputStream = ClassLoader.getSystemResourceAsStream(input.inputResource)
    val jsonSchema = readJsonSchema(inputStream) as JsonSchemaObject

    contextOf(jsonSchema)
            .map(SchemaScaffolder(jsonSchemaSchema(input.pkg, input.name)))
            .map { generateCodeForSchema(it, input.outputDirectory) }
}