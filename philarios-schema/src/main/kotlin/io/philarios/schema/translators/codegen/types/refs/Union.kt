package io.philarios.schema.translators.codegen.types.refs

import io.philarios.schema.Union

internal val Union.refTypeSpecs
    get() =
        shapes.mapNotNull { it.refTypeSpec }
