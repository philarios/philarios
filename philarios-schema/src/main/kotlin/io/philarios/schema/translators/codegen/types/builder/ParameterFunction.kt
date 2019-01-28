package io.philarios.schema.translators.codegen.types.builder

import io.philarios.schema.Field
import io.philarios.schema.Struct
import io.philarios.schema.Type

sealed class ParameterFunction {
    data class SetParameterFunction(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithWrapper(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithBody(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithSpec(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithRef(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class AddParameterFunction(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithWrapper(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithBody(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithSpec(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithRef(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class PutKeyValueParameterFunction(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithBody(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithSpec(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithRef(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutPairParameterFunction(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddAllParameterFunction(val type: Struct, val field: Field): ParameterFunction()
    data class AddAllParameterFunctionWithWrapper(val type: Struct, val field: Field): ParameterFunction()
}