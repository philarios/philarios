package io.philarios.schema.v0

import io.philarios.core.v0.emptyContext
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

fun main(args: Array<kotlin.String>) {
    emptyContext()
            .translate(SchemaTranslator(SchemaSchemaSpec))
            .translate(SchemaCodegen("./philarios-schema-v0-common/src/generated/kotlin"))
}