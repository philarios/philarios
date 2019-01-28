package io.philarios.jsonschema

import io.philarios.core.DslBuilder
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
interface JsonSchemaBuilder<out C> {
    val context: C

    fun id(id: String)

    fun schema(schema: String)

    fun title(title: String)

    fun description(description: String)

    fun definitions(key: String, body: JsonSchemaBuilder<C>.() -> Unit)

    fun definitions(key: String, spec: JsonSchemaSpec<C>)

    fun definitions(key: String, ref: JsonSchemaRef)

    fun definitions(key: String, value: JsonSchema)

    fun include(body: JsonSchemaBuilder<C>.() -> Unit)

    fun include(spec: JsonSchemaSpec<C>)

    fun <C2> include(context: C2, body: JsonSchemaBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: JsonSchemaSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: JsonSchemaBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: JsonSchemaSpec<C2>)
}
