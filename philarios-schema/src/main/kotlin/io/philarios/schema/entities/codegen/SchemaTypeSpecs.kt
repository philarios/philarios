package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.RefType
import io.philarios.schema.Schema
import io.philarios.schema.Type
import io.philarios.schema.entities.codegen.functions.spec.specFunSpecs
import io.philarios.schema.entities.codegen.types.builder.builderInterfaceTypeSpecs
import io.philarios.schema.entities.codegen.types.builder.builderScaffoldTypeSpecs
import io.philarios.schema.entities.codegen.types.model.modelTypeSpecs
import io.philarios.schema.entities.codegen.types.refs.refTypeSpecs
import io.philarios.schema.entities.codegen.types.scaffold.shellTypeSpecs
import io.philarios.schema.entities.codegen.types.scaffolder.scaffolderTypeSpecs
import io.philarios.schema.entities.codegen.types.spec.specTypeSpecs
import io.philarios.schema.entities.codegen.util.className

internal data class Classifier(
        val type: String,
        val kind: Kind
)

internal enum class Kind(val fileName: String, val layer: Layer) {
    MODEL("Models", Layer.MODEL),
    SPEC("Specs", Layer.SPEC),
    BUILDER("Builders", Layer.SPEC),
    REF("Refs", Layer.SPEC),
    SCAFFOLD("Scaffolds", Layer.SCAFFOLD),
    SCAFFOLD_BUILDER("ScaffoldBuilders", Layer.SCAFFOLD),
    SCAFFOLDER("Scaffolders", Layer.SCAFFOLD)
}

internal enum class Layer(val fileName: String) {
    MODEL("Model"),
    SPEC("Spec"),
    SCAFFOLD("Scaffold")
}

internal fun Schema.codeSpecsByClassifier(): Map<Classifier, CodeSpecs> {
    val schemaWithPkg = propagatePkg()
    val typeRefs = schemaWithPkg.buildTypeRefs()
    return schemaWithPkg.codeSpecsByClassifier(typeRefs)
}

internal fun Schema.codeSpecsByClassifier(typeRefs: Map<RefType, Type>): Map<Classifier, CodeSpecs> {
    return types.flatMap { type ->
        val typeName = type.className.simpleName()
        type.codeSpecsByKind(typeRefs)
                .map { Pair(Classifier(typeName, it.key), it.value) }
    }.toMap()
}

internal fun Type.codeSpecsByKind(typeRefs: Map<RefType, Type>): Map<Kind, CodeSpecs> {
    return mapOf(
            Kind.MODEL to CodeSpecs(modelTypeSpecs),
            Kind.SPEC to CodeSpecs(specTypeSpecs, specFunSpecs),
            Kind.BUILDER to CodeSpecs(builderInterfaceTypeSpecs(typeRefs)),
            Kind.REF to CodeSpecs(refTypeSpecs),
            Kind.SCAFFOLDER to CodeSpecs(scaffolderTypeSpecs),
            Kind.SCAFFOLD_BUILDER to CodeSpecs(builderScaffoldTypeSpecs(typeRefs)),
            Kind.SCAFFOLD to CodeSpecs(shellTypeSpecs(typeRefs))
    )
}

data class CodeSpecs(
        val typeSpecs: List<TypeSpec> = emptyList(),
        val funSpecs: List<FunSpec> = emptyList()
)

operator fun CodeSpecs.plus(other: CodeSpecs) = CodeSpecs(
        typeSpecs = typeSpecs + other.typeSpecs,
        funSpecs = funSpecs + other.funSpecs
)