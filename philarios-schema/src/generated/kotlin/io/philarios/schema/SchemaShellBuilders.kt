package io.philarios.schema

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

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
        shell = shell.copy(types = shell.types.orEmpty() + ref)
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

@DslBuilder
internal class FieldShellBuilder<out C>(override val context: C, internal var shell: FieldShell = FieldShell()) : FieldBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun key(value: Boolean) {
        shell = shell.copy(key = Wrapper(value))
    }

    override fun <T : Type> type(spec: TypeSpec<C, T>) {
        shell = shell.copy(type = TypeScaffolder<C, Type>(spec).createScaffold(context))
    }

    override fun <T : Type> type(ref: TypeRef<T>) {
        shell = shell.copy(type = ref)
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
