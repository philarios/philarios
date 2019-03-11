package io.philarios.circleci

import io.philarios.jsonschema.JsonSchemaGenerationMetadata
import io.philarios.jsonschema.generateSchema
import io.philarios.jsonschema.Type as JsonSchemaType

suspend fun main() = generateSchema(JsonSchemaGenerationMetadata(
        "circleciconfig.json",
        "io.philarios.circleci",
        "CircleCI"
))
