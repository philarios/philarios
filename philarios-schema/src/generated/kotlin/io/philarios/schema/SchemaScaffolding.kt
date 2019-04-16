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
        val builder = SchemaShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class SchemaShellBuilder(internal var shell: SchemaShell = SchemaShell()) : SchemaBuilder {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        shell = shell.copy(types = shell.types.orEmpty() + TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(types = shell.types.orEmpty() + Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(value))
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
        val builder = StructShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class UnionScaffolder(internal val spec: UnionSpec) : Scaffolder<Union> {
    override fun createScaffold(): Scaffold<Union> {
        val builder = UnionShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class EnumTypeScaffolder(internal val spec: EnumTypeSpec) : Scaffolder<EnumType> {
    override fun createScaffold(): Scaffold<EnumType> {
        val builder = EnumTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class RefTypeScaffolder(internal val spec: RefTypeSpec) : Scaffolder<RefType> {
    override fun createScaffold(): Scaffold<RefType> {
        val builder = RefTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class OptionTypeScaffolder(internal val spec: OptionTypeSpec) : Scaffolder<OptionType> {
    override fun createScaffold(): Scaffold<OptionType> {
        val builder = OptionTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class ListTypeScaffolder(internal val spec: ListTypeSpec) : Scaffolder<ListType> {
    override fun createScaffold(): Scaffold<ListType> {
        val builder = ListTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

class MapTypeScaffolder(internal val spec: MapTypeSpec) : Scaffolder<MapType> {
    override fun createScaffold(): Scaffold<MapType> {
        val builder = MapTypeShellBuilder()
        builder.apply(spec.body)
        return builder.shell
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
internal class StructShellBuilder(internal var shell: StructShell = StructShell()) : StructBuilder {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun field(body: FieldBuilder.() -> Unit) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldScaffolder(FieldSpec(body)).createScaffold())
    }

    override fun field(spec: FieldSpec) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldScaffolder(spec).createScaffold())
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
}

@DslBuilder
internal class UnionShellBuilder(internal var shell: UnionShell = UnionShell()) : UnionBuilder {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun shape(body: StructBuilder.() -> Unit) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructScaffolder(StructSpec(body)).createScaffold())
    }

    override fun shape(spec: StructSpec) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructScaffolder(spec).createScaffold())
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
}

@DslBuilder
internal class EnumTypeShellBuilder(internal var shell: EnumTypeShell = EnumTypeShell()) : EnumTypeBuilder {
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
}

@DslBuilder
internal class RefTypeShellBuilder(internal var shell: RefTypeShell = RefTypeShell()) : RefTypeBuilder {
    override fun pkg(value: String) {
        shell = shell.copy(pkg = Wrapper(value))
    }

    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }
}

@DslBuilder
internal class OptionTypeShellBuilder(internal var shell: OptionTypeShell = OptionTypeShell()) : OptionTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        shell = shell.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }
}

@DslBuilder
internal class ListTypeShellBuilder(internal var shell: ListTypeShell = ListTypeShell()) : ListTypeBuilder {
    override fun <T : Type> type(spec: TypeSpec<T>) {
        shell = shell.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
    }
}

@DslBuilder
internal class MapTypeShellBuilder(internal var shell: MapTypeShell = MapTypeShell()) : MapTypeBuilder {
    override fun <T : Type> keyType(spec: TypeSpec<T>) {
        shell = shell.copy(keyType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> keyType(ref: TypeRef<T>) {
        shell = shell.copy(keyType = Deferred(ref.key))
    }

    override fun <T : Type> keyType(value: T) {
        shell = shell.copy(keyType = Wrapper(value))
    }

    override fun <T : Type> valueType(spec: TypeSpec<T>) {
        shell = shell.copy(valueType = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> valueType(ref: TypeRef<T>) {
        shell = shell.copy(valueType = Deferred(ref.key))
    }

    override fun <T : Type> valueType(value: T) {
        shell = shell.copy(valueType = Wrapper(value))
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
        registry.put(Type::class, value.name, value)
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
        registry.put(Type::class, value.name, value)
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
        registry.put(Type::class, value.name, value)
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
        registry.put(Type::class, value.name, value)
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

class FieldScaffolder(internal val spec: FieldSpec) : Scaffolder<Field> {
    override fun createScaffold(): Scaffold<Field> {
        val builder = FieldShellBuilder()
        builder.apply(spec.body)
        return builder.shell
    }
}

@DslBuilder
internal class FieldShellBuilder(internal var shell: FieldShell = FieldShell()) : FieldBuilder {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun key(value: Boolean) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun singularName(value: String) {
        shell = shell.copy(singularName = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<T>) {
        shell = shell.copy(type = TypeScaffolder<Type>(spec).createScaffold())
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = Deferred(ref.key))
    }

    override fun <T : Type> type(value: T) {
        shell = shell.copy(type = Wrapper(value))
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
