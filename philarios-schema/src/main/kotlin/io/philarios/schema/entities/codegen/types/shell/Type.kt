package io.philarios.schema.entities.codegen.types.shell

import io.philarios.schema.RefType
import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

internal fun Type.shellTypeSpecs(typeRefs: Map<RefType, Type>) = when (this) {
    is Struct -> listOf(shellTypeSpec(typeRefs))
    is Union -> shellTypeSpecs(typeRefs)
    else -> emptyList()
}.mapNotNull { it }
