package io.philarios.filesystem.cmd

import io.philarios.filesystem.fileSystemSchema
import io.philarios.schema.usecases.generateCodeForSchemaSpec

suspend fun main() = generateCodeForSchemaSpec(fileSystemSchema)
