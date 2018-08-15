package io.philarios.codegen.v0

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Builder
import io.philarios.core.v0.DslBuilder
import io.philarios.schema.v0.*

val dslBuilderClassName
    get() = ClassName.bestGuess(DslBuilder::class.qualifiedName!!)

val builderClassName
    get() = ClassName.bestGuess(Builder::class.qualifiedName!!)

val Type.typeName: TypeName
    get() = when (this) {
        is Struct -> ClassName("", name)
        is Union -> ClassName("", name)
        is EnumType -> ClassName("", name)
        is OptionType -> type.nullableTypeName
        is BooleanType -> ClassName.bestGuess("kotlin.Boolean")
        is DoubleType -> ClassName.bestGuess("kotlin.Double")
        is FloatType -> ClassName.bestGuess("kotlin.Float")
        is LongType -> ClassName.bestGuess("kotlin.Long")
        is IntType -> ClassName.bestGuess("kotlin.Int")
        is ShortType -> ClassName.bestGuess("kotlin.Short")
        is ByteType -> ClassName.bestGuess("kotlin.Byte")
        is CharacterType -> ClassName.bestGuess("kotlin.Character")
        is StringType -> ClassName.bestGuess("kotlin.String")
        is AnyType -> ClassName.bestGuess("kotlin.Any")
        is ListType -> ParameterizedTypeName.get(ClassName.bestGuess("kotlin.collections.List"), type.typeName)
        is MapType -> ParameterizedTypeName.get(ClassName.bestGuess("kotlin.collections.Map"), keyType.typeName, valueType.typeName)
        is RefType -> ClassName("", name)
    }

val Type.nullableTypeName
    get() = typeName.asNullable()

val Type.className: ClassName
    get() = when (this) {
        is Struct -> ClassName("", name)
        is Union -> ClassName("", name)
        is EnumType -> ClassName("", name)
        is OptionType -> type.nullableClassName
        is BooleanType -> ClassName.bestGuess("kotlin.Boolean")
        is DoubleType -> ClassName.bestGuess("kotlin.Double")
        is FloatType -> ClassName.bestGuess("kotlin.Float")
        is LongType -> ClassName.bestGuess("kotlin.Long")
        is IntType -> ClassName.bestGuess("kotlin.Int")
        is ShortType -> ClassName.bestGuess("kotlin.Short")
        is ByteType -> ClassName.bestGuess("kotlin.Byte")
        is CharacterType -> ClassName.bestGuess("kotlin.Character")
        is StringType -> ClassName.bestGuess("kotlin.String")
        is AnyType -> ClassName.bestGuess("kotlin.Any")
        is ListType -> ClassName.bestGuess("kotlin.collections.List")
        is MapType -> ClassName.bestGuess("kotlin.collections.Map")
        is RefType -> ClassName("", name)
    }

val Type.nullableClassName
    get() = className.asNullable()

fun Type.className(suffix: String) = when (this) {
    is Struct ->  ClassName("", "$name$suffix")
    is Union ->  ClassName("", "$name$suffix")
    is RefType ->  ClassName("", "$name$suffix")
    else -> className
}

val Type.builderSuperinterface
    get() = ParameterizedTypeName.get(io.philarios.codegen.v0.builderClassName, className)

val Type.builderListSuperinterface
    get() = ParameterizedTypeName.get(io.philarios.codegen.v0.builderClassName,
                ParameterizedTypeName.get(ClassName.bestGuess(List::class.qualifiedName!!), className))

val Type.translatorClassName
    get() = ParameterizedTypeName.get(className("Translator"), TypeVariableName("C", KModifier.IN))

val Type.translatorListClassName
    get() = ParameterizedTypeName.get(className("ListTranslator"), TypeVariableName("C", KModifier.IN))

val Type.specClassName
    get() = ParameterizedTypeName.get(className("Spec"), TypeVariableName("C", KModifier.IN))

val Type.otherSpecClassName
    get() = ParameterizedTypeName.get(className("Spec"), TypeVariableName("C2", KModifier.IN))

val Type.builderClassName
    get() = ParameterizedTypeName.get(className("Builder"), TypeVariableName("C"))

val Type.otherBuilderClassName
    get() = ParameterizedTypeName.get(className("Builder"), TypeVariableName("C2"))

val Type.listBuilderClassName
    get() = ParameterizedTypeName.get(className("ListBuilder"), TypeVariableName("C"))

val Type.otherListBuilderClassName
    get() = ParameterizedTypeName.get(className("ListBuilder"), TypeVariableName("C2"))

val Type.bodyLambdaTypeName
    get() = LambdaTypeName.get(builderClassName, emptyList(), ClassName("", "Unit"))

val Type.otherBodyLambdaTypeName
    get() = LambdaTypeName.get(otherBuilderClassName, emptyList(), ClassName("", "Unit"))

val Type.listBodyLambdaTypeName
    get() = LambdaTypeName.get(listBuilderClassName, emptyList(), ClassName("", "Unit"))

val Type.otherListBodyLambdaTypeName
    get() = LambdaTypeName.get(otherListBuilderClassName, emptyList(), ClassName("", "Unit"))

val Field.singularName
    get() = name.removeSuffix("s")