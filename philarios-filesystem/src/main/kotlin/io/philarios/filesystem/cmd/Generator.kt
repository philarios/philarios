package io.philarios.filesystem.cmd

import io.philarios.filesystem.fileSystemSchema
import io.philarios.schema.usecases.generateCode

suspend fun main() = generateCode(fileSystemSchema)
