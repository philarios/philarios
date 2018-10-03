package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.RefType
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements


object TemplateTypeSpec {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructTemplateTypeSpec.build(type)
            is Union -> UnionTemplateTypeSpec.build(type)
            else -> emptyList()
        }
    }

}

private object StructTemplateTypeSpec {

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
        return TypeSpec.classBuilder(type.templateClassName.rawType)
                .addSuperinterface(type.builderSuperinterface)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addProperty(PropertySpec
                        .builder("spec", type.specClassName)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("spec")
                        .build())
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("context")
                        .build())
                .primaryConstructor(FunSpec
                        .constructorBuilder()
                        .addParameter(ParameterSpec
                                .builder("spec", type.specClassName)
                                .build())
                        .addParameter(contextParameterSpec)
                        .build())
                .addFunction(FunSpec
                        .constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .addParameter(contextParameterSpec)
                        .callThisConstructor("${type.specClassName}(body)", "context")
                        .build())
                .addFunction(FunSpec.builder("scaffold")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(type.parameterizedScaffoldClassName)
                        .addStatement("return %T(%T)", ClassName.bestGuess(Wrapper::class.qualifiedName!!), type.className)
                        .build())
                .build()
    }

    private fun buildDataClass(type: Struct): TypeSpec {
        val className = type.className
        return TypeSpec.classBuilder(type.templateClassName.rawType)
                .addSuperinterface(type.builderSuperinterface)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addProperty(PropertySpec
                        .builder("spec", type.specClassName)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("spec")
                        .build())
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("context")
                        .build())
                .primaryConstructor(FunSpec
                        .constructorBuilder()
                        .addParameter(ParameterSpec
                                .builder("spec", type.specClassName)
                                .build())
                        .addParameter(contextParameterSpec)
                        .build())
                .addFunction(FunSpec
                        .constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .addParameter(contextParameterSpec)
                        .callThisConstructor("${type.specClassName}(body)", "context")
                        .build())
                .addFunction(FunSpec.builder("scaffold")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(type.parameterizedScaffoldClassName)
                        .addStatements(
                                Statement("val builder = %T(context)", listOf(type.builderClassName)),
                                Statement("builder.apply(spec.body)"),
                                Statement("return builder.shell")
                        )
                        .build())
                .build()
    }

}

private object UnionTemplateTypeSpec {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructTemplateTypeSpec.build(it) }
    }

}