package io.philarios.structurizr.cmd

import io.philarios.schema.usecases.generateCodeForSchemaSpec
import io.philarios.structurizr.structurizrSchema

suspend fun main() = generateCodeForSchemaSpec(structurizrSchema)
