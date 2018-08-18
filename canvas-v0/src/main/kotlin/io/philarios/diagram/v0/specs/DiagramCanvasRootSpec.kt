package io.philarios.diagram.v0.specs

import io.philarios.canvas.v0.CanvasLeaf
import io.philarios.canvas.v0.CanvasRootSpec
import io.philarios.canvas.v0.LineTo
import io.philarios.canvas.v0.MoveTo
import io.philarios.diagram.v0.Diagram

object DiagramCanvasRootSpec : CanvasRootSpec<Diagram>({

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
                        x(00.0)
                        y(00.0)
                    })
                    verb(LineTo {
                        x(context.size.width)
                        y(0.0)
                    })
                    verb(LineTo {
                        x(context.size.width)
                        y(context.size.height)
                    })
                    verb(LineTo {
                        x(00.0)
                        y(context.size.height)
                    })
                    verb(LineTo {
                        x(0.0)
                        y(0.0)
                    })
                }
            }
        }
    })

})