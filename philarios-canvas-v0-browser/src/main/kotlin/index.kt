import io.philarios.canvas.v0.CanvasRootTranslator
import io.philarios.canvas.v0.ExampleCanvas
import io.philarios.canvas.v0.translators.CanvasDrawer
import io.philarios.core.v0.emptyContext
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    val canvas = initializeCanvas()
    val canvasDrawer = CanvasDrawer(canvas)

    println("hello world")

    emptyContext()
            .translate(CanvasRootTranslator(ExampleCanvas))
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