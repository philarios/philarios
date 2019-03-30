package io.philarios.schema.entities.codegen.types.scaffolder

import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal val Type.scaffolderTypeSpecs
    get() = when (this) {
        is Struct -> listOf(scaffolderTypeSpec)
        is Union -> scaffolderTypeSpecs
        else -> emptyList()
    }.mapNotNull { it }
