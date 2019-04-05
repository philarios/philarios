package io.philarios.schema.entities.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.*
import io.philarios.schema.entities.codegen.types.builder.builderInterfaceTypeSpec
import io.philarios.schema.entities.codegen.types.builder.builderShellTypeSpec
import io.philarios.schema.entities.codegen.types.builder.createTypeBuilderTypeSpec
import io.philarios.schema.entities.codegen.types.model.modelTypeSpecs
import io.philarios.schema.entities.codegen.types.refs.refTypeSpecs
import io.philarios.schema.entities.codegen.types.scaffolder.scaffolderTypeSpecs
import io.philarios.schema.entities.codegen.types.shell.shellTypeSpecs
import io.philarios.schema.entities.codegen.types.spec.specTypeSpecs
import io.philarios.util.kotlinpoet.addTypes

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

private fun Schema.propagatePkg(): Schema {
    return copy(types = types.map { it.propagatePkg(pkg) })
}

private fun <T : Type> T.propagatePkg(pkg: String): T {
    return when (this) {
        is Struct -> copy(pkg = this.pkg?: pkg, fields = fields.map { it.propagatePkg(pkg) })
        is Union -> copy(pkg = this.pkg ?: pkg, shapes = shapes.map { it.propagatePkg(pkg) })
        is EnumType -> copy(pkg = this.pkg ?: pkg)
        is RefType -> copy(pkg = this.pkg ?: pkg)
        is OptionType -> copy(type = type.propagatePkg(pkg))
        is ListType -> copy(type = type.propagatePkg(pkg))
        is MapType -> copy(keyType = keyType.propagatePkg(pkg), valueType = valueType.propagatePkg(pkg))
        else -> this
    } as T
}

private fun Field.propagatePkg(pkg: String): Field = copy(type = type.propagatePkg(pkg))

private fun Schema.buildTypeRefs(): Map<RefType, Type> {
    return types
            .flatMap { it.buildTypeRefs() }
            .toMap()
}

private fun Type.buildTypeRefs(): List<Pair<RefType, Type>> {
    return when (this) {
        is Struct -> listOf(Pair(RefType(pkg, name), this))
        is Union -> shapes.flatMap { it.buildTypeRefs() } + Pair(RefType(pkg, name), this)
        is EnumType -> listOf(Pair(RefType(pkg, name), this))
        else -> emptyList()
    }
}
