package io.philarios.codegen.v0

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Spec
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union


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
        return TypeSpec.classBuilder(type.specClassName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addModifiers(KModifier.OPEN)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(Spec::class.qualifiedName!!), type.builderClassName, type.className))
                .addProperty(PropertySpec
                        .builder("body", type.bodyLambdaTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("body")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(type.bodyParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("body")
                        .addModifiers(KModifier.OVERRIDE)
                        .receiver(type.builderClassName)
                        .addStatement("this@%T.body.invoke(this)", type.specClassName.rawType)
                        .build())
                .build()
    }

}

private object UnionSpecTypeSpec {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructSpecTypeSpec.build(it) }
    }

}