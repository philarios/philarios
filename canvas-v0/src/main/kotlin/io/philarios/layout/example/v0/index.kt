package io.philarios.layout.example.v0

import io.philarios.canvas.v0.CanvasRootTranslator
import io.philarios.core.v0.emptyContext
import io.philarios.layout.v0.BoxTranslator


fun main(args: Array<String>) {
    emptyContext()
            .translate(BoxTranslator(TestBox))
            .translate(BoxResolver)
            .translate(CanvasRootTranslator(BoxCanvasRootSpec))
            .value.let { println(it) }
}