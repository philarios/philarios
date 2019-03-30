package io.philarios.schema.entities.codegen.types.spec

import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal val Type.specTypeSpecs
    get() = when (this) {
        is Struct -> listOf(specTypeSpec)
        is Union -> specTypeSpecs
        else -> emptyList()
    }.mapNotNull { it }

