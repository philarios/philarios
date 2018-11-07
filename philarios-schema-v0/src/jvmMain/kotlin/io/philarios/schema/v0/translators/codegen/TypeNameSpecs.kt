package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Scaffold
import io.philarios.schema.v0.*
import kotlin.reflect.KClass

internal val KClass<*>.className get() = ClassName.bestGuess(qualifiedName!!)

val Type.typeName: TypeName
    get() = when (this) {
        is Struct -> ClassName(pkg ?: "", name)
        is Union -> ClassName(pkg ?: "", name)
        is EnumType -> ClassName(pkg ?: "", name)
        is RefType -> ClassName(pkg ?: "", name)
        is OptionType -> type.nullableTypeName
        is BooleanType -> Boolean::class.className
        is DoubleType -> Double::class.className
        is FloatType -> Float::class.className
        is LongType -> Long::class.className
        is IntType -> Int::class.className
        is ShortType -> Short::class.className
        is ByteType -> Byte::class.className
        is CharacterType -> Character::class.className
        is StringType -> String::class.className
        is AnyType -> Any::class.className
        is ListType -> ParameterizedTypeName.get(List::class.className, type.typeName)
        is MapType -> ParameterizedTypeName.get(Map::class.className, keyType.typeName, valueType.typeName)
    }

val Type.nullableTypeName
    get() = typeName.asNullable()

val Type.className: ClassName
    get() = when (this) {
        is Struct -> ClassName(pkg ?: "", name)
        is Union -> ClassName(pkg ?: "", name)
        is EnumType -> ClassName(pkg ?: "", name)
        is RefType -> ClassName(pkg ?: "", name)
        is OptionType -> type.nullableClassName
        is BooleanType -> Boolean::class.className
        is DoubleType -> Double::class.className
        is FloatType -> Float::class.className
        is LongType -> Long::class.className
        is IntType -> Int::class.className
        is ShortType -> Short::class.className
        is ByteType -> Byte::class.className
        is CharacterType -> Character::class.className
        is StringType -> String::class.className
        is AnyType -> Any::class.className
        is ListType -> List::class.className
        is MapType -> Map::class.className
    }

val Type.nullableClassName
    get() = className.asNullable()

fun Type.className(suffix: String) = when (this) {
    is Struct -> ClassName(pkg ?: "", "$name$suffix")
    is Union -> ClassName(pkg ?: "", "$name$suffix")
    is EnumType -> ClassName(pkg ?: "", "$name$suffix")
    is RefType -> ClassName(pkg ?: "", "$name$suffix")
    else -> className
}

val Type.shellClassName
    get() = className("Shell")

val Type.scaffoldClassName: ParameterizedTypeName
    get() = className.scaffoldClassName

val ClassName.scaffoldClassName: ParameterizedTypeName
    get() = ParameterizedTypeName.get(Scaffold::class.className, this)

val Type.refClassName
    get() = className("Ref")

val Type.specClassName
    get() = ParameterizedTypeName.get(className("Spec"), TypeVariableName("C", KModifier.IN))

val Type.otherSpecClassName
    get() = ParameterizedTypeName.get(className("Spec"), TypeVariableName("C2", KModifier.IN))

val Type.builderClassName
    get() = ParameterizedTypeName.get(className("Builder"), TypeVariableName("C"))

val Type.otherBuilderClassName
    get() = ParameterizedTypeName.get(className("Builder"), TypeVariableName("C2"))

val Type.shellBuilderClassName
    get() = ParameterizedTypeName.get(className("ShellBuilder"), TypeVariableName("C"))

val Type.otherShellBuilderClassName
    get() = ParameterizedTypeName.get(className("ShellBuilder"), TypeVariableName("C2"))

val Type.bodyLambdaTypeName
    get() = LambdaTypeName.get(builderClassName, emptyList(), ClassName("", "Unit"))

val Type.otherBodyLambdaTypeName
    get() = LambdaTypeName.get(otherBuilderClassName, emptyList(), ClassName("", "Unit"))

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
