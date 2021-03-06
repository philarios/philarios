package io.philarios.schema.entities.codegen.types.scaffolder

import com.squareup.kotlinpoet.*
import io.philarios.core.Scaffolder
import io.philarios.core.Scaffold
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.*
import io.philarios.util.kotlinpoet.Statement
import io.philarios.util.kotlinpoet.addStatements

internal val Union.scaffolderTypeSpecs
    get() =
        listOf(superclassScaffolderTypeSpec) + shapeScaffolderTypeSpecs

private val Union.superclassScaffolderTypeSpec
    get() =
        TypeSpec.classBuilder(scaffolderClassName)
                .addTypeVariable(TypeVariableName("T", KModifier.OUT).withBounds(className))
                .addSuperinterface(ParameterizedTypeName.get(
                        Scaffolder::class.className,
                        TypeVariableName("T")
                ))
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
                        .returns(ParameterizedTypeName.get(Scaffold::class.className, TypeVariableName("T")))
                        .addStatements(
                                listOf(
                                        listOf(Statement("val result = when (spec) {")),
                                        shapes.map {
                                            Statement(
                                                    "%>is %T -> %T(spec).createScaffold()%<",
                                                    listOf(it.specTypeName, it.scaffolderClassName)
                                            )
                                        },
                                        listOf(Statement("}")),
                                        listOf(Statement("return result as Scaffold<T>"))
                                ).flatten()
                        )
                        .build())
                .build()

internal val Union.shapeScaffolderTypeSpecs
    get() =
        shapes.map { it.scaffolderTypeSpec }
