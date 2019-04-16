// The builder interfaces needed to create type-safe specs.
//
// The specs and builders are located one layer below the model. While they need to reference the model classes
// for obvious reasons, they should still be as un-opinionated as possible and should not depend on any
// implementation details. This allows you to write specs without depending on how the specs are actually
// materialized.
//
// It is inevitable to take an at least somewhat opinionated approach while designing interfaces and some of the
// decisions reflect this. However, since comments or concerns are always welcome, please feel free to open an
// issue in the project's repository.
package io.philarios.schema

import io.philarios.core.Builder
import io.philarios.core.DslBuilder
import io.philarios.core.Spec
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

class SchemaSpec<C>(override val body: SchemaBuilder<C>.() -> Unit) : Spec<SchemaBuilder<C>>

@DslBuilder
interface SchemaBuilder<C> : Builder<SchemaSpec<C>, SchemaBuilder<C>> {
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

class SchemaRef(internal val key: String)

sealed class TypeSpec<in C, out T : Type>

class StructSpec<C>(override val body: StructBuilder<C>.() -> Unit) : TypeSpec<C, Struct>(),
        Spec<StructBuilder<C>>

class UnionSpec<C>(override val body: UnionBuilder<C>.() -> Unit) : TypeSpec<C, Union>(),
        Spec<UnionBuilder<C>>

class EnumTypeSpec<C>(override val body: EnumTypeBuilder<C>.() -> Unit) : TypeSpec<C, EnumType>(),
        Spec<EnumTypeBuilder<C>>

class RefTypeSpec<C>(override val body: RefTypeBuilder<C>.() -> Unit) : TypeSpec<C, RefType>(),
        Spec<RefTypeBuilder<C>>

class OptionTypeSpec<C>(override val body: OptionTypeBuilder<C>.() -> Unit) : TypeSpec<C, OptionType>(),
        Spec<OptionTypeBuilder<C>>

class ListTypeSpec<C>(override val body: ListTypeBuilder<C>.() -> Unit) : TypeSpec<C, ListType>(),
        Spec<ListTypeBuilder<C>>

class MapTypeSpec<C>(override val body: MapTypeBuilder<C>.() -> Unit) : TypeSpec<C, MapType>(),
        Spec<MapTypeBuilder<C>>

class BooleanTypeSpec<C> : TypeSpec<C, BooleanType>()

class DoubleTypeSpec<C> : TypeSpec<C, DoubleType>()

class FloatTypeSpec<C> : TypeSpec<C, FloatType>()

class LongTypeSpec<C> : TypeSpec<C, LongType>()

class IntTypeSpec<C> : TypeSpec<C, IntType>()

class ShortTypeSpec<C> : TypeSpec<C, ShortType>()

class ByteTypeSpec<C> : TypeSpec<C, ByteType>()

class CharacterTypeSpec<C> : TypeSpec<C, CharacterType>()

class StringTypeSpec<C> : TypeSpec<C, StringType>()

class AnyTypeSpec<C> : TypeSpec<C, AnyType>()

@DslBuilder
interface StructBuilder<C> : Builder<StructSpec<C>, StructBuilder<C>> {
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
interface UnionBuilder<C> : Builder<UnionSpec<C>, UnionBuilder<C>> {
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
interface EnumTypeBuilder<C> : Builder<EnumTypeSpec<C>, EnumTypeBuilder<C>> {
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
interface RefTypeBuilder<C> : Builder<RefTypeSpec<C>, RefTypeBuilder<C>> {
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
interface OptionTypeBuilder<C> : Builder<OptionTypeSpec<C>, OptionTypeBuilder<C>> {
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
interface ListTypeBuilder<C> : Builder<ListTypeSpec<C>, ListTypeBuilder<C>> {
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
interface MapTypeBuilder<C> : Builder<MapTypeSpec<C>, MapTypeBuilder<C>> {
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

sealed class TypeRef<T : Type> {
    internal abstract val key: String
}

class StructRef(override val key: String) : TypeRef<Struct>()

class UnionRef(override val key: String) : TypeRef<Union>()

class EnumTypeRef(override val key: String) : TypeRef<EnumType>()

class RefTypeRef(override val key: String) : TypeRef<RefType>()

class OptionTypeRef(override val key: String) : TypeRef<OptionType>()

class ListTypeRef(override val key: String) : TypeRef<ListType>()

class MapTypeRef(override val key: String) : TypeRef<MapType>()

class FieldSpec<C>(override val body: FieldBuilder<C>.() -> Unit) : Spec<FieldBuilder<C>>

@DslBuilder
interface FieldBuilder<C> : Builder<FieldSpec<C>, FieldBuilder<C>> {
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

class FieldRef(internal val key: String)
