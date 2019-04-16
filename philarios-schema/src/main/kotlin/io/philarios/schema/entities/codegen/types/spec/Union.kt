package io.philarios.schema.entities.codegen.types.spec

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.specClassName
import io.philarios.schema.entities.codegen.util.specTypeName

internal val Union.specTypeSpecs
    get() =
        listOf(superclassSpecTypeSpec) + shapeSpecTypeSpecs

private val Union.superclassSpecTypeSpec
    get() =
        TypeSpec.classBuilder(specClassName)
                .addModifiers(KModifier.SEALED)
                .addTypeVariable(TypeVariableName("T", KModifier.OUT).withBounds(className))
                .build()

internal val Union.shapeSpecTypeSpecs
    get() =
        shapes.map { it.specTypeSpec(specClassName) }
