package io.philarios.layout.v0

import io.philarios.core.v0.emptyContext
import io.philarios.schema.v0.SchemaTranslator
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

fun main(args: Array<kotlin.String>) {
    val schemaContext = emptyContext()
            .translate(SchemaTranslator(LayoutSchemaSpec))

    schemaContext.translate(SchemaCodegen("./src/generated/kotlin"))
    schemaContext.translate(SchemaCodegen("../canvas-js-v0/src/main/kotlin"))
}