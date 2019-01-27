package io.philarios.schema.translators.codegen.types.refs

import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal val Type.refTypeSpecs
    get() = when (this) {
        is Struct -> listOf(refTypeSpec).mapNotNull { it }
        is Union -> refTypeSpecs
        else -> emptyList()
    }
