package io.philarios.jsonschema

import io.philarios.core.Scaffold
import kotlin.String

class JsonSchemaRef(key: String) : Scaffold<JsonSchema> by io.philarios.core.RegistryRef(io.philarios.jsonschema.JsonSchema::class, key)
