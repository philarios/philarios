package io.philarios.codegen.v0.util.kotlinpoet

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

fun FileSpec.Builder.addTypes(typeSpecs: List<TypeSpec>): FileSpec.Builder {
    return typeSpecs.fold(this) { builder, typeSpec ->
        builder.addType(typeSpec)
    }
}