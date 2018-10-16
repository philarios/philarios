package io.philarios.schema.v0.translators.codegen.builders.builder

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.*

class BuilderTypeBuilder(typeRefs: Map<RefType, Type>) {

    private val unionBuilder = UnionBuilderTypeBuilder(typeRefs)
    private val structBuilder = StructBuilderTypeBuilder(typeRefs)

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Union -> unionBuilder.build(type)
            is Struct -> structBuilder.build(type)
            else -> emptyList()
        }
    }

}

private class UnionBuilderTypeBuilder(typeRefs: Map<RefType, Type>) {

    private val structBuilder = StructBuilderTypeBuilder(typeRefs)

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { structBuilder.build(it) }
    }

}

private class StructBuilderTypeBuilder(typeRefs: Map<RefType, Type>) {

    private val parameterFunctionResolver = ParameterFunctionResolver(typeRefs)

    private val structWithParametersBuilderTypeBuilder = ShellStructWithParametersBuilderTypeBuilder()

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type)).mapNotNull { it }
    }

    private fun buildOne(type: Struct): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildOneWithFields(type)
    }

    private fun buildOneWithFields(type: Struct): TypeSpec {
        val parameterFunctions = parameterFunctionResolver.parameterFunctions(type)
        return structWithParametersBuilderTypeBuilder.buildOneWithParameterFunctions(type, parameterFunctions)
    }
}

