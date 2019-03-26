package io.philarios.structurizr

import io.philarios.schema.translators.generateInto

suspend fun main() = structurizrSchema.generateInto()
