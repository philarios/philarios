package io.philarios.schema.entities.codegen.functions.spec

import com.squareup.kotlinpoet.*
import io.philarios.schema.Struct
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.specClassName

internal val Struct.specFunSpec
    get() =
        when {
            fields.isEmpty() -> objectSpecFunSpec
            else -> dataClassSpecFunSpec
        }

internal val Struct.objectSpecFunSpec: FunSpec
    get() = FunSpec.builder(specTypeName.rawType.simpleName())
            .returns(specTypeName)
            .addStatement("return %T()", specTypeName)
            .build()

internal val Struct.dataClassSpecFunSpec: FunSpec
    get() = FunSpec.builder(specTypeName.rawType.simpleName())
            .addParameter(parameterSpec)
            .returns(specTypeName)
            .addStatement("return %T(%L)", specTypeName, "body")
            .build()

val Struct.specTypeName
    get() = ParameterizedTypeName.get(specClassName, TypeVariableName("Any?"))

private val Struct.parameterSpec: ParameterSpec
    get() = ParameterSpec
            .builder("body", lambdaTypeName)
            .build()

private val Struct.lambdaTypeName
    get() = LambdaTypeName
            .get(builderTypeName, emptyList(), ClassName("", "Unit"))

private val Struct.builderTypeName
    get() = ParameterizedTypeName.get(className("Builder"), TypeVariableName("Any?"))