package io.philarios.canvas.examle.v0

import io.philarios.canvas.v0.CanvasLeaf
import io.philarios.canvas.v0.CanvasRootSpec
import io.philarios.canvas.v0.LineTo
import io.philarios.canvas.v0.MoveTo

object ExampleCanvasRoot : CanvasRootSpec<Any?>({

    tree(CanvasLeaf {
        canvas {
            path {
                path {
                    color {
                        red(100.0)
                        green(100.0)
                        blue(100.0)
                        alpha(100.0)
                    }
                    verb(MoveTo {
                        x(50.0)
                        y(50.0)
                    })
                    verb(LineTo {
                        x(100.0)
                        y(50.0)
                    })
                    verb(LineTo {
                        x(100.0)
                        y(100.0)
                    })
                    verb(LineTo {
                        x(50.0)
                        y(100.0)
                    })
                    verb(LineTo {
                        x(50.0)
                        y(50.0)
                    })
                }
            }
        }
    })

})