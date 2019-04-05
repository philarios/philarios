package io.philarios.schema.entities.codegen.types.refs

import com.squareup.kotlinpoet.*
import io.philarios.core.Scaffold
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.refClassName

internal val Union.refTypeSpecs
    get() =
        listOf(superclassRefTypeSpec) + shapeRefTypeSpecs

private val Union.superclassRefTypeSpec
    get() =
        TypeSpec.classBuilder(refClassName)
                .addModifiers(KModifier.SEALED)
                .addTypeVariable(TypeVariableName("T", className))
                .addProperty(PropertySpec.builder("key", String::class)
                        .addModifiers(KModifier.INTERNAL, KModifier.ABSTRACT)
                        .build())
                .build()

private val Union.shapeRefTypeSpecs
    get() =
        shapes.mapNotNull { it.refTypeSpec(refClassName) }
