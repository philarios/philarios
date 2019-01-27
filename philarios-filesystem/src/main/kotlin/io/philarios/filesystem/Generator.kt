package io.philarios.filesystem

import io.philarios.schema.translators.generateInto

suspend fun main() = fileSystemSchema.generateInto()
