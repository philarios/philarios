package io.philarios.schema.sample

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

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
