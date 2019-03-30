package io.philarios.schema.entities.codegen.types.builder

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.RefType
import io.philarios.schema.Struct
import io.philarios.schema.Type

typealias StructBuilderTypeSpec = Struct.(typeRefs: Map<RefType, Type>) -> TypeSpec?

typealias StructParameterBuilderTypeSpec = Struct.(parameterFunctions: List<ParameterFunction>) -> TypeSpec

fun createStructBuilderTypeSpec(
        structParameterBuilderTypeSpec: StructParameterBuilderTypeSpec
): StructBuilderTypeSpec = {
    fun Struct.dataClassBuilderTypeSpec(typeRefs: Map<RefType, Type>): TypeSpec {
        val parameterFunctionResolver = ParameterFunctionResolver(typeRefs)
        val parameterFunctions = parameterFunctionResolver.parameterFunctions(this)
        return structParameterBuilderTypeSpec.invoke(this, parameterFunctions)
    }

    fun Struct.builderTypeSpec(typeRefs: Map<RefType, Type>) = when {
        fields.isEmpty() -> null
        else -> dataClassBuilderTypeSpec(typeRefs)
    }

    this.builderTypeSpec(it)
}