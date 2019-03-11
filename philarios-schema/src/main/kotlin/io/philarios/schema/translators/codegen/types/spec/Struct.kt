package io.philarios.schema.translators.codegen.types.spec

import com.squareup.kotlinpoet.*
import io.philarios.schema.Struct
import io.philarios.schema.translators.codegen.util.*

internal val Struct.specTypeSpec get() = specTypeSpec()

internal fun Struct.specTypeSpec(superclass: ClassName? = null) =
        when {
            fields.isEmpty() -> objectSpecTypeSpec(superclass)
            else -> dataClassSpecTypeSpec(superclass)
        }

private fun Struct.objectSpecTypeSpec(superclass: ClassName?) =
        TypeSpec.classBuilder(specTypeName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .runIfNotNull(superclass) {
                    superclass(ParameterizedTypeName.get(it, TypeVariableName("C"), className))
                }
                .build()

private fun Struct.dataClassSpecTypeSpec(superclass: ClassName?) =
        TypeSpec.classBuilder(specTypeName.rawType)
                .addTypeVariable(TypeVariableName("C", KModifier.IN))
                .runIfNotNull(superclass) {
                    superclass(ParameterizedTypeName.get(it, TypeVariableName("C"), className))
                }
                .addProperty(PropertySpec
                        .builder("body", bodyLambdaTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("body")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(bodyParameterSpec)
                        .build())
                .build()
