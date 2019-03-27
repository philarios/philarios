package io.philarios.jsonschema.entities

import io.philarios.schema.*
import io.philarios.schema.Type

internal fun jsonSchemaSchema(pkg: String, schemaName: String) = SchemaSpec<JsonSchemaObject> {
    pkg(pkg)
    name(schemaName)

    type(context.type(schemaName))

    context.definitions
            ?.map { it.value.type(it.key.capitalize()) }
            ?.forEach {
                type(it)
            }
}

private fun JsonSchema.type(name: String? = null): TypeSpec<JsonSchemaObject, Type> = when (this) {
    is JsonSchemaBoolean -> throw unsupportedBooleanSchema
    is JsonSchemaObject -> when (type) {
        is TypeSimpleType -> when (type.value) {
            SimpleType.`null` -> throw unsupportedNullType
            SimpleType.boolean -> BooleanTypeSpec()
            SimpleType.integer -> IntTypeSpec()
            SimpleType.number -> DoubleTypeSpec()
            SimpleType.string -> StringTypeSpec()
            SimpleType.array -> when {
                items is ItemsJsonSchema -> ListTypeSpec {
                    type(items.value.type())
                }
                else -> throw unsupportedArrayType
            }
            SimpleType.`object` -> when {
                additionalProperties != null && additionalProperties is JsonSchemaObject -> MapTypeSpec {
                    keyType(StringType)
                    valueType(additionalProperties.type())
                }
                properties != null && (name != null || title != null) -> StructSpec {
                    name(name ?: title!!)
                    properties.forEach {
                        field {
                            name(it.key)
                            type(OptionTypeSpec {
                                type(it.value.type())
                            })
                        }
                    }
                }
                oneOf != null && title != null -> UnionSpec {
                    name(title)
                    oneOf
                            .map { it.type() }
                            .filter { it is StructSpec }
                            .map { it as StructSpec }
                            .forEach {
                                shape(it)
                            }
                }
                else -> throw unsupportedObjectType
            }
        }
        is TypeSimpleTypeArray -> throw unsupportedMultiType
        null -> when {
            `$ref` != null -> RefTypeSpec {
                name(`$ref`.substringAfter("#/definitions/").capitalize())
            }
            enum != null && name != null -> EnumTypeSpec<JsonSchemaObject> {
                name(name)
                values(enum)
            }
            oneOf != null && title != null -> UnionSpec<JsonSchemaObject> {
                name(title)
                oneOf
                        .map { it.type() }
                        .filter { it is StructSpec }
                        .map { it as StructSpec }
                        .forEach {
                            shape(it)
                        }
            }
            else -> throw unsupportedFields
        }
    }
}

internal val unsupportedBooleanSchema =
        UnsupportedJsonSchemaException("Boolean schemas are currently not supported")

internal val unsupportedNullType =
        UnsupportedJsonSchemaException("Null types are currently not supported")

internal val unsupportedArrayType =
        UnsupportedJsonSchemaException("Array types without items are currently not supported")

internal val unsupportedObjectType =
        UnsupportedJsonSchemaException(
                "Object types without additionalProperties, properties or oneOf are currently not supported")

internal val unsupportedMultiType =
        UnsupportedJsonSchemaException("Schemas with more than one type are currently not supported")

internal val unsupportedFields =
        UnsupportedJsonSchemaException(
                "Schemas without the properties type, \$ref, enum, oneOf are currently not supported")

internal class UnsupportedJsonSchemaException(message: String) : Exception(message)
