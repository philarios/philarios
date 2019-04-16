package io.philarios.schema.entities.codegen.types.builder

import com.squareup.kotlinpoet.*
import io.philarios.core.Deferred
import io.philarios.core.DslBuilder
import io.philarios.core.Scaffold
import io.philarios.core.Wrapper
import io.philarios.schema.Struct
import io.philarios.schema.Union
import io.philarios.schema.entities.codegen.util.*

internal val builderShellTypeSpecs = createTypeBuilderTypeSpec(Struct::builderShellTypeSpec)

private fun Struct.builderShellTypeSpec(parameterFunctions: List<ParameterFunction>): TypeSpec {
    return TypeSpec.classBuilder(shellBuilderClassName)
            .addModifiers(KModifier.INTERNAL)
            .addSuperinterface(builderClassName)
            .addAnnotation(DslBuilder::class.className)
            .primaryConstructor(constructor(this))
            .addProperty(PropertySpec.builder("shell", shellClassName, KModifier.INTERNAL)
                    .initializer("shell")
                    .mutable(true)
                    .build())
            .addFunctions(parameterFunctions.map { it.parameterFunSpec })
            .build()
}

private fun constructor(type: Struct): FunSpec {
    return FunSpec.constructorBuilder()
            .addParameter(ParameterSpec.builder("shell", type.shellClassName)
                    .defaultValue("%T()", type.shellClassName)
                    .build())
            .build()
}

private val ParameterFunction.parameterFunSpec
    get() = when (this) {
        is ParameterFunction.SetParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithWrapper -> parameterFunSpec

        is ParameterFunction.AddParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.AddAllParameterFunctionWithWrapper -> parameterFunSpec

        is ParameterFunction.PutKeyValueParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.PutPairParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.PutAllParameterFunctionWithWrapper -> parameterFunSpec

        is ParameterFunction.AddPutKeyValueParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.AddPutKeyValueParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.AddPutKeyValueParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.AddPutKeyValueParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.AddPutPairParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.AddPutAllParameterFunctionWithWrapper -> parameterFunSpec
    }

private val ParameterFunction.SetParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(fieldType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = %T(%T(body)).createScaffold())", name, fieldType.scaffolderTypeName, fieldType.specTypeName)
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(fieldType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(fieldType.className))
                }
                .addParameter(fieldType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = %T(spec).createScaffold())", name, fieldType.scaffolderTypeName)
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(fieldType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(fieldType.className))
                }
                .addParameter(fieldType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = %T(ref.key))", name, Deferred::class.className)
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(fieldType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(fieldType.className))
                }
                .addParameter(fieldType.wrapperParameterSpec)
                .addStatement("shell = shell.copy(%L = %T(value))", name, Wrapper::class.className)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val singularName = field.actualSingularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(listType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(%T(body)).createScaffold())", name, name, listType.scaffolderTypeName, listType.specTypeName)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val singularName = field.actualSingularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(listType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(listType.className))
                }
                .addParameter(listType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(spec).createScaffold())", name, name, listType.scaffolderTypeName)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val singularName = field.actualSingularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(listType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(listType.className))
                }
                .addParameter(listType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(ref.key))", name, name, Deferred::class.className)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val singularName = field.actualSingularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(listType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(listType.className))
                }
                .addParameter(listType.wrapperParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(value))", name, name, Wrapper::class.className)
                .build()
    }

private val ParameterFunction.AddAllParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L.map { %T(it) })", name, name, name, Wrapper::class.className)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%T(%L),%T(%T(body)).createScaffold()))",
                        name, name, Wrapper::class.className, "key", valueType.scaffolderTypeName, valueType.specTypeName)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(keyType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(keyType.className))
                }
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%T(%L),%T(spec).createScaffold()))",
                        name, name, Wrapper::class.className, "key", valueType.scaffolderTypeName)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(keyType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(keyType.className))
                }
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%T(%L),%T(ref.key)))",
                        name, name, Wrapper::class.className, "key", Deferred::class.className)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(ParameterSpec.builder("value", valueClassName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%T(%L),%T(%L)))",
                        name, name, Wrapper::class.className, "key", Wrapper::class.className, "value")
                .build()
    }

private val ParameterFunction.PutPairParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(Pair::class.className, keyClassName, valueClassName)).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%T(%L), %T(%L)))",
                        name, name, Wrapper::class.className, "pair.first", Wrapper::class.className, "pair.second")
                .build()
    }

private val ParameterFunction.PutAllParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L.map { Pair(%T(%L), %T(%L)) })",
                        name, name, name, Wrapper::class.className, "it.key", Wrapper::class.className, "it.value")
                .build()
    }

private val ParameterFunction.AddPutKeyValueParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + mapOf<Scaffold<String>, Scaffold<WorkflowJob>>(Pair(%T(%L),%T(%T(body)).createScaffold())))",
                        name, name, Wrapper::class.className, "key", valueType.scaffolderTypeName, valueType.specTypeName)
                .build()
    }

private val ParameterFunction.AddPutKeyValueParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(keyType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(keyType.className))
                }
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + mapOf<%T<%T>, %T<%T>>(Pair(%T(%L),%T(spec).createScaffold())))",
                        name, name, Scaffold::class.className, keyType.className, Scaffold::class.className, valueType.className, Wrapper::class.className, "key", valueType.scaffolderTypeName)
                .build()
    }

private val ParameterFunction.AddPutKeyValueParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .runIfTrue(keyType is Union) {
                    addTypeVariable(TypeVariableName("T").withBounds(keyType.className))
                }
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + mapOf<%T<%T>, %T<%T>>(Pair(%T(%L),%T(ref.key))))",
                        name, name, Scaffold::class.className, keyType.className, Scaffold::class.className, valueType.className, Wrapper::class.className, "key", Deferred::class.className)
                .build()
    }

private val ParameterFunction.AddPutKeyValueParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(ParameterSpec.builder("value", valueClassName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + mapOf<%T<%T>, %T<%T>>(Pair(%T(%L),%T(%L))))",
                        name, name, Scaffold::class.className, keyType.className, Scaffold::class.className, valueType.className, Wrapper::class.className, "key", Wrapper::class.className, "value")
                .build()
    }

private val ParameterFunction.AddPutPairParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(Pair::class.className, keyClassName, valueClassName)).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + mapOf<%T<%T>, %T<%T>>(Pair(%T(%L), %T(%L))))",
                        name, name, Scaffold::class.className, keyType.className, Scaffold::class.className, valueType.className, Wrapper::class.className, "pair.first", Wrapper::class.className, "pair.second")
                .build()
    }

private val ParameterFunction.AddPutAllParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L.map { Pair(%T(%L), %T(%L)) })",
                        name, name, name, Wrapper::class.className, "it.key", Wrapper::class.className, "it.value")
                .build()
    }
