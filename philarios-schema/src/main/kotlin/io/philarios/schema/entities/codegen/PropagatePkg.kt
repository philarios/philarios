package io.philarios.schema.entities.codegen

import io.philarios.schema.*

internal fun Schema.propagatePkg(): Schema {
    return copy(types = types.map { it.propagatePkg(pkg) })
}

private fun <T : Type> T.propagatePkg(pkg: String): T {
    return when (this) {
        is Struct -> copy(pkg = this.pkg?: pkg, fields = fields.map { it.propagatePkg(pkg) })
        is Union -> copy(pkg = this.pkg ?: pkg, shapes = shapes.map { it.propagatePkg(pkg) })
        is EnumType -> copy(pkg = this.pkg ?: pkg)
        is RefType -> copy(pkg = this.pkg ?: pkg)
        is OptionType -> copy(type = type.propagatePkg(pkg))
        is ListType -> copy(type = type.propagatePkg(pkg))
        is MapType -> copy(keyType = keyType.propagatePkg(pkg), valueType = valueType.propagatePkg(pkg))
        else -> this
    } as T
}

private fun Field.propagatePkg(pkg: String): Field = copy(type = type.propagatePkg(pkg))

