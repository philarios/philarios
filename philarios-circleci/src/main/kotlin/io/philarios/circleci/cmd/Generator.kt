package io.philarios.circleci.cmd

import io.philarios.jsonschema.usecases.generateCode
import io.philarios.jsonschema.entities.Type as JsonSchemaType

suspend fun main() = generateCode(
        "circleciconfig.json",
        "io.philarios.circleci",
        "CircleCI"
)
