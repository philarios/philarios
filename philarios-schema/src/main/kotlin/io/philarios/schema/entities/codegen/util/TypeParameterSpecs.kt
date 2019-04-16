package io.philarios.schema.entities.codegen.util

import com.squareup.kotlinpoet.ParameterSpec
import io.philarios.schema.Type

val Type.wrapperParameterSpec
    get() = ParameterSpec
            .builder("value", wrapperTypeName)
            .build()

val Type.refParameterSpec
    get() = ParameterSpec
            .builder("ref", refTypeName)
            .build()

val Type.specParameterSpec
    get() = ParameterSpec
            .builder("spec", specTypeName)
            .build()

val Type.bodyParameterSpec
    get() = ParameterSpec
            .builder("body", bodyLambdaTypeName)
            .build()
