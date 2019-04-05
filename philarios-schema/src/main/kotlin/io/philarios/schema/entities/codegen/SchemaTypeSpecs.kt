package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.RefType
import io.philarios.schema.Schema
import io.philarios.schema.Type
import io.philarios.schema.entities.codegen.types.builder.builderInterfaceTypeSpecs
import io.philarios.schema.entities.codegen.types.builder.builderShellTypeSpecs
import io.philarios.schema.entities.codegen.types.model.modelTypeSpecs
import io.philarios.schema.entities.codegen.types.refs.refTypeSpecs
import io.philarios.schema.entities.codegen.types.scaffolder.scaffolderTypeSpecs
import io.philarios.schema.entities.codegen.types.shell.shellTypeSpecs
import io.philarios.schema.entities.codegen.types.spec.specTypeSpecs
import io.philarios.schema.entities.codegen.util.className

internal data class Classifier(
        val type: String,
        val kind: Kind
)

internal enum class Kind(val groupName: String) {
    MODEL("Models"),
    SPEC("Specs"),
    REF("Refs"),
    SHELL("Shells"),
    BUILDER("Builders"),
    BUILDER_SHELL("ShellBuilders"),
    SCAFFOLDER("Scaffolders")
}

internal fun Schema.typeSpecsByClassifer(): Map<Classifier, List<TypeSpec>> {
    val schemaWithPkg = propagatePkg()
    val typeRefs = schemaWithPkg.buildTypeRefs()
    return schemaWithPkg.typeSpecsByClassifer(typeRefs)
}

internal fun Schema.typeSpecsByClassifer(typeRefs: Map<RefType, Type>): Map<Classifier, List<TypeSpec>> {
    return types.flatMap { type ->
        val typeName = type.className.simpleName()
        type.typeSpecsByKind(typeRefs)
                .map { Pair(Classifier(typeName, it.key), it.value) }
    }.toMap()
}

internal fun Type.typeSpecsByKind(typeRefs: Map<RefType, Type>): Map<Kind, List<TypeSpec>> {
    return mapOf(
            Kind.MODEL to modelTypeSpecs,
            Kind.SPEC to specTypeSpecs,
            Kind.BUILDER to builderInterfaceTypeSpecs(typeRefs),
            Kind.REF to refTypeSpecs,
            Kind.SHELL to shellTypeSpecs(typeRefs),
            Kind.BUILDER_SHELL to builderShellTypeSpecs(typeRefs),
            Kind.SCAFFOLDER to scaffolderTypeSpecs
    )
}
