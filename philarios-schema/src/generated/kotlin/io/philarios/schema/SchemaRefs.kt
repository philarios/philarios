package io.philarios.schema

import io.philarios.core.Scaffold
import kotlin.String

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.RegistryRef(io.philarios.schema.Schema::class, key)

sealed class TypeRef<T : Type> : Scaffold<T>

class StructRef(key: String) : TypeRef<Struct>(),
        Scaffold<Struct> by io.philarios.core.RegistryRef(io.philarios.schema.Struct::class, key)

class UnionRef(key: String) : TypeRef<Union>(),
        Scaffold<Union> by io.philarios.core.RegistryRef(io.philarios.schema.Union::class, key)

class EnumTypeRef(key: String) : TypeRef<EnumType>(),
        Scaffold<EnumType> by io.philarios.core.RegistryRef(io.philarios.schema.EnumType::class, key)

class RefTypeRef(key: String) : TypeRef<RefType>(),
        Scaffold<RefType> by io.philarios.core.RegistryRef(io.philarios.schema.RefType::class, key)

class OptionTypeRef(key: String) : TypeRef<OptionType>(),
        Scaffold<OptionType> by io.philarios.core.RegistryRef(io.philarios.schema.OptionType::class, key)

class ListTypeRef(key: String) : TypeRef<ListType>(),
        Scaffold<ListType> by io.philarios.core.RegistryRef(io.philarios.schema.ListType::class, key)

class MapTypeRef(key: String) : TypeRef<MapType>(),
        Scaffold<MapType> by io.philarios.core.RegistryRef(io.philarios.schema.MapType::class, key)

class FieldRef(key: String) : Scaffold<Field> by io.philarios.core.RegistryRef(io.philarios.schema.Field::class, key)
