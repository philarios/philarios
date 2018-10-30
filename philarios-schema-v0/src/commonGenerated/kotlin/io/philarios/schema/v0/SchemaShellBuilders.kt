package io.philarios.schema.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
internal class SchemaShellBuilder<out C>(override val context: C, internal var shell: SchemaShell = SchemaShell()) : SchemaBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    override fun type(spec: StructSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: StructRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: UnionSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: UnionRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: EnumTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: RefTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: RefTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: OptionTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: ListTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: ListTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(spec: MapTypeSpec<C>) {
        shell = shell.copy(types = shell.types.orEmpty() + spec.connect(context))
    }

    override fun type(ref: MapTypeRef) {
        shell = shell.copy(types = shell.types.orEmpty() + ref)
    }

    override fun type(type: BooleanType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: DoubleType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: FloatType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: LongType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: IntType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: ShortType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: ByteType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: CharacterType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: StringType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun type(type: AnyType) {
        shell = shell.copy(types = shell.types.orEmpty() + Wrapper(type))
    }

    override fun reference(body: SchemaBuilder<C>.() -> Unit) {
        shell = shell.copy(references = shell.references.orEmpty() + SchemaSpec<C>(body).connect(context))
    }

    override fun reference(spec: SchemaSpec<C>) {
        shell = shell.copy(references = shell.references.orEmpty() + spec.connect(context))
    }

    override fun reference(ref: SchemaRef) {
        shell = shell.copy(references = shell.references.orEmpty() + ref)
    }

    override fun reference(reference: Schema) {
        shell = shell.copy(references = shell.references.orEmpty() + Wrapper(reference))
    }

    override fun references(references: List<Schema>) {
        shell = shell.copy(references = shell.references.orEmpty() + references.map { Wrapper(it) })
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

@DslBuilder
internal class StructShellBuilder<out C>(override val context: C, internal var shell: StructShell = StructShell()) : StructBuilder<C> {
    override fun pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun field(body: FieldBuilder<C>.() -> Unit) {
        shell = shell.copy(fields = shell.fields.orEmpty() + FieldSpec<C>(body).connect(context))
    }

    override fun field(spec: FieldSpec<C>) {
        shell = shell.copy(fields = shell.fields.orEmpty() + spec.connect(context))
    }

    override fun field(ref: FieldRef) {
        shell = shell.copy(fields = shell.fields.orEmpty() + ref)
    }

    override fun field(field: Field) {
        shell = shell.copy(fields = shell.fields.orEmpty() + Wrapper(field))
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
    override fun pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun shape(body: StructBuilder<C>.() -> Unit) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + StructSpec<C>(body).connect(context))
    }

    override fun shape(spec: StructSpec<C>) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + spec.connect(context))
    }

    override fun shape(ref: StructRef) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + ref)
    }

    override fun shape(shape: Struct) {
        shell = shell.copy(shapes = shell.shapes.orEmpty() + Wrapper(shape))
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
    override fun pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun value(value: String) {
        shell = shell.copy(values = shell.values.orEmpty() + value)
    }

    override fun values(values: List<String>) {
        shell = shell.copy(values = shell.values.orEmpty() + values)
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
    override fun pkg(pkg: String) {
        shell = shell.copy(pkg = pkg)
    }

    override fun name(name: String) {
        shell = shell.copy(name = name)
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
    override fun type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(type: BooleanType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: DoubleType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: FloatType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: LongType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: IntType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ShortType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ByteType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: CharacterType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: StringType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: AnyType) {
        shell = shell.copy(type = Wrapper(type))
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
    override fun type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(type: BooleanType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: DoubleType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: FloatType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: LongType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: IntType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ShortType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ByteType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: CharacterType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: StringType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: AnyType) {
        shell = shell.copy(type = Wrapper(type))
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
    override fun keyType(spec: StructSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: StructRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: UnionSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: UnionRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: EnumTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: RefTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: RefTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: OptionTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: ListTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: ListTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(spec: MapTypeSpec<C>) {
        shell = shell.copy(keyType = spec.connect(context))
    }

    override fun keyType(ref: MapTypeRef) {
        shell = shell.copy(keyType = ref)
    }

    override fun keyType(keyType: BooleanType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: DoubleType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: FloatType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: LongType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: IntType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: ShortType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: ByteType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: CharacterType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: StringType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun keyType(keyType: AnyType) {
        shell = shell.copy(keyType = Wrapper(keyType))
    }

    override fun valueType(spec: StructSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: StructRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: UnionSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: UnionRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: EnumTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: EnumTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: RefTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: RefTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: OptionTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: OptionTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: ListTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: ListTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(spec: MapTypeSpec<C>) {
        shell = shell.copy(valueType = spec.connect(context))
    }

    override fun valueType(ref: MapTypeRef) {
        shell = shell.copy(valueType = ref)
    }

    override fun valueType(valueType: BooleanType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: DoubleType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: FloatType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: LongType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: IntType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: ShortType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: ByteType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: CharacterType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: StringType) {
        shell = shell.copy(valueType = Wrapper(valueType))
    }

    override fun valueType(valueType: AnyType) {
        shell = shell.copy(valueType = Wrapper(valueType))
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

@DslBuilder
internal class FieldShellBuilder<out C>(override val context: C, internal var shell: FieldShell = FieldShell()) : FieldBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun key(key: Boolean) {
        shell = shell.copy(key = key)
    }

    override fun type(spec: StructSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: StructRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: UnionSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: UnionRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: EnumTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: EnumTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: RefTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: RefTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: OptionTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: OptionTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: ListTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: ListTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(spec: MapTypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: MapTypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(type: BooleanType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: DoubleType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: FloatType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: LongType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: IntType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ShortType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: ByteType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: CharacterType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: StringType) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun type(type: AnyType) {
        shell = shell.copy(type = Wrapper(type))
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
