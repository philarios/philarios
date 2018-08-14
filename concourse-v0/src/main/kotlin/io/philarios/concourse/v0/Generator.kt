package io.philarios.concourse.v0

import io.philarios.codegen.v0.SchemaFileSpec
import io.philarios.core.v0.Translator
import io.philarios.core.v0.translate
import io.philarios.schema.v0.Schema
import io.philarios.schema.v0.SchemaTranslator
import java.io.File

fun main(args: Array<kotlin.String>) {
    translate(SchemaTranslator(ConcourseSchemaSpec))
            .translate(SchemaCodegen("./src/generated/kotlin"))
}

class SchemaCodegen(private val outputDirectory: kotlin.String) : Translator<Schema, Unit> {
    override fun translate(context: Schema) {
        val fileSpec = SchemaFileSpec.build(context)
        fileSpec.writeTo(File(outputDirectory))
    }
}