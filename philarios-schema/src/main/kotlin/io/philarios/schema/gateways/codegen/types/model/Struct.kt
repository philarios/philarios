package io.philarios.schema.gateways.codegen.types.model

import com.squareup.kotlinpoet.*
import io.philarios.schema.Field
import io.philarios.schema.Struct
import io.philarios.schema.gateways.codegen.util.escapedName
import io.philarios.schema.gateways.codegen.util.runIfNotNull
import io.philarios.schema.gateways.codegen.util.typeName

internal val Struct.modelTypeSpec get() = modelTypeSpec()

internal fun Struct.modelTypeSpec(superclass: ClassName? = null) = when {
    fields.isEmpty() -> objectModelTypeSpec(superclass)
    else -> dataClassModelTypeSpec(superclass)
}

private fun Struct.objectModelTypeSpec(superclass: ClassName?) =
        TypeSpec.objectBuilder(name)
                .runIfNotNull(superclass) { superclass(it) }
                .build()

private fun Struct.dataClassModelTypeSpec(superclass: ClassName?): TypeSpec {
    return TypeSpec.classBuilder(name)
            .runIfNotNull(superclass) { superclass(it) }
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
