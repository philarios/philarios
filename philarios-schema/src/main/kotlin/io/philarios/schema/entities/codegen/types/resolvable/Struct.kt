package io.philarios.schema.entities.codegen.types.resolvable

import com.squareup.kotlinpoet.*
import io.philarios.schema.Field
import io.philarios.schema.RefType
import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.entities.codegen.util.escapedName
import io.philarios.schema.entities.codegen.util.scaffoldTypeName
import io.philarios.schema.entities.codegen.util.shellClassName
import io.philarios.util.kotlinpoet.superclass

internal fun Struct.shellTypeSpec(
        typeRefs: Map<RefType, Type>,
        superclass: ClassName? = null,
        shellSuperclass: ClassName? = null
) = when {
    fields.isEmpty() -> null
    else -> dataClassShellTypeSpec(typeRefs, superclass, shellSuperclass)
}

private fun Struct.dataClassShellTypeSpec(
        typeRefs: Map<RefType, Type>,
        superclass: ClassName?,
        shellSuperclass: ClassName?
) =
        TypeSpec.classBuilder(shellClassName)
                .addSuperinterface(scaffoldTypeName)
                .superclass(shellSuperclass)
                .addModifiers(KModifier.INTERNAL)
                .addModifiers(KModifier.DATA)
                .primaryConstructor(dataClassShellConstructorSpec(typeRefs))
                .addProperties(fields.map { field -> field.getFieldPropertySpec(typeRefs) })
                .addFunction(resolveFun(typeRefs, superclass))
                .build()

private fun Struct.dataClassShellConstructorSpec(typeRefs: Map<RefType, Type>) =
        FunSpec.constructorBuilder()
                .addParameters(fields.map { field -> field.fieldParameterSpec(typeRefs) })
                .build()

private fun Field.fieldParameterSpec(typeRefs: Map<RefType, Type>) =
        ParameterSpec.builder(escapedName, scaffoldTypeName(typeRefs))
                .defaultValue("null")
                .build()

private fun Field.getFieldPropertySpec(typeRefs: Map<RefType, Type>) =
        PropertySpec.builder(escapedName, scaffoldTypeName(typeRefs))
                .mutable(true)
                .initializer(escapedName)
                .build()

