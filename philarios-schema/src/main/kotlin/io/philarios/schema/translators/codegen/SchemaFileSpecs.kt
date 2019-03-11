package io.philarios.schema.translators.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.types.builder.builderInterfaceTypeSpec
import io.philarios.schema.translators.codegen.types.builder.builderShellTypeSpec
import io.philarios.schema.translators.codegen.types.builder.createTypeBuilderTypeSpec
import io.philarios.schema.translators.codegen.types.model.modelTypeSpecs
import io.philarios.schema.translators.codegen.types.refs.refTypeSpecs
import io.philarios.schema.translators.codegen.types.scaffolder.scaffolderTypeSpecs
import io.philarios.schema.translators.codegen.types.shell.shellTypeSpecs
import io.philarios.schema.translators.codegen.types.spec.specTypeSpecs
import io.philarios.schema.util.kotlinpoet.addTypes

internal val Schema.fileSpecs: List<FileSpec>
    get() {
        val schemaWithPkg = propagatePkg()
        val typeRefs = schemaWithPkg.buildTypeRefs()

        return listOf(
                schemaWithPkg.modelFileSpec,
                schemaWithPkg.refFileSpec,
                schemaWithPkg.shellFileSpec(typeRefs),
                schemaWithPkg.specFileSpec,
                schemaWithPkg.builderInterfaceFileSpec(typeRefs),
                schemaWithPkg.builderShellFileSpec(typeRefs),
                schemaWithPkg.scaffolderFileSpec
        )
    }

private val Schema.modelFileSpec
    get() =
        FileSpec.builder(pkg, "${name}Model")
                .addTypes(types.flatMap { it.modelTypeSpecs })
                .build()

private val Schema.refFileSpec
    get() =
        FileSpec.builder(pkg, "${name}Refs")
                .addTypes(types.flatMap { it.refTypeSpecs })
                .build()

private val Schema.specFileSpec
    get() =
        FileSpec.builder(pkg, "${name}Specs")
                .addTypes(types.flatMap { it.specTypeSpecs })
                .build()

private fun Schema.shellFileSpec(typeRefs: Map<RefType, Type>) =
        FileSpec.builder(pkg, "${name}Shells")
                .addStaticImport("kotlinx.coroutines", "coroutineScope", "launch")
                .addTypes(types.flatMap { it.shellTypeSpecs(typeRefs) })
                .build()

private fun Schema.builderInterfaceFileSpec(typeRefs: Map<RefType, Type>): FileSpec {
    val builderTypeSpecs = createTypeBuilderTypeSpec(Struct::builderInterfaceTypeSpec)
    return FileSpec.builder(pkg, "${name}Builders")
            .addTypes(types.flatMap { it.builderTypeSpecs(typeRefs) })
            .build()
}

private fun Schema.builderShellFileSpec(typeRefs: Map<RefType, Type>): FileSpec {
    val builderTypeSpecs = createTypeBuilderTypeSpec(Struct::builderShellTypeSpec)
    return FileSpec.builder(pkg, "${name}ShellBuilders")
            .addTypes(types.flatMap { it.builderTypeSpecs(typeRefs) })
            .build()
}

private val Schema.scaffolderFileSpec
    get() =
        FileSpec.builder(pkg, "${name}Scaffolders")
                .addTypes(types.flatMap { it.scaffolderTypeSpecs })
                .build()

// TODO decide on how hacky this is?
private fun Schema.propagatePkg(): Schema {
    return copy(
            types = types.map { it.propagatePkg(pkg) }//,
//            references = references.map { it.propagatePkg() }
    )
}

private fun <T : Type> T.propagatePkg(pkg: String): T {
    return when (this) {
        is Struct -> this.copy(pkg = this.pkg
                ?: pkg, fields = fields.map { it.copy(type = it.type.propagatePkg(pkg)) }) as T
        is Union -> this.copy(pkg = this.pkg ?: pkg, shapes = shapes.map { it.propagatePkg(pkg) }) as T
        is EnumType -> this.copy(pkg = this.pkg ?: pkg) as T
        is RefType -> this.copy(pkg = this.pkg ?: pkg) as T
        is OptionType -> this.copy(type = type.propagatePkg(pkg)) as T
        is ListType -> this.copy(type = type.propagatePkg(pkg)) as T
        is MapType -> this.copy(keyType = keyType.propagatePkg(pkg), valueType = valueType.propagatePkg(pkg)) as T
        else -> this
    }
}

private fun Schema.buildTypeRefs(): Map<RefType, Type> {
    return types
            .flatMap { it.buildTypeRefs() }
            .toMap()
//            .let {
//                references.fold(it) { typeRefs, referencedSchema ->
//                    typeRefs + referencedSchema.buildTypeRefs()
//                }
//            }
}

private fun Type.buildTypeRefs(): List<Pair<RefType, Type>> {
    return when (this) {
        is Struct -> listOf(Pair(RefType(pkg, name), this))
        is Union -> shapes.flatMap { it.buildTypeRefs() } + Pair(RefType(pkg, name), this)
        is EnumType -> listOf(Pair(RefType(pkg, name), this))
        else -> emptyList()
    }
}
