package io.philarios.jsonschema

import io.philarios.core.contextOf
import io.philarios.core.map
import io.philarios.schema.SchemaScaffolder
import io.philarios.schema.translators.SchemaCodegen

data class JsonSchemaGenerationMetadata(
        val inputResource: String,
        val pkg: String,
        val name: String,
        val outputDirectory: String = "./src/generated/kotlin"
)

suspend fun generateSchema(metadata: JsonSchemaGenerationMetadata) {
    val input = ClassLoader.getSystemResourceAsStream(metadata.inputResource)
    val jsonSchema = read(input) as JsonSchemaObject

    contextOf(jsonSchema)
            .map(SchemaScaffolder(jsonSchemaSchema(metadata.pkg, metadata.name)))
            .map(SchemaCodegen(metadata.outputDirectory))
}