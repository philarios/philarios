import io.philarios.canvas.examle.v0.ExampleDiagram
import io.philarios.canvas.v0.CanvasRootTranslator
import io.philarios.canvas.v0.translators.js.CanvasDrawer
import io.philarios.canvas.v0.translators.js.DebugTranslator
import io.philarios.core.v0.emptyContext
import io.philarios.diagram.v0.DiagramTranslator
import io.philarios.diagram.v0.specs.DiagramCanvasRootSpec
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    val canvas = initializeCanvas()
    val canvasDrawer = CanvasDrawer(canvas)

    console.log("test")

    emptyContext()
            .translate(DiagramTranslator(ExampleDiagram))
            .translate(DebugTranslator())
            .translate(CanvasRootTranslator(DiagramCanvasRootSpec))
            .translate(DebugTranslator())
            .translate(canvasDrawer)
}

fun initializeCanvas(): HTMLCanvasElement {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.canvas.width = window.innerWidth
    context.canvas.height = window.innerHeight
    document.body!!.appendChild(canvas)
    return canvas
}