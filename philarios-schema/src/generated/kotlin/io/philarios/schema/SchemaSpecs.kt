package io.philarios.schema

class SchemaSpec<in C>(internal val body: SchemaBuilder<C>.() -> Unit)

sealed class TypeSpec<in C, out T : Type>

class StructSpec<in C>(internal val body: StructBuilder<C>.() -> Unit) : TypeSpec<C, Struct>()

class UnionSpec<in C>(internal val body: UnionBuilder<C>.() -> Unit) : TypeSpec<C, Union>()

class EnumTypeSpec<in C>(internal val body: EnumTypeBuilder<C>.() -> Unit) : TypeSpec<C, EnumType>()

class RefTypeSpec<in C>(internal val body: RefTypeBuilder<C>.() -> Unit) : TypeSpec<C, RefType>()

class OptionTypeSpec<in C>(internal val body: OptionTypeBuilder<C>.() -> Unit) : TypeSpec<C, OptionType>()

class ListTypeSpec<in C>(internal val body: ListTypeBuilder<C>.() -> Unit) : TypeSpec<C, ListType>()

class MapTypeSpec<in C>(internal val body: MapTypeBuilder<C>.() -> Unit) : TypeSpec<C, MapType>()

class BooleanTypeSpec<in C> : TypeSpec<C, BooleanType>()

class DoubleTypeSpec<in C> : TypeSpec<C, DoubleType>()

class FloatTypeSpec<in C> : TypeSpec<C, FloatType>()

class LongTypeSpec<in C> : TypeSpec<C, LongType>()

class IntTypeSpec<in C> : TypeSpec<C, IntType>()

class ShortTypeSpec<in C> : TypeSpec<C, ShortType>()

class ByteTypeSpec<in C> : TypeSpec<C, ByteType>()

class CharacterTypeSpec<in C> : TypeSpec<C, CharacterType>()

class StringTypeSpec<in C> : TypeSpec<C, StringType>()

class AnyTypeSpec<in C> : TypeSpec<C, AnyType>()

class FieldSpec<in C>(internal val body: FieldBuilder<C>.() -> Unit)
