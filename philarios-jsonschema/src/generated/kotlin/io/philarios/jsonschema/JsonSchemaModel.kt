package io.philarios.jsonschema

import kotlin.String
import kotlin.collections.Map

data class JsonSchema(
        val id: String?,
        val schema: String?,
        val title: String?,
        val description: String?,
        val definitions: Map<String, JsonSchema>
)
