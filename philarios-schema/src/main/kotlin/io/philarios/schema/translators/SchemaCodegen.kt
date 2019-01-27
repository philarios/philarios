package io.philarios.schema.translators

import io.philarios.core.Translator
import io.philarios.core.emptyContext
import io.philarios.core.map
import io.philarios.schema.Schema
import io.philarios.schema.SchemaSpec
import io.philarios.schema.translators.codegen.fileSpecs
import java.io.File

suspend fun SchemaSpec<Any?>.generateInto(outputDirectory: String = "./src/generated/kotlin") {
    emptyContext()
            .map(this)
            .map(SchemaCodegen(outputDirectory))
}

class SchemaCodegen(private val outputDirectory: kotlin.String) : Translator<Schema, Unit> {
    override fun translate(context: Schema) {
        val directory = File(outputDirectory)
        context.fileSpecs
                .forEach { it.writeTo(directory) }
    }
}