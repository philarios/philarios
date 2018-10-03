package io.philarios.schema.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Translator
import io.philarios.core.v0.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

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
        val value = Schema(name!!,pkg!!,types!!.map { it.resolve(registry) },references!!.map { it.resolve(registry) })
        registry.put(Schema::class, name!!, value)
        return value
    }
}

class SchemaRef(key: String) : Scaffold<Schema> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Schema::class, key)

class SchemaTemplate<in C>(private val spec: SchemaSpec<C>, private val context: C) : Builder<Schema> {
    constructor(body: SchemaBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.SchemaSpec<C>(body), context)

    override fun scaffold(): Scaffold<Schema> {
        val builder = SchemaBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class SchemaSpec<in C>(internal val body: SchemaBuilder<C>.() -> Unit)

@DslBuilder
class SchemaBuilder<out C>(val context: C, internal var shell: SchemaShell = SchemaShell()) {
    fun <C> SchemaBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> SchemaBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> SchemaBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + AnyTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.type(ref: AnyTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    fun <C> SchemaBuilder<C>.reference(body: SchemaBuilder<C>.() -> Unit) {
        shell = shell.copy(references = shell.references.orEmpty() + SchemaTemplate<C>(body, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.reference(spec: SchemaSpec<C>) {
        shell = shell.copy(references = shell.references.orEmpty() + SchemaTemplate<C>(spec, context).scaffold())
    }

    fun <C> SchemaBuilder<C>.reference(ref: SchemaRef) {
        shell = shell.copy(references = shell.references.orEmpty() + ref)
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

open class SchemaTranslator<in C>(private val spec: SchemaSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Schema> {
    constructor(body: SchemaBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.SchemaSpec<C>(body), registry)

    override suspend fun translate(context: C): Schema {
        val builder = SchemaTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
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
        val value = Struct(pkg,name!!,fields!!.map { it.resolve(registry) })
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
        val value = Union(pkg,name!!,shapes!!.map { it.resolve(registry) })
        registry.put(Union::class, name!!, value)
        return value
    }
}

data class EnumTypeShell(
        var pkg: String? = null,
        var name: String? = null,
        var values: List<String>? = emptyList()
) : TypeShell(), Scaffold<EnumType> {
    override suspend fun resolve(registry: Registry): EnumType {
        val value = EnumType(pkg,name!!,values!!)
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
        val value = OptionType(type!!.resolve(registry))
        return value
    }
}

data class ListTypeShell(var type: Scaffold<Type>? = null) : TypeShell(), Scaffold<ListType> {
    override suspend fun resolve(registry: Registry): ListType {
        val value = ListType(type!!.resolve(registry))
        return value
    }
}

data class MapTypeShell(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeShell(),
        Scaffold<MapType> {
    override suspend fun resolve(registry: Registry): MapType {
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

class StructTemplate<in C>(private val spec: StructSpec<C>, private val context: C) : Builder<Struct> {
    constructor(body: StructBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.StructSpec<C>(body), context)

    override fun scaffold(): Scaffold<Struct> {
        val builder = StructBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class UnionTemplate<in C>(private val spec: UnionSpec<C>, private val context: C) : Builder<Union> {
    constructor(body: UnionBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.UnionSpec<C>(body), context)

    override fun scaffold(): Scaffold<Union> {
        val builder = UnionBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class EnumTypeTemplate<in C>(private val spec: EnumTypeSpec<C>, private val context: C) : Builder<EnumType> {
    constructor(body: EnumTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.EnumTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<EnumType> {
        val builder = EnumTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RefTypeTemplate<in C>(private val spec: RefTypeSpec<C>, private val context: C) : Builder<RefType> {
    constructor(body: RefTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.RefTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<RefType> {
        val builder = RefTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class OptionTypeTemplate<in C>(private val spec: OptionTypeSpec<C>, private val context: C) : Builder<OptionType> {
    constructor(body: OptionTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.OptionTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<OptionType> {
        val builder = OptionTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ListTypeTemplate<in C>(private val spec: ListTypeSpec<C>, private val context: C) : Builder<ListType> {
    constructor(body: ListTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.ListTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<ListType> {
        val builder = ListTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class MapTypeTemplate<in C>(private val spec: MapTypeSpec<C>, private val context: C) : Builder<MapType> {
    constructor(body: MapTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.MapTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<MapType> {
        val builder = MapTypeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class BooleanTypeTemplate<in C>(private val spec: BooleanTypeSpec<C>, private val context: C) : Builder<BooleanType> {
    constructor(body: BooleanTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.BooleanTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<BooleanType> = Wrapper(BooleanType)
}

class DoubleTypeTemplate<in C>(private val spec: DoubleTypeSpec<C>, private val context: C) : Builder<DoubleType> {
    constructor(body: DoubleTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.DoubleTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<DoubleType> = Wrapper(DoubleType)
}

class FloatTypeTemplate<in C>(private val spec: FloatTypeSpec<C>, private val context: C) : Builder<FloatType> {
    constructor(body: FloatTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.FloatTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<FloatType> = Wrapper(FloatType)
}

class LongTypeTemplate<in C>(private val spec: LongTypeSpec<C>, private val context: C) : Builder<LongType> {
    constructor(body: LongTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.LongTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<LongType> = Wrapper(LongType)
}

class IntTypeTemplate<in C>(private val spec: IntTypeSpec<C>, private val context: C) : Builder<IntType> {
    constructor(body: IntTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.IntTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<IntType> = Wrapper(IntType)
}

class ShortTypeTemplate<in C>(private val spec: ShortTypeSpec<C>, private val context: C) : Builder<ShortType> {
    constructor(body: ShortTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.ShortTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<ShortType> = Wrapper(ShortType)
}

class ByteTypeTemplate<in C>(private val spec: ByteTypeSpec<C>, private val context: C) : Builder<ByteType> {
    constructor(body: ByteTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.ByteTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<ByteType> = Wrapper(ByteType)
}

class CharacterTypeTemplate<in C>(private val spec: CharacterTypeSpec<C>, private val context: C) : Builder<CharacterType> {
    constructor(body: CharacterTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.CharacterTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<CharacterType> = Wrapper(CharacterType)
}

class StringTypeTemplate<in C>(private val spec: StringTypeSpec<C>, private val context: C) : Builder<StringType> {
    constructor(body: StringTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.StringTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<StringType> = Wrapper(StringType)
}

class AnyTypeTemplate<in C>(private val spec: AnyTypeSpec<C>, private val context: C) : Builder<AnyType> {
    constructor(body: AnyTypeBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.AnyTypeSpec<C>(body), context)

    override fun scaffold(): Scaffold<AnyType> = Wrapper(AnyType)
}

open class StructSpec<in C>(internal val body: StructBuilder<C>.() -> Unit)

open class UnionSpec<in C>(internal val body: UnionBuilder<C>.() -> Unit)

open class EnumTypeSpec<in C>(internal val body: EnumTypeBuilder<C>.() -> Unit)

open class RefTypeSpec<in C>(internal val body: RefTypeBuilder<C>.() -> Unit)

open class OptionTypeSpec<in C>(internal val body: OptionTypeBuilder<C>.() -> Unit)

open class ListTypeSpec<in C>(internal val body: ListTypeBuilder<C>.() -> Unit)

open class MapTypeSpec<in C>(internal val body: MapTypeBuilder<C>.() -> Unit)

open class BooleanTypeSpec<in C>(internal val body: BooleanTypeBuilder<C>.() -> Unit)

open class DoubleTypeSpec<in C>(internal val body: DoubleTypeBuilder<C>.() -> Unit)

open class FloatTypeSpec<in C>(internal val body: FloatTypeBuilder<C>.() -> Unit)

open class LongTypeSpec<in C>(internal val body: LongTypeBuilder<C>.() -> Unit)

open class IntTypeSpec<in C>(internal val body: IntTypeBuilder<C>.() -> Unit)

open class ShortTypeSpec<in C>(internal val body: ShortTypeBuilder<C>.() -> Unit)

open class ByteTypeSpec<in C>(internal val body: ByteTypeBuilder<C>.() -> Unit)

open class CharacterTypeSpec<in C>(internal val body: CharacterTypeBuilder<C>.() -> Unit)

open class StringTypeSpec<in C>(internal val body: StringTypeBuilder<C>.() -> Unit)

open class AnyTypeSpec<in C>(internal val body: AnyTypeBuilder<C>.() -> Unit)

@DslBuilder
class StructBuilder<out C>(val context: C, internal var shell: StructShell = StructShell()) {
    fun <C> StructBuilder<C>.pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    fun <C> StructBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> StructBuilder<C>.field(body: FieldBuilder<C>.() -> Unit) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldTemplate<C>(body, context).scaffold())
    }

    fun <C> StructBuilder<C>.field(spec: FieldSpec<C>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldTemplate<C>(spec, context).scaffold())
    }

    fun <C> StructBuilder<C>.field(ref: FieldRef) {
        shell = shell.copy(fields = shell.fields.orEmpty() + ref)
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
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructTemplate<C>(body, context).scaffold())
    }

    fun <C> UnionBuilder<C>.shape(spec: StructSpec<C>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> UnionBuilder<C>.shape(ref: StructRef) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + ref)
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
        shell = shell.copy(type = StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> OptionTypeBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = AnyTypeTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(type = StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> ListTypeBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> ListTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = AnyTypeTemplate<C>(spec, context).scaffold())
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
        shell = shell.copy(keyType = StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: StructRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: UnionSpec<C>) {
        shell = shell.copy(keyType = UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: UnionRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(keyType = EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: EnumTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: RefTypeSpec<C>) {
        shell = shell.copy(keyType = RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: RefTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(keyType = OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: OptionTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ListTypeSpec<C>) {
        shell = shell.copy(keyType = ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ListTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: MapTypeSpec<C>) {
        shell = shell.copy(keyType = MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: MapTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(keyType = BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: BooleanTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(keyType = DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: DoubleTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: FloatTypeSpec<C>) {
        shell = shell.copy(keyType = FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: FloatTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: LongTypeSpec<C>) {
        shell = shell.copy(keyType = LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: LongTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: IntTypeSpec<C>) {
        shell = shell.copy(keyType = IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: IntTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ShortTypeSpec<C>) {
        shell = shell.copy(keyType = ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ShortTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ByteTypeSpec<C>) {
        shell = shell.copy(keyType = ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: ByteTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(keyType = CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: CharacterTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: StringTypeSpec<C>) {
        shell = shell.copy(keyType = StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: StringTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: AnyTypeSpec<C>) {
        shell = shell.copy(keyType = AnyTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.keyType(ref: AnyTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StructSpec<C>) {
        shell = shell.copy(valueType = StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: StructRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: UnionSpec<C>) {
        shell = shell.copy(valueType = UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: UnionRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(valueType = EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: EnumTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: RefTypeSpec<C>) {
        shell = shell.copy(valueType = RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: RefTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(valueType = OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: OptionTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ListTypeSpec<C>) {
        shell = shell.copy(valueType = ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ListTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: MapTypeSpec<C>) {
        shell = shell.copy(valueType = MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: MapTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(valueType = BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: BooleanTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(valueType = DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: DoubleTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: FloatTypeSpec<C>) {
        shell = shell.copy(valueType = FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: FloatTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: LongTypeSpec<C>) {
        shell = shell.copy(valueType = LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: LongTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: IntTypeSpec<C>) {
        shell = shell.copy(valueType = IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: IntTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ShortTypeSpec<C>) {
        shell = shell.copy(valueType = ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ShortTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ByteTypeSpec<C>) {
        shell = shell.copy(valueType = ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: ByteTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(valueType = CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: CharacterTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StringTypeSpec<C>) {
        shell = shell.copy(valueType = StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> MapTypeBuilder<C>.valueType(ref: StringTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: AnyTypeSpec<C>) {
        shell = shell.copy(valueType = AnyTypeTemplate<C>(spec, context).scaffold())
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

open class StructTranslator<in C>(private val spec: StructSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Struct> {
    constructor(body: StructBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.StructSpec<C>(body), registry)

    override suspend fun translate(context: C): Struct {
        val builder = StructTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class UnionTranslator<in C>(private val spec: UnionSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Union> {
    constructor(body: UnionBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.UnionSpec<C>(body), registry)

    override suspend fun translate(context: C): Union {
        val builder = UnionTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class EnumTypeTranslator<in C>(private val spec: EnumTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, EnumType> {
    constructor(body: EnumTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.EnumTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): EnumType {
        val builder = EnumTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class RefTypeTranslator<in C>(private val spec: RefTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, RefType> {
    constructor(body: RefTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.RefTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): RefType {
        val builder = RefTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class OptionTypeTranslator<in C>(private val spec: OptionTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, OptionType> {
    constructor(body: OptionTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.OptionTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): OptionType {
        val builder = OptionTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class ListTypeTranslator<in C>(private val spec: ListTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, ListType> {
    constructor(body: ListTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.ListTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): ListType {
        val builder = ListTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class MapTypeTranslator<in C>(private val spec: MapTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, MapType> {
    constructor(body: MapTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.MapTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): MapType {
        val builder = MapTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class BooleanTypeTranslator<in C>(private val spec: BooleanTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, BooleanType> {
    constructor(body: BooleanTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.BooleanTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): BooleanType {
        val builder = BooleanTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class DoubleTypeTranslator<in C>(private val spec: DoubleTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, DoubleType> {
    constructor(body: DoubleTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.DoubleTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): DoubleType {
        val builder = DoubleTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class FloatTypeTranslator<in C>(private val spec: FloatTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, FloatType> {
    constructor(body: FloatTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.FloatTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): FloatType {
        val builder = FloatTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class LongTypeTranslator<in C>(private val spec: LongTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, LongType> {
    constructor(body: LongTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.LongTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): LongType {
        val builder = LongTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class IntTypeTranslator<in C>(private val spec: IntTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, IntType> {
    constructor(body: IntTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.IntTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): IntType {
        val builder = IntTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class ShortTypeTranslator<in C>(private val spec: ShortTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, ShortType> {
    constructor(body: ShortTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.ShortTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): ShortType {
        val builder = ShortTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class ByteTypeTranslator<in C>(private val spec: ByteTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, ByteType> {
    constructor(body: ByteTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.ByteTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): ByteType {
        val builder = ByteTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class CharacterTypeTranslator<in C>(private val spec: CharacterTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, CharacterType> {
    constructor(body: CharacterTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.CharacterTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): CharacterType {
        val builder = CharacterTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class StringTypeTranslator<in C>(private val spec: StringTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, StringType> {
    constructor(body: StringTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.StringTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): StringType {
        val builder = StringTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

open class AnyTypeTranslator<in C>(private val spec: AnyTypeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, AnyType> {
    constructor(body: AnyTypeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.AnyTypeSpec<C>(body), registry)

    override suspend fun translate(context: C): AnyType {
        val builder = AnyTypeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

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
        val value = Field(name!!,key,type!!.resolve(registry))
        return value
    }
}

class FieldRef(key: String) : Scaffold<Field> by io.philarios.core.v0.RegistryRef(io.philarios.schema.v0.Field::class, key)

class FieldTemplate<in C>(private val spec: FieldSpec<C>, private val context: C) : Builder<Field> {
    constructor(body: FieldBuilder<C>.() -> Unit, context: C) : this(io.philarios.schema.v0.FieldSpec<C>(body), context)

    override fun scaffold(): Scaffold<Field> {
        val builder = FieldBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class FieldSpec<in C>(internal val body: FieldBuilder<C>.() -> Unit)

@DslBuilder
class FieldBuilder<out C>(val context: C, internal var shell: FieldShell = FieldShell()) {
    fun <C> FieldBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> FieldBuilder<C>.key(key: Boolean) {
        shell = shell.copy(key = key)
    }

    fun <C> FieldBuilder<C>.type(spec: StructSpec<C>) {
        shell = shell.copy(type = StructTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: UnionSpec<C>) {
        shell = shell.copy(type = UnionTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = EnumTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = RefTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = OptionTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = ListTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = MapTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        shell = shell.copy(type = BooleanTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: BooleanTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        shell = shell.copy(type = DoubleTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: DoubleTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: FloatTypeSpec<C>) {
        shell = shell.copy(type = FloatTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: FloatTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: LongTypeSpec<C>) {
        shell = shell.copy(type = LongTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: LongTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: IntTypeSpec<C>) {
        shell = shell.copy(type = IntTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: IntTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ShortTypeSpec<C>) {
        shell = shell.copy(type = ShortTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: ShortTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: ByteTypeSpec<C>) {
        shell = shell.copy(type = ByteTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: ByteTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        shell = shell.copy(type = CharacterTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: CharacterTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: StringTypeSpec<C>) {
        shell = shell.copy(type = StringTypeTemplate<C>(spec, context).scaffold())
    }

    fun <C> FieldBuilder<C>.type(ref: StringTypeRef) {
        shell = shell.copy(type = ref)
    }

    fun <C> FieldBuilder<C>.type(spec: AnyTypeSpec<C>) {
        shell = shell.copy(type = AnyTypeTemplate<C>(spec, context).scaffold())
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

open class FieldTranslator<in C>(private val spec: FieldSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Field> {
    constructor(body: FieldBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.schema.v0.FieldSpec<C>(body), registry)

    override suspend fun translate(context: C): Field {
        val builder = FieldTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}
