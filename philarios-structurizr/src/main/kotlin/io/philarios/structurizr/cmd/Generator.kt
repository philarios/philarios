package io.philarios.structurizr.cmd

import io.philarios.schema.usecases.generateCode
import io.philarios.structurizr.structurizrSchema

suspend fun main() = generateCode(structurizrSchema)
