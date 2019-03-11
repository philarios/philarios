package io.philarios.schema.translators.codegen.types.refs

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.philarios.core.Scaffold
import io.philarios.schema.Union
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.refClassName

internal val Union.refTypeSpecs
    get() =
        listOf(superclassRefTypeSpec) + shapeRefTypeSpecs

private val Union.superclassRefTypeSpec
    get() =
        TypeSpec.classBuilder(refClassName)
                .addModifiers(KModifier.SEALED)
                .addTypeVariable(TypeVariableName("T", className))
                .addSuperinterface(ParameterizedTypeName.get(Scaffold::class.className, TypeVariableName("T")))
                .build()

private val Union.shapeRefTypeSpecs
    get() =
        shapes.mapNotNull { it.refTypeSpec(refClassName) }
