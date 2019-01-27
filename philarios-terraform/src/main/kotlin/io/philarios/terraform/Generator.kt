package io.philarios.terraform

import io.philarios.schema.translators.generateInto

suspend fun main() = terraformSchema.generateInto()
