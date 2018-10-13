package io.philarios.schema.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

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