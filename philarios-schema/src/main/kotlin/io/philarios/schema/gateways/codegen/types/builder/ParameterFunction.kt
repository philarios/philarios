package io.philarios.schema.gateways.codegen.types.builder

import io.philarios.schema.Field
import io.philarios.schema.Struct
import io.philarios.schema.Type

sealed class ParameterFunction {
    data class SetParameterFunctionWithBody(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithSpec(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithRef(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()
    data class SetParameterFunctionWithWrapper(val type: Struct, val field: Field, val fieldType: Type): ParameterFunction()

    data class AddParameterFunctionWithBody(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithSpec(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithRef(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddParameterFunctionWithWrapper(val type: Struct, val field: Field, val listType: Type): ParameterFunction()
    data class AddAllParameterFunctionWithWrapper(val type: Struct, val field: Field): ParameterFunction()

    data class PutKeyValueParameterFunctionWithBody(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithSpec(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithRef(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutKeyValueParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutPairParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class PutAllParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()

    data class AddPutKeyValueParameterFunctionWithBody(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddPutKeyValueParameterFunctionWithSpec(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddPutKeyValueParameterFunctionWithRef(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddPutKeyValueParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddPutPairParameterFunctionWithWrapper(val type: Struct, val field: Field, val keyType: Type, val valueType: Type): ParameterFunction()
    data class AddPutAllParameterFunctionWithWrapper(val type: Struct, val field: Field): ParameterFunction()
}