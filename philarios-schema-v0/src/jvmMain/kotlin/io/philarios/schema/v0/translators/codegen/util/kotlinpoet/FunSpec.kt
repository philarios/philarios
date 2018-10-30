package io.philarios.schema.v0.translators.codegen.util.kotlinpoet

import com.squareup.kotlinpoet.FunSpec

data class Statement(val format: String, val args: List<Any> = emptyList())

fun FunSpec.Builder.addStatements(statements: List<Statement>): FunSpec.Builder {
    return statements.fold(this) { builder, statement ->
        builder.addStatement(statement.format, *statement.args.toTypedArray())
    }
}

fun FunSpec.Builder.addStatements(vararg statements: Statement): FunSpec.Builder {
    return addStatements(statements.toList())
}