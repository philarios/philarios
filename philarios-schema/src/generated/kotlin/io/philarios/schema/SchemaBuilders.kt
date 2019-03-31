package io.philarios.schema

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
interface SchemaBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: SchemaBuilder<C>.() -> Unit)

    fun include(spec: SchemaSpec<C>)

    fun <C2> include(context: C2, body: SchemaBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SchemaSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>)
}

@DslBuilder
interface StructBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun field(body: FieldBuilder<C>.() -> Unit)

    fun field(spec: FieldSpec<C>)

    fun field(ref: FieldRef)

    fun field(value: Field)

    fun fields(fields: List<Field>)

    fun include(body: StructBuilder<C>.() -> Unit)

    fun include(spec: StructSpec<C>)

    fun <C2> include(context: C2, body: StructBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StructSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StructBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StructSpec<C2>)
}

@DslBuilder
interface UnionBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun shape(body: StructBuilder<C>.() -> Unit)

    fun shape(spec: StructSpec<C>)

    fun shape(ref: StructRef)

    fun shape(value: Struct)

    fun shapes(shapes: List<Struct>)

    fun include(body: UnionBuilder<C>.() -> Unit)

    fun include(spec: UnionSpec<C>)

    fun <C2> include(context: C2, body: UnionBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: UnionSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: UnionBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: UnionSpec<C2>)
}

@DslBuilder
interface EnumTypeBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun value(value: String)

    fun values(values: List<String>)

    fun include(body: EnumTypeBuilder<C>.() -> Unit)

    fun include(spec: EnumTypeSpec<C>)

    fun <C2> include(context: C2, body: EnumTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: EnumTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: EnumTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: EnumTypeSpec<C2>)
}

@DslBuilder
interface RefTypeBuilder<out C> {
    val context: C

    fun pkg(value: String)

    fun name(value: String)

    fun include(body: RefTypeBuilder<C>.() -> Unit)

    fun include(spec: RefTypeSpec<C>)

    fun <C2> include(context: C2, body: RefTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RefTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RefTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RefTypeSpec<C2>)
}

@DslBuilder
interface OptionTypeBuilder<out C> {
    val context: C

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: OptionTypeBuilder<C>.() -> Unit)

    fun include(spec: OptionTypeSpec<C>)

    fun <C2> include(context: C2, body: OptionTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: OptionTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: OptionTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: OptionTypeSpec<C2>)
}

@DslBuilder
interface ListTypeBuilder<out C> {
    val context: C

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: ListTypeBuilder<C>.() -> Unit)

    fun include(spec: ListTypeSpec<C>)

    fun <C2> include(context: C2, body: ListTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ListTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ListTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ListTypeSpec<C2>)
}

@DslBuilder
interface MapTypeBuilder<out C> {
    val context: C

    fun <T : Type> keyType(spec: TypeSpec<C, T>)

    fun <T : Type> keyType(ref: TypeRef<T>)

    fun <T : Type> keyType(value: T)

    fun <T : Type> valueType(spec: TypeSpec<C, T>)

    fun <T : Type> valueType(ref: TypeRef<T>)

    fun <T : Type> valueType(value: T)

    fun include(body: MapTypeBuilder<C>.() -> Unit)

    fun include(spec: MapTypeSpec<C>)

    fun <C2> include(context: C2, body: MapTypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MapTypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MapTypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MapTypeSpec<C2>)
}

@DslBuilder
interface FieldBuilder<out C> {
    val context: C

    fun name(value: String)

    fun key(value: Boolean)

    fun singularName(value: String)

    fun <T : Type> type(spec: TypeSpec<C, T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)

    fun include(body: FieldBuilder<C>.() -> Unit)

    fun include(spec: FieldSpec<C>)

    fun <C2> include(context: C2, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FieldSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>)
}
