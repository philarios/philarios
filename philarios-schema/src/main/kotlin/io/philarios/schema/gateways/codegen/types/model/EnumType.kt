package io.philarios.schema.gateways.codegen.types.model

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.EnumType
import io.philarios.schema.gateways.codegen.util.escaped

internal val EnumType.modelTypeSpec
    get() = TypeSpec.enumBuilder(name)
        .let { values.fold(it) { builder, value -> builder.addEnumConstant(value.escaped) } }
        .build()