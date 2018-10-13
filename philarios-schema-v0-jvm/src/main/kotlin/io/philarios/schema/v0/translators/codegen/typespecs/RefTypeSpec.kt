package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.philarios.core.v0.RegistryRef
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.className
import io.philarios.schema.v0.translators.codegen.refClassName
import io.philarios.schema.v0.translators.codegen.scaffoldClassName

object RefTypeSpec {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructRefTypeSpec.build(type)
            is Union -> UnionRefTypeSpec.build(type)
            else -> emptyList()
        }
    }

}

object StructRefTypeSpec {

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type))
    }

    private fun buildOne(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.refClassName)
                .addSuperinterface(
                        type.scaffoldClassName,
                        CodeBlock.of(
                                "%T(%T::class, %L)",
                                RegistryRef::class.className,
                                type.className,
                                "key"
                        )
                )
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter("key", String::class)
                        .build())
                .build()
    }

}

private object UnionRefTypeSpec {

    fun build(type: Union): List<TypeSpec> {
        return buildShapes(type)
    }

    private fun buildShapes(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructRefTypeSpec.build(it) }
    }

}