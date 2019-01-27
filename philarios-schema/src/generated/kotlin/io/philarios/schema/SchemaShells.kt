package io.philarios.schema

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class SchemaShell(
        var pkg: String? = null,
        var name: String? = null,
        var types: List<Scaffold<Type>> = emptyList()
) : Scaffold<Schema> {
    override suspend fun resolve(registry: Registry): Schema {
        checkNotNull(pkg) { "Schema is missing the pkg property" }
        checkNotNull(name) { "Schema is missing the name property" }
        coroutineScope {
        	types.forEach { launch { it.resolve(registry) } }
        }
        val value = Schema(pkg!!,name!!,types.map { it.resolve(registry) })
        registry.put(Schema::class, name!!, value)
        return value
    }
}

internal sealed class TypeShell

internal data class StructShell(
        var pkg: String? = null,
        var name: String? = null,
        var fields: List<Scaffold<Field>> = emptyList()
) : TypeShell(), Scaffold<Struct> {
    override suspend fun resolve(registry: Registry): Struct {
        checkNotNull(name) { "Struct is missing the name property" }
        coroutineScope {
        	fields.forEach { launch { it.resolve(registry) } }
        }
        val value = Struct(pkg,name!!,fields.map { it.resolve(registry) })
        registry.put(Struct::class, name!!, value)
        return value
    }
}

internal data class UnionShell(
        var pkg: String? = null,
        var name: String? = null,
        var shapes: List<Scaffold<Struct>> = emptyList()
) : TypeShell(), Scaffold<Union> {
    override suspend fun resolve(registry: Registry): Union {
        checkNotNull(name) { "Union is missing the name property" }
        coroutineScope {
        	shapes.forEach { launch { it.resolve(registry) } }
        }
        val value = Union(pkg,name!!,shapes.map { it.resolve(registry) })
        registry.put(Union::class, name!!, value)
        return value
    }
}

internal data class EnumTypeShell(
        var pkg: String? = null,
        var name: String? = null,
        var values: List<String> = emptyList()
) : TypeShell(), Scaffold<EnumType> {
    override suspend fun resolve(registry: Registry): EnumType {
        checkNotNull(name) { "EnumType is missing the name property" }
        val value = EnumType(pkg,name!!,values)
        registry.put(EnumType::class, name!!, value)
        return value
    }
}

internal data class RefTypeShell(var pkg: String? = null, var name: String? = null) : TypeShell(),
        Scaffold<RefType> {
    override suspend fun resolve(registry: Registry): RefType {
        checkNotNull(name) { "RefType is missing the name property" }
        val value = RefType(pkg,name!!)
        registry.put(RefType::class, name!!, value)
        return value
    }
}

internal data class OptionTypeShell(var type: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<OptionType> {
    override suspend fun resolve(registry: Registry): OptionType {
        checkNotNull(type) { "OptionType is missing the type property" }
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = OptionType(type!!.resolve(registry))
        return value
    }
}

internal data class ListTypeShell(var type: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<ListType> {
    override suspend fun resolve(registry: Registry): ListType {
        checkNotNull(type) { "ListType is missing the type property" }
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = ListType(type!!.resolve(registry))
        return value
    }
}

internal data class MapTypeShell(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<MapType> {
    override suspend fun resolve(registry: Registry): MapType {
        checkNotNull(keyType) { "MapType is missing the keyType property" }
        checkNotNull(valueType) { "MapType is missing the valueType property" }
        coroutineScope {
        	launch { keyType!!.resolve(registry) }
        	launch { valueType!!.resolve(registry) }
        }
        val value = MapType(keyType!!.resolve(registry),valueType!!.resolve(registry))
        return value
    }
}

internal data class FieldShell(
        var name: String? = null,
        var key: Boolean? = null,
        var type: Scaffold<Type>? = null
) : Scaffold<Field> {
    override suspend fun resolve(registry: Registry): Field {
        checkNotNull(name) { "Field is missing the name property" }
        checkNotNull(type) { "Field is missing the type property" }
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = Field(name!!,key,type!!.resolve(registry))
        return value
    }
}
