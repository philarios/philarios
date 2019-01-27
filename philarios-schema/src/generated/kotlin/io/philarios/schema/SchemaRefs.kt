package io.philarios.schema

import io.philarios.core.Scaffold
import kotlin.String

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.RegistryRef(io.philarios.schema.Schema::class, key)

class StructRef(key: String) : Scaffold<Struct> by io.philarios.core.RegistryRef(io.philarios.schema.Struct::class, key)

class UnionRef(key: String) : Scaffold<Union> by io.philarios.core.RegistryRef(io.philarios.schema.Union::class, key)

class EnumTypeRef(key: String) : Scaffold<EnumType> by io.philarios.core.RegistryRef(io.philarios.schema.EnumType::class, key)

class RefTypeRef(key: String) : Scaffold<RefType> by io.philarios.core.RegistryRef(io.philarios.schema.RefType::class, key)

class OptionTypeRef(key: String) : Scaffold<OptionType> by io.philarios.core.RegistryRef(io.philarios.schema.OptionType::class, key)

class ListTypeRef(key: String) : Scaffold<ListType> by io.philarios.core.RegistryRef(io.philarios.schema.ListType::class, key)

class MapTypeRef(key: String) : Scaffold<MapType> by io.philarios.core.RegistryRef(io.philarios.schema.MapType::class, key)

class FieldRef(key: String) : Scaffold<Field> by io.philarios.core.RegistryRef(io.philarios.schema.Field::class, key)
