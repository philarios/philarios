package io.philarios.terraform.cmd

import io.philarios.schema.usecases.generateCode
import io.philarios.terraform.terraformSchema

suspend fun main() = generateCode(terraformSchema)
