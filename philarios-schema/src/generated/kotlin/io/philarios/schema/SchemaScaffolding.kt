// The implementation specifics required for materializing the specs into the model classes.
//
// Unless you are really curious about how the sausage gets made there is almost no reason to take a look at this
// file. The approach taken is rather opinionated and changes to this file are the most frequent.
//
// In case you want to report any issues or bugs with the materialization process, please feel free to open an
// issue in the project's repository.
package io.philarios.schema

import io.philarios.core.DslBuilder
import io.philarios.core.RefScaffold
import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.ValueScaffold
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SchemaScaffolder(internal val spec: SchemaSpec) : Scaffolder<Schema> {
    override fun createScaffold(): Scaffold<Schema> {
        val builder = SchemaScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class SchemaScaffoldBuilder(internal var scaffold: SchemaScaffold = SchemaScaffold()) : SchemaBuilder {
    override fun pkg(value: String) {
        scaffold = scaffold.copy(pkg = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(types = scaffold.types.orEmpty() + TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        scaffold = scaffold.copy(types = scaffold.types.orEmpty() + RefScaffold(ref.key))
    }

    override fun <T : Type> type(value: T) {
        scaffold = scaffold.copy(types = scaffold.types.orEmpty() + ValueScaffold(value))
    }
}

internal data class SchemaScaffold(
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
        val builder = StructScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class UnionScaffolder(internal val spec: UnionSpec) : Scaffolder<Union> {
    override fun createScaffold(): Scaffold<Union> {
        val builder = UnionScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class EnumTypeScaffolder(internal val spec: EnumTypeSpec) : Scaffolder<EnumType> {
    override fun createScaffold(): Scaffold<EnumType> {
        val builder = EnumTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class RefTypeScaffolder(internal val spec: RefTypeSpec) : Scaffolder<RefType> {
    override fun createScaffold(): Scaffold<RefType> {
        val builder = RefTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class OptionTypeScaffolder(internal val spec: OptionTypeSpec) : Scaffolder<OptionType> {
    override fun createScaffold(): Scaffold<OptionType> {
        val builder = OptionTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class ListTypeScaffolder(internal val spec: ListTypeSpec) : Scaffolder<ListType> {
    override fun createScaffold(): Scaffold<ListType> {
        val builder = ListTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class MapTypeScaffolder(internal val spec: MapTypeSpec) : Scaffolder<MapType> {
    override fun createScaffold(): Scaffold<MapType> {
        val builder = MapTypeScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

class BooleanTypeScaffolder(internal val spec: BooleanTypeSpec) : Scaffolder<BooleanType> {
    override fun createScaffold(): Scaffold<BooleanType> = ValueScaffold(BooleanType)
}

class DoubleTypeScaffolder(internal val spec: DoubleTypeSpec) : Scaffolder<DoubleType> {
    override fun createScaffold(): Scaffold<DoubleType> = ValueScaffold(DoubleType)
}

class FloatTypeScaffolder(internal val spec: FloatTypeSpec) : Scaffolder<FloatType> {
    override fun createScaffold(): Scaffold<FloatType> = ValueScaffold(FloatType)
}

class LongTypeScaffolder(internal val spec: LongTypeSpec) : Scaffolder<LongType> {
    override fun createScaffold(): Scaffold<LongType> = ValueScaffold(LongType)
}

class IntTypeScaffolder(internal val spec: IntTypeSpec) : Scaffolder<IntType> {
    override fun createScaffold(): Scaffold<IntType> = ValueScaffold(IntType)
}

class ShortTypeScaffolder(internal val spec: ShortTypeSpec) : Scaffolder<ShortType> {
    override fun createScaffold(): Scaffold<ShortType> = ValueScaffold(ShortType)
}

class ByteTypeScaffolder(internal val spec: ByteTypeSpec) : Scaffolder<ByteType> {
    override fun createScaffold(): Scaffold<ByteType> = ValueScaffold(ByteType)
}

class CharacterTypeScaffolder(internal val spec: CharacterTypeSpec) : Scaffolder<CharacterType> {
    override fun createScaffold(): Scaffold<CharacterType> = ValueScaffold(CharacterType)
}

class StringTypeScaffolder(internal val spec: StringTypeSpec) : Scaffolder<StringType> {
    override fun createScaffold(): Scaffold<StringType> = ValueScaffold(StringType)
}

class AnyTypeScaffolder(internal val spec: AnyTypeSpec) : Scaffolder<AnyType> {
    override fun createScaffold(): Scaffold<AnyType> = ValueScaffold(AnyType)
}

@DslBuilder
internal class StructScaffoldBuilder(internal var scaffold: StructScaffold = StructScaffold()) : StructBuilder {
    override fun pkg(value: String) {
        scaffold = scaffold.copy(pkg = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun field(body: FieldBuilder.() -> Unit) {
        scaffold = scaffold.copy(fields = scaffold.fields.orEmpty() + FieldScaffolder(FieldSpec(body)).createScaffold())
    }

    override fun field(spec: FieldSpec) {
        scaffold = scaffold.copy(fields = scaffold.fields.orEmpty() + FieldScaffolder(spec).createScaffold())
    }

    override fun field(ref: FieldRef) {
        scaffold = scaffold.copy(fields = scaffold.fields.orEmpty() + RefScaffold(ref.key))
    }

    override fun field(value: Field) {
        scaffold = scaffold.copy(fields = scaffold.fields.orEmpty() + ValueScaffold(value))
    }

    override fun fields(fields: List<Field>) {
        scaffold = scaffold.copy(fields = scaffold.fields.orEmpty() + fields.map { ValueScaffold(it) })
    }
}

@DslBuilder
internal class UnionScaffoldBuilder(internal var scaffold: UnionScaffold = UnionScaffold()) : UnionBuilder {
    override fun pkg(value: String) {
        scaffold = scaffold.copy(pkg = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun shape(body: StructBuilder.() -> Unit) {
        scaffold = scaffold.copy(shapes = scaffold.shapes.orEmpty() + StructScaffolder(StructSpec(body)).createScaffold())
    }

    override fun shape(spec: StructSpec) {
        scaffold = scaffold.copy(shapes = scaffold.shapes.orEmpty() + StructScaffolder(spec).createScaffold())
    }

    override fun shape(ref: StructRef) {
        scaffold = scaffold.copy(shapes = scaffold.shapes.orEmpty() + RefScaffold(ref.key))
    }

    override fun shape(value: Struct) {
        scaffold = scaffold.copy(shapes = scaffold.shapes.orEmpty() + ValueScaffold(value))
    }

    override fun shapes(shapes: List<Struct>) {
        scaffold = scaffold.copy(shapes = scaffold.shapes.orEmpty() + shapes.map { ValueScaffold(it) })
    }
}

@DslBuilder
internal class EnumTypeScaffoldBuilder(internal var scaffold: EnumTypeScaffold = EnumTypeScaffold()) : EnumTypeBuilder {
    override fun pkg(value: String) {
        scaffold = scaffold.copy(pkg = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun value(value: String) {
        scaffold = scaffold.copy(values = scaffold.values.orEmpty() + ValueScaffold(value))
    }

    override fun values(values: List<String>) {
        scaffold = scaffold.copy(values = scaffold.values.orEmpty() + values.map { ValueScaffold(it) })
    }
}

@DslBuilder
internal class RefTypeScaffoldBuilder(internal var scaffold: RefTypeScaffold = RefTypeScaffold()) : RefTypeBuilder {
    override fun pkg(value: String) {
        scaffold = scaffold.copy(pkg = ValueScaffold(value))
    }

    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }
}

@DslBuilder
internal class OptionTypeScaffoldBuilder(internal var scaffold: OptionTypeScaffold = OptionTypeScaffold()) : OptionTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        scaffold = scaffold.copy(type = RefScaffold(ref.key))
    }

    override fun <T : Type> type(value: T) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }
}

@DslBuilder
internal class ListTypeScaffoldBuilder(internal var scaffold: ListTypeScaffold = ListTypeScaffold()) : ListTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        scaffold = scaffold.copy(type = RefScaffold(ref.key))
    }

    override fun <T : Type> type(value: T) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }
}

@DslBuilder
internal class MapTypeScaffoldBuilder(internal var scaffold: MapTypeScaffold = MapTypeScaffold()) : MapTypeBuilder {
    override fun <T : Type> keyType(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(keyType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> keyType(ref: TypeRef<T>) {
        scaffold = scaffold.copy(keyType = RefScaffold(ref.key))
    }

    override fun <T : Type> keyType(value: T) {
        scaffold = scaffold.copy(keyType = ValueScaffold(value))
    }

    override fun <T : Type> valueType(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(valueType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> valueType(ref: TypeRef<T>) {
        scaffold = scaffold.copy(valueType = RefScaffold(ref.key))
    }

    override fun <T : Type> valueType(value: T) {
        scaffold = scaffold.copy(valueType = ValueScaffold(value))
    }
}

internal sealed class TypeScaffold

internal data class StructScaffold(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var fields: List<Scaffold<Field>>? = null
) : TypeScaffold(), Scaffold<Struct> {
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

internal data class UnionScaffold(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var shapes: List<Scaffold<Struct>>? = null
) : TypeScaffold(), Scaffold<Union> {
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

internal data class EnumTypeScaffold(
        var pkg: Scaffold<String>? = null,
        var name: Scaffold<String>? = null,
        var values: List<Scaffold<String>>? = null
) : TypeScaffold(), Scaffold<EnumType> {
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

internal data class RefTypeScaffold(var pkg: Scaffold<String>? = null, var name: Scaffold<String>? = null) : TypeScaffold(),
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

internal data class OptionTypeScaffold(var type: Scaffold<Type>? = null) : TypeScaffold(),
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

internal data class ListTypeScaffold(var type: Scaffold<Type>? = null) : TypeScaffold(),
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

internal data class MapTypeScaffold(var keyType: Scaffold<Type>? = null, var valueType: Scaffold<Type>? = null) : TypeScaffold(),
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
        val builder = FieldScaffoldBuilder()
        builder.apply(spec.body)
        return builder.scaffold
    }
}

@DslBuilder
internal class FieldScaffoldBuilder(internal var scaffold: FieldScaffold = FieldScaffold()) : FieldBuilder {
    override fun name(value: String) {
        scaffold = scaffold.copy(name = ValueScaffold(value))
    }

    override fun key(value: Boolean) {
        scaffold = scaffold.copy(key = ValueScaffold(value))
    }

    override fun singularName(value: String) {
        scaffold = scaffold.copy(singularName = ValueScaffold(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        scaffold = scaffold.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        scaffold = scaffold.copy(type = RefScaffold(ref.key))
    }

    override fun <T : Type> type(value: T) {
        scaffold = scaffold.copy(type = ValueScaffold(value))
    }
}

internal data class FieldScaffold(
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
