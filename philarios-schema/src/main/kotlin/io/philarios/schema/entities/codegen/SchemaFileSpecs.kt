package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.Schema
import io.philarios.util.kotlinpoet.*

internal fun Schema.fileSpecs(): List<FileSpec> {
    return fileSpecsByLayer()
}

private fun Schema.fileSpecsByType(): List<FileSpec> {
    return codeSpecsByClassifier()
            .entries
            .groupBy { it.key.type }
            .mapValues { it.value.map { it.value }.reduce(CodeSpecs::plus) }
            .map {
                FileSpec.builder(pkg, it.key)
                        .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                        .addTypes(it.value.typeSpecs)
                        .addFunctions(it.value.funSpecs)
                        .build()
            }
}

private fun Schema.fileSpecsByKind(): List<FileSpec> {
    return codeSpecsByClassifier()
            .entries
            .groupBy { it.key.kind }
            .mapValues { it.value.map { it.value }.reduce(CodeSpecs::plus) }
            .map {
                FileSpec.builder(pkg, "$name${it.key.fileName}")
                        .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                        .addTypes(it.value.typeSpecs)
                        .addFunctions(it.value.funSpecs)
                        .build()
            }
}

private fun Schema.fileSpecsByLayer(): List<FileSpec> {
    return codeSpecsByClassifier()
            .entries
            .groupBy { it.key.kind.layer }
            .mapValues { it.value.map { it.value }.reduce(CodeSpecs::plus) }
            .map {
                FileSpec.builder(pkg, "$name${it.key.fileName}")
                        .addComment(it.key.comment())
                        .addStaticImports(it.key.imports())
                        .addTypes(it.value.typeSpecs)
                        .addFunctions(it.value.funSpecs)
                        .build()
            }
}

private fun Layer.imports() = when (this) {
    Layer.MODEL -> emptyList()
    Layer.SPEC -> emptyList()
    Layer.SCAFFOLD -> listOf(Import("kotlinx.coroutines", listOf("coroutineScope", "launch")))
}

private fun Layer.comment() = when (this) {
    Layer.MODEL -> Comment("""
        The model of your schema written as pure Kotlin classes.

        Because the model expresses the high-level domain, nothing in this file will depend on the generator or on any
        of the other files. This is done to ensure that you could potentially take this file and reuse the classes
        without having a dependency on the specs or materialization process.

        If you feel like something is preventing you from separating the model classes from the specific specs, builders,
        or materialization, please feel free to open an issue in the project's repository.
    """.trimIndent(), emptyList())
    Layer.SPEC -> Comment("""
        The builder interfaces needed to create type-safe specs.

        The specs and builders are located one layer below the model. While they need to reference the model classes
        for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
        implementation details. This allows you to write specs without depending on how the specs are actually
        materialized.

        It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
        decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
        issue in the project's repository.
    """.trimIndent(), emptyList())
    Layer.SCAFFOLD -> Comment("""
        The implementation specifics required for materializing the specs into the model classes.

        Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
        file. The approach taken is rather opinionated and changes to this file are the most frequent.

        In case you want to report any issues or bugs with the materialization process, please feel free to open an
        issue in the project's repository.
    """.trimIndent(), emptyList())
}