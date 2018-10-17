package io.philarios.schema.v0.translators.codegen.builders.builder

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union

class BuilderTypeBuilder(
        parameterFunctionResolver: ParameterFunctionResolver,
        structWithParametersBuilderTypeBuilder: StructWithParametersBuilderTypeBuilder
) {

    private val structBuilder = StructBuilderTypeBuilder(parameterFunctionResolver, structWithParametersBuilderTypeBuilder)
    private val unionBuilder = UnionBuilderTypeBuilder(structBuilder)

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Union -> unionBuilder.build(type)
            is Struct -> structBuilder.build(type)
            else -> emptyList()
        }
    }

}

private class UnionBuilderTypeBuilder(private val structBuilder: StructBuilderTypeBuilder) {

    fun build(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { structBuilder.build(it) }
    }

}

private class StructBuilderTypeBuilder(
        private val parameterFunctionResolver: ParameterFunctionResolver,
        private val structWithParametersBuilderTypeBuilder: StructWithParametersBuilderTypeBuilder
) {

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

