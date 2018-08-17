package io.philarios.canvas.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.BuilderSpecTranslator
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Translator
import kotlin.Boolean
import kotlin.Double
import kotlin.collections.Iterable
import kotlin.collections.List

data class CanvasRoot(val tree: CanvasTree) {
    companion object {
        operator fun <C> invoke(body: CanvasRootBuilder<C>.() -> Unit): CanvasRootSpec<C> = CanvasRootSpec<C>(body)
    }
}

@DslBuilder
class CanvasRootBuilder<out C>(val context: C, private var tree: CanvasTree? = null) : Builder<CanvasRoot> {
    fun <C> CanvasRootBuilder<C>.tree(spec: CanvasNodeSpec<C>) {
        this.tree = CanvasNodeTranslator<C>(spec).translate(context)
    }

    fun <C> CanvasRootBuilder<C>.tree(spec: CanvasLeafSpec<C>) {
        this.tree = CanvasLeafTranslator<C>(spec).translate(context)
    }

    fun <C, C2> CanvasRootBuilder<C>.include(context: C2, body: CanvasRootBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> CanvasRootBuilder<C>.include(context: C2, spec: CanvasRootSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> CanvasRootBuilder<C>.include(body: CanvasRootBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> CanvasRootBuilder<C>.include(spec: CanvasRootSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> CanvasRootBuilder<C>.includeForEach(context: Iterable<C2>, body: CanvasRootBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> CanvasRootBuilder<C>.includeForEach(context: Iterable<C2>, spec: CanvasRootSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CanvasRootBuilder<C2> = CanvasRootBuilder(context,tree)

    private fun <C2> merge(other: CanvasRootBuilder<C2>) {
        this.tree = other.tree
    }

    override fun build(): CanvasRoot = CanvasRoot(tree!!)
}

open class CanvasRootSpec<in C>(internal val body: CanvasRootBuilder<C>.() -> Unit) : Spec<CanvasRootBuilder<C>, CanvasRoot> {
    override fun CanvasRootBuilder<C>.body() {
        this@CanvasRootSpec.body.invoke(this)
    }
}

open class CanvasRootTranslator<in C>(private val spec: CanvasRootSpec<C>) : Translator<C, CanvasRoot> {
    constructor(body: CanvasRootBuilder<C>.() -> Unit) : this(CanvasRootSpec<C>(body))

    override fun translate(context: C): CanvasRoot {
        val builder = CanvasRootBuilder(context)
        val translator = BuilderSpecTranslator<C, CanvasRootBuilder<C>, CanvasRoot>(builder, spec)
        return translator.translate(context)
    }
}

sealed class CanvasTree

data class CanvasNode(val transform: Transform, val children: List<CanvasTree>) : CanvasTree() {
    companion object {
        operator fun <C> invoke(body: CanvasNodeBuilder<C>.() -> Unit): CanvasNodeSpec<C> = CanvasNodeSpec<C>(body)
    }
}

data class CanvasLeaf(val transform: Transform, val canvas: Canvas) : CanvasTree() {
    companion object {
        operator fun <C> invoke(body: CanvasLeafBuilder<C>.() -> Unit): CanvasLeafSpec<C> = CanvasLeafSpec<C>(body)
    }
}

@DslBuilder
class CanvasNodeBuilder<out C>(
        val context: C,
        private var transform: Transform? = null,
        private var children: List<CanvasTree>? = emptyList()
) : Builder<CanvasNode> {
    fun <C> CanvasNodeBuilder<C>.transform(body: TransformBuilder<C>.() -> Unit) {
        this.transform = TransformTranslator<C>(body).translate(context)
    }

    fun <C> CanvasNodeBuilder<C>.transform(spec: TransformSpec<C>) {
        this.transform = TransformTranslator<C>(spec).translate(context)
    }

    fun <C> CanvasNodeBuilder<C>.children(spec: CanvasNodeSpec<C>) {
        this.children = this.children.orEmpty() + CanvasNodeTranslator<C>(spec).translate(context)
    }

    fun <C> CanvasNodeBuilder<C>.children(spec: CanvasLeafSpec<C>) {
        this.children = this.children.orEmpty() + CanvasLeafTranslator<C>(spec).translate(context)
    }

    fun <C, C2> CanvasNodeBuilder<C>.include(context: C2, body: CanvasNodeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> CanvasNodeBuilder<C>.include(context: C2, spec: CanvasNodeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> CanvasNodeBuilder<C>.include(body: CanvasNodeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> CanvasNodeBuilder<C>.include(spec: CanvasNodeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> CanvasNodeBuilder<C>.includeForEach(context: Iterable<C2>, body: CanvasNodeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> CanvasNodeBuilder<C>.includeForEach(context: Iterable<C2>, spec: CanvasNodeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CanvasNodeBuilder<C2> = CanvasNodeBuilder(context,transform,children)

    private fun <C2> merge(other: CanvasNodeBuilder<C2>) {
        this.transform = other.transform
        this.children = other.children
    }

    override fun build(): CanvasNode = CanvasNode(transform!!,children!!)
}

@DslBuilder
class CanvasLeafBuilder<out C>(
        val context: C,
        private var transform: Transform? = null,
        private var canvas: Canvas? = null
) : Builder<CanvasLeaf> {
    fun <C> CanvasLeafBuilder<C>.transform(body: TransformBuilder<C>.() -> Unit) {
        this.transform = TransformTranslator<C>(body).translate(context)
    }

    fun <C> CanvasLeafBuilder<C>.transform(spec: TransformSpec<C>) {
        this.transform = TransformTranslator<C>(spec).translate(context)
    }

    fun <C> CanvasLeafBuilder<C>.canvas(body: CanvasBuilder<C>.() -> Unit) {
        this.canvas = CanvasTranslator<C>(body).translate(context)
    }

    fun <C> CanvasLeafBuilder<C>.canvas(spec: CanvasSpec<C>) {
        this.canvas = CanvasTranslator<C>(spec).translate(context)
    }

    fun <C, C2> CanvasLeafBuilder<C>.include(context: C2, body: CanvasLeafBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> CanvasLeafBuilder<C>.include(context: C2, spec: CanvasLeafSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> CanvasLeafBuilder<C>.include(body: CanvasLeafBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> CanvasLeafBuilder<C>.include(spec: CanvasLeafSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> CanvasLeafBuilder<C>.includeForEach(context: Iterable<C2>, body: CanvasLeafBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> CanvasLeafBuilder<C>.includeForEach(context: Iterable<C2>, spec: CanvasLeafSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CanvasLeafBuilder<C2> = CanvasLeafBuilder(context,transform,canvas)

    private fun <C2> merge(other: CanvasLeafBuilder<C2>) {
        this.transform = other.transform
        this.canvas = other.canvas
    }

    override fun build(): CanvasLeaf = CanvasLeaf(transform!!,canvas!!)
}

open class CanvasNodeSpec<in C>(internal val body: CanvasNodeBuilder<C>.() -> Unit) : Spec<CanvasNodeBuilder<C>, CanvasNode> {
    override fun CanvasNodeBuilder<C>.body() {
        this@CanvasNodeSpec.body.invoke(this)
    }
}

open class CanvasLeafSpec<in C>(internal val body: CanvasLeafBuilder<C>.() -> Unit) : Spec<CanvasLeafBuilder<C>, CanvasLeaf> {
    override fun CanvasLeafBuilder<C>.body() {
        this@CanvasLeafSpec.body.invoke(this)
    }
}

open class CanvasNodeTranslator<in C>(private val spec: CanvasNodeSpec<C>) : Translator<C, CanvasNode> {
    constructor(body: CanvasNodeBuilder<C>.() -> Unit) : this(CanvasNodeSpec<C>(body))

    override fun translate(context: C): CanvasNode {
        val builder = CanvasNodeBuilder(context)
        val translator = BuilderSpecTranslator<C, CanvasNodeBuilder<C>, CanvasNode>(builder, spec)
        return translator.translate(context)
    }
}

open class CanvasLeafTranslator<in C>(private val spec: CanvasLeafSpec<C>) : Translator<C, CanvasLeaf> {
    constructor(body: CanvasLeafBuilder<C>.() -> Unit) : this(CanvasLeafSpec<C>(body))

    override fun translate(context: C): CanvasLeaf {
        val builder = CanvasLeafBuilder(context)
        val translator = BuilderSpecTranslator<C, CanvasLeafBuilder<C>, CanvasLeaf>(builder, spec)
        return translator.translate(context)
    }
}

data class Canvas(val paths: List<TransformedPath>) {
    companion object {
        operator fun <C> invoke(body: CanvasBuilder<C>.() -> Unit): CanvasSpec<C> = CanvasSpec<C>(body)
    }
}

@DslBuilder
class CanvasBuilder<out C>(val context: C, private var paths: List<TransformedPath>? = emptyList()) : Builder<Canvas> {
    fun <C> CanvasBuilder<C>.path(body: TransformedPathBuilder<C>.() -> Unit) {
        this.paths = this.paths.orEmpty() + TransformedPathTranslator<C>(body).translate(context)
    }

    fun <C> CanvasBuilder<C>.path(spec: TransformedPathSpec<C>) {
        this.paths = this.paths.orEmpty() + TransformedPathTranslator<C>(spec).translate(context)
    }

    fun <C, C2> CanvasBuilder<C>.include(context: C2, body: CanvasBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> CanvasBuilder<C>.include(context: C2, spec: CanvasSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> CanvasBuilder<C>.include(body: CanvasBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> CanvasBuilder<C>.include(spec: CanvasSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> CanvasBuilder<C>.includeForEach(context: Iterable<C2>, body: CanvasBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> CanvasBuilder<C>.includeForEach(context: Iterable<C2>, spec: CanvasSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CanvasBuilder<C2> = CanvasBuilder(context,paths)

    private fun <C2> merge(other: CanvasBuilder<C2>) {
        this.paths = other.paths
    }

    override fun build(): Canvas = Canvas(paths!!)
}

open class CanvasSpec<in C>(internal val body: CanvasBuilder<C>.() -> Unit) : Spec<CanvasBuilder<C>, Canvas> {
    override fun CanvasBuilder<C>.body() {
        this@CanvasSpec.body.invoke(this)
    }
}

open class CanvasTranslator<in C>(private val spec: CanvasSpec<C>) : Translator<C, Canvas> {
    constructor(body: CanvasBuilder<C>.() -> Unit) : this(CanvasSpec<C>(body))

    override fun translate(context: C): Canvas {
        val builder = CanvasBuilder(context)
        val translator = BuilderSpecTranslator<C, CanvasBuilder<C>, Canvas>(builder, spec)
        return translator.translate(context)
    }
}

data class TransformedPath(val transform: Transform, val path: Path) {
    companion object {
        operator fun <C> invoke(body: TransformedPathBuilder<C>.() -> Unit): TransformedPathSpec<C> = TransformedPathSpec<C>(body)
    }
}

@DslBuilder
class TransformedPathBuilder<out C>(
        val context: C,
        private var transform: Transform? = null,
        private var path: Path? = null
) : Builder<TransformedPath> {
    fun <C> TransformedPathBuilder<C>.transform(body: TransformBuilder<C>.() -> Unit) {
        this.transform = TransformTranslator<C>(body).translate(context)
    }

    fun <C> TransformedPathBuilder<C>.transform(spec: TransformSpec<C>) {
        this.transform = TransformTranslator<C>(spec).translate(context)
    }

    fun <C> TransformedPathBuilder<C>.path(body: PathBuilder<C>.() -> Unit) {
        this.path = PathTranslator<C>(body).translate(context)
    }

    fun <C> TransformedPathBuilder<C>.path(spec: PathSpec<C>) {
        this.path = PathTranslator<C>(spec).translate(context)
    }

    fun <C, C2> TransformedPathBuilder<C>.include(context: C2, body: TransformedPathBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TransformedPathBuilder<C>.include(context: C2, spec: TransformedPathSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> TransformedPathBuilder<C>.include(body: TransformedPathBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TransformedPathBuilder<C>.include(spec: TransformedPathSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TransformedPathBuilder<C>.includeForEach(context: Iterable<C2>, body: TransformedPathBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TransformedPathBuilder<C>.includeForEach(context: Iterable<C2>, spec: TransformedPathSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TransformedPathBuilder<C2> = TransformedPathBuilder(context,transform,path)

    private fun <C2> merge(other: TransformedPathBuilder<C2>) {
        this.transform = other.transform
        this.path = other.path
    }

    override fun build(): TransformedPath = TransformedPath(transform!!,path!!)
}

open class TransformedPathSpec<in C>(internal val body: TransformedPathBuilder<C>.() -> Unit) : Spec<TransformedPathBuilder<C>, TransformedPath> {
    override fun TransformedPathBuilder<C>.body() {
        this@TransformedPathSpec.body.invoke(this)
    }
}

open class TransformedPathTranslator<in C>(private val spec: TransformedPathSpec<C>) : Translator<C, TransformedPath> {
    constructor(body: TransformedPathBuilder<C>.() -> Unit) : this(TransformedPathSpec<C>(body))

    override fun translate(context: C): TransformedPath {
        val builder = TransformedPathBuilder(context)
        val translator = BuilderSpecTranslator<C, TransformedPathBuilder<C>, TransformedPath>(builder, spec)
        return translator.translate(context)
    }
}

data class Path(val color: Color, val verbs: List<Verb>) {
    companion object {
        operator fun <C> invoke(body: PathBuilder<C>.() -> Unit): PathSpec<C> = PathSpec<C>(body)
    }
}

@DslBuilder
class PathBuilder<out C>(
        val context: C,
        private var color: Color? = null,
        private var verbs: List<Verb>? = emptyList()
) : Builder<Path> {
    fun <C> PathBuilder<C>.color(body: ColorBuilder<C>.() -> Unit) {
        this.color = ColorTranslator<C>(body).translate(context)
    }

    fun <C> PathBuilder<C>.color(spec: ColorSpec<C>) {
        this.color = ColorTranslator<C>(spec).translate(context)
    }

    fun <C> PathBuilder<C>.verb(spec: MoveToSpec<C>) {
        this.verbs = this.verbs.orEmpty() + MoveToTranslator<C>(spec).translate(context)
    }

    fun <C> PathBuilder<C>.verb(spec: LineToSpec<C>) {
        this.verbs = this.verbs.orEmpty() + LineToTranslator<C>(spec).translate(context)
    }

    fun <C> PathBuilder<C>.verb(spec: ArcSpec<C>) {
        this.verbs = this.verbs.orEmpty() + ArcTranslator<C>(spec).translate(context)
    }

    fun <C> PathBuilder<C>.verb(spec: ArcToSpec<C>) {
        this.verbs = this.verbs.orEmpty() + ArcToTranslator<C>(spec).translate(context)
    }

    fun <C, C2> PathBuilder<C>.include(context: C2, body: PathBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> PathBuilder<C>.include(context: C2, spec: PathSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> PathBuilder<C>.include(body: PathBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> PathBuilder<C>.include(spec: PathSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> PathBuilder<C>.includeForEach(context: Iterable<C2>, body: PathBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> PathBuilder<C>.includeForEach(context: Iterable<C2>, spec: PathSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PathBuilder<C2> = PathBuilder(context,color,verbs)

    private fun <C2> merge(other: PathBuilder<C2>) {
        this.color = other.color
        this.verbs = other.verbs
    }

    override fun build(): Path = Path(color!!,verbs!!)
}

open class PathSpec<in C>(internal val body: PathBuilder<C>.() -> Unit) : Spec<PathBuilder<C>, Path> {
    override fun PathBuilder<C>.body() {
        this@PathSpec.body.invoke(this)
    }
}

open class PathTranslator<in C>(private val spec: PathSpec<C>) : Translator<C, Path> {
    constructor(body: PathBuilder<C>.() -> Unit) : this(PathSpec<C>(body))

    override fun translate(context: C): Path {
        val builder = PathBuilder(context)
        val translator = BuilderSpecTranslator<C, PathBuilder<C>, Path>(builder, spec)
        return translator.translate(context)
    }
}

data class Color(
        val red: Double,
        val green: Double,
        val blue: Double,
        val alpha: Double
) {
    companion object {
        operator fun <C> invoke(body: ColorBuilder<C>.() -> Unit): ColorSpec<C> = ColorSpec<C>(body)
    }
}

@DslBuilder
class ColorBuilder<out C>(
        val context: C,
        private var red: Double? = null,
        private var green: Double? = null,
        private var blue: Double? = null,
        private var alpha: Double? = null
) : Builder<Color> {
    fun <C> ColorBuilder<C>.red(red: Double) {
        this.red = red
    }

    fun <C> ColorBuilder<C>.green(green: Double) {
        this.green = green
    }

    fun <C> ColorBuilder<C>.blue(blue: Double) {
        this.blue = blue
    }

    fun <C> ColorBuilder<C>.alpha(alpha: Double) {
        this.alpha = alpha
    }

    fun <C, C2> ColorBuilder<C>.include(context: C2, body: ColorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ColorBuilder<C>.include(context: C2, spec: ColorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> ColorBuilder<C>.include(body: ColorBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ColorBuilder<C>.include(spec: ColorSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ColorBuilder<C>.includeForEach(context: Iterable<C2>, body: ColorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ColorBuilder<C>.includeForEach(context: Iterable<C2>, spec: ColorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ColorBuilder<C2> = ColorBuilder(context,red,green,blue,alpha)

    private fun <C2> merge(other: ColorBuilder<C2>) {
        this.red = other.red
        this.green = other.green
        this.blue = other.blue
        this.alpha = other.alpha
    }

    override fun build(): Color = Color(red!!,green!!,blue!!,alpha!!)
}

open class ColorSpec<in C>(internal val body: ColorBuilder<C>.() -> Unit) : Spec<ColorBuilder<C>, Color> {
    override fun ColorBuilder<C>.body() {
        this@ColorSpec.body.invoke(this)
    }
}

open class ColorTranslator<in C>(private val spec: ColorSpec<C>) : Translator<C, Color> {
    constructor(body: ColorBuilder<C>.() -> Unit) : this(ColorSpec<C>(body))

    override fun translate(context: C): Color {
        val builder = ColorBuilder(context)
        val translator = BuilderSpecTranslator<C, ColorBuilder<C>, Color>(builder, spec)
        return translator.translate(context)
    }
}

data class Transform(
        val a: Double,
        val b: Double,
        val c: Double,
        val d: Double,
        val e: Double,
        val f: Double
) {
    companion object {
        operator fun <C> invoke(body: TransformBuilder<C>.() -> Unit): TransformSpec<C> = TransformSpec<C>(body)
    }
}

@DslBuilder
class TransformBuilder<out C>(
        val context: C,
        private var a: Double? = null,
        private var b: Double? = null,
        private var c: Double? = null,
        private var d: Double? = null,
        private var e: Double? = null,
        private var f: Double? = null
) : Builder<Transform> {
    fun <C> TransformBuilder<C>.a(a: Double) {
        this.a = a
    }

    fun <C> TransformBuilder<C>.b(b: Double) {
        this.b = b
    }

    fun <C> TransformBuilder<C>.c(c: Double) {
        this.c = c
    }

    fun <C> TransformBuilder<C>.d(d: Double) {
        this.d = d
    }

    fun <C> TransformBuilder<C>.e(e: Double) {
        this.e = e
    }

    fun <C> TransformBuilder<C>.f(f: Double) {
        this.f = f
    }

    fun <C, C2> TransformBuilder<C>.include(context: C2, body: TransformBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TransformBuilder<C>.include(context: C2, spec: TransformSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> TransformBuilder<C>.include(body: TransformBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TransformBuilder<C>.include(spec: TransformSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TransformBuilder<C>.includeForEach(context: Iterable<C2>, body: TransformBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TransformBuilder<C>.includeForEach(context: Iterable<C2>, spec: TransformSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TransformBuilder<C2> = TransformBuilder(context,a,b,c,d,e,f)

    private fun <C2> merge(other: TransformBuilder<C2>) {
        this.a = other.a
        this.b = other.b
        this.c = other.c
        this.d = other.d
        this.e = other.e
        this.f = other.f
    }

    override fun build(): Transform = Transform(a!!,b!!,c!!,d!!,e!!,f!!)
}

open class TransformSpec<in C>(internal val body: TransformBuilder<C>.() -> Unit) : Spec<TransformBuilder<C>, Transform> {
    override fun TransformBuilder<C>.body() {
        this@TransformSpec.body.invoke(this)
    }
}

open class TransformTranslator<in C>(private val spec: TransformSpec<C>) : Translator<C, Transform> {
    constructor(body: TransformBuilder<C>.() -> Unit) : this(TransformSpec<C>(body))

    override fun translate(context: C): Transform {
        val builder = TransformBuilder(context)
        val translator = BuilderSpecTranslator<C, TransformBuilder<C>, Transform>(builder, spec)
        return translator.translate(context)
    }
}

sealed class Verb

data class MoveTo(val x: Double, val y: Double) : Verb() {
    companion object {
        operator fun <C> invoke(body: MoveToBuilder<C>.() -> Unit): MoveToSpec<C> = MoveToSpec<C>(body)
    }
}

data class LineTo(val x: Double, val y: Double) : Verb() {
    companion object {
        operator fun <C> invoke(body: LineToBuilder<C>.() -> Unit): LineToSpec<C> = LineToSpec<C>(body)
    }
}

data class Arc(
        val x: Double,
        val y: Double,
        val radius: Double,
        val startAngle: Double,
        val endAngle: Double,
        val anticlockwise : Boolean
) : Verb() {
    companion object {
        operator fun <C> invoke(body: ArcBuilder<C>.() -> Unit): ArcSpec<C> = ArcSpec<C>(body)
    }
}

data class ArcTo(
        val x1: Double,
        val y1: Double,
        val x2: Double,
        val y2: Double,
        val radius: Double
) : Verb() {
    companion object {
        operator fun <C> invoke(body: ArcToBuilder<C>.() -> Unit): ArcToSpec<C> = ArcToSpec<C>(body)
    }
}

@DslBuilder
class MoveToBuilder<out C>(
        val context: C,
        private var x: Double? = null,
        private var y: Double? = null
) : Builder<MoveTo> {
    fun <C> MoveToBuilder<C>.x(x: Double) {
        this.x = x
    }

    fun <C> MoveToBuilder<C>.y(y: Double) {
        this.y = y
    }

    fun <C, C2> MoveToBuilder<C>.include(context: C2, body: MoveToBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> MoveToBuilder<C>.include(context: C2, spec: MoveToSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> MoveToBuilder<C>.include(body: MoveToBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> MoveToBuilder<C>.include(spec: MoveToSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> MoveToBuilder<C>.includeForEach(context: Iterable<C2>, body: MoveToBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> MoveToBuilder<C>.includeForEach(context: Iterable<C2>, spec: MoveToSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): MoveToBuilder<C2> = MoveToBuilder(context,x,y)

    private fun <C2> merge(other: MoveToBuilder<C2>) {
        this.x = other.x
        this.y = other.y
    }

    override fun build(): MoveTo = MoveTo(x!!,y!!)
}

@DslBuilder
class LineToBuilder<out C>(
        val context: C,
        private var x: Double? = null,
        private var y: Double? = null
) : Builder<LineTo> {
    fun <C> LineToBuilder<C>.x(x: Double) {
        this.x = x
    }

    fun <C> LineToBuilder<C>.y(y: Double) {
        this.y = y
    }

    fun <C, C2> LineToBuilder<C>.include(context: C2, body: LineToBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> LineToBuilder<C>.include(context: C2, spec: LineToSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> LineToBuilder<C>.include(body: LineToBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> LineToBuilder<C>.include(spec: LineToSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> LineToBuilder<C>.includeForEach(context: Iterable<C2>, body: LineToBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> LineToBuilder<C>.includeForEach(context: Iterable<C2>, spec: LineToSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): LineToBuilder<C2> = LineToBuilder(context,x,y)

    private fun <C2> merge(other: LineToBuilder<C2>) {
        this.x = other.x
        this.y = other.y
    }

    override fun build(): LineTo = LineTo(x!!,y!!)
}

@DslBuilder
class ArcBuilder<out C>(
        val context: C,
        private var x: Double? = null,
        private var y: Double? = null,
        private var radius: Double? = null,
        private var startAngle: Double? = null,
        private var endAngle: Double? = null,
        private var anticlockwise : Boolean? = null
) : Builder<Arc> {
    fun <C> ArcBuilder<C>.x(x: Double) {
        this.x = x
    }

    fun <C> ArcBuilder<C>.y(y: Double) {
        this.y = y
    }

    fun <C> ArcBuilder<C>.radius(radius: Double) {
        this.radius = radius
    }

    fun <C> ArcBuilder<C>.startAngle(startAngle: Double) {
        this.startAngle = startAngle
    }

    fun <C> ArcBuilder<C>.endAngle(endAngle: Double) {
        this.endAngle = endAngle
    }

    fun <C> ArcBuilder<C>.anticlockwise (anticlockwise : Boolean) {
        this.anticlockwise  = anticlockwise 
    }

    fun <C, C2> ArcBuilder<C>.include(context: C2, body: ArcBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ArcBuilder<C>.include(context: C2, spec: ArcSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> ArcBuilder<C>.include(body: ArcBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ArcBuilder<C>.include(spec: ArcSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ArcBuilder<C>.includeForEach(context: Iterable<C2>, body: ArcBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ArcBuilder<C>.includeForEach(context: Iterable<C2>, spec: ArcSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ArcBuilder<C2> = ArcBuilder(context,x,y,radius,startAngle,endAngle,anticlockwise )

    private fun <C2> merge(other: ArcBuilder<C2>) {
        this.x = other.x
        this.y = other.y
        this.radius = other.radius
        this.startAngle = other.startAngle
        this.endAngle = other.endAngle
        this.anticlockwise  = other.anticlockwise 
    }

    override fun build(): Arc = Arc(x!!,y!!,radius!!,startAngle!!,endAngle!!,anticlockwise !!)
}

@DslBuilder
class ArcToBuilder<out C>(
        val context: C,
        private var x1: Double? = null,
        private var y1: Double? = null,
        private var x2: Double? = null,
        private var y2: Double? = null,
        private var radius: Double? = null
) : Builder<ArcTo> {
    fun <C> ArcToBuilder<C>.x1(x1: Double) {
        this.x1 = x1
    }

    fun <C> ArcToBuilder<C>.y1(y1: Double) {
        this.y1 = y1
    }

    fun <C> ArcToBuilder<C>.x2(x2: Double) {
        this.x2 = x2
    }

    fun <C> ArcToBuilder<C>.y2(y2: Double) {
        this.y2 = y2
    }

    fun <C> ArcToBuilder<C>.radius(radius: Double) {
        this.radius = radius
    }

    fun <C, C2> ArcToBuilder<C>.include(context: C2, body: ArcToBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> ArcToBuilder<C>.include(context: C2, spec: ArcToSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C> ArcToBuilder<C>.include(body: ArcToBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> ArcToBuilder<C>.include(spec: ArcToSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> ArcToBuilder<C>.includeForEach(context: Iterable<C2>, body: ArcToBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> ArcToBuilder<C>.includeForEach(context: Iterable<C2>, spec: ArcToSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ArcToBuilder<C2> = ArcToBuilder(context,x1,y1,x2,y2,radius)

    private fun <C2> merge(other: ArcToBuilder<C2>) {
        this.x1 = other.x1
        this.y1 = other.y1
        this.x2 = other.x2
        this.y2 = other.y2
        this.radius = other.radius
    }

    override fun build(): ArcTo = ArcTo(x1!!,y1!!,x2!!,y2!!,radius!!)
}

open class MoveToSpec<in C>(internal val body: MoveToBuilder<C>.() -> Unit) : Spec<MoveToBuilder<C>, MoveTo> {
    override fun MoveToBuilder<C>.body() {
        this@MoveToSpec.body.invoke(this)
    }
}

open class LineToSpec<in C>(internal val body: LineToBuilder<C>.() -> Unit) : Spec<LineToBuilder<C>, LineTo> {
    override fun LineToBuilder<C>.body() {
        this@LineToSpec.body.invoke(this)
    }
}

open class ArcSpec<in C>(internal val body: ArcBuilder<C>.() -> Unit) : Spec<ArcBuilder<C>, Arc> {
    override fun ArcBuilder<C>.body() {
        this@ArcSpec.body.invoke(this)
    }
}

open class ArcToSpec<in C>(internal val body: ArcToBuilder<C>.() -> Unit) : Spec<ArcToBuilder<C>, ArcTo> {
    override fun ArcToBuilder<C>.body() {
        this@ArcToSpec.body.invoke(this)
    }
}

open class MoveToTranslator<in C>(private val spec: MoveToSpec<C>) : Translator<C, MoveTo> {
    constructor(body: MoveToBuilder<C>.() -> Unit) : this(MoveToSpec<C>(body))

    override fun translate(context: C): MoveTo {
        val builder = MoveToBuilder(context)
        val translator = BuilderSpecTranslator<C, MoveToBuilder<C>, MoveTo>(builder, spec)
        return translator.translate(context)
    }
}

open class LineToTranslator<in C>(private val spec: LineToSpec<C>) : Translator<C, LineTo> {
    constructor(body: LineToBuilder<C>.() -> Unit) : this(LineToSpec<C>(body))

    override fun translate(context: C): LineTo {
        val builder = LineToBuilder(context)
        val translator = BuilderSpecTranslator<C, LineToBuilder<C>, LineTo>(builder, spec)
        return translator.translate(context)
    }
}

open class ArcTranslator<in C>(private val spec: ArcSpec<C>) : Translator<C, Arc> {
    constructor(body: ArcBuilder<C>.() -> Unit) : this(ArcSpec<C>(body))

    override fun translate(context: C): Arc {
        val builder = ArcBuilder(context)
        val translator = BuilderSpecTranslator<C, ArcBuilder<C>, Arc>(builder, spec)
        return translator.translate(context)
    }
}

open class ArcToTranslator<in C>(private val spec: ArcToSpec<C>) : Translator<C, ArcTo> {
    constructor(body: ArcToBuilder<C>.() -> Unit) : this(ArcToSpec<C>(body))

    override fun translate(context: C): ArcTo {
        val builder = ArcToBuilder(context)
        val translator = BuilderSpecTranslator<C, ArcToBuilder<C>, ArcTo>(builder, spec)
        return translator.translate(context)
    }
}
