package io.philarios.domain.cmd

import io.philarios.domain.domainSchema
import io.philarios.schema.usecases.generateCodeForSchemaSpec

suspend fun main() = generateCodeForSchemaSpec(domainSchema)
