package io.philarios.schema.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
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

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Schema::class, key)

open class SchemaSpec<in C>(internal val body: SchemaBuilder<C>.() -> Unit) : Spec<C, Schema> {
    override fun connect(context: C): Scaffold<Schema> {
        val builder = SchemaBuilder<C>(context)
        builder.apply(body)
        return builder.shell
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

class StructRef(key: String) : Scaffold<Struct> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Struct::class, key)

class UnionRef(key: String) : Scaffold<Union> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Union::class, key)

class EnumTypeRef(key: String) : Scaffold<EnumType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.EnumType::class, key)

class RefTypeRef(key: String) : Scaffold<RefType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.RefType::class, key)

class OptionTypeRef(key: String) : Scaffold<OptionType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.OptionType::class, key)

class ListTypeRef(key: String) : Scaffold<ListType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.ListType::class, key)

class MapTypeRef(key: String) : Scaffold<MapType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.MapType::class, key)

open class StructSpec<in C>(internal val body: StructBuilder<C>.() -> Unit) : Spec<C, Struct> {
    override fun connect(context: C): Scaffold<Struct> {
        val builder = StructBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class UnionSpec<in C>(internal val body: UnionBuilder<C>.() -> Unit) : Spec<C, Union> {
    override fun connect(context: C): Scaffold<Union> {
        val builder = UnionBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class EnumTypeSpec<in C>(internal val body: EnumTypeBuilder<C>.() -> Unit) : Spec<C, EnumType> {
    override fun connect(context: C): Scaffold<EnumType> {
        val builder = EnumTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class RefTypeSpec<in C>(internal val body: RefTypeBuilder<C>.() -> Unit) : Spec<C, RefType> {
    override fun connect(context: C): Scaffold<RefType> {
        val builder = RefTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class OptionTypeSpec<in C>(internal val body: OptionTypeBuilder<C>.() -> Unit) : Spec<C, OptionType> {
    override fun connect(context: C): Scaffold<OptionType> {
        val builder = OptionTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class ListTypeSpec<in C>(internal val body: ListTypeBuilder<C>.() -> Unit) : Spec<C, ListType> {
    override fun connect(context: C): Scaffold<ListType> {
        val builder = ListTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class MapTypeSpec<in C>(internal val body: MapTypeBuilder<C>.() -> Unit) : Spec<C, MapType> {
    override fun connect(context: C): Scaffold<MapType> {
        val builder = MapTypeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
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

class FieldRef(key: String) : Scaffold<Field> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Field::class, key)

open class FieldSpec<in C>(internal val body: FieldBuilder<C>.() -> Unit) : Spec<C, Field> {
    override fun connect(context: C): Scaffold<Field> {
        val builder = FieldBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
