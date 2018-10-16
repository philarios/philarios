package io.philarios.schema.v0.translators.codegen.builders.builder

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.v0.Struct

interface StructWithParametersBuilderTypeBuilder {
    fun buildOneWithParameterFunctions(type: Struct, parameterFunctions: List<ParameterFunction>): TypeSpec
}