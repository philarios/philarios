package io.philarios.schema.entities.codegen.types.model

import io.philarios.schema.EnumType
import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal val Type.modelTypeSpecs
    get() = when (this) {
        is Struct -> listOf(modelTypeSpec)
        is Union -> modelTypeSpecs
        is EnumType -> listOf(modelTypeSpec)
        else -> emptyList()
    }
