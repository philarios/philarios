package io.philarios.schema.cmd

import io.philarios.schema.schemaSchema
import io.philarios.schema.usecases.generateCodeForSchemaSpec

suspend fun main() = generateCodeForSchemaSpec(schemaSchema)
