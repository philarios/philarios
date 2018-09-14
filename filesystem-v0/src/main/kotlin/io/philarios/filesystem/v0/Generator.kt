package io.philarios.filesystem.v0

import io.philarios.core.v0.emptyContext
import io.philarios.filesystem.v0.FileSystemSchemaSpec
import io.philarios.schema.v0.SchemaTranslator
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

fun main(args: Array<kotlin.String>) {
    emptyContext()
            .translate(SchemaTranslator(FileSystemSchemaSpec))
            .translate(SchemaCodegen("./src/generated/kotlin"))
}