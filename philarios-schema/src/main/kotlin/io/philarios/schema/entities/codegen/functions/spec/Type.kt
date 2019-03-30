package io.philarios.schema.entities.codegen.functions.spec

import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal val Type.specFunSpecs
    get() = when (this) {
        is Struct -> listOf(specFunSpec)
        is Union -> specFunSpecs
        else -> emptyList()
    }.mapNotNull { it }

