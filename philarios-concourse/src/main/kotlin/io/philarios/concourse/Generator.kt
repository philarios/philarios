package io.philarios.concourse

import io.philarios.schema.translators.generateInto

suspend fun main() = concourseSchema.generateInto()
