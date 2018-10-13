package io.philarios.schema.v0.translators.codegen

import io.philarios.core.v0.Translator
import io.philarios.schema.v0.Schema
import java.io.File

class SchemaCodegen(private val outputDirectory: kotlin.String) : Translator<Schema, Unit> {
    override fun translate(context: Schema) {
        val directory = File(outputDirectory)
        SchemaFileSpec.build(context)
                .forEach { it.writeTo(directory) }
    }
}