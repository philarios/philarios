package io.philarios.schema.translators.codegen.types.builder

import io.philarios.schema.*

class ParameterFunctionResolver(private val typeRefs: Map<RefType, Type>) {
    
    fun parameterFunctions(struct: Struct): List<ParameterFunction> {
        return struct.fields.flatMap { parameterFunctions(struct, it) }
    }

    private fun parameterFunctions(type: Struct, field: Field): List<ParameterFunction> {
        val fieldType = field.type
        return when (fieldType) {
            is Struct -> structParameterFunctions(type, field, fieldType)
            is Union -> unionParameterFunctions(type, field, fieldType)
            is ListType -> listParameterFunctions(type, field, fieldType)
            is MapType -> mapParameterFunctions(type, field, fieldType)
            is RefType -> refTypeParameterFunctions(type, field, fieldType)
            is OptionType -> optionTypeParameterFunctions(type, field, fieldType)
            else -> primitiveParameterFunctions(type, field)
        }
    }

    private fun structParameterFunctions(type: Struct, field: Field, fieldType: Struct): List<ParameterFunction> {
        return if (fieldType.fields.isEmpty()) {
            listOf(
                    ParameterFunction.SetParameterFunction(type, field, fieldType)
            )
        } else {
            listOf(
                    ParameterFunction.SetParameterFunctionWithBody(type, field, fieldType),
                    ParameterFunction.SetParameterFunctionWithSpec(type, field, fieldType),
                    ParameterFunction.SetParameterFunctionWithRef(type, field, fieldType),
                    ParameterFunction.SetParameterFunctionWithWrapper(type, field, fieldType)
            )
        }
    }

    private fun unionParameterFunctions(type: Struct, field: Field, fieldType: Union): List<ParameterFunction> {
        return fieldType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        ParameterFunction.SetParameterFunctionWithWrapper(type, field, it)
                )
            } else {
                listOf(
                        ParameterFunction.SetParameterFunctionWithSpec(type, field, it),
                        ParameterFunction.SetParameterFunctionWithRef(type, field, it)
                )
            }
        }
    }

    private fun listParameterFunctions(type: Struct, field: Field, fieldType: ListType): List<ParameterFunction> {
        val listType = fieldType.type
        return when (listType) {
            is Struct -> structListParameterFunctions(type, field, listType)
            is Union -> unionListParameterFunctions(type, field, listType)
            is ListType -> throw UnsupportedOperationException("Nested lists are not (yet) supported")
            is MapType -> throw UnsupportedOperationException("Nested lists are not (yet) supported")
            is RefType -> refListParameterFunctions(type, field, listType)
            else -> primitiveListParameterFunction(type, field, listType)
        }
    }

    private fun structListParameterFunctions(type: Struct, field: Field, listType: Struct): List<ParameterFunction> {
        return if (listType.fields.isEmpty()) {
            listOf(
                    ParameterFunction.AddParameterFunction(type, field, listType),
                    ParameterFunction.AddAllParameterFunction(type, field)
            )
        } else {
            listOf(
                    ParameterFunction.AddParameterFunctionWithBody(type, field, listType),
                    ParameterFunction.AddParameterFunctionWithSpec(type, field, listType),
                    ParameterFunction.AddParameterFunctionWithRef(type, field, listType),
                    ParameterFunction.AddParameterFunctionWithWrapper(type, field, listType),
                    ParameterFunction.AddAllParameterFunctionWithWrapper(type, field)
            )
        }
    }

    private fun unionListParameterFunctions(type: Struct, field: Field, listType: Union): List<ParameterFunction> {
        return listType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        ParameterFunction.AddParameterFunctionWithWrapper(type, field, it)
                )
            } else {
                listOf(
                        ParameterFunction.AddParameterFunctionWithSpec(type, field, it),
                        ParameterFunction.AddParameterFunctionWithRef(type, field, it)
                )
            }
        }
    }

    private fun refListParameterFunctions(type: Struct, field: Field, listType: RefType): List<ParameterFunction> {
        return parameterFunctions(type, field.copy(type = ListType(typeRefs[listType]!!)))
    }

    private fun primitiveListParameterFunction(type: Struct, field: Field, listType: Type): List<ParameterFunction> {
        return listOf(
                ParameterFunction.AddParameterFunction(type, field, listType),
                ParameterFunction.AddAllParameterFunction(type, field)
        )
    }

    private fun mapParameterFunctions(type: Struct, field: Field, fieldType: MapType): List<ParameterFunction> {
        val keyType = fieldType.keyType
        val valueType = fieldType.valueType
        return when {
            keyType is RefType || valueType is RefType -> refMapParameterFunctions(type, field, keyType, valueType)
            keyType is ListType || valueType is ListType -> throw UnsupportedOperationException("Nested maps are not (yet) supported")
            keyType is MapType || valueType is MapType -> throw UnsupportedOperationException("Nested maps are not (yet) supported")
            keyType is Struct -> throw UnsupportedOperationException("Structs as map keys are not (yet) supported")
            keyType is Union -> throw UnsupportedOperationException("Unions as map keys are not (yet) supported")
            valueType is Struct -> structValueMapParameterFunctions(type, field, keyType, valueType)
            valueType is Union -> unionValueMapParameterFunctions(type, field, keyType, valueType)
            else -> primitiveMapParameterFunctions(type, field, keyType, valueType)
        }
    }

    private fun structValueMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Struct): List<ParameterFunction> {
        return if (valueType.fields.isEmpty()) {
            listOf(
                    ParameterFunction.PutKeyValueParameterFunction(type, field, keyType, valueType),
                    ParameterFunction.PutPairParameterFunction(type, field, keyType, valueType)
            )
        } else {
            listOf(
                    ParameterFunction.PutKeyValueParameterFunctionWithBody(type, field, keyType, valueType),
                    ParameterFunction.PutKeyValueParameterFunctionWithSpec(type, field, keyType, valueType),
                    ParameterFunction.PutKeyValueParameterFunctionWithRef(type, field, keyType, valueType),
                    ParameterFunction.PutKeyValueParameterFunctionWithWrapper(type, field, keyType, valueType)
            )
        }
    }

    private fun unionValueMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Union): List<ParameterFunction> {
        return valueType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        ParameterFunction.PutKeyValueParameterFunction(type, field, keyType, it),
                        ParameterFunction.PutPairParameterFunction(type, field, keyType, it)
                )
            } else {
                listOf(
                        ParameterFunction.PutKeyValueParameterFunctionWithSpec(type, field, keyType, it),
                        ParameterFunction.PutKeyValueParameterFunctionWithRef(type, field, keyType, it)
                )
            }
        }
    }

    private fun refMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Type): List<ParameterFunction> {
        return parameterFunctions(type, Field(field.name, field.key, MapType(
                (keyType as? RefType)?.let { typeRefs[it]!! } ?: keyType,
                (valueType as? RefType)?.let { typeRefs[it]!! } ?: valueType
        )))
    }

    private fun primitiveMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Type): List<ParameterFunction> {
        return listOf(
                ParameterFunction.PutKeyValueParameterFunction(type, field, keyType, valueType),
                ParameterFunction.PutPairParameterFunction(type, field, keyType, valueType),
                ParameterFunction.AddAllParameterFunction(type, field)
        )
    }

    private fun refTypeParameterFunctions(type: Struct, field: Field, fieldType: RefType): List<ParameterFunction> {
        return parameterFunctions(type, field.copy(type = typeRefs[fieldType]!!))
    }

    private fun optionTypeParameterFunctions(type: Struct, field: Field, fieldType: OptionType): List<ParameterFunction> {
        return parameterFunctions(type, field.copy(type = fieldType.type))
    }

    private fun primitiveParameterFunctions(type: Struct, field: Field): List<ParameterFunction> {
        return listOf(
                ParameterFunction.SetParameterFunction(type, field, field.type)
        )
    }
    
}