package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.RefType
import io.philarios.schema.v0.Type


object TypeTypeSpec {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return DataTypeSpec.build(type) +
                BuilderTypeSpec.build(type, typeRefs) +
                SpecTypeSpec.build(type) +
                TranslatorTypeSpec.build(type)
    }

}
