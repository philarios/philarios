package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.TypeName
import io.philarios.schema.Field
import io.philarios.schema.RefType
import io.philarios.schema.Type
import io.philarios.schema.translators.codegen.util.scaffoldTypeName

fun Field.scaffoldTypeName(typeRefs: Map<RefType, Type>): TypeName =
        copy(type = type.resolveRefs(typeRefs))
                .scaffoldTypeName

private val Field.scaffoldTypeName: TypeName
    get() = type.scaffoldTypeName.asNullable()
