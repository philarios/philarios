// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.schema

import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SchemaScaffolder<in C>(internal val spec: SchemaSpec<C>) : Scaffolder<C, Schema> {
    override fun createScaffold(context: C): Scaffold<Schema> {
        val builder = SchemaShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SchemaShellBuilder<out C>(override val context: C, internal var shell: SchemaShell = SchemaShell()) : SchemaBuilder<C> {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(types = shell.types.orEmpty() + TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(types = shell.types.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(value))
    }

    override fun include(body: SchemaBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SchemaSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SchemaBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SchemaSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SchemaShellBuilder<C2> = SchemaShellBuilder(context, shell)

    private fun <C2> merge(other: SchemaShellBuilder<C2>) {
        this.shell = other.shell
    }
}

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
        shell = shell.copy(fields = shell.fields.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(shapes = shell.shapes.orEmpty() + Deferred(ref.key))
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
        shell = shell.copy(type = Deferred(ref.key))
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
        shell = shell.copy(type = Deferred(ref.key))
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
        shell = shell.copy(keyType = Deferred(ref.key))
    }

    override fun <T : Type> keyType(value: T) {
        shell = shell.copy(keyType = Wrapper(value))
    }

    override fun <T : Type> valueType(spec: TypeSpec<C, T>) {
        shell = shell.copy(valueType = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> valueType(ref: TypeRef<T>) {
        shell = shell.copy(valueType = Deferred(ref.key))
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

class FieldScaffolder<in C>(internal val spec: FieldSpec<C>) : Scaffolder<C, Field> {
    override fun createScaffold(context: C): Scaffold<Field> {
        val builder = FieldShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class FieldShellBuilder<out C>(override val context: C, internal var shell: FieldShell = FieldShell()) : FieldBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun key(value: Boolean) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun singularName(value: String) {
        shell = shell.copy(singularName = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(type = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }

    override fun include(body: FieldBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FieldSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FieldBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FieldSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FieldShellBuilder<C2> = FieldShellBuilder(context, shell)

    private fun <C2> merge(other: FieldShellBuilder<C2>) {
        this.shell = other.shell
    }
}

internal data class FieldShell(
        var name: Scaffold<String>? = null,
        var key: Scaffold<Boolean>? = null,
        var singularName: Scaffold<String>? = null,
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
            singularName?.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}
