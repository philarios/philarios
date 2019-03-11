package io.philarios.schema.translators.codegen.types.spec

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.philarios.schema.Union
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.specTypeName

internal val Union.specTypeSpecs
    get() =
        listOf(superclassSpecTypeSpec) + shapeSpecTypeSpecs

private val Union.superclassSpecTypeSpec
    get() =
        TypeSpec.classBuilder(specTypeName.rawType)
                .addModifiers(KModifier.SEALED)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .addTypeVariable(TypeVariableName("T", KModifier.OUT).withBounds(className))
                .build()

internal val Union.shapeSpecTypeSpecs
    get() =
        shapes.map { it.specTypeSpec(specTypeName.rawType) }
