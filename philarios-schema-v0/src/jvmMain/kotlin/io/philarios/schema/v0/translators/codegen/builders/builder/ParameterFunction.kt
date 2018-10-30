package io.philarios.schema.v0.translators.codegen.builders.builder

import io.philarios.schema.v0.Field
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type

sealed class ParameterFunction {
    data class SetParameterFunction(public val type: Struct, public val field: Field, public val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithWrapper(public val type: Struct, public val field: Field, public val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithBody(public val type: Struct, public val field: Field, public val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithSpec(public val type: Struct, public val field: Field, public val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithRef(public val type: Struct, public val field: Field, public val fieldType: Type): ParameterFunction()
    data class AddParameterFunction(public val type: Struct, public val field: Field, public val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithWrapper(public val type: Struct, public val field: Field, public val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithBody(public val type: Struct, public val field: Field, public val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithSpec(public val type: Struct, public val field: Field, public val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithRef(public val type: Struct, public val field: Field, public val listType: Type): ParameterFunction()
    data class PutKeyValueParameterFunction(public val type: Struct, public val field: Field, public val keyType: Type, public val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithBody(public val type: Struct, public val field: Field, public val keyType: Type, public val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithSpec(public val type: Struct, public val field: Field, public val keyType: Type, public val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithRef(public val type: Struct, public val field: Field, public val keyType: Type, public val valueType: Type): ParameterFunction()
    data class PutPairParameterFunction(public val type: Struct, public val field: Field, public val keyType: Type, public val valueType: Type): ParameterFunction()
    data class AddAllParameterFunction(public val type: Struct, public val field: Field): ParameterFunction()
    data class AddAllParameterFunctionWithWrapper(public val type: Struct, public val field: Field): ParameterFunction()
}