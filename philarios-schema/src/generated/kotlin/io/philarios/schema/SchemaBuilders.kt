package io.philarios.schema

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
interface SchemaBuilder<out C> {
    val context: C

    fun pkg(pkg: String)

    fun name(name: String)

    fun type(spec: StructSpec<C>)

    fun type(ref: StructRef)

    fun type(spec: UnionSpec<C>)

    fun type(ref: UnionRef)

    fun type(spec: EnumTypeSpec<C>)

    fun type(ref: EnumTypeRef)

    fun type(spec: RefTypeSpec<C>)

    fun type(ref: RefTypeRef)

    fun type(spec: OptionTypeSpec<C>)

    fun type(ref: OptionTypeRef)

    fun type(spec: ListTypeSpec<C>)

    fun type(ref: ListTypeRef)

    fun type(spec: MapTypeSpec<C>)

    fun type(ref: MapTypeRef)

    fun type(type: BooleanType)

    fun type(type: DoubleType)

    fun type(type: FloatType)

    fun type(type: LongType)

    fun type(type: IntType)

    fun type(type: ShortType)

    fun type(type: ByteType)

    fun type(type: CharacterType)

    fun type(type: StringType)

    fun type(type: AnyType)

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

    fun pkg(pkg: String)

    fun name(name: String)

    fun field(body: FieldBuilder<C>.() -> Unit)

    fun field(spec: FieldSpec<C>)

    fun field(ref: FieldRef)

    fun field(field: Field)

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

    fun pkg(pkg: String)

    fun name(name: String)

    fun shape(body: StructBuilder<C>.() -> Unit)

    fun shape(spec: StructSpec<C>)

    fun shape(ref: StructRef)

    fun shape(shape: Struct)

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

    fun pkg(pkg: String)

    fun name(name: String)

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

    fun pkg(pkg: String)

    fun name(name: String)

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

    fun type(spec: StructSpec<C>)

    fun type(ref: StructRef)

    fun type(spec: UnionSpec<C>)

    fun type(ref: UnionRef)

    fun type(spec: EnumTypeSpec<C>)

    fun type(ref: EnumTypeRef)

    fun type(spec: RefTypeSpec<C>)

    fun type(ref: RefTypeRef)

    fun type(spec: OptionTypeSpec<C>)

    fun type(ref: OptionTypeRef)

    fun type(spec: ListTypeSpec<C>)

    fun type(ref: ListTypeRef)

    fun type(spec: MapTypeSpec<C>)

    fun type(ref: MapTypeRef)

    fun type(type: BooleanType)

    fun type(type: DoubleType)

    fun type(type: FloatType)

    fun type(type: LongType)

    fun type(type: IntType)

    fun type(type: ShortType)

    fun type(type: ByteType)

    fun type(type: CharacterType)

    fun type(type: StringType)

    fun type(type: AnyType)

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

    fun type(spec: StructSpec<C>)

    fun type(ref: StructRef)

    fun type(spec: UnionSpec<C>)

    fun type(ref: UnionRef)

    fun type(spec: EnumTypeSpec<C>)

    fun type(ref: EnumTypeRef)

    fun type(spec: RefTypeSpec<C>)

    fun type(ref: RefTypeRef)

    fun type(spec: OptionTypeSpec<C>)

    fun type(ref: OptionTypeRef)

    fun type(spec: ListTypeSpec<C>)

    fun type(ref: ListTypeRef)

    fun type(spec: MapTypeSpec<C>)

    fun type(ref: MapTypeRef)

    fun type(type: BooleanType)

    fun type(type: DoubleType)

    fun type(type: FloatType)

    fun type(type: LongType)

    fun type(type: IntType)

    fun type(type: ShortType)

    fun type(type: ByteType)

    fun type(type: CharacterType)

    fun type(type: StringType)

    fun type(type: AnyType)

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

    fun keyType(spec: StructSpec<C>)

    fun keyType(ref: StructRef)

    fun keyType(spec: UnionSpec<C>)

    fun keyType(ref: UnionRef)

    fun keyType(spec: EnumTypeSpec<C>)

    fun keyType(ref: EnumTypeRef)

    fun keyType(spec: RefTypeSpec<C>)

    fun keyType(ref: RefTypeRef)

    fun keyType(spec: OptionTypeSpec<C>)

    fun keyType(ref: OptionTypeRef)

    fun keyType(spec: ListTypeSpec<C>)

    fun keyType(ref: ListTypeRef)

    fun keyType(spec: MapTypeSpec<C>)

    fun keyType(ref: MapTypeRef)

    fun keyType(keyType: BooleanType)

    fun keyType(keyType: DoubleType)

    fun keyType(keyType: FloatType)

    fun keyType(keyType: LongType)

    fun keyType(keyType: IntType)

    fun keyType(keyType: ShortType)

    fun keyType(keyType: ByteType)

    fun keyType(keyType: CharacterType)

    fun keyType(keyType: StringType)

    fun keyType(keyType: AnyType)

    fun valueType(spec: StructSpec<C>)

    fun valueType(ref: StructRef)

    fun valueType(spec: UnionSpec<C>)

    fun valueType(ref: UnionRef)

    fun valueType(spec: EnumTypeSpec<C>)

    fun valueType(ref: EnumTypeRef)

    fun valueType(spec: RefTypeSpec<C>)

    fun valueType(ref: RefTypeRef)

    fun valueType(spec: OptionTypeSpec<C>)

    fun valueType(ref: OptionTypeRef)

    fun valueType(spec: ListTypeSpec<C>)

    fun valueType(ref: ListTypeRef)

    fun valueType(spec: MapTypeSpec<C>)

    fun valueType(ref: MapTypeRef)

    fun valueType(valueType: BooleanType)

    fun valueType(valueType: DoubleType)

    fun valueType(valueType: FloatType)

    fun valueType(valueType: LongType)

    fun valueType(valueType: IntType)

    fun valueType(valueType: ShortType)

    fun valueType(valueType: ByteType)

    fun valueType(valueType: CharacterType)

    fun valueType(valueType: StringType)

    fun valueType(valueType: AnyType)

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

    fun name(name: String)

    fun key(key: Boolean)

    fun type(spec: StructSpec<C>)

    fun type(ref: StructRef)

    fun type(spec: UnionSpec<C>)

    fun type(ref: UnionRef)

    fun type(spec: EnumTypeSpec<C>)

    fun type(ref: EnumTypeRef)

    fun type(spec: RefTypeSpec<C>)

    fun type(ref: RefTypeRef)

    fun type(spec: OptionTypeSpec<C>)

    fun type(ref: OptionTypeRef)

    fun type(spec: ListTypeSpec<C>)

    fun type(ref: ListTypeRef)

    fun type(spec: MapTypeSpec<C>)

    fun type(ref: MapTypeRef)

    fun type(type: BooleanType)

    fun type(type: DoubleType)

    fun type(type: FloatType)

    fun type(type: LongType)

    fun type(type: IntType)

    fun type(type: ShortType)

    fun type(type: ByteType)

    fun type(type: CharacterType)

    fun type(type: StringType)

    fun type(type: AnyType)

    fun include(body: FieldBuilder<C>.() -> Unit)

    fun include(spec: FieldSpec<C>)

    fun <C2> include(context: C2, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FieldSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>)
}
