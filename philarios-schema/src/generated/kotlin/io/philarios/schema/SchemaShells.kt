package io.philarios.schema

import io.philarios.util.registry.Registry
import io.philarios.core.Scaffold
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class SchemaShell(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var types: List<Scaffold<Type>>? = null
) : Scaffold<Schema> {
    override suspend fun resolve(registry: Registry): Schema {
        checkNotNull(pkg) { "Schema is missing the pkg property" }
        checkNotNull(name) { "Schema is missing the name property" }
        coroutineScope {
            types?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Schema(
            pkg!!.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            types.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Schema::class, value.name, value)
        return value
    }
}

internal sealed class TypeShell

internal data class StructShell(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var fields: List<Scaffold<Field>>? = null
) : TypeShell(), Scaffold<Struct> {
    override suspend fun resolve(registry: Registry): Struct {
        checkNotNull(name) { "Struct is missing the name property" }
        coroutineScope {
            fields?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Struct(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            fields.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Struct::class, value.name, value)
        return value
    }
}

internal data class UnionShell(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var shapes: List<Scaffold<Struct>>? = null
) : TypeShell(), Scaffold<Union> {
    override suspend fun resolve(registry: Registry): Union {
        checkNotNull(name) { "Union is missing the name property" }
        coroutineScope {
            shapes?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Union(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            shapes.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Union::class, value.name, value)
        return value
    }
}

internal data class EnumTypeShell(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var values: List<Scaffold<String>>? = null
) : TypeShell(), Scaffold<EnumType> {
    override suspend fun resolve(registry: Registry): EnumType {
        checkNotNull(name) { "EnumType is missing the name property" }
        val value = EnumType(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            values.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(EnumType::class, value.name, value)
        return value
    }
}

internal data class RefTypeShell(var pkg: Scaffold<String>? = null, var name: Scaffold<String>? = null) : TypeShell(),
        Scaffold<RefType> {
    override suspend fun resolve(registry: Registry): RefType {
        checkNotNull(name) { "RefType is missing the name property" }
        val value = RefType(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) }
        )
        registry.put(RefType::class, value.name, value)
        return value
    }
}

internal data class OptionTypeShell(var type: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<OptionType> {
    override suspend fun resolve(registry: Registry): OptionType {
        checkNotNull(type) { "OptionType is missing the type property" }
        coroutineScope {
            type?.let{ launch { it.resolve(registry) } }
        }
        val value = OptionType(
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ListTypeShell(var type: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<ListType> {
    override suspend fun resolve(registry: Registry): ListType {
        checkNotNull(type) { "ListType is missing the type property" }
        coroutineScope {
            type?.let{ launch { it.resolve(registry) } }
        }
        val value = ListType(
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class MapTypeShell(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<MapType> {
    override suspend fun resolve(registry: Registry): MapType {
        checkNotNull(keyType) { "MapType is missing the keyType property" }
        checkNotNull(valueType) { "MapType is missing the valueType property" }
        coroutineScope {
            keyType?.let{ launch { it.resolve(registry) } }
            valueType?.let{ launch { it.resolve(registry) } }
        }
        val value = MapType(
            keyType!!.let{ it.resolve(registry) },
            valueType!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class FieldShell(
        var name: Scaffold<String>? = null,
        var key: Scaffold<Boolean>? = null,
        var type: Scaffold<Type>? = null
) : Scaffold<Field> {
    override suspend fun resolve(registry: Registry): Field {
        checkNotNull(name) { "Field is missing the name property" }
        checkNotNull(type) { "Field is missing the type property" }
        coroutineScope {
            type?.let{ launch { it.resolve(registry) } }
        }
        val value = Field(
            name!!.let{ it.resolve(registry) },
            key?.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}
