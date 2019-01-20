package io.philarios.schema.v0.translators.codegen.builders

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Spec
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements


object SpecTypeBuilder {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructSpecTypeBuilder.build(type)
            is Union -> UnionSpecTypeBuilder.build(type)
            else -> emptyList()
        }
    }

}

private object StructSpecTypeBuilder {

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type)).mapNotNull { it }
    }

    private fun buildOne(type: Struct): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildDataClass(type)
    }

    private fun buildDataClass(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.specClassName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addSuperinterface(ParameterizedTypeName.get(Spec::class.className, TypeVariableName("C"), type.className))
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
                        .returns(type.scaffoldClassName)
                        .addStatements(
                                Statement("val builder = %T(context)", listOf(type.shellBuilderClassName)),
                                Statement("builder.apply(body)"),
                                Statement("return builder.shell")
                        )
                        .build())
                .build()
    }

}

private object UnionSpecTypeBuilder {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructSpecTypeBuilder.build(it) }
    }

}