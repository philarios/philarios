package io.philarios.schema.translators.codegen.types.refs

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.philarios.core.RegistryRef
import io.philarios.schema.Struct
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.refClassName
import io.philarios.schema.translators.codegen.util.scaffoldTypeName

internal val Struct.refTypeSpec
    get() = when {
        fields.isEmpty() -> null
        else -> dataClassRefTypeSpec
    }

private val Struct.dataClassRefTypeSpec
    get() = TypeSpec.classBuilder(refClassName)
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