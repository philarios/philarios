package io.philarios.schema.gateways.codegen.types.refs

import com.squareup.kotlinpoet.*
import io.philarios.core.RegistryRef
import io.philarios.schema.Struct
import io.philarios.schema.gateways.codegen.util.className
import io.philarios.schema.gateways.codegen.util.refClassName
import io.philarios.schema.gateways.codegen.util.runIfNotNull
import io.philarios.schema.gateways.codegen.util.scaffoldTypeName

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
                .addSuperinterface(
                        scaffoldTypeName,
                        CodeBlock.of(
                                "%T(%T::class, %L)",
                                RegistryRef::class.className,
                                className,
                                "key"
                        )
                )
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter("key", String::class)
                        .build())
                .build()
