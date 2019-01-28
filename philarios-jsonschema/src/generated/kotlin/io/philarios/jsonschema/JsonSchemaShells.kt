package io.philarios.jsonschema

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.String
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class JsonSchemaShell(
        var id: String? = null,
        var schema: String? = null,
        var title: String? = null,
        var description: String? = null,
        var definitions: Map<String, Scaffold<JsonSchema>> = emptyMap()
) : Scaffold<JsonSchema> {
    override suspend fun resolve(registry: Registry): JsonSchema {
        coroutineScope {
        	definitions.forEach { it.value.let { launch { it.resolve(registry) } } }
        }
        val value = JsonSchema(id,schema,title,description,definitions.map { Pair(it.key, it.value.let { it.resolve(registry) }) }.toMap())
        return value
    }
}
