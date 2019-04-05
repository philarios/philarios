package io.philarios.schema

import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class Type

data class Struct(
        val pkg: String?,
        val name: String,
        val fields: List<Field>
) : Type()

data class Union(
        val pkg: String?,
        val name: String,
        val shapes: List<Struct>
) : Type()

data class EnumType(
        val pkg: String?,
        val name: String,
        val values: List<String>
) : Type()

data class RefType(val pkg: String?, val name: String) : Type()

data class OptionType(val type: Type) : Type()

data class ListType(val type: Type) : Type()

data class MapType(val keyType: Type, val valueType: Type) : Type()

object BooleanType : Type()

object DoubleType : Type()

object FloatType : Type()

object LongType : Type()

object IntType : Type()

object ShortType : Type()

object ByteType : Type()

object CharacterType : Type()

object StringType : Type()

object AnyType : Type()

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

@DslBuilder
interface StructBuilder<out C> {
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
interface UnionBuilder<out C> {
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
interface EnumTypeBuilder<out C> {
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
interface RefTypeBuilder<out C> {
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
interface OptionTypeBuilder<out C> {
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
interface ListTypeBuilder<out C> {
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
interface MapTypeBuilder<out C> {
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

@DslBuilder
internal class StructShellBuilder<out C>(override val context: C, internal var shell: StructShell = StructShell()) : StructBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun field(body: FieldBuilder<C>.() -> Unit) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldScaffolder<C>(FieldSpec<C>(body)).createScaffold(context))
    }

    override fun field(spec: FieldSpec<C>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldScaffolder<C>(spec).createScaffold(context))
    }

    override fun field(ref: FieldRef) {
        shell = shell.copy(fields = shell.fields.orEmpty() + ref)
    }

    override fun field(value: Field) {
        shell = shell.copy(fields = shell.fields.orEmpty() + Wrapper(value))
    }

    override fun fields(fields: List<Field>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + fields.map { Wrapper(it) })
    }

    override fun include(body: StructBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StructSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StructBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StructSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StructBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StructSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StructShellBuilder<C2> = StructShellBuilder(context, shell)

    private fun <C2> merge(other: StructShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class UnionShellBuilder<out C>(override val context: C, internal var shell: UnionShell = UnionShell()) : UnionBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun shape(body: StructBuilder<C>.() -> Unit) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructScaffolder<C>(StructSpec<C>(body)).createScaffold(context))
    }

    override fun shape(spec: StructSpec<C>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructScaffolder<C>(spec).createScaffold(context))
    }

    override fun shape(ref: StructRef) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + ref)
    }

    override fun shape(value: Struct) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + Wrapper(value))
    }

    override fun shapes(shapes: List<Struct>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + shapes.map { Wrapper(it) })
    }

    override fun include(body: UnionBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: UnionSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: UnionBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: UnionSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: UnionBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: UnionSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): UnionShellBuilder<C2> = UnionShellBuilder(context, shell)

    private fun <C2> merge(other: UnionShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class EnumTypeShellBuilder<out C>(override val context: C, internal var shell: EnumTypeShell = EnumTypeShell()) : EnumTypeBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun value(value: String) {
        shell = shell.copy(values = shell.values.orEmpty() + Wrapper(value))
    }

    override fun values(values: List<String>) {
        shell = shell.copy(values = shell.values.orEmpty() + values.map { Wrapper(it) })
    }

    override fun include(body: EnumTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: EnumTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: EnumTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: EnumTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: EnumTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: EnumTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EnumTypeShellBuilder<C2> = EnumTypeShellBuilder(context, shell)

    private fun <C2> merge(other: EnumTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RefTypeShellBuilder<out C>(override val context: C, internal var shell: RefTypeShell = RefTypeShell()) : RefTypeBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun include(body: RefTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RefTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RefTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RefTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RefTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RefTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RefTypeShellBuilder<C2> = RefTypeShellBuilder(context, shell)

    private fun <C2> merge(other: RefTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class OptionTypeShellBuilder<out C>(override val context: C, internal var shell: OptionTypeShell = OptionTypeShell()) : OptionTypeBuilder<C> {
    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(type = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = ref)
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun include(body: OptionTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: OptionTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: OptionTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: OptionTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: OptionTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: OptionTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): OptionTypeShellBuilder<C2> = OptionTypeShellBuilder(context, shell)

    private fun <C2> merge(other: OptionTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ListTypeShellBuilder<out C>(override val context: C, internal var shell: ListTypeShell = ListTypeShell()) : ListTypeBuilder<C> {
    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(type = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = ref)
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun include(body: ListTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ListTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ListTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ListTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ListTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ListTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ListTypeShellBuilder<C2> = ListTypeShellBuilder(context, shell)

    private fun <C2> merge(other: ListTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class MapTypeShellBuilder<out C>(override val context: C, internal var shell: MapTypeShell = MapTypeShell()) : MapTypeBuilder<C> {
    override fun <T : Type> keyType(spec: TypeSpec<C, T>) {
        shell = shell.copy(keyType = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> keyType(ref: TypeRef<T>) {
        shell = shell.copy(keyType = ref)
    }

    override fun <T : Type> keyType(value: T) {
        shell = shell.copy(keyType = Wrapper(value))
    }

    override fun <T : Type> valueType(spec: TypeSpec<C, T>) {
        shell = shell.copy(valueType = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> valueType(ref: TypeRef<T>) {
        shell = shell.copy(valueType = ref)
    }

    override fun <T : Type> valueType(value: T) {
        shell = shell.copy(valueType = Wrapper(value))
    }

    override fun include(body: MapTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: MapTypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: MapTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: MapTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: MapTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: MapTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MapTypeShellBuilder<C2> = MapTypeShellBuilder(context, shell)

    private fun <C2> merge(other: MapTypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

class TypeScaffolder<in C, out T : Type>(internal val spec: TypeSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is StructSpec<C> -> StructScaffolder(spec).createScaffold(context)
            is UnionSpec<C> -> UnionScaffolder(spec).createScaffold(context)
            is EnumTypeSpec<C> -> EnumTypeScaffolder(spec).createScaffold(context)
            is RefTypeSpec<C> -> RefTypeScaffolder(spec).createScaffold(context)
            is OptionTypeSpec<C> -> OptionTypeScaffolder(spec).createScaffold(context)
            is ListTypeSpec<C> -> ListTypeScaffolder(spec).createScaffold(context)
            is MapTypeSpec<C> -> MapTypeScaffolder(spec).createScaffold(context)
            is BooleanTypeSpec<C> -> BooleanTypeScaffolder(spec).createScaffold(context)
            is DoubleTypeSpec<C> -> DoubleTypeScaffolder(spec).createScaffold(context)
            is FloatTypeSpec<C> -> FloatTypeScaffolder(spec).createScaffold(context)
            is LongTypeSpec<C> -> LongTypeScaffolder(spec).createScaffold(context)
            is IntTypeSpec<C> -> IntTypeScaffolder(spec).createScaffold(context)
            is ShortTypeSpec<C> -> ShortTypeScaffolder(spec).createScaffold(context)
            is ByteTypeSpec<C> -> ByteTypeScaffolder(spec).createScaffold(context)
            is CharacterTypeSpec<C> -> CharacterTypeScaffolder(spec).createScaffold(context)
            is StringTypeSpec<C> -> StringTypeScaffolder(spec).createScaffold(context)
            is AnyTypeSpec<C> -> AnyTypeScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class StructScaffolder<in C>(internal val spec: StructSpec<C>) : Scaffolder<C, Struct> {
    override fun createScaffold(context: C): Scaffold<Struct> {
        val builder = StructShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class UnionScaffolder<in C>(internal val spec: UnionSpec<C>) : Scaffolder<C, Union> {
    override fun createScaffold(context: C): Scaffold<Union> {
        val builder = UnionShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class EnumTypeScaffolder<in C>(internal val spec: EnumTypeSpec<C>) : Scaffolder<C, EnumType> {
    override fun createScaffold(context: C): Scaffold<EnumType> {
        val builder = EnumTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RefTypeScaffolder<in C>(internal val spec: RefTypeSpec<C>) : Scaffolder<C, RefType> {
    override fun createScaffold(context: C): Scaffold<RefType> {
        val builder = RefTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class OptionTypeScaffolder<in C>(internal val spec: OptionTypeSpec<C>) : Scaffolder<C, OptionType> {
    override fun createScaffold(context: C): Scaffold<OptionType> {
        val builder = OptionTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ListTypeScaffolder<in C>(internal val spec: ListTypeSpec<C>) : Scaffolder<C, ListType> {
    override fun createScaffold(context: C): Scaffold<ListType> {
        val builder = ListTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class MapTypeScaffolder<in C>(internal val spec: MapTypeSpec<C>) : Scaffolder<C, MapType> {
    override fun createScaffold(context: C): Scaffold<MapType> {
        val builder = MapTypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class BooleanTypeScaffolder<in C>(internal val spec: BooleanTypeSpec<C>) : Scaffolder<C, BooleanType> {
    override fun createScaffold(context: C): Scaffold<BooleanType> = Wrapper(BooleanType)
}

class DoubleTypeScaffolder<in C>(internal val spec: DoubleTypeSpec<C>) : Scaffolder<C, DoubleType> {
    override fun createScaffold(context: C): Scaffold<DoubleType> = Wrapper(DoubleType)
}

class FloatTypeScaffolder<in C>(internal val spec: FloatTypeSpec<C>) : Scaffolder<C, FloatType> {
    override fun createScaffold(context: C): Scaffold<FloatType> = Wrapper(FloatType)
}

class LongTypeScaffolder<in C>(internal val spec: LongTypeSpec<C>) : Scaffolder<C, LongType> {
    override fun createScaffold(context: C): Scaffold<LongType> = Wrapper(LongType)
}

class IntTypeScaffolder<in C>(internal val spec: IntTypeSpec<C>) : Scaffolder<C, IntType> {
    override fun createScaffold(context: C): Scaffold<IntType> = Wrapper(IntType)
}

class ShortTypeScaffolder<in C>(internal val spec: ShortTypeSpec<C>) : Scaffolder<C, ShortType> {
    override fun createScaffold(context: C): Scaffold<ShortType> = Wrapper(ShortType)
}

class ByteTypeScaffolder<in C>(internal val spec: ByteTypeSpec<C>) : Scaffolder<C, ByteType> {
    override fun createScaffold(context: C): Scaffold<ByteType> = Wrapper(ByteType)
}

class CharacterTypeScaffolder<in C>(internal val spec: CharacterTypeSpec<C>) : Scaffolder<C, CharacterType> {
    override fun createScaffold(context: C): Scaffold<CharacterType> = Wrapper(CharacterType)
}

class StringTypeScaffolder<in C>(internal val spec: StringTypeSpec<C>) : Scaffolder<C, StringType> {
    override fun createScaffold(context: C): Scaffold<StringType> = Wrapper(StringType)
}

class AnyTypeScaffolder<in C>(internal val spec: AnyTypeSpec<C>) : Scaffolder<C, AnyType> {
    override fun createScaffold(context: C): Scaffold<AnyType> = Wrapper(AnyType)
}
