package io.philarios.schema.translators.codegen.types.shell

import io.philarios.schema.*

internal fun Type.resolveRefs(typeRefs: Map<RefType, Type>): Type = when (this) {
    is RefType -> typeRefs[this]!!
    is OptionType -> copy(type = type.resolveRefs(typeRefs))
    is ListType -> copy(type = type.resolveRefs(typeRefs))
    is MapType -> copy(
            keyType = keyType.resolveRefs(typeRefs),
            valueType = valueType.resolveRefs(typeRefs)
    )
    else -> this
}