package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.scaffoldTypeName

fun Field.scaffoldTypeName(typeRefs: Map<RefType, Type>): TypeName =
        copy(type = type.resolveRefs(typeRefs))
                .scaffoldTypeName

private val Field.scaffoldTypeName get() = when (type) {
    is OptionType -> type.type.scaffoldTypeName.asNullable()
    is ListType -> ParameterizedTypeName.get(List::class.className, type.type.scaffoldTypeName)
    is MapType -> ParameterizedTypeName.get(
            Map::class.className, type.keyType.scaffoldTypeName, type.valueType.scaffoldTypeName)
    else -> type.scaffoldTypeName.asNullable()
}
