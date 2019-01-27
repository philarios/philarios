package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.*
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.escapedName
import io.philarios.schema.translators.codegen.util.scaffoldTypeName
import io.philarios.schema.translators.codegen.util.shellClassName
import io.philarios.schema.util.kotlinpoet.superclass

internal fun Struct.shellTypeSpec(typeRefs: Map<RefType, Type>, superclass: ClassName? = null) = when {
    fields.isEmpty() -> null
    else -> dataClassShellTypeSpec(typeRefs, superclass)
}

private fun Struct.dataClassShellTypeSpec(typeRefs: Map<RefType, Type>, superclass: ClassName? = null) =
        TypeSpec.classBuilder(shellClassName)
                .addSuperinterface(scaffoldTypeName)
                .superclass(superclass)
                .addModifiers(KModifier.INTERNAL)
                .addModifiers(KModifier.DATA)
                .primaryConstructor(dataClassShellConstructorSpec(typeRefs))
                .addProperties(fields.map { field -> field.getFieldPropertySpec(typeRefs) })
                .addFunction(resolveFun(typeRefs))
                .build()

private fun Struct.dataClassShellConstructorSpec(typeRefs: Map<RefType, Type>) =
        FunSpec.constructorBuilder()
                .addParameters(fields.map { field -> field.fieldParameterSpec(typeRefs) })
                .build()

private fun Field.fieldParameterSpec(typeRefs: Map<RefType, Type>) =
        ParameterSpec.builder(escapedName, scaffoldTypeName(typeRefs))
                .defaultValue(when (type) {
                    is ListType -> "emptyList()"
                    is MapType -> "emptyMap()"
                    else -> "null"
                })
                .build()

private fun Field.getFieldPropertySpec(typeRefs: Map<RefType, Type>) =
        PropertySpec.builder(escapedName, scaffoldTypeName(typeRefs))
                .mutable(true)
                .initializer(escapedName)
                .build()

