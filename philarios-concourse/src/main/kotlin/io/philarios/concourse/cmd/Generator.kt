package io.philarios.concourse.cmd

import io.philarios.concourse.concourseSchema
import io.philarios.schema.usecases.generateCode

suspend fun main() = generateCode(concourseSchema)

