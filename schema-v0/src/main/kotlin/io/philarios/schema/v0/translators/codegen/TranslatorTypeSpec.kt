package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.Translator
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union


object TranslatorTypeSpec {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructTranslatorTypeSpec.build(type)
            is Union -> UnionTranslatorTypeSpec.build(type)
            else -> emptyList()
        }
    }

}

private object StructTranslatorTypeSpec {

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type))
    }

    private fun buildOne(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.translatorClassName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addModifiers(KModifier.OPEN)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(Translator::class.qualifiedName!!), TypeVariableName("C"), type.className))
                .addProperty(PropertySpec
                        .builder("spec", type.specClassName)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("spec")
                        .build())
                .primaryConstructor(FunSpec
                        .constructorBuilder()
                        .addParameter(ParameterSpec
                                .builder("spec", type.specClassName)
                                .build())
                        .build())
                .addFunction(FunSpec
                        .constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .callThisConstructor("${type.specClassName}(body)")
                        .build())
                .addFunction(FunSpec
                        .builder("translate")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter(contextParameterSpec)
                        .returns(type.className)
                        .addStatements(
                                Statement("val builder = %T(context)", listOf(type.builderClassName.rawType)),
                                Statement("val translator = %T(builder, spec)",
                                        listOf(ParameterizedTypeName.get(ClassName.bestGuess(BuilderSpecTranslator::class.qualifiedName!!), TypeVariableName("C"), type.builderClassName, type.className))),
                                Statement("return translator.translate(context)")
                        )
                        .build())
                .build()
    }

}

private object UnionTranslatorTypeSpec {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructTranslatorTypeSpec.build(it) }
    }

}