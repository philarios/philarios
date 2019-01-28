package io.philarios.schema.translators.codegen.types.builder

import com.squareup.kotlinpoet.*
import io.philarios.core.DslBuilder
import io.philarios.schema.Struct
import io.philarios.schema.translators.codegen.util.*

internal fun Struct.builderInterfaceTypeSpec(parameterFunctions: List<ParameterFunction>): TypeSpec {
    return TypeSpec.interfaceBuilder(builderClassName.rawType)
            .addAnnotation(DslBuilder::class.className)
            .addTypeVariable(TypeVariableName("C", KModifier.OUT))
            .addProperty(PropertySpec
                    .builder("context", TypeVariableName("C"))
                    .build())
            .addFunctions(parameterFunctions.map { it.parameterFunSpec })
            .addFunctions(includeFunctions)
            .build()
}

private val ParameterFunction.parameterFunSpec
    get() = when (this) {
        is ParameterFunction.SetParameterFunction -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.SetParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.AddParameterFunction -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.AddParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunction -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithWrapper -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithBody -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithSpec -> parameterFunSpec
        is ParameterFunction.PutKeyValueParameterFunctionWithRef -> parameterFunSpec
        is ParameterFunction.PutPairParameterFunction -> parameterFunSpec
        is ParameterFunction.AddAllParameterFunction -> parameterFunSpec
        is ParameterFunction.AddAllParameterFunctionWithWrapper -> parameterFunSpec
    }

private val ParameterFunction.SetParameterFunction.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = fieldType.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = fieldType.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(fieldType.bodyParameterSpec)
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(fieldType.specParameterSpec)
                .build()
    }

private val ParameterFunction.SetParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(fieldType.refParameterSpec)
                .build()
    }

private val ParameterFunction.AddParameterFunction.parameterFunSpec
    get(): FunSpec {
        val listTypeName = listType.typeName
        val singularName = field.singularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val listTypeName = listType.typeName
        val singularName = field.singularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val singularName = field.singularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(listType.bodyParameterSpec)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val singularName = field.singularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(listType.specParameterSpec)
                .build()
    }

private val ParameterFunction.AddParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val singularName = field.singularName
        return FunSpec.builder(singularName)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(listType.refParameterSpec)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunction.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(ParameterSpec.builder("value", valueClassName).build())
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(ParameterSpec.builder("value", valueClassName).build())
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithBody.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.bodyParameterSpec)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithSpec.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.specParameterSpec)
                .build()
    }

private val ParameterFunction.PutKeyValueParameterFunctionWithRef.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("key", keyClassName).build())
                .addParameter(valueType.refParameterSpec)
                .build()
    }

private val ParameterFunction.PutPairParameterFunction.parameterFunSpec
    get(): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.escapedName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(Pair::class.className, keyClassName, valueClassName)).build())
                .build()
    }

private val ParameterFunction.AddAllParameterFunction.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .build()
    }

private val ParameterFunction.AddAllParameterFunctionWithWrapper.parameterFunSpec
    get(): FunSpec {
        val name = field.escapedName
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .build()
    }

private val Struct.includeFunctions
    get() = listOf(
            includeFunctionWithBody,
            includeFunctionWithSpec,
            includeFunctionWithContextAndBody,
            includeFunctionWithContextAndSpec,
            includeForEachFunctionWithBody,
            includeForEachFunctionWithSpec
    )

private val Struct.includeFunctionWithBody
    get() =
        FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(bodyParameterSpec)
                .build()

private val Struct.includeFunctionWithSpec
    get() =
        FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(specParameterSpec)
                .build()

private val Struct.includeFunctionWithContextAndBody
    get() =
        FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(otherBodyParameterSpec)
                .build()

private val Struct.includeFunctionWithContextAndSpec
    get() =
        FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(otherSpecParameterSpec)
                .build()

private val Struct.includeForEachFunctionWithBody
    get() =
        FunSpec.builder("includeForEach")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(otherBodyParameterSpec)
                .build()

private val Struct.includeForEachFunctionWithSpec
    get() =
        FunSpec.builder("includeForEach")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(otherSpecParameterSpec)
                .build()
