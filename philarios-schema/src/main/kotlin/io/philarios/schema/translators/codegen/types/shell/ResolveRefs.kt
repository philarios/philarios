package io.philarios.schema.translators.codegen.types.shell

import io.philarios.schema.*

internal fun Type.resolveRefs(typeRefs: Map<RefType, Type>) = when (this) {
    is RefType -> typeRefs[this]!!
    is OptionType -> when (type) {
        is RefType -> copy(type = typeRefs[type]!!)
        else -> this
    }
    is ListType -> when (type) {
        is RefType -> copy(type = typeRefs[type]!!)
        else -> this
    }
    is MapType -> run {
        when (keyType) {
            is RefType -> copy(keyType = typeRefs[keyType]!!)
            else -> this
        }
    }.run {
        when (valueType) {
            is RefType -> copy(valueType = typeRefs[valueType]!!)
            else -> this
        }
    }
    else -> this
}