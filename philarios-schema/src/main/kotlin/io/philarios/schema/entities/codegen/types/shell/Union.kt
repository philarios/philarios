package io.philarios.schema.entities.codegen.types.shell

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.RefType
import io.philarios.schema.Type
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.shellClassName

internal fun Union.shellTypeSpecs(typeRefs: Map<RefType, Type>) =
        listOf(superclassShellTypeSpec) + shapeShellTypeSpecs(typeRefs)

private val Union.superclassShellTypeSpec
    get() =
        TypeSpec.classBuilder(shellClassName)
                .addModifiers(KModifier.INTERNAL)
                .addModifiers(KModifier.SEALED)
                .build()

private fun Union.shapeShellTypeSpecs(typeRefs: Map<RefType, Type>) =
        shapes.map { it.shellTypeSpec(typeRefs, this.shellClassName) }
