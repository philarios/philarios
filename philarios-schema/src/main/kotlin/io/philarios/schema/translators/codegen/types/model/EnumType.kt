package io.philarios.schema.translators.codegen.types.model

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.EnumType

internal val EnumType.modelTypeSpec
    get() = TypeSpec.enumBuilder(name)
        .let { values.fold(it) { builder, value -> builder.addEnumConstant(value) } }
        .build()