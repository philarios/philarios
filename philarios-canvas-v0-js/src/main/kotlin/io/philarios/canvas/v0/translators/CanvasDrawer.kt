package io.philarios.canvas.v0.translators

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
                canvasContext.withTransform(tree.transform) {
                    tree.children.forEach { translate(it) }
                }
            }
            is CanvasLeaf -> {
                canvasContext.withTransform(tree.transform) {
                    translate(tree.canvas)
                }
            }
        }
    }

    private fun translate(canvas: Canvas) {
        canvas.paths.forEach { (transform, path) ->
            canvasContext.withTransform(transform) {
                canvasContext.beginPath()
                path.verbs.forEach {
                    when (it) {
                        is MoveTo -> canvasContext.moveTo(it.x, it.y)
                        is LineTo -> canvasContext.lineTo(it.x, it.y)
                        is Arc -> canvasContext.arc(it.x, it.y, it.radius, it.startAngle, it.endAngle, it.anticlockwise)
                        is ArcTo -> canvasContext.arcTo(it.x1, it.y1, it.x2, it.y2, it.radius)
                    }
                }
                when (path.method) {
                    is Fill -> {
                        canvasContext.fillStyle = "rgb(${path.color.red},${path.color.green},${path.color.blue})"
                        canvasContext.fill()
                    }
                    is Stroke -> {
                        canvasContext.lineWidth = path.method.lineWidth
                        canvasContext.strokeStyle = "rgb(${path.color.red},${path.color.green},${path.color.blue})"
                        canvasContext.stroke()
                    }
                }
            }
        }
    }
}

private inline fun CanvasRenderingContext2D.withTransform(transform: Transform?, body: () -> Unit) {
    if (transform == null) {
        body()
        return
    }
    save()
    transform(transform)
    body()
    restore()
}

private fun CanvasRenderingContext2D.transform(transform: Transform) {
    transform(transform.a, transform.b, transform.c, transform.d, transform.e, transform.f)
}