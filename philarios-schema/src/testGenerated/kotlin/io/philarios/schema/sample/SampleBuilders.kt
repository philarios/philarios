package io.philarios.schema.sample

import io.philarios.core.DslBuilder
import kotlin.Any
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Pair
import kotlin.Short
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

@DslBuilder
interface BooleanValueBuilder<out C> {
    val context: C

    fun value(value: Boolean)

    fun include(body: BooleanValueBuilder<C>.() -> Unit)

    fun include(spec: BooleanValueSpec<C>)

    fun <C2> include(context: C2, body: BooleanValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: BooleanValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: BooleanValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: BooleanValueSpec<C2>)
}

@DslBuilder
interface DoubleValueBuilder<out C> {
    val context: C

    fun value(value: Double)

    fun include(body: DoubleValueBuilder<C>.() -> Unit)

    fun include(spec: DoubleValueSpec<C>)

    fun <C2> include(context: C2, body: DoubleValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DoubleValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DoubleValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DoubleValueSpec<C2>)
}

@DslBuilder
interface FloatValueBuilder<out C> {
    val context: C

    fun value(value: Float)

    fun include(body: FloatValueBuilder<C>.() -> Unit)

    fun include(spec: FloatValueSpec<C>)

    fun <C2> include(context: C2, body: FloatValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: FloatValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: FloatValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: FloatValueSpec<C2>)
}

@DslBuilder
interface LongValueBuilder<out C> {
    val context: C

    fun value(value: Long)

    fun include(body: LongValueBuilder<C>.() -> Unit)

    fun include(spec: LongValueSpec<C>)

    fun <C2> include(context: C2, body: LongValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: LongValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: LongValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: LongValueSpec<C2>)
}

@DslBuilder
interface IntValueBuilder<out C> {
    val context: C

    fun value(value: Int)

    fun include(body: IntValueBuilder<C>.() -> Unit)

    fun include(spec: IntValueSpec<C>)

    fun <C2> include(context: C2, body: IntValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: IntValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: IntValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: IntValueSpec<C2>)
}

@DslBuilder
interface ShortValueBuilder<out C> {
    val context: C

    fun value(value: Short)

    fun include(body: ShortValueBuilder<C>.() -> Unit)

    fun include(spec: ShortValueSpec<C>)

    fun <C2> include(context: C2, body: ShortValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ShortValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ShortValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ShortValueSpec<C2>)
}

@DslBuilder
interface ByteValueBuilder<out C> {
    val context: C

    fun value(value: Byte)

    fun include(body: ByteValueBuilder<C>.() -> Unit)

    fun include(spec: ByteValueSpec<C>)

    fun <C2> include(context: C2, body: ByteValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ByteValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ByteValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ByteValueSpec<C2>)
}

@DslBuilder
interface CharacterValueBuilder<out C> {
    val context: C

    fun value(value: Char)

    fun include(body: CharacterValueBuilder<C>.() -> Unit)

    fun include(spec: CharacterValueSpec<C>)

    fun <C2> include(context: C2, body: CharacterValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: CharacterValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: CharacterValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: CharacterValueSpec<C2>)
}

@DslBuilder
interface StringValueBuilder<out C> {
    val context: C

    fun value(value: String)

    fun include(body: StringValueBuilder<C>.() -> Unit)

    fun include(spec: StringValueSpec<C>)

    fun <C2> include(context: C2, body: StringValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: StringValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: StringValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: StringValueSpec<C2>)
}

@DslBuilder
interface AnyValueBuilder<out C> {
    val context: C

    fun value(value: Any)

    fun include(body: AnyValueBuilder<C>.() -> Unit)

    fun include(spec: AnyValueSpec<C>)

    fun <C2> include(context: C2, body: AnyValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AnyValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AnyValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AnyValueSpec<C2>)
}

@DslBuilder
interface OptionValueBuilder<out C> {
    val context: C

    fun value(value: String)

    fun include(body: OptionValueBuilder<C>.() -> Unit)

    fun include(spec: OptionValueSpec<C>)

    fun <C2> include(context: C2, body: OptionValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: OptionValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: OptionValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: OptionValueSpec<C2>)
}

@DslBuilder
interface ListValueBuilder<out C> {
    val context: C

    fun value(value: String)

    fun value(value: List<String>)

    fun include(body: ListValueBuilder<C>.() -> Unit)

    fun include(spec: ListValueSpec<C>)

    fun <C2> include(context: C2, body: ListValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ListValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ListValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ListValueSpec<C2>)
}

@DslBuilder
interface MapValueBuilder<out C> {
    val context: C

    fun value(key: String, value: String)

    fun value(pair: Pair<String, String>)

    fun value(value: Map<String, String>)

    fun include(body: MapValueBuilder<C>.() -> Unit)

    fun include(spec: MapValueSpec<C>)

    fun <C2> include(context: C2, body: MapValueBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: MapValueSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: MapValueBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: MapValueSpec<C2>)
}

@DslBuilder
interface SimpleBuilder<out C> {
    val context: C

    fun value(value: String)

    fun include(body: SimpleBuilder<C>.() -> Unit)

    fun include(spec: SimpleSpec<C>)

    fun <C2> include(context: C2, body: SimpleBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: SimpleSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: SimpleBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: SimpleSpec<C2>)
}

@DslBuilder
interface ComplexBuilder<out C> {
    val context: C

    fun value(value: String)

    fun age(value: Int)

    fun done(value: Boolean)

    fun children(body: SimpleBuilder<C>.() -> Unit)

    fun children(spec: SimpleSpec<C>)

    fun children(ref: SimpleRef)

    fun children(value: Simple)

    fun children(children: List<Simple>)

    fun capitalCities(key: String, value: String)

    fun capitalCities(pair: Pair<String, String>)

    fun capitalCities(capitalCities: Map<String, String>)

    fun dataByKey(key: String, body: DataBuilder<C>.() -> Unit)

    fun dataByKey(key: String, spec: DataSpec<C>)

    fun dataByKey(key: String, ref: DataRef)

    fun dataByKey(key: String, value: Data)

    fun include(body: ComplexBuilder<C>.() -> Unit)

    fun include(spec: ComplexSpec<C>)

    fun <C2> include(context: C2, body: ComplexBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ComplexSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ComplexBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ComplexSpec<C2>)
}

@DslBuilder
interface DataBuilder<out C> {
    val context: C

    fun x(value: Int)

    fun y(value: Int)

    fun include(body: DataBuilder<C>.() -> Unit)

    fun include(spec: DataSpec<C>)

    fun <C2> include(context: C2, body: DataBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DataSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DataBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DataSpec<C2>)
}

@DslBuilder
interface ResponseBuilder<out C> {
    val context: C

    fun <T : Error> error(spec: ErrorSpec<C, T>)

    fun <T : Error> error(ref: ErrorRef<T>)

    fun <T : Error> error(value: T)

    fun include(body: ResponseBuilder<C>.() -> Unit)

    fun include(spec: ResponseSpec<C>)

    fun <C2> include(context: C2, body: ResponseBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: ResponseSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: ResponseBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: ResponseSpec<C2>)
}

@DslBuilder
interface InsufficientAmountErrorBuilder<out C> {
    val context: C

    fun amount(value: Double)

    fun include(body: InsufficientAmountErrorBuilder<C>.() -> Unit)

    fun include(spec: InsufficientAmountErrorSpec<C>)

    fun <C2> include(context: C2, body: InsufficientAmountErrorBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: InsufficientAmountErrorSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: InsufficientAmountErrorBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: InsufficientAmountErrorSpec<C2>)
}
