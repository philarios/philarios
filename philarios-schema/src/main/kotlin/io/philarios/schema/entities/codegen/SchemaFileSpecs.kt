package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.Schema
import io.philarios.util.kotlinpoet.Import
import io.philarios.util.kotlinpoet.addStaticImports
import io.philarios.util.kotlinpoet.addTypes

internal fun Schema.fileSpecs(): List<FileSpec> {
    return fileSpecsByLayer()
}

private fun Schema.fileSpecsByType(): List<FileSpec> {
    return typeSpecsByClassifer()
            .entries
            .groupBy { it.key.type }
            .mapValues { it.value.flatMap { it.value } }
            .map {
                FileSpec.builder(pkg, it.key)
                        .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                        .addTypes(it.value)
                        .build()
            }
}

private fun Schema.fileSpecsByKind(): List<FileSpec> {
    return typeSpecsByClassifer()
            .entries
            .groupBy { it.key.kind }
            .mapValues { it.value.flatMap { it.value } }
            .map {
                FileSpec.builder(pkg, "$name${it.key.fileName}")
                        .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                        .addTypes(it.value)
                        .build()
            }
}

private fun Schema.fileSpecsByLayer(): List<FileSpec> {
    return typeSpecsByClassifer()
            .entries
            .groupBy { it.key.kind.layer }
            .mapValues { it.value.flatMap { it.value } }
            .map {
                FileSpec.builder(pkg, "$name${it.key.fileName}")
                        .addStaticImports(it.key.imports())
                        .addTypes(it.value)
                        .build()
            }
}

private fun Layer.imports() = when (this) {
    Layer.MODEL -> emptyList()
    Layer.SPEC -> emptyList()
    Layer.SCAFFOLD -> listOf(Import("kotlinx.coroutines", listOf("coroutineScope", "launch")))
}
