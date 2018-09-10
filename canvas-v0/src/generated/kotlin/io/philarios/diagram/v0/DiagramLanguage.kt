package io.philarios.diagram.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.Double
import kotlin.collections.Iterable
import kotlin.collections.List

data class Diagram(val size: Bounds, val shapes: List<Shape>) {
    companion object {
        operator fun <C> invoke(body: DiagramBuilder<C>.() -> Unit): DiagramSpec<C> = DiagramSpec<C>(body)
    }
}

@DslBuilder
class DiagramBuilder<out C>(
        val context: C,
        private var size: Bounds? = null,
        private var shapes: List<Shape>? = emptyList()
) : Builder<Diagram> {
    fun <C> DiagramBuilder<C>.size(body: BoundsBuilder<C>.() -> Unit) {
        this.size = BoundsTranslator<C>(body).translate(context)
    }

    fun <C> DiagramBuilder<C>.size(spec: BoundsSpec<C>) {
        this.size = BoundsTranslator<C>(spec).translate(context)
    }

    fun <C> DiagramBuilder<C>.shape(spec: RectangleSpec<C>) {
        this.shapes = this.shapes.orEmpty() + RectangleTranslator<C>(spec).translate(context)
    }

    fun <C, C2> DiagramBuilder<C>.include(context: C2, body: DiagramBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> DiagramBuilder<C>.include(context: C2, spec: DiagramSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> DiagramBuilder<C>.include(body: DiagramBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DiagramBuilder<C>.include(spec: DiagramSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DiagramBuilder<C>.includeForEach(context: Iterable<C2>, body: DiagramBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DiagramBuilder<C>.includeForEach(context: Iterable<C2>, spec: DiagramSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DiagramBuilder<C2> = DiagramBuilder(context,size,shapes)

    private fun <C2> merge(other: DiagramBuilder<C2>) {
        this.size = other.size
        this.shapes = other.shapes
    }

    override fun build(): Diagram = Diagram(size!!,shapes!!)
}

open class DiagramSpec<in C>(internal val body: DiagramBuilder<C>.() -> Unit) : Spec<DiagramBuilder<C>, Diagram> {
    override fun DiagramBuilder<C>.body() {
        this@DiagramSpec.body.invoke(this)
    }
}

open class DiagramTranslator<in C>(private val spec: DiagramSpec<C>) : Translator<C, Diagram> {
    constructor(body: DiagramBuilder<C>.() -> Unit) : this(DiagramSpec<C>(body))

    override fun translate(context: C): Diagram {
        val builder = DiagramBuilder(context)
        val translator = BuilderSpecTranslator<C, DiagramBuilder<C>, Diagram>(builder, spec)
        return translator.translate(context)
    }
}

sealed class Shape

data class Rectangle(val origin: Point, val size: Bounds) : Shape() {
    companion object {
        operator fun <C> invoke(body: RectangleBuilder<C>.() -> Unit): RectangleSpec<C> = RectangleSpec<C>(body)
    }
}

@DslBuilder
class RectangleBuilder<out C>(
        val context: C,
        private var origin: Point? = null,
        private var size: Bounds? = null
) : Builder<Rectangle> {
    fun <C> RectangleBuilder<C>.origin(body: PointBuilder<C>.() -> Unit) {
        this.origin = PointTranslator<C>(body).translate(context)
    }

    fun <C> RectangleBuilder<C>.origin(spec: PointSpec<C>) {
        this.origin = PointTranslator<C>(spec).translate(context)
    }

    fun <C> RectangleBuilder<C>.size(body: BoundsBuilder<C>.() -> Unit) {
        this.size = BoundsTranslator<C>(body).translate(context)
    }

    fun <C> RectangleBuilder<C>.size(spec: BoundsSpec<C>) {
        this.size = BoundsTranslator<C>(spec).translate(context)
    }

    fun <C, C2> RectangleBuilder<C>.include(context: C2, body: RectangleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> RectangleBuilder<C>.include(context: C2, spec: RectangleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> RectangleBuilder<C>.include(body: RectangleBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> RectangleBuilder<C>.include(spec: RectangleSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> RectangleBuilder<C>.includeForEach(context: Iterable<C2>, body: RectangleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> RectangleBuilder<C>.includeForEach(context: Iterable<C2>, spec: RectangleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RectangleBuilder<C2> = RectangleBuilder(context,origin,size)

    private fun <C2> merge(other: RectangleBuilder<C2>) {
        this.origin = other.origin
        this.size = other.size
    }

    override fun build(): Rectangle = Rectangle(origin!!,size!!)
}

open class RectangleSpec<in C>(internal val body: RectangleBuilder<C>.() -> Unit) : Spec<RectangleBuilder<C>, Rectangle> {
    override fun RectangleBuilder<C>.body() {
        this@RectangleSpec.body.invoke(this)
    }
}

open class RectangleTranslator<in C>(private val spec: RectangleSpec<C>) : Translator<C, Rectangle> {
    constructor(body: RectangleBuilder<C>.() -> Unit) : this(RectangleSpec<C>(body))

    override fun translate(context: C): Rectangle {
        val builder = RectangleBuilder(context)
        val translator = BuilderSpecTranslator<C, RectangleBuilder<C>, Rectangle>(builder, spec)
        return translator.translate(context)
    }
}

data class Point(val x: Double, val y: Double) {
    companion object {
        operator fun <C> invoke(body: PointBuilder<C>.() -> Unit): PointSpec<C> = PointSpec<C>(body)
    }
}

@DslBuilder
class PointBuilder<out C>(
        val context: C,
        private var x: Double? = null,
        private var y: Double? = null
) : Builder<Point> {
    fun <C> PointBuilder<C>.x(x: Double) {
        this.x = x
    }

    fun <C> PointBuilder<C>.y(y: Double) {
        this.y = y
    }

    fun <C, C2> PointBuilder<C>.include(context: C2, body: PointBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> PointBuilder<C>.include(context: C2, spec: PointSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> PointBuilder<C>.include(body: PointBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> PointBuilder<C>.include(spec: PointSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> PointBuilder<C>.includeForEach(context: Iterable<C2>, body: PointBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> PointBuilder<C>.includeForEach(context: Iterable<C2>, spec: PointSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PointBuilder<C2> = PointBuilder(context,x,y)

    private fun <C2> merge(other: PointBuilder<C2>) {
        this.x = other.x
        this.y = other.y
    }

    override fun build(): Point = Point(x!!,y!!)
}

open class PointSpec<in C>(internal val body: PointBuilder<C>.() -> Unit) : Spec<PointBuilder<C>, Point> {
    override fun PointBuilder<C>.body() {
        this@PointSpec.body.invoke(this)
    }
}

open class PointTranslator<in C>(private val spec: PointSpec<C>) : Translator<C, Point> {
    constructor(body: PointBuilder<C>.() -> Unit) : this(PointSpec<C>(body))

    override fun translate(context: C): Point {
        val builder = PointBuilder(context)
        val translator = BuilderSpecTranslator<C, PointBuilder<C>, Point>(builder, spec)
        return translator.translate(context)
    }
}

data class Bounds(val width: Double, val height: Double) {
    companion object {
        operator fun <C> invoke(body: BoundsBuilder<C>.() -> Unit): BoundsSpec<C> = BoundsSpec<C>(body)
    }
}

@DslBuilder
class BoundsBuilder<out C>(
        val context: C,
        private var width: Double? = null,
        private var height: Double? = null
) : Builder<Bounds> {
    fun <C> BoundsBuilder<C>.width(width: Double) {
        this.width = width
    }

    fun <C> BoundsBuilder<C>.height(height: Double) {
        this.height = height
    }

    fun <C, C2> BoundsBuilder<C>.include(context: C2, body: BoundsBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> BoundsBuilder<C>.include(context: C2, spec: BoundsSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> BoundsBuilder<C>.include(body: BoundsBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> BoundsBuilder<C>.include(spec: BoundsSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> BoundsBuilder<C>.includeForEach(context: Iterable<C2>, body: BoundsBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> BoundsBuilder<C>.includeForEach(context: Iterable<C2>, spec: BoundsSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): BoundsBuilder<C2> = BoundsBuilder(context,width,height)

    private fun <C2> merge(other: BoundsBuilder<C2>) {
        this.width = other.width
        this.height = other.height
    }

    override fun build(): Bounds = Bounds(width!!,height!!)
}

open class BoundsSpec<in C>(internal val body: BoundsBuilder<C>.() -> Unit) : Spec<BoundsBuilder<C>, Bounds> {
    override fun BoundsBuilder<C>.body() {
        this@BoundsSpec.body.invoke(this)
    }
}

open class BoundsTranslator<in C>(private val spec: BoundsSpec<C>) : Translator<C, Bounds> {
    constructor(body: BoundsBuilder<C>.() -> Unit) : this(BoundsSpec<C>(body))

    override fun translate(context: C): Bounds {
        val builder = BoundsBuilder(context)
        val translator = BuilderSpecTranslator<C, BoundsBuilder<C>, Bounds>(builder, spec)
        return translator.translate(context)
    }
}
