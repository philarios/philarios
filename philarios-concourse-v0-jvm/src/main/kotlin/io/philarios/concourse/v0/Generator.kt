package io.philarios.concourse.v0

import io.philarios.core.v0.emptyContext
import io.philarios.schema.v0.SchemaTranslator
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

fun main(args: Array<kotlin.String>) {
    emptyContext()
            .translate(SchemaTranslator(ConcourseSchemaSpec))
            .translate(SchemaCodegen("./philarios-concourse-v0-common/src/generated/kotlin"))
}