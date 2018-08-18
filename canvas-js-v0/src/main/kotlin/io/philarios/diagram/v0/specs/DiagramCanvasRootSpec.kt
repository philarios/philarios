package io.philarios.diagram.v0.specs

import io.philarios.canvas.v0.CanvasLeaf
import io.philarios.canvas.v0.CanvasRootSpec
import io.philarios.canvas.v0.LineTo
import io.philarios.canvas.v0.MoveTo
import io.philarios.diagram.v0.Diagram

object DiagramCanvasRootSpec : CanvasRootSpec<Diagram>({

    tree(CanvasLeaf {
        transform {
            a(1.0);b(0.0);c(0.0);d(1.0);e(100.0);f(100.0)
        }
        canvas {
            path {
                path {
                    color {
                        red(250.0)
                        green(250.0)
                        blue(250.0)
                        alpha(255.0)
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