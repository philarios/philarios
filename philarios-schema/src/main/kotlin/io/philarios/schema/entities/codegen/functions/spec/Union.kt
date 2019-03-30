package io.philarios.schema.entities.codegen.functions.spec

import io.philarios.schema.Union

internal val Union.specFunSpecs
    get() = shapeSpecFunSpecs

internal val Union.shapeSpecFunSpecs
    get() =
        shapes.map { it.specFunSpec }
