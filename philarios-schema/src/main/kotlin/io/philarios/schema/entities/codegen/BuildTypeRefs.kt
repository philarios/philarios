package io.philarios.schema.entities.codegen

import io.philarios.schema.*

internal fun Schema.buildTypeRefs(): Map<RefType, Type> {
    return types
            .flatMap { it.buildTypeRefs() }
            .toMap()
}

private fun Type.buildTypeRefs(): List<Pair<RefType, Type>> {
    return when (this) {
        is Struct -> listOf(Pair(RefType(pkg, name), this))
        is Union -> shapes.flatMap { it.buildTypeRefs() } + Pair(RefType(pkg, name), this)
        is EnumType -> listOf(Pair(RefType(pkg, name), this))
        else -> emptyList()
    }
}
