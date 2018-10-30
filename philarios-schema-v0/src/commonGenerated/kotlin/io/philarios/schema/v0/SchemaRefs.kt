package io.philarios.schema.v0

import io.philarios.core.v0.Scaffold
import kotlin.String

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Schema::class, key)

class StructRef(key: String) : Scaffold<Struct> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Struct::class, key)

class UnionRef(key: String) : Scaffold<Union> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Union::class, key)

class EnumTypeRef(key: String) : Scaffold<EnumType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.EnumType::class, key)

class RefTypeRef(key: String) : Scaffold<RefType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.RefType::class, key)

class OptionTypeRef(key: String) : Scaffold<OptionType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.OptionType::class, key)

class ListTypeRef(key: String) : Scaffold<ListType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.ListType::class, key)

class MapTypeRef(key: String) : Scaffold<MapType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.MapType::class, key)

class FieldRef(key: String) : Scaffold<Field> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Field::class, key)
