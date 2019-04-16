package io.philarios.schema

fun SchemaBuilder.enum(name: String, body: EnumTypeBuilder.() -> Unit = {}) {
    type(EnumTypeSpec {
        name(name)
        apply(body)
    })
}

fun SchemaBuilder.struct(name: String, body: StructBuilder.() -> Unit = {}) {
    type(StructSpec {
        name(name)
        apply(body)
    })
}

fun SchemaBuilder.union(name: String, body: UnionBuilder.() -> Unit = {}) {
    type(UnionSpec {
        name(name)
        apply(body)
    })
}

fun UnionBuilder.shape(name: String, body: StructBuilder.() -> Unit = {}) {
    shape {
        name(name)
        apply(body)
    }
}

fun StructBuilder.field(name: String, body: FieldBuilder.() -> Unit = {}) {
    field {
        name(name)
        apply(body)
    }
}

fun StructBuilder.field(name: String, type: Type, body: FieldBuilder.() -> Unit = {}) {
    field(name) {
        type(type)
        apply(body)
    }
}

fun <T : Type> StructBuilder.field(name: String, type: TypeSpec<T>, body: FieldBuilder.() -> Unit = {}) {
    field(name) {
        type(type)
        apply(body)
    }
}

fun <T : Type> option(type: TypeSpec<T>) = OptionTypeSpec {
    type(type)
}

fun option(type: Type) = OptionTypeSpec {
    type(type)
}

fun <T : Type> list(type: TypeSpec<T>) = ListTypeSpec {
    type(type)
}

fun list(type: Type) = ListTypeSpec {
    type(type)
}

fun ref(name: String) = RefTypeSpec {
    name(name)
}