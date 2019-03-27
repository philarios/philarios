package io.philarios.schema.cmd

import io.philarios.schema.schemaSchema
import io.philarios.schema.usecases.generateCodeForSchema

suspend fun main() = generateCodeForSchema(schemaSchema)
