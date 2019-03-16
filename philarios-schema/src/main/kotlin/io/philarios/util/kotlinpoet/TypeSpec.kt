package io.philarios.util.kotlinpoet

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.superclass(superclass: TypeName?) =
        superclass?.let { superclass(it) } ?: this