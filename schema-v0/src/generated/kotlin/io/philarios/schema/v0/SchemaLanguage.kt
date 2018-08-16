package io.philarios.schema.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

data class Schema(
        val name: String,
        val pkg: String,
        val types: List<Type>
) {
    companion object {
        operator fun <C> invoke(body: SchemaBuilder<C>.() -> Unit): SchemaSpec<C> = SchemaSpec<C>(body)
    }
}

@DslBuilder
class SchemaBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var pkg: String? = null,
        private var types: List<Type>? = emptyList()
) : Builder<Schema> {
    fun <C> SchemaBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> SchemaBuilder<C>.pkg(pkg: String) {
        this.pkg = pkg
    }

    fun <C> SchemaBuilder<C>.type(spec: StructSpec<C>) {
        this.types = this.types.orEmpty() + StructTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: UnionSpec<C>) {
        this.types = this.types.orEmpty() + UnionTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: EnumTypeSpec<C>) {
        this.types = this.types.orEmpty() + EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: OptionTypeSpec<C>) {
        this.types = this.types.orEmpty() + OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: ListTypeSpec<C>) {
        this.types = this.types.orEmpty() + ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: MapTypeSpec<C>) {
        this.types = this.types.orEmpty() + MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        this.types = this.types.orEmpty() + BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        this.types = this.types.orEmpty() + DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: FloatTypeSpec<C>) {
        this.types = this.types.orEmpty() + FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: LongTypeSpec<C>) {
        this.types = this.types.orEmpty() + LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: IntTypeSpec<C>) {
        this.types = this.types.orEmpty() + IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: ShortTypeSpec<C>) {
        this.types = this.types.orEmpty() + ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: ByteTypeSpec<C>) {
        this.types = this.types.orEmpty() + ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        this.types = this.types.orEmpty() + CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: StringTypeSpec<C>) {
        this.types = this.types.orEmpty() + StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: AnyTypeSpec<C>) {
        this.types = this.types.orEmpty() + AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> SchemaBuilder<C>.type(spec: RefTypeSpec<C>) {
        this.types = this.types.orEmpty() + RefTypeTranslator<C>(spec).translate(context)
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

    fun <C> SchemaBuilder<C>.include(body: SchemaBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> SchemaBuilder<C>.include(spec: SchemaSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> SchemaBuilder<C>.includeForEach(context: Iterable<C2>, body: SchemaBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> SchemaBuilder<C>.includeForEach(context: Iterable<C2>, spec: SchemaSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SchemaBuilder<C2> = SchemaBuilder(context,name,pkg,types)

    private fun <C2> merge(other: SchemaBuilder<C2>) {
        this.name = other.name
        this.pkg = other.pkg
        this.types = other.types
    }

    override fun build(): Schema = Schema(name!!,pkg!!,types!!)
}

open class SchemaSpec<in C>(internal val body: SchemaBuilder<C>.() -> Unit) : Spec<SchemaBuilder<C>, Schema> {
    override fun SchemaBuilder<C>.body() {
        this@SchemaSpec.body.invoke(this)
    }
}

open class SchemaTranslator<in C>(private val spec: SchemaSpec<C>) : Translator<C, Schema> {
    constructor(body: SchemaBuilder<C>.() -> Unit) : this(SchemaSpec<C>(body))

    override fun translate(context: C): Schema {
        val builder = SchemaBuilder(context)
        val translator = BuilderSpecTranslator<C, SchemaBuilder<C>, Schema>(builder, spec)
        return translator.translate(context)
    }
}

sealed class Type

data class Struct(val name: String, val fields: List<Field>) : Type() {
    companion object {
        operator fun <C> invoke(body: StructBuilder<C>.() -> Unit): StructSpec<C> = StructSpec<C>(body)
    }
}

data class Union(val name: String, val shapes: List<Struct>) : Type() {
    companion object {
        operator fun <C> invoke(body: UnionBuilder<C>.() -> Unit): UnionSpec<C> = UnionSpec<C>(body)
    }
}

data class EnumType(val name: String, val values: List<String>) : Type() {
    companion object {
        operator fun <C> invoke(body: EnumTypeBuilder<C>.() -> Unit): EnumTypeSpec<C> = EnumTypeSpec<C>(body)
    }
}

data class OptionType(val type: Type) : Type() {
    companion object {
        operator fun <C> invoke(body: OptionTypeBuilder<C>.() -> Unit): OptionTypeSpec<C> = OptionTypeSpec<C>(body)
    }
}

data class ListType(val type: Type) : Type() {
    companion object {
        operator fun <C> invoke(body: ListTypeBuilder<C>.() -> Unit): ListTypeSpec<C> = ListTypeSpec<C>(body)
    }
}

data class MapType(val keyType: Type, val valueType: Type) : Type() {
    companion object {
        operator fun <C> invoke(body: MapTypeBuilder<C>.() -> Unit): MapTypeSpec<C> = MapTypeSpec<C>(body)
    }
}

object BooleanType : Type() {
    operator fun <C> invoke(body: BooleanTypeBuilder<C>.() -> Unit): BooleanTypeSpec<C> = BooleanTypeSpec<C>(body)
}

object DoubleType : Type() {
    operator fun <C> invoke(body: DoubleTypeBuilder<C>.() -> Unit): DoubleTypeSpec<C> = DoubleTypeSpec<C>(body)
}

object FloatType : Type() {
    operator fun <C> invoke(body: FloatTypeBuilder<C>.() -> Unit): FloatTypeSpec<C> = FloatTypeSpec<C>(body)
}

object LongType : Type() {
    operator fun <C> invoke(body: LongTypeBuilder<C>.() -> Unit): LongTypeSpec<C> = LongTypeSpec<C>(body)
}

object IntType : Type() {
    operator fun <C> invoke(body: IntTypeBuilder<C>.() -> Unit): IntTypeSpec<C> = IntTypeSpec<C>(body)
}

object ShortType : Type() {
    operator fun <C> invoke(body: ShortTypeBuilder<C>.() -> Unit): ShortTypeSpec<C> = ShortTypeSpec<C>(body)
}

object ByteType : Type() {
    operator fun <C> invoke(body: ByteTypeBuilder<C>.() -> Unit): ByteTypeSpec<C> = ByteTypeSpec<C>(body)
}

object CharacterType : Type() {
    operator fun <C> invoke(body: CharacterTypeBuilder<C>.() -> Unit): CharacterTypeSpec<C> = CharacterTypeSpec<C>(body)
}

object StringType : Type() {
    operator fun <C> invoke(body: StringTypeBuilder<C>.() -> Unit): StringTypeSpec<C> = StringTypeSpec<C>(body)
}

object AnyType : Type() {
    operator fun <C> invoke(body: AnyTypeBuilder<C>.() -> Unit): AnyTypeSpec<C> = AnyTypeSpec<C>(body)
}

data class RefType(val name: String) : Type() {
    companion object {
        operator fun <C> invoke(body: RefTypeBuilder<C>.() -> Unit): RefTypeSpec<C> = RefTypeSpec<C>(body)
    }
}

@DslBuilder
class StructBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var fields: List<Field>? = emptyList()
) : Builder<Struct> {
    fun <C> StructBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> StructBuilder<C>.field(body: FieldBuilder<C>.() -> Unit) {
        this.fields = this.fields.orEmpty() + FieldTranslator<C>(body).translate(context)
    }

    fun <C> StructBuilder<C>.field(spec: FieldSpec<C>) {
        this.fields = this.fields.orEmpty() + FieldTranslator<C>(spec).translate(context)
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

    fun <C> StructBuilder<C>.include(body: StructBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> StructBuilder<C>.include(spec: StructSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> StructBuilder<C>.includeForEach(context: Iterable<C2>, body: StructBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> StructBuilder<C>.includeForEach(context: Iterable<C2>, spec: StructSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StructBuilder<C2> = StructBuilder(context,name,fields)

    private fun <C2> merge(other: StructBuilder<C2>) {
        this.name = other.name
        this.fields = other.fields
    }

    override fun build(): Struct = Struct(name!!,fields!!)
}

@DslBuilder
class UnionBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var shapes: List<Struct>? = emptyList()
) : Builder<Union> {
    fun <C> UnionBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> UnionBuilder<C>.shape(body: StructBuilder<C>.() -> Unit) {
        this.shapes = this.shapes.orEmpty() + StructTranslator<C>(body).translate(context)
    }

    fun <C> UnionBuilder<C>.shape(spec: StructSpec<C>) {
        this.shapes = this.shapes.orEmpty() + StructTranslator<C>(spec).translate(context)
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

    fun <C> UnionBuilder<C>.include(body: UnionBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> UnionBuilder<C>.include(spec: UnionSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> UnionBuilder<C>.includeForEach(context: Iterable<C2>, body: UnionBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> UnionBuilder<C>.includeForEach(context: Iterable<C2>, spec: UnionSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): UnionBuilder<C2> = UnionBuilder(context,name,shapes)

    private fun <C2> merge(other: UnionBuilder<C2>) {
        this.name = other.name
        this.shapes = other.shapes
    }

    override fun build(): Union = Union(name!!,shapes!!)
}

@DslBuilder
class EnumTypeBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var values: List<String>? = emptyList()
) : Builder<EnumType> {
    fun <C> EnumTypeBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> EnumTypeBuilder<C>.value(value: String) {
        this.values = this.values.orEmpty() + value
    }

    fun <C> EnumTypeBuilder<C>.values(values: List<String>) {
        this.values = this.values.orEmpty() + values
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

    fun <C> EnumTypeBuilder<C>.include(body: EnumTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> EnumTypeBuilder<C>.include(spec: EnumTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> EnumTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: EnumTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> EnumTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: EnumTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EnumTypeBuilder<C2> = EnumTypeBuilder(context,name,values)

    private fun <C2> merge(other: EnumTypeBuilder<C2>) {
        this.name = other.name
        this.values = other.values
    }

    override fun build(): EnumType = EnumType(name!!,values!!)
}

@DslBuilder
class OptionTypeBuilder<out C>(val context: C, private var type: Type? = null) : Builder<OptionType> {
    fun <C> OptionTypeBuilder<C>.type(spec: StructSpec<C>) {
        this.type = StructTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: UnionSpec<C>) {
        this.type = UnionTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        this.type = EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        this.type = OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        this.type = ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        this.type = MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        this.type = BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        this.type = DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        this.type = FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        this.type = LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        this.type = IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        this.type = ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        this.type = ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        this.type = CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        this.type = StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        this.type = AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> OptionTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        this.type = RefTypeTranslator<C>(spec).translate(context)
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

    fun <C> OptionTypeBuilder<C>.include(body: OptionTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> OptionTypeBuilder<C>.include(spec: OptionTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> OptionTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: OptionTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> OptionTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: OptionTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): OptionTypeBuilder<C2> = OptionTypeBuilder(context,type)

    private fun <C2> merge(other: OptionTypeBuilder<C2>) {
        this.type = other.type
    }

    override fun build(): OptionType = OptionType(type!!)
}

@DslBuilder
class ListTypeBuilder<out C>(val context: C, private var type: Type? = null) : Builder<ListType> {
    fun <C> ListTypeBuilder<C>.type(spec: StructSpec<C>) {
        this.type = StructTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: UnionSpec<C>) {
        this.type = UnionTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: EnumTypeSpec<C>) {
        this.type = EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: OptionTypeSpec<C>) {
        this.type = OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ListTypeSpec<C>) {
        this.type = ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: MapTypeSpec<C>) {
        this.type = MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        this.type = BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        this.type = DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: FloatTypeSpec<C>) {
        this.type = FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: LongTypeSpec<C>) {
        this.type = LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: IntTypeSpec<C>) {
        this.type = IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ShortTypeSpec<C>) {
        this.type = ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: ByteTypeSpec<C>) {
        this.type = ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        this.type = CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: StringTypeSpec<C>) {
        this.type = StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: AnyTypeSpec<C>) {
        this.type = AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> ListTypeBuilder<C>.type(spec: RefTypeSpec<C>) {
        this.type = RefTypeTranslator<C>(spec).translate(context)
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

    fun <C> ListTypeBuilder<C>.include(body: ListTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ListTypeBuilder<C>.include(spec: ListTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ListTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: ListTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ListTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: ListTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ListTypeBuilder<C2> = ListTypeBuilder(context,type)

    private fun <C2> merge(other: ListTypeBuilder<C2>) {
        this.type = other.type
    }

    override fun build(): ListType = ListType(type!!)
}

@DslBuilder
class MapTypeBuilder<out C>(
        val context: C,
        private var keyType: Type? = null,
        private var valueType: Type? = null
) : Builder<MapType> {
    fun <C> MapTypeBuilder<C>.keyType(spec: StructSpec<C>) {
        this.keyType = StructTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: UnionSpec<C>) {
        this.keyType = UnionTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: EnumTypeSpec<C>) {
        this.keyType = EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: OptionTypeSpec<C>) {
        this.keyType = OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ListTypeSpec<C>) {
        this.keyType = ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: MapTypeSpec<C>) {
        this.keyType = MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: BooleanTypeSpec<C>) {
        this.keyType = BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: DoubleTypeSpec<C>) {
        this.keyType = DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: FloatTypeSpec<C>) {
        this.keyType = FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: LongTypeSpec<C>) {
        this.keyType = LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: IntTypeSpec<C>) {
        this.keyType = IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ShortTypeSpec<C>) {
        this.keyType = ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: ByteTypeSpec<C>) {
        this.keyType = ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: CharacterTypeSpec<C>) {
        this.keyType = CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: StringTypeSpec<C>) {
        this.keyType = StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: AnyTypeSpec<C>) {
        this.keyType = AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.keyType(spec: RefTypeSpec<C>) {
        this.keyType = RefTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StructSpec<C>) {
        this.valueType = StructTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: UnionSpec<C>) {
        this.valueType = UnionTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: EnumTypeSpec<C>) {
        this.valueType = EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: OptionTypeSpec<C>) {
        this.valueType = OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ListTypeSpec<C>) {
        this.valueType = ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: MapTypeSpec<C>) {
        this.valueType = MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: BooleanTypeSpec<C>) {
        this.valueType = BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: DoubleTypeSpec<C>) {
        this.valueType = DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: FloatTypeSpec<C>) {
        this.valueType = FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: LongTypeSpec<C>) {
        this.valueType = LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: IntTypeSpec<C>) {
        this.valueType = IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ShortTypeSpec<C>) {
        this.valueType = ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: ByteTypeSpec<C>) {
        this.valueType = ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: CharacterTypeSpec<C>) {
        this.valueType = CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: StringTypeSpec<C>) {
        this.valueType = StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: AnyTypeSpec<C>) {
        this.valueType = AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> MapTypeBuilder<C>.valueType(spec: RefTypeSpec<C>) {
        this.valueType = RefTypeTranslator<C>(spec).translate(context)
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

    fun <C> MapTypeBuilder<C>.include(body: MapTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> MapTypeBuilder<C>.include(spec: MapTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> MapTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: MapTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> MapTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: MapTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MapTypeBuilder<C2> = MapTypeBuilder(context,keyType,valueType)

    private fun <C2> merge(other: MapTypeBuilder<C2>) {
        this.keyType = other.keyType
        this.valueType = other.valueType
    }

    override fun build(): MapType = MapType(keyType!!,valueType!!)
}

@DslBuilder
class BooleanTypeBuilder<out C>(val context: C) : Builder<BooleanType> {
    override fun build(): BooleanType = BooleanType
}

@DslBuilder
class DoubleTypeBuilder<out C>(val context: C) : Builder<DoubleType> {
    override fun build(): DoubleType = DoubleType
}

@DslBuilder
class FloatTypeBuilder<out C>(val context: C) : Builder<FloatType> {
    override fun build(): FloatType = FloatType
}

@DslBuilder
class LongTypeBuilder<out C>(val context: C) : Builder<LongType> {
    override fun build(): LongType = LongType
}

@DslBuilder
class IntTypeBuilder<out C>(val context: C) : Builder<IntType> {
    override fun build(): IntType = IntType
}

@DslBuilder
class ShortTypeBuilder<out C>(val context: C) : Builder<ShortType> {
    override fun build(): ShortType = ShortType
}

@DslBuilder
class ByteTypeBuilder<out C>(val context: C) : Builder<ByteType> {
    override fun build(): ByteType = ByteType
}

@DslBuilder
class CharacterTypeBuilder<out C>(val context: C) : Builder<CharacterType> {
    override fun build(): CharacterType = CharacterType
}

@DslBuilder
class StringTypeBuilder<out C>(val context: C) : Builder<StringType> {
    override fun build(): StringType = StringType
}

@DslBuilder
class AnyTypeBuilder<out C>(val context: C) : Builder<AnyType> {
    override fun build(): AnyType = AnyType
}

@DslBuilder
class RefTypeBuilder<out C>(val context: C, private var name: String? = null) : Builder<RefType> {
    fun <C> RefTypeBuilder<C>.name(name: String) {
        this.name = name
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

    fun <C> RefTypeBuilder<C>.include(body: RefTypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> RefTypeBuilder<C>.include(spec: RefTypeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> RefTypeBuilder<C>.includeForEach(context: Iterable<C2>, body: RefTypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> RefTypeBuilder<C>.includeForEach(context: Iterable<C2>, spec: RefTypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RefTypeBuilder<C2> = RefTypeBuilder(context,name)

    private fun <C2> merge(other: RefTypeBuilder<C2>) {
        this.name = other.name
    }

    override fun build(): RefType = RefType(name!!)
}

open class StructSpec<in C>(internal val body: StructBuilder<C>.() -> Unit) : Spec<StructBuilder<C>, Struct> {
    override fun StructBuilder<C>.body() {
        this@StructSpec.body.invoke(this)
    }
}

open class UnionSpec<in C>(internal val body: UnionBuilder<C>.() -> Unit) : Spec<UnionBuilder<C>, Union> {
    override fun UnionBuilder<C>.body() {
        this@UnionSpec.body.invoke(this)
    }
}

open class EnumTypeSpec<in C>(internal val body: EnumTypeBuilder<C>.() -> Unit) : Spec<EnumTypeBuilder<C>, EnumType> {
    override fun EnumTypeBuilder<C>.body() {
        this@EnumTypeSpec.body.invoke(this)
    }
}

open class OptionTypeSpec<in C>(internal val body: OptionTypeBuilder<C>.() -> Unit) : Spec<OptionTypeBuilder<C>, OptionType> {
    override fun OptionTypeBuilder<C>.body() {
        this@OptionTypeSpec.body.invoke(this)
    }
}

open class ListTypeSpec<in C>(internal val body: ListTypeBuilder<C>.() -> Unit) : Spec<ListTypeBuilder<C>, ListType> {
    override fun ListTypeBuilder<C>.body() {
        this@ListTypeSpec.body.invoke(this)
    }
}

open class MapTypeSpec<in C>(internal val body: MapTypeBuilder<C>.() -> Unit) : Spec<MapTypeBuilder<C>, MapType> {
    override fun MapTypeBuilder<C>.body() {
        this@MapTypeSpec.body.invoke(this)
    }
}

open class BooleanTypeSpec<in C>(internal val body: BooleanTypeBuilder<C>.() -> Unit) : Spec<BooleanTypeBuilder<C>, BooleanType> {
    override fun BooleanTypeBuilder<C>.body() {
        this@BooleanTypeSpec.body.invoke(this)
    }
}

open class DoubleTypeSpec<in C>(internal val body: DoubleTypeBuilder<C>.() -> Unit) : Spec<DoubleTypeBuilder<C>, DoubleType> {
    override fun DoubleTypeBuilder<C>.body() {
        this@DoubleTypeSpec.body.invoke(this)
    }
}

open class FloatTypeSpec<in C>(internal val body: FloatTypeBuilder<C>.() -> Unit) : Spec<FloatTypeBuilder<C>, FloatType> {
    override fun FloatTypeBuilder<C>.body() {
        this@FloatTypeSpec.body.invoke(this)
    }
}

open class LongTypeSpec<in C>(internal val body: LongTypeBuilder<C>.() -> Unit) : Spec<LongTypeBuilder<C>, LongType> {
    override fun LongTypeBuilder<C>.body() {
        this@LongTypeSpec.body.invoke(this)
    }
}

open class IntTypeSpec<in C>(internal val body: IntTypeBuilder<C>.() -> Unit) : Spec<IntTypeBuilder<C>, IntType> {
    override fun IntTypeBuilder<C>.body() {
        this@IntTypeSpec.body.invoke(this)
    }
}

open class ShortTypeSpec<in C>(internal val body: ShortTypeBuilder<C>.() -> Unit) : Spec<ShortTypeBuilder<C>, ShortType> {
    override fun ShortTypeBuilder<C>.body() {
        this@ShortTypeSpec.body.invoke(this)
    }
}

open class ByteTypeSpec<in C>(internal val body: ByteTypeBuilder<C>.() -> Unit) : Spec<ByteTypeBuilder<C>, ByteType> {
    override fun ByteTypeBuilder<C>.body() {
        this@ByteTypeSpec.body.invoke(this)
    }
}

open class CharacterTypeSpec<in C>(internal val body: CharacterTypeBuilder<C>.() -> Unit) : Spec<CharacterTypeBuilder<C>, CharacterType> {
    override fun CharacterTypeBuilder<C>.body() {
        this@CharacterTypeSpec.body.invoke(this)
    }
}

open class StringTypeSpec<in C>(internal val body: StringTypeBuilder<C>.() -> Unit) : Spec<StringTypeBuilder<C>, StringType> {
    override fun StringTypeBuilder<C>.body() {
        this@StringTypeSpec.body.invoke(this)
    }
}

open class AnyTypeSpec<in C>(internal val body: AnyTypeBuilder<C>.() -> Unit) : Spec<AnyTypeBuilder<C>, AnyType> {
    override fun AnyTypeBuilder<C>.body() {
        this@AnyTypeSpec.body.invoke(this)
    }
}

open class RefTypeSpec<in C>(internal val body: RefTypeBuilder<C>.() -> Unit) : Spec<RefTypeBuilder<C>, RefType> {
    override fun RefTypeBuilder<C>.body() {
        this@RefTypeSpec.body.invoke(this)
    }
}

open class StructTranslator<in C>(private val spec: StructSpec<C>) : Translator<C, Struct> {
    constructor(body: StructBuilder<C>.() -> Unit) : this(StructSpec<C>(body))

    override fun translate(context: C): Struct {
        val builder = StructBuilder(context)
        val translator = BuilderSpecTranslator<C, StructBuilder<C>, Struct>(builder, spec)
        return translator.translate(context)
    }
}

open class UnionTranslator<in C>(private val spec: UnionSpec<C>) : Translator<C, Union> {
    constructor(body: UnionBuilder<C>.() -> Unit) : this(UnionSpec<C>(body))

    override fun translate(context: C): Union {
        val builder = UnionBuilder(context)
        val translator = BuilderSpecTranslator<C, UnionBuilder<C>, Union>(builder, spec)
        return translator.translate(context)
    }
}

open class EnumTypeTranslator<in C>(private val spec: EnumTypeSpec<C>) : Translator<C, EnumType> {
    constructor(body: EnumTypeBuilder<C>.() -> Unit) : this(EnumTypeSpec<C>(body))

    override fun translate(context: C): EnumType {
        val builder = EnumTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, EnumTypeBuilder<C>, EnumType>(builder, spec)
        return translator.translate(context)
    }
}

open class OptionTypeTranslator<in C>(private val spec: OptionTypeSpec<C>) : Translator<C, OptionType> {
    constructor(body: OptionTypeBuilder<C>.() -> Unit) : this(OptionTypeSpec<C>(body))

    override fun translate(context: C): OptionType {
        val builder = OptionTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, OptionTypeBuilder<C>, OptionType>(builder, spec)
        return translator.translate(context)
    }
}

open class ListTypeTranslator<in C>(private val spec: ListTypeSpec<C>) : Translator<C, ListType> {
    constructor(body: ListTypeBuilder<C>.() -> Unit) : this(ListTypeSpec<C>(body))

    override fun translate(context: C): ListType {
        val builder = ListTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, ListTypeBuilder<C>, ListType>(builder, spec)
        return translator.translate(context)
    }
}

open class MapTypeTranslator<in C>(private val spec: MapTypeSpec<C>) : Translator<C, MapType> {
    constructor(body: MapTypeBuilder<C>.() -> Unit) : this(MapTypeSpec<C>(body))

    override fun translate(context: C): MapType {
        val builder = MapTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, MapTypeBuilder<C>, MapType>(builder, spec)
        return translator.translate(context)
    }
}

open class BooleanTypeTranslator<in C>(private val spec: BooleanTypeSpec<C>) : Translator<C, BooleanType> {
    constructor(body: BooleanTypeBuilder<C>.() -> Unit) : this(BooleanTypeSpec<C>(body))

    override fun translate(context: C): BooleanType {
        val builder = BooleanTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, BooleanTypeBuilder<C>, BooleanType>(builder, spec)
        return translator.translate(context)
    }
}

open class DoubleTypeTranslator<in C>(private val spec: DoubleTypeSpec<C>) : Translator<C, DoubleType> {
    constructor(body: DoubleTypeBuilder<C>.() -> Unit) : this(DoubleTypeSpec<C>(body))

    override fun translate(context: C): DoubleType {
        val builder = DoubleTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, DoubleTypeBuilder<C>, DoubleType>(builder, spec)
        return translator.translate(context)
    }
}

open class FloatTypeTranslator<in C>(private val spec: FloatTypeSpec<C>) : Translator<C, FloatType> {
    constructor(body: FloatTypeBuilder<C>.() -> Unit) : this(FloatTypeSpec<C>(body))

    override fun translate(context: C): FloatType {
        val builder = FloatTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, FloatTypeBuilder<C>, FloatType>(builder, spec)
        return translator.translate(context)
    }
}

open class LongTypeTranslator<in C>(private val spec: LongTypeSpec<C>) : Translator<C, LongType> {
    constructor(body: LongTypeBuilder<C>.() -> Unit) : this(LongTypeSpec<C>(body))

    override fun translate(context: C): LongType {
        val builder = LongTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, LongTypeBuilder<C>, LongType>(builder, spec)
        return translator.translate(context)
    }
}

open class IntTypeTranslator<in C>(private val spec: IntTypeSpec<C>) : Translator<C, IntType> {
    constructor(body: IntTypeBuilder<C>.() -> Unit) : this(IntTypeSpec<C>(body))

    override fun translate(context: C): IntType {
        val builder = IntTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, IntTypeBuilder<C>, IntType>(builder, spec)
        return translator.translate(context)
    }
}

open class ShortTypeTranslator<in C>(private val spec: ShortTypeSpec<C>) : Translator<C, ShortType> {
    constructor(body: ShortTypeBuilder<C>.() -> Unit) : this(ShortTypeSpec<C>(body))

    override fun translate(context: C): ShortType {
        val builder = ShortTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, ShortTypeBuilder<C>, ShortType>(builder, spec)
        return translator.translate(context)
    }
}

open class ByteTypeTranslator<in C>(private val spec: ByteTypeSpec<C>) : Translator<C, ByteType> {
    constructor(body: ByteTypeBuilder<C>.() -> Unit) : this(ByteTypeSpec<C>(body))

    override fun translate(context: C): ByteType {
        val builder = ByteTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, ByteTypeBuilder<C>, ByteType>(builder, spec)
        return translator.translate(context)
    }
}

open class CharacterTypeTranslator<in C>(private val spec: CharacterTypeSpec<C>) : Translator<C, CharacterType> {
    constructor(body: CharacterTypeBuilder<C>.() -> Unit) : this(CharacterTypeSpec<C>(body))

    override fun translate(context: C): CharacterType {
        val builder = CharacterTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, CharacterTypeBuilder<C>, CharacterType>(builder, spec)
        return translator.translate(context)
    }
}

open class StringTypeTranslator<in C>(private val spec: StringTypeSpec<C>) : Translator<C, StringType> {
    constructor(body: StringTypeBuilder<C>.() -> Unit) : this(StringTypeSpec<C>(body))

    override fun translate(context: C): StringType {
        val builder = StringTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, StringTypeBuilder<C>, StringType>(builder, spec)
        return translator.translate(context)
    }
}

open class AnyTypeTranslator<in C>(private val spec: AnyTypeSpec<C>) : Translator<C, AnyType> {
    constructor(body: AnyTypeBuilder<C>.() -> Unit) : this(AnyTypeSpec<C>(body))

    override fun translate(context: C): AnyType {
        val builder = AnyTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, AnyTypeBuilder<C>, AnyType>(builder, spec)
        return translator.translate(context)
    }
}

open class RefTypeTranslator<in C>(private val spec: RefTypeSpec<C>) : Translator<C, RefType> {
    constructor(body: RefTypeBuilder<C>.() -> Unit) : this(RefTypeSpec<C>(body))

    override fun translate(context: C): RefType {
        val builder = RefTypeBuilder(context)
        val translator = BuilderSpecTranslator<C, RefTypeBuilder<C>, RefType>(builder, spec)
        return translator.translate(context)
    }
}

data class Field(val name: String, val type: Type) {
    companion object {
        operator fun <C> invoke(body: FieldBuilder<C>.() -> Unit): FieldSpec<C> = FieldSpec<C>(body)
    }
}

@DslBuilder
class FieldBuilder<out C>(
        val context: C,
        private var name: String? = null,
        private var type: Type? = null
) : Builder<Field> {
    fun <C> FieldBuilder<C>.name(name: String) {
        this.name = name
    }

    fun <C> FieldBuilder<C>.type(spec: StructSpec<C>) {
        this.type = StructTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: UnionSpec<C>) {
        this.type = UnionTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: EnumTypeSpec<C>) {
        this.type = EnumTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: OptionTypeSpec<C>) {
        this.type = OptionTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: ListTypeSpec<C>) {
        this.type = ListTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: MapTypeSpec<C>) {
        this.type = MapTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: BooleanTypeSpec<C>) {
        this.type = BooleanTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: DoubleTypeSpec<C>) {
        this.type = DoubleTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: FloatTypeSpec<C>) {
        this.type = FloatTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: LongTypeSpec<C>) {
        this.type = LongTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: IntTypeSpec<C>) {
        this.type = IntTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: ShortTypeSpec<C>) {
        this.type = ShortTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: ByteTypeSpec<C>) {
        this.type = ByteTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: CharacterTypeSpec<C>) {
        this.type = CharacterTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: StringTypeSpec<C>) {
        this.type = StringTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: AnyTypeSpec<C>) {
        this.type = AnyTypeTranslator<C>(spec).translate(context)
    }

    fun <C> FieldBuilder<C>.type(spec: RefTypeSpec<C>) {
        this.type = RefTypeTranslator<C>(spec).translate(context)
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

    fun <C> FieldBuilder<C>.include(body: FieldBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> FieldBuilder<C>.include(spec: FieldSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> FieldBuilder<C>.includeForEach(context: Iterable<C2>, body: FieldBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> FieldBuilder<C>.includeForEach(context: Iterable<C2>, spec: FieldSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FieldBuilder<C2> = FieldBuilder(context,name,type)

    private fun <C2> merge(other: FieldBuilder<C2>) {
        this.name = other.name
        this.type = other.type
    }

    override fun build(): Field = Field(name!!,type!!)
}

open class FieldSpec<in C>(internal val body: FieldBuilder<C>.() -> Unit) : Spec<FieldBuilder<C>, Field> {
    override fun FieldBuilder<C>.body() {
        this@FieldSpec.body.invoke(this)
    }
}

open class FieldTranslator<in C>(private val spec: FieldSpec<C>) : Translator<C, Field> {
    constructor(body: FieldBuilder<C>.() -> Unit) : this(FieldSpec<C>(body))

    override fun translate(context: C): Field {
        val builder = FieldBuilder(context)
        val translator = BuilderSpecTranslator<C, FieldBuilder<C>, Field>(builder, spec)
        return translator.translate(context)
    }
}
