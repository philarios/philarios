package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.nullableTypeName
import io.philarios.schema.translators.codegen.util.scaffoldClassName
import io.philarios.schema.translators.codegen.util.typeName

fun Field.scaffoldType(typeRefs: Map<RefType, Type>): TypeName = when {
    type is Struct && type.fields.isEmpty() -> this.type.nullableTypeName
    type is Struct -> this.type.scaffoldClassName.asNullable()
    type is Union -> this.type.scaffoldClassName.asNullable()
    type is RefType -> this.copy(type = typeRefs[this.type]!!).scaffoldType(typeRefs)
    type is OptionType -> {
        val optionType = this.type.type
        when (optionType) {
            is Struct -> this.type.className.asNonNullable().scaffoldClassName.asNullable()
            is Union -> this.type.className.asNonNullable().scaffoldClassName.asNullable()
            is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).scaffoldType(typeRefs)
            else -> this.type.nullableTypeName
        }
    }
    type is ListType -> {
        val listType = this.type.type
        when (listType) {
            is Struct -> ParameterizedTypeName.get(List::class.className, listType.scaffoldClassName)
            is Union -> ParameterizedTypeName.get(List::class.className, listType.scaffoldClassName)
            is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).scaffoldType(typeRefs)
            else -> this.type.typeName
        }
    }
//            is MapType -> {
//                val keyType = this.type.keyType
//                val valueType = this.type.valueType
//
//
//            }
    else -> this.type.nullableTypeName
}