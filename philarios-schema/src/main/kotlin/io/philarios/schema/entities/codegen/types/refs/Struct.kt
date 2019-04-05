package io.philarios.schema.entities.codegen.types.refs

import com.squareup.kotlinpoet.*
import io.philarios.schema.Struct
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.refClassName
import io.philarios.schema.entities.codegen.util.runIfNotNull

internal val Struct.refTypeSpec get() = refTypeSpec()

internal fun Struct.refTypeSpec(superinterface: ClassName? = null) =
        when {
            fields.isEmpty() -> null
            else -> dataClassRefTypeSpec(superinterface)
        }

private fun Struct.dataClassRefTypeSpec(superinterface: ClassName?) =
        TypeSpec.classBuilder(refClassName)
                .runIfNotNull(superinterface) {
                    superclass(ParameterizedTypeName.get(it, className))
                }
                .addProperty(PropertySpec
                        .builder("key", String::class)
                        .run {
                            if (superinterface == null) {
                                addModifiers(KModifier.INTERNAL)
                            } else {
                                addModifiers(KModifier.OVERRIDE)
                            }
                        }
                        .initializer("key")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter("key", String::class)
                        .build())
                .build()
