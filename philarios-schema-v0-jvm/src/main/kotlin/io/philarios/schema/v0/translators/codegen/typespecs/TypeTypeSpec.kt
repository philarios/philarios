package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.RefType
import io.philarios.schema.v0.Type


object TypeTypeSpec {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return DataTypeSpec.build(type) +
                ShellTypeSpec.build(type, typeRefs) +
                RefTypeSpec.build(type) +
                TemplateTypeSpec.build(type) +
                SpecTypeSpec.build(type) +
                BuilderTypeSpec.build(type, typeRefs) +
                TranslatorTypeSpec.build(type)
    }

}
