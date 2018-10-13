package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements


object SpecTypeSpec {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructSpecTypeSpec.build(type)
            is Union -> UnionSpecTypeSpec.build(type)
            else -> emptyList()
        }
    }

}

private object StructSpecTypeSpec {

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type))
    }

    private fun buildOne(type: Struct): TypeSpec {
        if (type.fields.isEmpty()) {
            return buildObject(type)
        }
        return buildDataClass(type)
    }

    private fun buildObject(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.specClassName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addModifiers(KModifier.OPEN)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(Spec::class.qualifiedName!!), TypeVariableName("C"), type.className))
                .addProperty(PropertySpec
                        .builder("body", type.bodyLambdaTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("body")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("connect")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(contextParameterSpec)
                        .returns(type.parameterizedScaffoldClassName)
                        .addStatement("return %T(%T)", ClassName.bestGuess(Wrapper::class.qualifiedName!!), type.className)
                        .build())
                .build()
    }

    private fun buildDataClass(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.specClassName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addModifiers(KModifier.OPEN)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(Spec::class.qualifiedName!!), TypeVariableName("C"), type.className))
                .addProperty(PropertySpec
                        .builder("body", type.bodyLambdaTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("body")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("connect")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(contextParameterSpec)
                        .returns(type.parameterizedScaffoldClassName)
                        .addStatements(
                                Statement("val builder = %T(context)", listOf(type.builderClassName)),
                                Statement("builder.apply(body)"),
                                Statement("return builder.shell")
                        )
                        .build())
                .build()
    }

}

private object UnionSpecTypeSpec {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructSpecTypeSpec.build(it) }
    }

}