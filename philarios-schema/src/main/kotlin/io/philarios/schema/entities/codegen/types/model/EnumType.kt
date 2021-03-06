package io.philarios.schema.entities.codegen.types.model

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.EnumType
import io.philarios.schema.entities.codegen.util.escaped

internal val EnumType.modelTypeSpec
    get() = TypeSpec.enumBuilder(name)
        .let { values.fold(it) { builder, value -> builder.addEnumConstant(value.escaped) } }
        .build()