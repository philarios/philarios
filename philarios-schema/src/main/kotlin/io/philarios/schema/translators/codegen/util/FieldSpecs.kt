package io.philarios.schema.translators.codegen.util

import io.philarios.schema.Field

val Field.singularName
    get() = when {
        name.endsWith("ies") -> name.removeSuffix("ies").plus("y")
        else -> name.removeSuffix("s")
    }

val Field.escapedName
    get() = if (name.isKeyword) {
        "`$name`"
    } else {
        name
    }

// This is copied over from the Kotlin Poet library
// https://github.com/square/kotlinpoet/blob/master/src/main/java/com/squareup/kotlinpoet/Util.kt
// TODO decide if I should keep, move or replace this

internal val String.isKeyword get() = KEYWORDS.contains(this)

internal val String.isName get() = split("\\.").none { it.isKeyword }

// https://github.com/JetBrains/kotlin/blob/master/core/descriptors/src/org/jetbrains/kotlin/renderer/KeywordStringsGenerated.java
private val KEYWORDS = setOf(
        "package",
        "as",
        "typealias",
        "class",
        "this",
        "super",
        "val",
        "var",
        "fun",
        "for",
        "null",
        "true",
        "false",
        "is",
        "in",
        "throw",
        "return",
        "break",
        "continue",
        "object",
        "if",
        "try",
        "else",
        "while",
        "do",
        "when",
        "interface",
        "typeof"
)
