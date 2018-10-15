package io.philarios.schema.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class SchemaShell(
        var name: String? = null,
        var pkg: String? = null,
        var types: List<Scaffold<Type>> = emptyList(),
        var references: List<Scaffold<Schema>> = emptyList()
) : Scaffold<Schema> {
    override suspend fun resolve(registry: Registry): Schema {
        coroutineScope {
        	types.forEach { launch { it.resolve(registry) } }
        	references.forEach { launch { it.resolve(registry) } }
        }
        val value = Schema(name!!,pkg!!,types.map { it.resolve(registry) },references.map { it.resolve(registry) })
        registry.put(Schema::class, name!!, value)
        return value
    }
}

sealed class TypeShell

data class StructShell(
        var pkg: String? = null,
        var name: String? = null,
        var fields: List<Scaffold<Field>> = emptyList()
) : TypeShell(), Scaffold<Struct> {
    override suspend fun resolve(registry: Registry): Struct {
        coroutineScope {
        	fields.forEach { launch { it.resolve(registry) } }
        }
        val value = Struct(pkg,name!!,fields.map { it.resolve(registry) })
        registry.put(Struct::class, name!!, value)
        return value
    }
}

data class UnionShell(
        var pkg: String? = null,
        var name: String? = null,
        var shapes: List<Scaffold<Struct>> = emptyList()
) : TypeShell(), Scaffold<Union> {
    override suspend fun resolve(registry: Registry): Union {
        coroutineScope {
        	shapes.forEach { launch { it.resolve(registry) } }
        }
        val value = Union(pkg,name!!,shapes.map { it.resolve(registry) })
        registry.put(Union::class, name!!, value)
        return value
    }
}

data class EnumTypeShell(
        var pkg: String? = null,
        var name: String? = null,
        var values: List<String> = emptyList()
) : TypeShell(), Scaffold<EnumType> {
    override suspend fun resolve(registry: Registry): EnumType {
        val value = EnumType(pkg,name!!,values)
        registry.put(EnumType::class, name!!, value)
        return value
    }
}

data class RefTypeShell(var pkg: String? = null, var name: String? = null) : TypeShell(),
        Scaffold<RefType> {
    override suspend fun resolve(registry: Registry): RefType {
        val value = RefType(pkg,name!!)
        registry.put(RefType::class, name!!, value)
        return value
    }
}

data class OptionTypeShell(var type: Scaffold<Type>? = null) : TypeShell(), Scaffold<OptionType> {
    override suspend fun resolve(registry: Registry): OptionType {
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = OptionType(type!!.resolve(registry))
        return value
    }
}

data class ListTypeShell(var type: Scaffold<Type>? = null) : TypeShell(), Scaffold<ListType> {
    override suspend fun resolve(registry: Registry): ListType {
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = ListType(type!!.resolve(registry))
        return value
    }
}

data class MapTypeShell(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<MapType> {
    override suspend fun resolve(registry: Registry): MapType {
        coroutineScope {
        	launch { keyType!!.resolve(registry) }
        	launch { valueType!!.resolve(registry) }
        }
        val value = MapType(keyType!!.resolve(registry),valueType!!.resolve(registry))
        return value
    }
}

data class FieldShell(
        var name: String? = null,
        var key: Boolean? = null,
        var type: Scaffold<Type>? = null
) : Scaffold<Field> {
    override suspend fun resolve(registry: Registry): Field {
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = Field(name!!,key,type!!.resolve(registry))
        return value
    }
}
