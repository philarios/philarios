package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.scaffoldTypeName

fun Field.scaffoldTypeName(typeRefs: Map<RefType, Type>): TypeName =
        copy(type = type.resolveRefs(typeRefs))
                .resolvedScaffoldTypeName

private fun Type.resolveRefs(typeRefs: Map<RefType, Type>) = when (this) {
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

private val Field.resolvedScaffoldTypeName get() = when (type) {
    is OptionType -> type.type.scaffoldTypeName.asNullable()
    is ListType -> ParameterizedTypeName.get(List::class.className, type.type.scaffoldTypeName)
    is MapType -> ParameterizedTypeName.get(
            Map::class.className, type.keyType.scaffoldTypeName, type.valueType.scaffoldTypeName)
    else -> type.scaffoldTypeName.asNullable()
}
