package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.FileSpec
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.builders.DataTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.RefTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.ShellTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.SpecTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.builder.BuilderTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder
import io.philarios.schema.v0.translators.codegen.builders.builder.ParameterFunctionResolver
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addTypes

object SchemaFileBuilder {

    fun build(schema: Schema): List<FileSpec> {
        val schemaWithPkg = schema.propagatePkg()
        val typeRefs = schemaWithPkg.buildTypeRefs()

        return listOf(
                buildBuilders(schemaWithPkg, typeRefs),
                buildShellBuilders(schemaWithPkg, typeRefs),
                buildModel(schemaWithPkg),
                buildShells(schemaWithPkg, typeRefs),
                buildRefs(schemaWithPkg),
                buildSpecs(schemaWithPkg)
        )
    }

    private fun buildBuilders(schema: Schema, typeRefs: Map<RefType, Type>): FileSpec {
        val builderTypeBuilder = BuilderTypeBuilder(
                ParameterFunctionResolver(typeRefs),
                InterfaceStructWithParametersBuilderTypeBuilder
        )
        return FileSpec.builder(schema.pkg, "${schema.name}Builders")
                .addTypes(schema.types.flatMap { builderTypeBuilder.build(it) })
                .build()
    }

    private fun buildShellBuilders(schema: Schema, typeRefs: Map<RefType, Type>): FileSpec {
        val builderTypeBuilder = BuilderTypeBuilder(
                ParameterFunctionResolver(typeRefs),
                ShellStructWithParametersBuilderTypeBuilder
        )
        return FileSpec.builder(schema.pkg, "${schema.name}ShellBuilders")
                .addTypes(schema.types.flatMap { builderTypeBuilder.build(it) })
                .build()
    }

    private fun buildModel(schema: Schema): FileSpec {
        return FileSpec.builder(schema.pkg, "${schema.name}Model")
                .addTypes(schema.types.flatMap { DataTypeBuilder.build(it) })
                .build()
    }

    private fun buildShells(schema: Schema, typeRefs: Map<RefType, Type>): FileSpec {
        return FileSpec.builder(schema.pkg, "${schema.name}Shells")
                .addStaticImport("kotlinx.coroutines.experimental", "coroutineScope", "launch")
                .addTypes(schema.types.flatMap { ShellTypeBuilder(typeRefs).build(it) })
                .build()
    }

    private fun buildRefs(schema: Schema): FileSpec {
        return FileSpec.builder(schema.pkg, "${schema.name}Refs")
                .addTypes(schema.types.flatMap { RefTypeBuilder.build(it) })
                .build()
    }

    private fun buildSpecs(schema: Schema): FileSpec {
        return FileSpec.builder(schema.pkg, "${schema.name}Specs")
                .addTypes(schema.types.flatMap { SpecTypeBuilder.build(it) })
                .build()
    }

    // TODO decide on how hacky this is?
    private fun Schema.propagatePkg(): Schema {
        return copy(
                types = types.map { it.propagatePkg(pkg) },
                references = references.map { it.propagatePkg() }
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
                .let {
                    references.fold(it) { typeRefs, referencedSchema ->
                        typeRefs + referencedSchema.buildTypeRefs()
                    }
                }
    }

    private fun Type.buildTypeRefs(): List<Pair<RefType, Type>> {
        return when (this) {
            is Struct -> listOf(Pair(RefType(pkg, name), this))
            is Union -> shapes.flatMap { it.buildTypeRefs() } + Pair(RefType(pkg, name), this)
            is EnumType -> listOf(Pair(RefType(pkg, name), this))
            else -> emptyList()
        }
    }

}