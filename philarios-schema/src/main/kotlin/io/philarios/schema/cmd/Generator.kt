package io.philarios.schema.cmd

import io.philarios.schema.schemaSchema
import io.philarios.schema.usecases.generateCode

suspend fun main() = generateCode(schemaSchema)
