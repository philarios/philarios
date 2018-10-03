package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DelegatingRegistry
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Translator
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.*


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
                .addProperty(PropertySpec
                        .builder("registry", ClassName.bestGuess(Registry::class.qualifiedName!!))
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("registry")
                        .build())
                .primaryConstructor(FunSpec
                        .constructorBuilder()
                        .addParameter(ParameterSpec
                                .builder("spec", type.specClassName)
                                .build())
                        .addParameter(ParameterSpec
                                .builder("registry", ClassName.bestGuess(Registry::class.qualifiedName!!))
                                .defaultValue("io.philarios.core.v0.emptyRegistry()") // TODO how to do function imports?
                                .build())
                        .build())
                .addFunction(FunSpec
                        .constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .addParameter(ParameterSpec
                                .builder("registry", ClassName.bestGuess(Registry::class.qualifiedName!!))
                                .defaultValue("io.philarios.core.v0.emptyRegistry()") // TODO how to do function imports?
                                .build())
                        .callThisConstructor("${type.specClassName}(body)", "registry")
                        .build())
                .addFunction(FunSpec
                        .builder("translate")
                        .addModifiers(KModifier.OVERRIDE)
                        .addModifiers(KModifier.SUSPEND)
                        .addParameter(contextParameterSpec)
                        .returns(type.className)
                        .addStatements(
                                Statement("val builder = %T(spec, context)", listOf(type.templateClassName)),
                                Statement("val scaffold = builder.scaffold()"),
                                Statement("return scaffold.resolve(registry)")
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