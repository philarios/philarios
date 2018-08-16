package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addTypes
import io.philarios.schema.v0.*

object SchemaFileSpec {

    fun build(schema: Schema): FileSpec {
        val typeRefs = buildTypeRefs(schema)
        return FileSpec.builder(schema.pkg, "${schema.name}Language")
                .addTypes(schema.types.flatMap { TypeTypeSpec.build(it, typeRefs) })
                .build()
    }

    private fun buildTypeRefs(schema: Schema): Map<RefType, Type> {
        return schema.types
                .flatMap { buildTypeRefs(it) }
                .toMap()
    }

    private fun buildTypeRefs(type: Type): List<Pair<RefType, Type>> {
        return when (type) {
            is Struct -> listOf(Pair(RefType(type.name), type))
            is Union -> type.shapes.flatMap { buildTypeRefs(it) } + Pair(RefType(type.name), type)
            is EnumType -> listOf(Pair(RefType(type.name), type))
            else -> emptyList()
        }
    }

}