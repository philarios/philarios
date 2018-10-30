package io.philarios.schema.v0.translators.codegen.builders

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.EnumType
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.Union
import io.philarios.schema.v0.translators.codegen.className
import io.philarios.schema.v0.translators.codegen.typeName

object DataTypeBuilder {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructDataTypeBuilder.build(type)
            is Union -> UnionDataTypeBuilder.build(type)
            is EnumType -> EnumTypeDataTypeBuilder.build(type)
            else -> emptyList()
        }
    }

}

private object StructDataTypeBuilder {

    fun build(type: Struct, superclass: ClassName? = null): List<TypeSpec> {
        return listOf(buildOne(type, superclass))
    }

    private fun buildOne(type: Struct, superclass: ClassName? = null): TypeSpec {
        if (type.fields.isEmpty()) {
            return buildObject(type, superclass)
        }
        return buildDataClass(type, superclass)
    }

    private fun buildObject(type: Struct, superclass: ClassName? = null): TypeSpec {
        return TypeSpec.objectBuilder(type.name)
                .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
                .build()
    }

    private fun buildDataClass(type: Struct, superclass: ClassName? = null): TypeSpec {
        return TypeSpec.classBuilder(type.name)
                .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
                .addModifiers(KModifier.DATA)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameters(
                                type.fields.map {
                                    ParameterSpec.builder(it.name, it.type.typeName)
                                            .build()
                                }
                        )
                        .build()
                )
                .addProperties(
                        type.fields.map {
                            PropertySpec.builder(it.name, it.type.typeName)
                                    .initializer(it.name)
                                    .build()
                        }
                )
                .build()
    }

}

private object UnionDataTypeBuilder {

    fun build(type: Union): List<TypeSpec> {
        return listOf(buildSuperclass(type)) + buildShapes(type)
    }

    private fun buildSuperclass(type: Union): TypeSpec {
        return TypeSpec.classBuilder(type.name)
                .addModifiers(KModifier.SEALED)
                .build()
    }

    private fun buildShapes(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructDataTypeBuilder.build(it, type.className) }
    }

}

private object EnumTypeDataTypeBuilder {

    fun build(type: EnumType): List<TypeSpec> {
        if (type.values.isEmpty()) {
            return emptyList()
        }
        return listOf(buildOne(type))
    }

    private fun buildOne(type: EnumType): TypeSpec {
        return TypeSpec.enumBuilder(type.name)
                .let { type.values.fold(it) { builder, value -> builder.addEnumConstant(value) } }
                .build()
    }

}