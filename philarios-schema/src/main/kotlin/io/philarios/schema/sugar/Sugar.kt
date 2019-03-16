package io.philarios.schema.sugar

import io.philarios.schema.*

fun <C> SchemaBuilder<C>.struct(name: String, body: StructBuilder<C>.() -> Unit = {}) {
    type(StructSpec {
        name(name)
        apply(body)
    })
}

fun <C> SchemaBuilder<C>.union(name: String, body: UnionBuilder<C>.() -> Unit = {}) {
    type(UnionSpec {
        name(name)
        apply(body)
    })
}

fun <C> UnionBuilder<C>.shape(name: String, body: StructBuilder<C>.() -> Unit = {}) {
    shape {
        name(name)
        apply(body)
    }
}

fun <C> StructBuilder<C>.field(name: String, body: FieldBuilder<C>.() -> Unit = {}) {
    field {
        name(name)
        apply(body)
    }
}

fun <C> StructBuilder<C>.field(name: String, type: Type, body: FieldBuilder<C>.() -> Unit = {}) {
    field(name) {
        type(type)
        apply(body)
    }
}

fun <C, T : Type> StructBuilder<C>.field(name: String, type: TypeSpec<C, T>, body: FieldBuilder<C>.() -> Unit = {}) {
    field(name) {
        type(type)
        apply(body)
    }
}

fun <C, T : Type> list(type: TypeSpec<C, T>) = ListTypeSpec<C> {
    type(type)
}

fun <C> ref(name: String) = RefTypeSpec<C> {
    name(name)
}