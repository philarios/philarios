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
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SchemaScaffolder(internal val spec: SchemaSpec) : Scaffolder<Schema> {
    override fun createScaffold(): Scaffold<Schema> {
        val builder = SchemaResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

@DslBuilder
internal class SchemaResolvableBuilder(internal var resolvable: SchemaResolvable = SchemaResolvable()) : SchemaBuilder {
    override fun pkg(value: String) {
        resolvable = resolvable.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(types = resolvable.types.orEmpty() + TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        resolvable = resolvable.copy(types = resolvable.types.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        resolvable = resolvable.copy(types = resolvable.types.orEmpty() + Wrapper(value))
    }
}

internal data class SchemaResolvable(
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

class TypeScaffolder<out T : Type>(internal val spec: TypeSpec<T>) : Scaffolder<T> {
    override fun createScaffold(): Scaffold<T> {
        val result = when (spec) {
            is StructSpec -> StructScaffolder(spec).createScaffold()
            is UnionSpec -> UnionScaffolder(spec).createScaffold()
            is EnumTypeSpec -> EnumTypeScaffolder(spec).createScaffold()
            is RefTypeSpec -> RefTypeScaffolder(spec).createScaffold()
            is OptionTypeSpec -> OptionTypeScaffolder(spec).createScaffold()
            is ListTypeSpec -> ListTypeScaffolder(spec).createScaffold()
            is MapTypeSpec -> MapTypeScaffolder(spec).createScaffold()
            is BooleanTypeSpec -> BooleanTypeScaffolder(spec).createScaffold()
            is DoubleTypeSpec -> DoubleTypeScaffolder(spec).createScaffold()
            is FloatTypeSpec -> FloatTypeScaffolder(spec).createScaffold()
            is LongTypeSpec -> LongTypeScaffolder(spec).createScaffold()
            is IntTypeSpec -> IntTypeScaffolder(spec).createScaffold()
            is ShortTypeSpec -> ShortTypeScaffolder(spec).createScaffold()
            is ByteTypeSpec -> ByteTypeScaffolder(spec).createScaffold()
            is CharacterTypeSpec -> CharacterTypeScaffolder(spec).createScaffold()
            is StringTypeSpec -> StringTypeScaffolder(spec).createScaffold()
            is AnyTypeSpec -> AnyTypeScaffolder(spec).createScaffold()
        }
        return result as Scaffold<T>
    }
}

class StructScaffolder(internal val spec: StructSpec) : Scaffolder<Struct> {
    override fun createScaffold(): Scaffold<Struct> {
        val builder = StructResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class UnionScaffolder(internal val spec: UnionSpec) : Scaffolder<Union> {
    override fun createScaffold(): Scaffold<Union> {
        val builder = UnionResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class EnumTypeScaffolder(internal val spec: EnumTypeSpec) : Scaffolder<EnumType> {
    override fun createScaffold(): Scaffold<EnumType> {
        val builder = EnumTypeResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class RefTypeScaffolder(internal val spec: RefTypeSpec) : Scaffolder<RefType> {
    override fun createScaffold(): Scaffold<RefType> {
        val builder = RefTypeResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class OptionTypeScaffolder(internal val spec: OptionTypeSpec) : Scaffolder<OptionType> {
    override fun createScaffold(): Scaffold<OptionType> {
        val builder = OptionTypeResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class ListTypeScaffolder(internal val spec: ListTypeSpec) : Scaffolder<ListType> {
    override fun createScaffold(): Scaffold<ListType> {
        val builder = ListTypeResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class MapTypeScaffolder(internal val spec: MapTypeSpec) : Scaffolder<MapType> {
    override fun createScaffold(): Scaffold<MapType> {
        val builder = MapTypeResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

class BooleanTypeScaffolder(internal val spec: BooleanTypeSpec) : Scaffolder<BooleanType> {
    override fun createScaffold(): Scaffold<BooleanType> = Wrapper(BooleanType)
}

class DoubleTypeScaffolder(internal val spec: DoubleTypeSpec) : Scaffolder<DoubleType> {
    override fun createScaffold(): Scaffold<DoubleType> = Wrapper(DoubleType)
}

class FloatTypeScaffolder(internal val spec: FloatTypeSpec) : Scaffolder<FloatType> {
    override fun createScaffold(): Scaffold<FloatType> = Wrapper(FloatType)
}

class LongTypeScaffolder(internal val spec: LongTypeSpec) : Scaffolder<LongType> {
    override fun createScaffold(): Scaffold<LongType> = Wrapper(LongType)
}

class IntTypeScaffolder(internal val spec: IntTypeSpec) : Scaffolder<IntType> {
    override fun createScaffold(): Scaffold<IntType> = Wrapper(IntType)
}

class ShortTypeScaffolder(internal val spec: ShortTypeSpec) : Scaffolder<ShortType> {
    override fun createScaffold(): Scaffold<ShortType> = Wrapper(ShortType)
}

class ByteTypeScaffolder(internal val spec: ByteTypeSpec) : Scaffolder<ByteType> {
    override fun createScaffold(): Scaffold<ByteType> = Wrapper(ByteType)
}

class CharacterTypeScaffolder(internal val spec: CharacterTypeSpec) : Scaffolder<CharacterType> {
    override fun createScaffold(): Scaffold<CharacterType> = Wrapper(CharacterType)
}

class StringTypeScaffolder(internal val spec: StringTypeSpec) : Scaffolder<StringType> {
    override fun createScaffold(): Scaffold<StringType> = Wrapper(StringType)
}

class AnyTypeScaffolder(internal val spec: AnyTypeSpec) : Scaffolder<AnyType> {
    override fun createScaffold(): Scaffold<AnyType> = Wrapper(AnyType)
}

@DslBuilder
internal class StructResolvableBuilder(internal var resolvable: StructResolvable = StructResolvable()) : StructBuilder {
    override fun pkg(value: String) {
        resolvable = resolvable.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }

    override fun field(body: FieldBuilder.() -> Unit) {
        resolvable = resolvable.copy(fields = resolvable.fields.orEmpty() + FieldScaffolder(FieldSpec(body)).createScaffold())
    }

    override fun field(spec: FieldSpec) {
        resolvable = resolvable.copy(fields = resolvable.fields.orEmpty() + FieldScaffolder(spec).createScaffold())
    }

    override fun field(ref: FieldRef) {
        resolvable = resolvable.copy(fields = resolvable.fields.orEmpty() + Deferred(ref.key))
    }

    override fun field(value: Field) {
        resolvable = resolvable.copy(fields = resolvable.fields.orEmpty() + Wrapper(value))
    }

    override fun fields(fields: List<Field>) {
        resolvable = resolvable.copy(fields = resolvable.fields.orEmpty() + fields.map { Wrapper(it) })
    }
}

@DslBuilder
internal class UnionResolvableBuilder(internal var resolvable: UnionResolvable = UnionResolvable()) : UnionBuilder {
    override fun pkg(value: String) {
        resolvable = resolvable.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }

    override fun shape(body: StructBuilder.() -> Unit) {
        resolvable = resolvable.copy(shapes = resolvable.shapes.orEmpty() + StructScaffolder(StructSpec(body)).createScaffold())
    }

    override fun shape(spec: StructSpec) {
        resolvable = resolvable.copy(shapes = resolvable.shapes.orEmpty() + StructScaffolder(spec).createScaffold())
    }

    override fun shape(ref: StructRef) {
        resolvable = resolvable.copy(shapes = resolvable.shapes.orEmpty() + Deferred(ref.key))
    }

    override fun shape(value: Struct) {
        resolvable = resolvable.copy(shapes = resolvable.shapes.orEmpty() + Wrapper(value))
    }

    override fun shapes(shapes: List<Struct>) {
        resolvable = resolvable.copy(shapes = resolvable.shapes.orEmpty() + shapes.map { Wrapper(it) })
    }
}

@DslBuilder
internal class EnumTypeResolvableBuilder(internal var resolvable: EnumTypeResolvable = EnumTypeResolvable()) : EnumTypeBuilder {
    override fun pkg(value: String) {
        resolvable = resolvable.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }

    override fun value(value: String) {
        resolvable = resolvable.copy(values = resolvable.values.orEmpty() + Wrapper(value))
    }

    override fun values(values: List<String>) {
        resolvable = resolvable.copy(values = resolvable.values.orEmpty() + values.map { Wrapper(it) })
    }
}

@DslBuilder
internal class RefTypeResolvableBuilder(internal var resolvable: RefTypeResolvable = RefTypeResolvable()) : RefTypeBuilder {
    override fun pkg(value: String) {
        resolvable = resolvable.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }
}

@DslBuilder
internal class OptionTypeResolvableBuilder(internal var resolvable: OptionTypeResolvable = OptionTypeResolvable()) : OptionTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        resolvable = resolvable.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        resolvable = resolvable.copy(type = Wrapper(value))
    }
}

@DslBuilder
internal class ListTypeResolvableBuilder(internal var resolvable: ListTypeResolvable = ListTypeResolvable()) : ListTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        resolvable = resolvable.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        resolvable = resolvable.copy(type = Wrapper(value))
    }
}

@DslBuilder
internal class MapTypeResolvableBuilder(internal var resolvable: MapTypeResolvable = MapTypeResolvable()) : MapTypeBuilder {
    override fun <T : Type> keyType(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(keyType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> keyType(ref: TypeRef<T>) {
        resolvable = resolvable.copy(keyType = Deferred(ref.key))
    }

    override fun <T : Type> keyType(value: T) {
        resolvable = resolvable.copy(keyType = Wrapper(value))
    }

    override fun <T : Type> valueType(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(valueType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> valueType(ref: TypeRef<T>) {
        resolvable = resolvable.copy(valueType = Deferred(ref.key))
    }

    override fun <T : Type> valueType(value: T) {
        resolvable = resolvable.copy(valueType = Wrapper(value))
    }
}

internal sealed class TypeResolvable

internal data class StructResolvable(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var fields: List<Scaffold<Field>>? = null
) : TypeResolvable(), Scaffold<Struct> {
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
        registry.put(Type::class, value.name, value)
        return value
    }
}

internal data class UnionResolvable(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var shapes: List<Scaffold<Struct>>? = null
) : TypeResolvable(), Scaffold<Union> {
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
        registry.put(Type::class, value.name, value)
        return value
    }
}

internal data class EnumTypeResolvable(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var values: List<Scaffold<String>>? = null
) : TypeResolvable(), Scaffold<EnumType> {
    override suspend fun resolve(registry: Registry): EnumType {
        checkNotNull(name) { "EnumType is missing the name property" }
        val value = EnumType(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) },
            values.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(EnumType::class, value.name, value)
        registry.put(Type::class, value.name, value)
        return value
    }
}

internal data class RefTypeResolvable(var pkg: Scaffold<String>? = null, var name: Scaffold<String>? = null) : TypeResolvable(),
        Scaffold<RefType> {
    override suspend fun resolve(registry: Registry): RefType {
        checkNotNull(name) { "RefType is missing the name property" }
        val value = RefType(
            pkg?.let{ it.resolve(registry) },
            name!!.let{ it.resolve(registry) }
        )
        registry.put(RefType::class, value.name, value)
        registry.put(Type::class, value.name, value)
        return value
    }
}

internal data class OptionTypeResolvable(var type: Scaffold<Type>? = null) : TypeResolvable(),
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

internal data class ListTypeResolvable(var type: Scaffold<Type>? = null) : TypeResolvable(),
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

internal data class MapTypeResolvable(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeResolvable(),
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

class FieldScaffolder(internal val spec: FieldSpec) : Scaffolder<Field> {
    override fun createScaffold(): Scaffold<Field> {
        val builder = FieldResolvableBuilder()
        builder.apply(spec.body)
        return builder.resolvable
    }
}

@DslBuilder
internal class FieldResolvableBuilder(internal var resolvable: FieldResolvable = FieldResolvable()) : FieldBuilder {
    override fun name(value: String) {
        resolvable = resolvable.copy(name = Wrapper(value))
    }

    override fun key(value: Boolean) {
        resolvable = resolvable.copy(key = Wrapper(value))
    }

    override fun singularName(value: String) {
        resolvable = resolvable.copy(singularName = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        resolvable = resolvable.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        resolvable = resolvable.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        resolvable = resolvable.copy(type = Wrapper(value))
    }
}

internal data class FieldResolvable(
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
