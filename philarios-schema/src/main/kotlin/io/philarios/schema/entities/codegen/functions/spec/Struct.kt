package io.philarios.schema.entities.codegen.functions.spec

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import io.philarios.schema.Struct
import io.philarios.schema.entities.codegen.util.builderClassName
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.specClassName

internal val Struct.specFunSpec
    get() =
        when {
            fields.isEmpty() -> objectSpecFunSpec
            else -> dataClassSpecFunSpec
        }

internal val Struct.objectSpecFunSpec: FunSpec
    get() = FunSpec.builder(className.simpleName().decapitalize())
            .returns(specClassName)
            .addStatement("return %T", specClassName)
            .build()

internal val Struct.dataClassSpecFunSpec: FunSpec
    get() = FunSpec.builder(className.simpleName().decapitalize())
            .addParameter(parameterSpec)
            .returns(specClassName)
            .addStatement("return %T(%L)", specClassName, "body")
            .build()

private val Struct.parameterSpec: ParameterSpec
    get() = ParameterSpec
            .builder("body", lambdaTypeName)
            .build()

private val Struct.lambdaTypeName
    get() = LambdaTypeName
            .get(builderClassName, emptyList(), ClassName("", "Unit"))
