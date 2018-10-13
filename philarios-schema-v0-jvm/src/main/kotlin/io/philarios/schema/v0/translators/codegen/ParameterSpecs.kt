package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeVariableName
import io.philarios.schema.v0.Type

val contextParameterSpec
    get() = ParameterSpec
            .builder("context", TypeVariableName("C"))
            .build()

val otherContextParameterSpec
    get() = ParameterSpec
            .builder("context", TypeVariableName("C2"))
            .build()

val otherContextIterableParameterSpec
    get() = ParameterSpec
            .builder("context", ParameterizedTypeName.get(Iterable::class.className, TypeVariableName("C2")))
            .build()

val Type.refParameterSpec
    get() = ParameterSpec
            .builder("ref", refClassName)
            .build()

val Type.specParameterSpec
    get() = ParameterSpec
            .builder("spec", specClassName)
            .build()

val Type.otherSpecParameterSpec
    get() = ParameterSpec
            .builder("spec", otherSpecClassName)
            .build()

val Type.bodyParameterSpec
    get() = ParameterSpec
            .builder("body", bodyLambdaTypeName)
            .build()

val Type.otherBodyParameterSpec
    get() = ParameterSpec
            .builder("body", otherBodyLambdaTypeName)
            .build()
