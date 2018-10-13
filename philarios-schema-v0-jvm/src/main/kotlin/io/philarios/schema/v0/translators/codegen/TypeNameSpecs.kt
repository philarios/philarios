package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.*
import io.philarios.schema.v0.*

val dslBuilderClassName
    get() = ClassName.bestGuess(DslBuilder::class.qualifiedName!!)

val scaffoldClassName
    get() = ClassName.bestGuess(Scaffold::class.qualifiedName!!)

val wrapperClassName
    get() = ClassName.bestGuess(Wrapper::class.qualifiedName!!)

val Type.typeName: TypeName
    get() = when (this) {
        is Struct -> ClassName(pkg ?: "", name)
        is Union -> ClassName(pkg ?: "", name)
        is EnumType -> ClassName(pkg ?: "", name)
        is RefType -> ClassName(pkg ?: "", name)
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
    }

val Type.nullableClassName
    get() = className.asNullable()

fun Type.className(suffix: String) = when (this) {
    is Struct ->  ClassName(pkg ?: "", "$name$suffix")
    is Union ->  ClassName(pkg ?: "", "$name$suffix")
    is EnumType ->  ClassName(pkg ?: "", "$name$suffix")
    is RefType ->  ClassName(pkg ?: "", "$name$suffix")
    else -> className
}

val Type.specClassName
    get() = ParameterizedTypeName.get(className("Spec"), TypeVariableName("C", KModifier.IN))

val Type.shellClassName
    get() = className("Shell")

val Type.parameterizedScaffoldClassName: ParameterizedTypeName
    get() = ParameterizedTypeName.get(io.philarios.schema.v0.translators.codegen.scaffoldClassName, className)

val ClassName.parameterizedScaffoldClassName: ParameterizedTypeName
    get() = ParameterizedTypeName.get(io.philarios.schema.v0.translators.codegen.scaffoldClassName, this)

val Type.refClassName
    get() = className("Ref")

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
    get() = when {
        name.endsWith("ies") -> name.removeSuffix("ies").plus("y")
        else -> name.removeSuffix("s")
    }