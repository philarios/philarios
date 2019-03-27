package io.philarios.terraform.cmd

import io.philarios.schema.usecases.generateCodeForSchemaSpec
import io.philarios.terraform.terraformSchema

suspend fun main() = generateCodeForSchemaSpec(terraformSchema)
