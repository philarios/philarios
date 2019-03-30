package io.philarios.schema.entities.codegen.util


// This is copied over from the Kotlin Poet library
// https://github.com/square/kotlinpoet/blob/master/src/main/java/com/squareup/kotlinpoet/Util.kt
// TODO decide if I should keep, move or replace this

internal val String.escaped
    get() = if (shouldBeEscaped) {
        "`$this`"
    } else {
        this
    }

internal val String.shouldBeEscaped get() = containsSpecialCharacter || isKeyword

internal val String.containsSpecialCharacter get() = listOf("$", "+").any { contains(it) }

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
