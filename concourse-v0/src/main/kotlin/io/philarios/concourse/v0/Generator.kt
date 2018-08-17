package io.philarios.concourse.v0

import io.philarios.core.v0.emptyContext
import io.philarios.schema.v0.SchemaTranslator
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

fun main(args: Array<kotlin.String>) {
    emptyContext()
            .translate(SchemaTranslator(ConcourseSchemaSpec))
            .translate(SchemaCodegen("./src/generated/kotlin"))
}