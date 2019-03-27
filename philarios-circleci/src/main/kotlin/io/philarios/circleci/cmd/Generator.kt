package io.philarios.circleci.cmd

import io.philarios.jsonschema.usecases.GenerateCodeForJsonSchemaInput
import io.philarios.jsonschema.usecases.generateCodeForJsonSchema
import io.philarios.jsonschema.entities.Type as JsonSchemaType

suspend fun main() = generateCodeForJsonSchema(GenerateCodeForJsonSchemaInput(
        "circleciconfig.json",
        "io.philarios.circleci",
        "CircleCI"
))
