package io.philarios.domain

import io.philarios.schema.translators.generateInto

suspend fun main() = domainSchema.generateInto()
