package io.philarios.schema.translators.codegen.types.model

import com.squareup.kotlinpoet.*
import io.philarios.schema.Field
import io.philarios.schema.Struct
import io.philarios.schema.translators.codegen.util.escapedName
import io.philarios.schema.translators.codegen.util.typeName

internal val Struct.modelTypeSpec get() = modelTypeSpec()

internal fun Struct.modelTypeSpec(superclass: ClassName? = null) = when {
    fields.isEmpty() -> objectModelTypeSpec(superclass)
    else -> dataClassModelTypeSpec(superclass)
}

private fun Struct.objectModelTypeSpec(superclass: ClassName? = null) =
        TypeSpec.objectBuilder(name)
                .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
                .build()

private fun Struct.dataClassModelTypeSpec(superclass: ClassName? = null): TypeSpec {
    return TypeSpec.classBuilder(name)
            .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
            .addModifiers(KModifier.DATA)
            .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameters(fields.map { it.fieldParameterSpec })
                    .build()
            )
            .addProperties(fields.map { it.fieldPropertySpec })
            .build()
}

private val Field.fieldParameterSpec
    get() =
        ParameterSpec.builder(escapedName, type.typeName)
                .build()

private val Field.fieldPropertySpec
    get() =
        PropertySpec.builder(escapedName, type.typeName)
                .initializer(escapedName)
                .build()
