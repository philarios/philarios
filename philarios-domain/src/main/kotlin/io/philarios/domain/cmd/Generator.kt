package io.philarios.domain.cmd

import io.philarios.domain.domainSchema
import io.philarios.schema.usecases.generateCode

suspend fun main() = generateCode(domainSchema)
