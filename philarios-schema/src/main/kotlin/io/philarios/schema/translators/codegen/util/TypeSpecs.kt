package io.philarios.schema.translators.codegen.util

import com.squareup.kotlinpoet.*
import io.philarios.core.Scaffold
import io.philarios.schema.*
import kotlin.reflect.KClass

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

val KClass<*>.className get() = ClassName.bestGuess(qualifiedName!!)