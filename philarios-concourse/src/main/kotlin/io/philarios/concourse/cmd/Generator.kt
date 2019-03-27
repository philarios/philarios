package io.philarios.concourse.cmd

import io.philarios.concourse.concourseSchema
import io.philarios.schema.usecases.generateCodeForSchemaSpec

suspend fun main() = generateCodeForSchemaSpec(concourseSchema)

