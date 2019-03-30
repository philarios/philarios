package io.philarios.schema.entities.codegen.types.model

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.className

internal val Union.modelTypeSpecs get() =
        listOf(superclassModelTypeSpec) + shapeModelTypeSpecs

private val Union.superclassModelTypeSpec get() =
        TypeSpec.classBuilder(name)
                .addModifiers(KModifier.SEALED)
                .build()

private val Union.shapeModelTypeSpecs get() =
    shapes.map { it.modelTypeSpec(this.className) }
