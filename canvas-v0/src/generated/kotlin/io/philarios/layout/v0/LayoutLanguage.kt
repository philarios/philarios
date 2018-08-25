package io.philarios.layout.v0

import io.philarios.canvas.v0.Canvas
import io.philarios.canvas.v0.CanvasBuilder
import io.philarios.canvas.v0.CanvasSpec
import io.philarios.canvas.v0.CanvasTranslator
import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.Double
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

data class Box(
        val key: String,
        val children: List<Box>,
        val constraints: Map<ConstraintType, ConstraintValue>,
        val canvas: Canvas
) {
    companion object {
        operator fun <C> invoke(body: BoxBuilder<C>.() -> Unit): BoxSpec<C> = BoxSpec<C>(body)
    }
}

@DslBuilder
class BoxBuilder<out C>(
        val context: C,
        private var key: String? = null,
        private var children: List<Box>? = emptyList(),
        private var constraints: Map<ConstraintType, ConstraintValue>? = emptyMap(),
        private var canvas: Canvas? = null
) : Builder<Box> {
    fun <C> BoxBuilder<C>.key(key: String) {
        this.key = key
    }

    fun <C> BoxBuilder<C>.children(body: BoxBuilder<C>.() -> Unit) {
        this.children = this.children.orEmpty() + BoxTranslator<C>(body).translate(context)
    }

    fun <C> BoxBuilder<C>.children(spec: BoxSpec<C>) {
        this.children = this.children.orEmpty() + BoxTranslator<C>(spec).translate(context)
    }

    fun <C> BoxBuilder<C>.children(children: Box) {
        this.children = this.children.orEmpty() + children
    }

    fun <C> BoxBuilder<C>.children(children: List<Box>) {
        this.children = this.children.orEmpty() + children
    }

    fun <C> BoxBuilder<C>.constraints(key: ConstraintType, spec: ScalarSpec<C>) {
        this.constraints = this.constraints.orEmpty() + Pair(key,ScalarTranslator<C>(spec).translate(context))
    }

    fun <C> BoxBuilder<C>.constraints(key: ConstraintType, spec: LinearSpec<C>) {
        this.constraints = this.constraints.orEmpty() + Pair(key,LinearTranslator<C>(spec).translate(context))
    }

    fun <C> BoxBuilder<C>.canvas(body: CanvasBuilder<C>.() -> Unit) {
        this.canvas = CanvasTranslator<C>(body).translate(context)
    }

    fun <C> BoxBuilder<C>.canvas(spec: CanvasSpec<C>) {
        this.canvas = CanvasTranslator<C>(spec).translate(context)
    }

    fun <C> BoxBuilder<C>.canvas(canvas: Canvas) {
        this.canvas = canvas
    }

    fun <C, C2> BoxBuilder<C>.include(context: C2, body: BoxBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> BoxBuilder<C>.include(context: C2, spec: BoxSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> BoxBuilder<C>.include(body: BoxBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> BoxBuilder<C>.include(spec: BoxSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> BoxBuilder<C>.includeForEach(context: Iterable<C2>, body: BoxBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> BoxBuilder<C>.includeForEach(context: Iterable<C2>, spec: BoxSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): BoxBuilder<C2> = BoxBuilder(context,key,children,constraints,canvas)

    private fun <C2> merge(other: BoxBuilder<C2>) {
        this.key = other.key
        this.children = other.children
        this.constraints = other.constraints
        this.canvas = other.canvas
    }

    override fun build(): Box = Box(key!!,children!!,constraints!!,canvas!!)
}

open class BoxSpec<in C>(internal val body: BoxBuilder<C>.() -> Unit) : Spec<BoxBuilder<C>, Box> {
    override fun BoxBuilder<C>.body() {
        this@BoxSpec.body.invoke(this)
    }
}

open class BoxTranslator<in C>(private val spec: BoxSpec<C>) : Translator<C, Box> {
    constructor(body: BoxBuilder<C>.() -> Unit) : this(io.philarios.layout.v0.BoxSpec<C>(body))

    override fun translate(context: C): Box {
        val builder = BoxBuilder(context)
        val translator = BuilderSpecTranslator<C, BoxBuilder<C>, Box>(builder, spec)
        return translator.translate(context)
    }
}

enum class ConstraintType {
    LEFT,

    CENTER,

    RIGHT,

    WIDTH,

    TOP,

    MIDDLE,

    BOTTOM,

    HEIGHT,

    TRANSLATE_X,

    TRANSLATE_Y,

    SCALE_X,

    SCALE_Y
}

sealed class ConstraintValue

data class Scalar(val value: Double) : ConstraintValue() {
    companion object {
        operator fun <C> invoke(body: ScalarBuilder<C>.() -> Unit): ScalarSpec<C> = ScalarSpec<C>(body)
    }
}

data class Linear(
        val partner: String,
        val type: ConstraintType,
        val offset: Double?,
        val multiplier: Double?
) : ConstraintValue() {
    companion object {
        operator fun <C> invoke(body: LinearBuilder<C>.() -> Unit): LinearSpec<C> = LinearSpec<C>(body)
    }
}

@DslBuilder
class ScalarBuilder<out C>(val context: C, private var value: Double? = null) : Builder<Scalar> {
    fun <C> ScalarBuilder<C>.value(value: Double) {
        this.value = value
    }

    fun <C, C2> ScalarBuilder<C>.include(context: C2, body: ScalarBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ScalarBuilder<C>.include(context: C2, spec: ScalarSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> ScalarBuilder<C>.include(body: ScalarBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ScalarBuilder<C>.include(spec: ScalarSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ScalarBuilder<C>.includeForEach(context: Iterable<C2>, body: ScalarBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ScalarBuilder<C>.includeForEach(context: Iterable<C2>, spec: ScalarSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ScalarBuilder<C2> = ScalarBuilder(context,value)

    private fun <C2> merge(other: ScalarBuilder<C2>) {
        this.value = other.value
    }

    override fun build(): Scalar = Scalar(value!!)
}

@DslBuilder
class LinearBuilder<out C>(
        val context: C,
        private var partner: String? = null,
        private var type: ConstraintType? = null,
        private var offset: Double? = null,
        private var multiplier: Double? = null
) : Builder<Linear> {
    fun <C> LinearBuilder<C>.partner(partner: String) {
        this.partner = partner
    }

    fun <C> LinearBuilder<C>.type(type: ConstraintType) {
        this.type = type
    }

    fun <C> LinearBuilder<C>.offset(offset: Double) {
        this.offset = offset
    }

    fun <C> LinearBuilder<C>.multiplier(multiplier: Double) {
        this.multiplier = multiplier
    }

    fun <C, C2> LinearBuilder<C>.include(context: C2, body: LinearBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> LinearBuilder<C>.include(context: C2, spec: LinearSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> LinearBuilder<C>.include(body: LinearBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> LinearBuilder<C>.include(spec: LinearSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> LinearBuilder<C>.includeForEach(context: Iterable<C2>, body: LinearBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> LinearBuilder<C>.includeForEach(context: Iterable<C2>, spec: LinearSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): LinearBuilder<C2> = LinearBuilder(context,partner,type,offset,multiplier)

    private fun <C2> merge(other: LinearBuilder<C2>) {
        this.partner = other.partner
        this.type = other.type
        this.offset = other.offset
        this.multiplier = other.multiplier
    }

    override fun build(): Linear = Linear(partner!!,type!!,offset,multiplier)
}

open class ScalarSpec<in C>(internal val body: ScalarBuilder<C>.() -> Unit) : Spec<ScalarBuilder<C>, Scalar> {
    override fun ScalarBuilder<C>.body() {
        this@ScalarSpec.body.invoke(this)
    }
}

open class LinearSpec<in C>(internal val body: LinearBuilder<C>.() -> Unit) : Spec<LinearBuilder<C>, Linear> {
    override fun LinearBuilder<C>.body() {
        this@LinearSpec.body.invoke(this)
    }
}

open class ScalarTranslator<in C>(private val spec: ScalarSpec<C>) : Translator<C, Scalar> {
    constructor(body: ScalarBuilder<C>.() -> Unit) : this(io.philarios.layout.v0.ScalarSpec<C>(body))

    override fun translate(context: C): Scalar {
        val builder = ScalarBuilder(context)
        val translator = BuilderSpecTranslator<C, ScalarBuilder<C>, Scalar>(builder, spec)
        return translator.translate(context)
    }
}

open class LinearTranslator<in C>(private val spec: LinearSpec<C>) : Translator<C, Linear> {
    constructor(body: LinearBuilder<C>.() -> Unit) : this(io.philarios.layout.v0.LinearSpec<C>(body))

    override fun translate(context: C): Linear {
        val builder = LinearBuilder(context)
        val translator = BuilderSpecTranslator<C, LinearBuilder<C>, Linear>(builder, spec)
        return translator.translate(context)
    }
}
