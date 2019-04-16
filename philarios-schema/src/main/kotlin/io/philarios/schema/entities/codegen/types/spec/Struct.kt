package io.philarios.schema.entities.codegen.types.spec

import com.squareup.kotlinpoet.*
import io.philarios.core.Spec
import io.philarios.schema.Struct
import io.philarios.schema.entities.codegen.util.*

internal val Struct.specTypeSpec get() = specTypeSpec()

internal fun Struct.specTypeSpec(superclass: ClassName? = null) =
        when {
            fields.isEmpty() -> objectSpecTypeSpec(superclass)
            else -> dataClassSpecTypeSpec(superclass)
        }

private fun Struct.objectSpecTypeSpec(superclass: ClassName?) =
        TypeSpec.classBuilder(specTypeName.rawType)
                .addTypeVariable(TypeVariableName("C"))
                .runIfNotNull(superclass) {
                    superclass(ParameterizedTypeName.get(it, TypeVariableName("C"), className))
                }
                .build()

private fun Struct.dataClassSpecTypeSpec(superclass: ClassName?) =
        TypeSpec.classBuilder(specTypeName.rawType)
                .addTypeVariable(TypeVariableName("C"))
                .runIfNotNull(superclass) {
                    superclass(ParameterizedTypeName.get(it, TypeVariableName("C"), className))
                }
                .addSuperinterface(ParameterizedTypeName.get(Spec::class.className, builderTypeName))
                .addProperty(PropertySpec
                        .builder("body", bodyLambdaTypeName)
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("body")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(bodyParameterSpec)
                        .build())
                .build()
