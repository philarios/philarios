package io.philarios.schema.entities.codegen.types.scaffolder

import com.squareup.kotlinpoet.*
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper
import io.philarios.schema.Struct
import io.philarios.schema.entities.codegen.util.*
import io.philarios.util.kotlinpoet.Statement
import io.philarios.util.kotlinpoet.addStatements

internal val Struct.scaffolderTypeSpec
    get() = when {
        fields.isEmpty() -> objectScaffolderTypeSpec
        else -> dataClassScaffolderTypeSpec
    }

private val Struct.objectScaffolderTypeSpec
    get() =
        TypeSpec.classBuilder(scaffolderClassName)
                .addSuperinterface(ParameterizedTypeName.get(Scaffolder::class.className, className))
                .addProperty(PropertySpec
                        .builder("spec", specTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("spec")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(specParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("createScaffold")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(scaffoldTypeName)
                        .addStatements(
                                Statement("return %T(%T)", listOf(Wrapper::class.className, className))
                        )
                        .build())
                .build()

private val Struct.dataClassScaffolderTypeSpec
    get() =
        TypeSpec.classBuilder(scaffolderClassName)
                .addSuperinterface(ParameterizedTypeName.get(Scaffolder::class.className, className))
                .addProperty(PropertySpec
                        .builder("spec", specTypeName)
                        .addModifiers(KModifier.INTERNAL)
                        .initializer("spec")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(specParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("createScaffold")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(scaffoldTypeName)
                        .addStatements(
                                Statement("val builder = %T()", listOf(resolvableBuilderClassName)),
                                Statement("builder.apply(spec.body)"),
                                Statement("return builder.resolvable")
                        )
                        .build())
                .build()