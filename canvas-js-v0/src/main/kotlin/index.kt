import io.philarios.canvas.v0.CanvasRootTranslator
import io.philarios.canvas.v0.translators.js.CanvasDrawer
import io.philarios.canvas.v0.translators.js.DebugTranslator
import io.philarios.core.v0.emptyContext
import io.philarios.layout.example.v0.BoxCanvasRootSpec
import io.philarios.layout.example.v0.BoxResolver
import io.philarios.layout.example.v0.TestBox
import io.philarios.layout.v0.BoxTranslator
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    val canvas = initializeCanvas()
    val canvasDrawer = CanvasDrawer(canvas)

    console.log("test")

    emptyContext()
            .translate(BoxTranslator(TestBox))
            .translate(DebugTranslator())
            .translate(BoxResolver)
            .translate(DebugTranslator())
            .translate(CanvasRootTranslator(BoxCanvasRootSpec))
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