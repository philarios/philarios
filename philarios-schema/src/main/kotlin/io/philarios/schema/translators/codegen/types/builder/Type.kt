package io.philarios.schema.translators.codegen.types.builder

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.RefType
import io.philarios.schema.Struct
import io.philarios.schema.Type
import io.philarios.schema.Union

typealias TypeBuilderTypeSpec = Type.(typeRefs: Map<RefType, Type>) -> List<TypeSpec>

fun createTypeBuilderTypeSpec(
        structParameterBuilderTypeSpec: StructParameterBuilderTypeSpec
): TypeBuilderTypeSpec {
    val structBuilderTypeSpec = createStructBuilderTypeSpec(structParameterBuilderTypeSpec)
    val unionBuilderTypeSpec = createUnionBuilderTypeSpec(structBuilderTypeSpec)

    return { typeRefs ->
        when (this) {
            is Union -> unionBuilderTypeSpec.invoke(this, typeRefs)
            is Struct -> listOf(structBuilderTypeSpec.invoke(this, typeRefs))
            else -> emptyList()
        }.mapNotNull { it }
    }
}
