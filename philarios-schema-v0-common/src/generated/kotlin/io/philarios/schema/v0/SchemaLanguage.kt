package io.philarios.schema.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class Schema(
        val name: String,
        val pkg: String,
        val types: List<Type>,
        val references: List<Schema>
)

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

@DslBuilder
class SchemaBuilder<out C>(val context: C, internal var shell: SchemaShell = SchemaShell()) {
    fun <C> SchemaBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> SchemaBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> SchemaBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.type(ref: AnyTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.reference(body: SchemaBuilder<C>.() -> Unit) {
        shell = shell.copy(references = shell.references.orEmpty() + SchemaSpec<C>(body).connect(context))
    }

    fun <C> SchemaBuilder<C>.reference(spec: SchemaSpec<C>) {
        shell = shell.copy(references = shell.references.orEmpty() + spec.connect(context))
    }

    fun <C> SchemaBuilder<C>.reference(ref: SchemaRef) {
        shell = shell.copy(references = shell.references.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.reference(reference: Schema) {
        shell = shell.copy(references = shell.references.orEmpty() + Wrapper(reference))
    }

    fun <C> SchemaBuilder<C>.references(references: List<Schema>) {
        shell = shell.copy(references = shell.references.orEmpty() + references.map { Wrapper(it) })
    }

    fun <C> SchemaBuilder<C>.include(body: SchemaBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> SchemaBuilder<C>.include(spec: SchemaSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> SchemaBuilder<C>.include(context: C2, body: SchemaBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> SchemaBuilder<C>.include(context: C2, spec: SchemaSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> SchemaBuilder<C>.includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> SchemaBuilder<C>.includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SchemaBuilder<C2> = SchemaBuilder(context, shell)

    private fun <C2> merge(other: SchemaBuilder<C2>) {
        this.shell = other.shell
    }
}

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

object BooleanTypeShell : TypeShell()

object DoubleTypeShell : TypeShell()

object FloatTypeShell : TypeShell()

object LongTypeShell : TypeShell()

object IntTypeShell : TypeShell()

object ShortTypeShell : TypeShell()

object ByteTypeShell : TypeShell()

object CharacterTypeShell : TypeShell()

object StringTypeShell : TypeShell()

object AnyTypeShell : TypeShell()

class StructRef(key: String) : Scaffold<Struct> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Struct::class, key)

class UnionRef(key: String) : Scaffold<Union> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Union::class, key)

class EnumTypeRef(key: String) : Scaffold<EnumType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.EnumType::class, key)

class RefTypeRef(key: String) : Scaffold<RefType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.RefType::class, key)

class OptionTypeRef(key: String) : Scaffold<OptionType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.OptionType::class, key)

class ListTypeRef(key: String) : Scaffold<ListType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.ListType::class, key)

class MapTypeRef(key: String) : Scaffold<MapType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.MapType::class, key)

class BooleanTypeRef(key: String) : Scaffold<BooleanType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.BooleanType::class, key)

class DoubleTypeRef(key: String) : Scaffold<DoubleType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.DoubleType::class, key)

class FloatTypeRef(key: String) : Scaffold<FloatType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.FloatType::class, key)

class LongTypeRef(key: String) : Scaffold<LongType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.LongType::class, key)

class IntTypeRef(key: String) : Scaffold<IntType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.IntType::class, key)

class ShortTypeRef(key: String) : Scaffold<ShortType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.ShortType::class, key)

class ByteTypeRef(key: String) : Scaffold<ByteType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.ByteType::class, key)

class CharacterTypeRef(key: String) : Scaffold<CharacterType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.CharacterType::class, key)

class StringTypeRef(key: String) : Scaffold<StringType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.StringType::class, key)

class AnyTypeRef(key: String) : Scaffold<AnyType> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.AnyType::class, key)

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

open class BooleanTypeSpec<in C>(internal val body: BooleanTypeBuilder<C>.() -> Unit) : Spec<C, BooleanType> {
    override fun connect(context: C): Scaffold<BooleanType> = Wrapper(BooleanType)
}

open class DoubleTypeSpec<in C>(internal val body: DoubleTypeBuilder<C>.() -> Unit) : Spec<C, DoubleType> {
    override fun connect(context: C): Scaffold<DoubleType> = Wrapper(DoubleType)
}

open class FloatTypeSpec<in C>(internal val body: FloatTypeBuilder<C>.() -> Unit) : Spec<C, FloatType> {
    override fun connect(context: C): Scaffold<FloatType> = Wrapper(FloatType)
}

open class LongTypeSpec<in C>(internal val body: LongTypeBuilder<C>.() -> Unit) : Spec<C, LongType> {
    override fun connect(context: C): Scaffold<LongType> = Wrapper(LongType)
}

open class IntTypeSpec<in C>(internal val body: IntTypeBuilder<C>.() -> Unit) : Spec<C, IntType> {
    override fun connect(context: C): Scaffold<IntType> = Wrapper(IntType)
}

open class ShortTypeSpec<in C>(internal val body: ShortTypeBuilder<C>.() -> Unit) : Spec<C, ShortType> {
    override fun connect(context: C): Scaffold<ShortType> = Wrapper(ShortType)
}

open class ByteTypeSpec<in C>(internal val body: ByteTypeBuilder<C>.() -> Unit) : Spec<C, ByteType> {
    override fun connect(context: C): Scaffold<ByteType> = Wrapper(ByteType)
}

open class CharacterTypeSpec<in C>(internal val body: CharacterTypeBuilder<C>.() -> Unit) : Spec<C, CharacterType> {
    override fun connect(context: C): Scaffold<CharacterType> = Wrapper(CharacterType)
}

open class StringTypeSpec<in C>(internal val body: StringTypeBuilder<C>.() -> Unit) : Spec<C, StringType> {
    override fun connect(context: C): Scaffold<StringType> = Wrapper(StringType)
}

open class AnyTypeSpec<in C>(internal val body: AnyTypeBuilder<C>.() -> Unit) : Spec<C, AnyType> {
    override fun connect(context: C): Scaffold<AnyType> = Wrapper(AnyType)
}

@DslBuilder
class StructBuilder<out C>(val context: C, internal var shell: StructShell = StructShell()) {
    fun <C> StructBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> StructBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> StructBuilder<C>.field(body: FieldBuilder<C>.() -> Unit) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldSpec<C>(body).connect(context))
    }

    fun <C> StructBuilder<C>.field(spec: FieldSpec<C>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + spec.connect(context))
    }

    fun <C> StructBuilder<C>.field(ref: FieldRef) {
        shell = shell.copy(fields = shell.fields.orEmpty() + ref)
    }

    fun <C> StructBuilder<C>.field(field: Field) {
        shell = shell.copy(fields = shell.fields.orEmpty() + Wrapper(field))
    }

    fun <C> StructBuilder<C>.fields(fields: List<Field>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + fields.map { Wrapper(it) })
    }

    fun <C> StructBuilder<C>.include(body: StructBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> StructBuilder<C>.include(spec: StructSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> StructBuilder<C>.include(context: C2, body: StructBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> StructBuilder<C>.include(context: C2, spec: StructSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> StructBuilder<C>.includeForEach(context: Iterable<C2>, body: StructBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> StructBuilder<C>.includeForEach(context: Iterable<C2>, spec: StructSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StructBuilder<C2> = StructBuilder(context, shell)

    private fun <C2> merge(other: StructBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class UnionBuilder<out C>(val context: C, internal var shell: UnionShell = UnionShell()) {
    fun <C> UnionBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> UnionBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> UnionBuilder<C>.shape(body: StructBuilder<C>.() -> Unit) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructSpec<C>(body).connect(context))
    }

    fun <C> UnionBuilder<C>.shape(spec: StructSpec<C>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + spec.connect(context))
    }

    fun <C> UnionBuilder<C>.shape(ref: StructRef) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + ref)
    }

    fun <C> UnionBuilder<C>.shape(shape: Struct) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + Wrapper(shape))
    }

    fun <C> UnionBuilder<C>.shapes(shapes: List<Struct>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + shapes.map { Wrapper(it) })
    }

    fun <C> UnionBuilder<C>.include(body: UnionBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> UnionBuilder<C>.include(spec: UnionSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> UnionBuilder<C>.include(context: C2, body: UnionBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> UnionBuilder<C>.include(context: C2, spec: UnionSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> UnionBuilder<C>.includeForEach(context: Iterable<C2>, body: UnionBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> UnionBuilder<C>.includeForEach(context: Iterable<C2>, spec: UnionSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): UnionBuilder<C2> = UnionBuilder(context, shell)

    private fun <C2> merge(other: UnionBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class EnumTypeBuilder<out C>(val context: C, internal var shell: EnumTypeShell = EnumTypeShell()) {
    fun <C> EnumTypeBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> EnumTypeBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> EnumTypeBuilder<C>.value(value: String) {
        shell = shell.copy(values = shell.values.orEmpty() + value)
    }

    fun <C> EnumTypeBuilder<C>.values(values: List<String>) {
        shell = shell.copy(values = shell.values.orEmpty() + values)
    }

    fun <C> EnumTypeBuilder<C>.include(body: EnumTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> EnumTypeBuilder<C>.include(spec: EnumTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> EnumTypeBuilder<C>.include(context: C2, body: EnumTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> EnumTypeBuilder<C>.include(context: C2, spec: EnumTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> EnumTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: EnumTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> EnumTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: EnumTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EnumTypeBuilder<C2> = EnumTypeBuilder(context, shell)

    private fun <C2> merge(other: EnumTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class RefTypeBuilder<out C>(val context: C, internal var shell: RefTypeShell = RefTypeShell()) {
    fun <C> RefTypeBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> RefTypeBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> RefTypeBuilder<C>.include(body: RefTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> RefTypeBuilder<C>.include(spec: RefTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> RefTypeBuilder<C>.include(context: C2, body: RefTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> RefTypeBuilder<C>.include(context: C2, spec: RefTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> RefTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: RefTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> RefTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: RefTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RefTypeBuilder<C2> = RefTypeBuilder(context, shell)

    private fun <C2> merge(other: RefTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class OptionTypeBuilder<out C>(val context: C, internal var shell: OptionTypeShell = OptionTypeShell()) {
    fun <C> OptionTypeBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> OptionTypeBuilder<C>.type(ref: AnyTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.include(body: OptionTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> OptionTypeBuilder<C>.include(spec: OptionTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> OptionTypeBuilder<C>.include(context: C2, body: OptionTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> OptionTypeBuilder<C>.include(context: C2, spec: OptionTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> OptionTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: OptionTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> OptionTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: OptionTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): OptionTypeBuilder<C2> = OptionTypeBuilder(context, shell)

    private fun <C2> merge(other: OptionTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class ListTypeBuilder<out C>(val context: C, internal var shell: ListTypeShell = ListTypeShell()) {
    fun <C> ListTypeBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> ListTypeBuilder<C>.type(ref: AnyTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.include(body: ListTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ListTypeBuilder<C>.include(spec: ListTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ListTypeBuilder<C>.include(context: C2, body: ListTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ListTypeBuilder<C>.include(context: C2, spec: ListTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> ListTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: ListTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ListTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: ListTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ListTypeBuilder<C2> = ListTypeBuilder(context, shell)

    private fun <C2> merge(other: ListTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class MapTypeBuilder<out C>(val context: C, internal var shell: MapTypeShell = MapTypeShell()) {
    fun <C> MapTypeBuilder<C>.keyType(spec: StructSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: StructRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: UnionSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: UnionRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: EnumTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: RefTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: RefTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: OptionTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ListTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ListTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: MapTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: MapTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: BooleanTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: DoubleTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: FloatTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: FloatTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: LongTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: LongTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: IntTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: IntTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ShortTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ShortTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ByteTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ByteTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: CharacterTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: StringTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: StringTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: AnyTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: AnyTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StructSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: StructRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: UnionSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: UnionRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: EnumTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: RefTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: RefTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: OptionTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ListTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ListTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: MapTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: MapTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: BooleanTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: DoubleTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: FloatTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: FloatTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: LongTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: LongTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: IntTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: IntTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ShortTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ShortTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ByteTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ByteTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: CharacterTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StringTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: StringTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: AnyTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: AnyTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.include(body: MapTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> MapTypeBuilder<C>.include(spec: MapTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> MapTypeBuilder<C>.include(context: C2, body: MapTypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> MapTypeBuilder<C>.include(context: C2, spec: MapTypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> MapTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: MapTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> MapTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: MapTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MapTypeBuilder<C2> = MapTypeBuilder(context, shell)

    private fun <C2> merge(other: MapTypeBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class BooleanTypeBuilder<out C>(val context: C)

@DslBuilder
class DoubleTypeBuilder<out C>(val context: C)

@DslBuilder
class FloatTypeBuilder<out C>(val context: C)

@DslBuilder
class LongTypeBuilder<out C>(val context: C)

@DslBuilder
class IntTypeBuilder<out C>(val context: C)

@DslBuilder
class ShortTypeBuilder<out C>(val context: C)

@DslBuilder
class ByteTypeBuilder<out C>(val context: C)

@DslBuilder
class CharacterTypeBuilder<out C>(val context: C)

@DslBuilder
class StringTypeBuilder<out C>(val context: C)

@DslBuilder
class AnyTypeBuilder<out C>(val context: C)

data class Field(
        val name: String,
        val key: Boolean?,
        val type: Type
)

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

@DslBuilder
class FieldBuilder<out C>(val context: C, internal var shell: FieldShell = FieldShell()) {
    fun <C> FieldBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> FieldBuilder<C>.key(key: Boolean) {
        shell = shell.copy(key = key)
    }

    fun <C> FieldBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    fun <C> FieldBuilder<C>.type(ref: AnyTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.include(body: FieldBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> FieldBuilder<C>.include(spec: FieldSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> FieldBuilder<C>.include(context: C2, body: FieldBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> FieldBuilder<C>.include(context: C2, spec: FieldSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> FieldBuilder<C>.includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> FieldBuilder<C>.includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FieldBuilder<C2> = FieldBuilder(context, shell)

    private fun <C2> merge(other: FieldBuilder<C2>) {
        this.shell = other.shell
    }
}
