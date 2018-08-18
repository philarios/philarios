package io.philarios.canvas.v0.translators.js

import io.philarios.canvas.v0.*
import io.philarios.core.v0.Translator
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class CanvasDrawer(private val canvas: HTMLCanvasElement) : Translator<CanvasRoot, Unit> {
    private val canvasContext: CanvasRenderingContext2D
        get() = canvas.getContext("2d") as CanvasRenderingContext2D

    override fun translate(context: CanvasRoot) {
        context.tree?.let { translate(it) }
    }

    private fun translate(tree: CanvasTree) {
        when (tree) {
            is CanvasNode -> {
                canvasContext.save()
                tree.transform?.let { canvasContext.transform(it) }
                tree.children.forEach { translate(it) }
                canvasContext.restore()
            }
            is CanvasLeaf -> {
                canvasContext.save()
                tree.transform?.let { canvasContext.transform(it) }
                translate(tree.canvas)
                canvasContext.restore()
            }
        }
    }

    private fun translate(canvas: Canvas) {
        canvas.paths.forEach { (transform, path) ->
            canvasContext.save()
            transform?.let { canvasContext.transform(it) }
            canvasContext.beginPath()
            path.verbs.forEach {
                when (it) {
                    is MoveTo -> canvasContext.moveTo(it.x, it.y)
                    is LineTo -> canvasContext.lineTo(it.x, it.y)
                    is Arc -> canvasContext.arc(it.x, it.y, it.radius, it.startAngle, it.endAngle, it.anticlockwise)
                    is ArcTo -> canvasContext.arcTo(it.x1, it.y1, it.x2, it.y2, it.radius)
                }
            }
            canvasContext.fillStyle = "rgb(${path.color.red},${path.color.green},${path.color.blue})"
            canvasContext.fill()
            canvasContext.restore()
        }
    }
}

private fun CanvasRenderingContext2D.transform(transform: Transform) {
    transform(transform.a, transform.b, transform.c, transform.d, transform.e, transform.f)
}