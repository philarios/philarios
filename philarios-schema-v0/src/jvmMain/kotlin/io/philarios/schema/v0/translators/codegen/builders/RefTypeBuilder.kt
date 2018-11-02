package io.philarios.schema.v0.translators.codegen.builders

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

object RefTypeBuilder {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructRefTypeBuilder.build(type)
            is Union -> UnionRefTypeBuilder.build(type)
            else -> emptyList()
        }
    }

}

private object StructRefTypeBuilder {

    fun build(type: Struct): List<TypeSpec> {
        return listOf(buildOne(type)).mapNotNull { it }
    }

    private fun buildOne(type: Struct): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildDataClass(type)
    }

    private fun buildDataClass(type: Struct): TypeSpec {
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

private object UnionRefTypeBuilder {

    fun build(type: Union): List<TypeSpec> {
        return buildShapes(type)
    }

    private fun buildShapes(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructRefTypeBuilder.build(it) }
    }

}