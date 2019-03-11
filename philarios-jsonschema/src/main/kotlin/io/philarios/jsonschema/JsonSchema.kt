package io.philarios.jsonschema

import kotlin.String
import kotlin.collections.Map

sealed class JsonSchema
data class JsonSchemaBoolean(val value: Boolean) : JsonSchema()
data class JsonSchemaObject(
        val `$id`: String?,
        val `$schema`: String?,
        val `$ref`: String?,
        val `$comment`: String?,
        val title: String?,
        val description: String?,
        val default: Any?,
        val readOnly: Boolean?,
        val examples: List<Any>?, // TODO what does the items: true mean in the schema?
        val multipleOf: Double?,
        val maximum: Double?,
        val exclusiveMaximum: Boolean?,
        val minimum: Double?,
        val exclusiveMinimum: Boolean?,
        val maxLength: Int?,
        val minLength: Int?,
        val pattern: String?,
        val additionalItems: JsonSchema?,
        val items: Items?,
        val maxItems: Int?,
        val minItems: Int?,
        val uniqueItems: Boolean?,
        val contains: JsonSchema?,
        val maxProperties: Int?,
        val minProperties: Int?,
        val required: List<String>?,
        val additionalProperties: JsonSchema?,
        val definitions: Map<String, JsonSchema>?,
        val properties: Map<String, JsonSchema>?,
        val patternProperties: Map<String, JsonSchema>?,
        val dependencies: Map<String, Dependencies>?,
        val propertyNames: JsonSchema?,
        val const: Boolean,
        val enum: List<String>?,
        val type: Type?,
        val format: String?,
        val contentMediaType: String?,
        val contentEncoding: String?,
        val `if`: JsonSchema?,
        val then: JsonSchema?,
        val `else`: JsonSchema?,
        val allOf: List<JsonSchema>?,
        val anyOf: List<JsonSchema>?,
        val oneOf: List<JsonSchema>?,
        val not: JsonSchema?
) : JsonSchema()

sealed class Items
data class ItemsJsonSchema(val value: JsonSchema) : Items()
data class ItemsJsonSchemaArray(val value: List<JsonSchema>) : Items()

sealed class Dependencies
data class DependenciesJsonSchema(val value: JsonSchema) : Dependencies()
data class DependenciesStringArray(val value: List<String>) : Dependencies()

sealed class Type
data class TypeSimpleType(val value: SimpleType) : Type()
data class TypeSimpleTypeArray(val value: List<SimpleType>) : Type()

enum class SimpleType {
    array, boolean, integer, `null`, number, `object`, string
}