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
import kotlin.collections.List

class SchemaSpec(override val body: SchemaBuilder.() -> Unit) : Spec<SchemaBuilder>

@DslBuilder
interface SchemaBuilder : Builder<SchemaSpec, SchemaBuilder> {
    fun pkg(value: String)

    fun name(value: String)

    fun <T : Type> type(spec: TypeSpec<T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)
}

class SchemaRef(internal val key: String)

sealed class TypeSpec<out T : Type>

class StructSpec(override val body: StructBuilder.() -> Unit) : TypeSpec<Struct>(),
        Spec<StructBuilder>

class UnionSpec(override val body: UnionBuilder.() -> Unit) : TypeSpec<Union>(), Spec<UnionBuilder>

class EnumTypeSpec(override val body: EnumTypeBuilder.() -> Unit) : TypeSpec<EnumType>(),
        Spec<EnumTypeBuilder>

class RefTypeSpec(override val body: RefTypeBuilder.() -> Unit) : TypeSpec<RefType>(),
        Spec<RefTypeBuilder>

class OptionTypeSpec(override val body: OptionTypeBuilder.() -> Unit) : TypeSpec<OptionType>(),
        Spec<OptionTypeBuilder>

class ListTypeSpec(override val body: ListTypeBuilder.() -> Unit) : TypeSpec<ListType>(),
        Spec<ListTypeBuilder>

class MapTypeSpec(override val body: MapTypeBuilder.() -> Unit) : TypeSpec<MapType>(),
        Spec<MapTypeBuilder>

class BooleanTypeSpec : TypeSpec<BooleanType>()

class DoubleTypeSpec : TypeSpec<DoubleType>()

class FloatTypeSpec : TypeSpec<FloatType>()

class LongTypeSpec : TypeSpec<LongType>()

class IntTypeSpec : TypeSpec<IntType>()

class ShortTypeSpec : TypeSpec<ShortType>()

class ByteTypeSpec : TypeSpec<ByteType>()

class CharacterTypeSpec : TypeSpec<CharacterType>()

class StringTypeSpec : TypeSpec<StringType>()

class AnyTypeSpec : TypeSpec<AnyType>()

@DslBuilder
interface StructBuilder : Builder<StructSpec, StructBuilder> {
    fun pkg(value: String)

    fun name(value: String)

    fun field(body: FieldBuilder.() -> Unit)

    fun field(spec: FieldSpec)

    fun field(ref: FieldRef)

    fun field(value: Field)

    fun fields(fields: List<Field>)
}

@DslBuilder
interface UnionBuilder : Builder<UnionSpec, UnionBuilder> {
    fun pkg(value: String)

    fun name(value: String)

    fun shape(body: StructBuilder.() -> Unit)

    fun shape(spec: StructSpec)

    fun shape(ref: StructRef)

    fun shape(value: Struct)

    fun shapes(shapes: List<Struct>)
}

@DslBuilder
interface EnumTypeBuilder : Builder<EnumTypeSpec, EnumTypeBuilder> {
    fun pkg(value: String)

    fun name(value: String)

    fun value(value: String)

    fun values(values: List<String>)
}

@DslBuilder
interface RefTypeBuilder : Builder<RefTypeSpec, RefTypeBuilder> {
    fun pkg(value: String)

    fun name(value: String)
}

@DslBuilder
interface OptionTypeBuilder : Builder<OptionTypeSpec, OptionTypeBuilder> {
    fun <T : Type> type(spec: TypeSpec<T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)
}

@DslBuilder
interface ListTypeBuilder : Builder<ListTypeSpec, ListTypeBuilder> {
    fun <T : Type> type(spec: TypeSpec<T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)
}

@DslBuilder
interface MapTypeBuilder : Builder<MapTypeSpec, MapTypeBuilder> {
    fun <T : Type> keyType(spec: TypeSpec<T>)

    fun <T : Type> keyType(ref: TypeRef<T>)

    fun <T : Type> keyType(value: T)

    fun <T : Type> valueType(spec: TypeSpec<T>)

    fun <T : Type> valueType(ref: TypeRef<T>)

    fun <T : Type> valueType(value: T)
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

class FieldSpec(override val body: FieldBuilder.() -> Unit) : Spec<FieldBuilder>

@DslBuilder
interface FieldBuilder : Builder<FieldSpec, FieldBuilder> {
    fun name(value: String)

    fun key(value: Boolean)

    fun singularName(value: String)

    fun <T : Type> type(spec: TypeSpec<T>)

    fun <T : Type> type(ref: TypeRef<T>)

    fun <T : Type> type(value: T)
}

class FieldRef(internal val key: String)
