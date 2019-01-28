package io.philarios.jsonschema

import io.philarios.core.Scaffold
import io.philarios.core.Spec

class JsonSchemaSpec<in C>(internal val body: JsonSchemaBuilder<C>.() -> Unit) : Spec<C, JsonSchema> {
    override fun connect(context: C): Scaffold<JsonSchema> {
        val builder = JsonSchemaShellBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
