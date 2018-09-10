package io.philarios.diagram.v0.specs

import io.philarios.canvas.v0.*
import io.philarios.diagram.v0.Bounds
import io.philarios.diagram.v0.Diagram
import io.philarios.layout.example.v0.rectangle

object DiagramCanvasRootSpec : CanvasRootSpec<Diagram>({

    tree(CanvasNode {
        transform {
            a(context.size.width);b(0.0);c(0.0);d(context.size.height);e(0.0);f(0.0)
        }
        include(context.size) {
            children(TestCanvasNode)
        }
    })

})

data class Box<in C>(
        val scale: Double,
        val translate: Double,
        val body: CanvasNodeBuilder<C>.() -> Unit
)

object TestCanvasNode : CanvasNodeSpec<Bounds>({
    transform {
        a(1.0);b(0.0);c(0.0);d(1.0);e(0.0);f(0.0)
    }
    rectangle(0.0, 0.0, 0.0)

    val boxes = listOf(
            fixedBox(50.0, 50.0) {
                rectangle(0.0, 0.0, 255.0)
            },
            transformedBox(0.5, 0.25) {
                rectangle(255.0, 0.0, 0.0)
            },
            transformedBox(0.25, 0.75) {
                rectangle(0.0, 255.0, 0.0)
            }
    )

    boxes.forEach { box(it) }

})

fun CanvasNodeBuilder<Bounds>.fixedBox(x: Double, width: Double, body: CanvasNodeBuilder<Bounds>.() -> Unit): Box<Bounds> {
    return transformedBox(width / context.width, x / context.width, body)
}

fun CanvasNodeBuilder<Bounds>.transformedBox(scale: Double, translate: Double, body: CanvasNodeBuilder<Bounds>.() -> Unit): Box<Bounds> {
    return Box(scale, translate, body)
}

fun <C> CanvasNodeBuilder<C>.box(box: Box<C>) {
    children(CanvasNode {
        transform {
            a(box.scale);b(0.0);c(0.0);d(1.0);e(box.translate);f(0.0)
        }
        include(box.body)
    })
}