package io.philarios.schema.util.kotlinpoet

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

fun FileSpec.Builder.addTypes(typeSpecs: List<TypeSpec>): FileSpec.Builder {
    return typeSpecs.fold(this) { builder, typeSpec ->
        builder.addType(typeSpec)
    }
}

fun FileSpec.Builder.addFunctions(funSpecs: List<FunSpec>): FileSpec.Builder {
    return funSpecs.fold(this) { builder, funSpec ->
        builder.addFunction(funSpec)
    }
}