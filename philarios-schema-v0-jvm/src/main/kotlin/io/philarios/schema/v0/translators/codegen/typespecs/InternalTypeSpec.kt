package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.RefType
import io.philarios.schema.v0.Type


object InternalTypeSpec {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return ShellTypeSpec.build(type, typeRefs) +
                RefTypeSpec.build(type) +
                SpecTypeSpec.build(type)
    }

}
