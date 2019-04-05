package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.Schema
import io.philarios.util.kotlinpoet.addTypes

internal fun Schema.fileSpecs(): List<FileSpec> {
    return fileSpecsByType()
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
                FileSpec.builder(pkg, "$name${it.key.groupName}")
                        .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                        .addTypes(it.value)
                        .build()
            }
}
