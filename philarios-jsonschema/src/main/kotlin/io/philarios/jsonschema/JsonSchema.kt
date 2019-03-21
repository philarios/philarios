package io.philarios.jsonschema

import kotlin.String
import kotlin.collections.Map

sealed class JsonSchema
data class JsonSchemaBoolean(val value: Boolean) : JsonSchema()
data class JsonSchemaObject(
        val `$id`: String? = null,
        val `$schema`: String? = null,
        val `$ref`: String? = null,
        val `$comment`: String? = null,
        val title: String? = null,
        val description: String? = null,
        val default: Any? = null,
        val readOnly: Boolean? = null,
        val examples: List<Any>? = null, // TODO what does the items: true mean in the schema?
        val multipleOf: Double? = null,
        val maximum: Double? = null,
        val exclusiveMaximum: Boolean? = null,
        val minimum: Double? = null,
        val exclusiveMinimum: Boolean? = null,
        val maxLength: Int? = null,
        val minLength: Int? = null,
        val pattern: String? = null,
        val additionalItems: JsonSchema? = null,
        val items: Items? = null,
        val maxItems: Int? = null,
        val minItems: Int? = null,
        val uniqueItems: Boolean? = null,
        val contains: JsonSchema? = null,
        val maxProperties: Int? = null,
        val minProperties: Int? = null,
        val required: List<String>? = null,
        val additionalProperties: JsonSchema? = null,
        val definitions: Map<String, JsonSchema>? = null,
        val properties: Map<String, JsonSchema>? = null,
        val patternProperties: Map<String, JsonSchema>? = null,
        val dependencies: Map<String, Dependencies>? = null,
        val propertyNames: JsonSchema? = null,
        val const: Boolean? = null,
        val enum: List<String>? = null,
        val type: Type? = null,
        val format: String? = null,
        val contentMediaType: String? = null,
        val contentEncoding: String? = null,
        val `if`: JsonSchema? = null,
        val then: JsonSchema? = null,
        val `else`: JsonSchema? = null,
        val allOf: List<JsonSchema>? = null,
        val anyOf: List<JsonSchema>? = null,
        val oneOf: List<JsonSchema>? = null,
        val not: JsonSchema? = null
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