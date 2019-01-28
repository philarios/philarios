package io.philarios.jsonschema

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.String
import kotlin.collections.Iterable

@DslBuilder
internal class JsonSchemaShellBuilder<out C>(override val context: C, internal var shell: JsonSchemaShell = JsonSchemaShell()) : JsonSchemaBuilder<C> {
    override fun id(id: String) {
        shell = shell.copy(id = id)
    }

    override fun schema(schema: String) {
        shell = shell.copy(schema = schema)
    }

    override fun title(title: String) {
        shell = shell.copy(title = title)
    }

    override fun description(description: String) {
        shell = shell.copy(description = description)
    }

    override fun definitions(key: String, body: JsonSchemaBuilder<C>.() -> Unit) {
        shell = shell.copy(definitions = shell.definitions.orEmpty() + Pair(key,JsonSchemaSpec<C>(body).connect(context)))
    }

    override fun definitions(key: String, spec: JsonSchemaSpec<C>) {
        shell = shell.copy(definitions = shell.definitions.orEmpty() + Pair(key,spec.connect(context)))
    }

    override fun definitions(key: String, ref: JsonSchemaRef) {
        shell = shell.copy(definitions = shell.definitions.orEmpty() + Pair(key,ref))
    }

    override fun definitions(key: String, value: JsonSchema) {
        shell = shell.copy(definitions = shell.definitions.orEmpty() + Pair(key,Wrapper(value)))
    }

    override fun include(body: JsonSchemaBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: JsonSchemaSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: JsonSchemaBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: JsonSchemaSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: JsonSchemaBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: JsonSchemaSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): JsonSchemaShellBuilder<C2> = JsonSchemaShellBuilder(context, shell)

    private fun <C2> merge(other: JsonSchemaShellBuilder<C2>) {
        this.shell = other.shell
    }
}
