package io.philarios.schema.v0.translators.codegen.builders.builder

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.Field
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putPairParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.InterfaceStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithWrapper

object InterfaceStructWithParametersBuilderTypeBuilder : StructWithParametersBuilderTypeBuilder {
    override fun buildOneWithParameterFunctions(type: Struct, parameterFunctions: List<ParameterFunction>): TypeSpec {
        return TypeSpec.interfaceBuilder(type.builderClassName.rawType)
                .addAnnotation(DslBuilder::class.className)
                .addTypeVariable(TypeVariableName("C", KModifier.OUT))
                .addProperty(PropertySpec
                        .builder("context", TypeVariableName("C"))
                        .build())
                .addFunctions(parameterFunctions.map { parameterFunction(it) })
                .addFunctions(includeFunctions(type))
                .build()
    }

    private fun parameterFunction(parameterFunction: ParameterFunction): FunSpec {
        return when (parameterFunction) {
            is ParameterFunction.SetParameterFunction -> setParameterFunction(parameterFunction.type, parameterFunction.field, parameterFunction.fieldType)
            is ParameterFunction.SetParameterFunctionWithWrapper -> setParameterFunctionWithWrapper(parameterFunction.type, parameterFunction.field, parameterFunction.fieldType)
            is ParameterFunction.SetParameterFunctionWithBody -> setParameterFunctionWithBody(parameterFunction.type, parameterFunction.field, parameterFunction.fieldType)
            is ParameterFunction.SetParameterFunctionWithSpec -> setParameterFunctionWithSpec(parameterFunction.type, parameterFunction.field, parameterFunction.fieldType)
            is ParameterFunction.SetParameterFunctionWithRef -> setParameterFunctionWithRef(parameterFunction.type, parameterFunction.field, parameterFunction.fieldType)
            is ParameterFunction.AddParameterFunction -> addParameterFunction(parameterFunction.type, parameterFunction.field, parameterFunction.listType)
            is ParameterFunction.AddParameterFunctionWithWrapper -> addParameterFunctionWithWrapper(parameterFunction.type, parameterFunction.field, parameterFunction.listType)
            is ParameterFunction.AddParameterFunctionWithBody -> addParameterFunctionWithBody(parameterFunction.type, parameterFunction.field, parameterFunction.listType)
            is ParameterFunction.AddParameterFunctionWithSpec -> addParameterFunctionWithSpec(parameterFunction.type, parameterFunction.field, parameterFunction.listType)
            is ParameterFunction.AddParameterFunctionWithRef -> addParameterFunctionWithRef(parameterFunction.type, parameterFunction.field, parameterFunction.listType)
            is ParameterFunction.PutKeyValueParameterFunction -> putKeyValueParameterFunction(parameterFunction.type, parameterFunction.field, parameterFunction.keyType, parameterFunction.valueType)
            is ParameterFunction.PutKeyValueParameterFunctionWithBody -> putKeyValueParameterFunctionWithBody(parameterFunction.type, parameterFunction.field, parameterFunction.keyType, parameterFunction.valueType)
            is ParameterFunction.PutKeyValueParameterFunctionWithSpec -> putKeyValueParameterFunctionWithSpec(parameterFunction.type, parameterFunction.field, parameterFunction.keyType, parameterFunction.valueType)
            is ParameterFunction.PutKeyValueParameterFunctionWithRef -> putKeyValueParameterFunctionWithRef(parameterFunction.type, parameterFunction.field, parameterFunction.keyType, parameterFunction.valueType)
            is ParameterFunction.PutPairParameterFunction -> putPairParameterFunction(parameterFunction.type, parameterFunction.field, parameterFunction.keyType, parameterFunction.valueType)
            is ParameterFunction.AddAllParameterFunction -> addAllParameterFunction(parameterFunction.type, parameterFunction.field)
            is ParameterFunction.AddAllParameterFunctionWithWrapper -> addAllParameterFunctionWithWrapper(parameterFunction.type, parameterFunction.field)
        }
    }

    private object SetParameterFunSpecs {
        fun setParameterFunction(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.escapedName
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .build()
        }

        fun setParameterFunctionWithWrapper(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.escapedName
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .build()
        }

        fun setParameterFunctionWithBody(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(fieldType.bodyParameterSpec)
                    .build()
        }

        fun setParameterFunctionWithSpec(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(fieldType.specParameterSpec)
                    .build()
        }

        fun setParameterFunctionWithRef(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(fieldType.refParameterSpec)
                    .build()
        }
    }

    private object AddParameterFunSpecs {
        fun addParameterFunction(type: Struct, field: Field, listType: Type): FunSpec {
            val listTypeName = listType.typeName
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .build()
        }

        fun addParameterFunctionWithWrapper(type: Struct, field: Field, listType: Type): FunSpec {
            val listTypeName = listType.typeName
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .build()
        }

        fun addParameterFunctionWithBody(type: Struct, field: Field, listType: Type): FunSpec {
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(listType.bodyParameterSpec)
                    .build()
        }

        fun addParameterFunctionWithSpec(type: Struct, field: Field, listType: Type): FunSpec {
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(listType.specParameterSpec)
                    .build()
        }

        fun addParameterFunctionWithRef(type: Struct, field: Field, listType: Type): FunSpec {
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(listType.refParameterSpec)
                    .build()
        }
    }

    private object PutParameterFunSpecs {
        fun putKeyValueParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val valueClassName = valueType.className
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(ParameterSpec.builder("value", valueClassName).build())
                    .build()
        }

        fun putKeyValueParameterFunctionWithBody(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.bodyParameterSpec)
                    .build()
        }

        fun putKeyValueParameterFunctionWithSpec(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.specParameterSpec)
                    .build()
        }

        fun putKeyValueParameterFunctionWithRef(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.refParameterSpec)
                    .build()
        }

        fun putPairParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val valueClassName = valueType.className
            val name = field.escapedName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(Pair::class.className, keyClassName, valueClassName)).build())
                    .build()
        }
    }

    private object AddAllParameterFunSpecs {
        fun addAllParameterFunction(type: Struct, field: Field): FunSpec {
            val name = field.escapedName
            val typeName = field.type.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .build()
        }

        fun addAllParameterFunctionWithWrapper(type: Struct, field: Field): FunSpec {
            val name = field.escapedName
            val typeName = field.type.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .build()
        }
    }

    private fun includeFunctions(type: Struct): List<FunSpec> {
        return listOf(
                includeFunctionWithBody(type),
                includeFunctionWithSpec(type),
                includeFunctionWithContextAndBody(type),
                includeFunctionWithContextAndSpec(type),
                includeForEachFunctionWithBody(type),
                includeForEachFunctionWithSpec(type)
        )
    }

    private fun includeFunctionWithBody(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(type.bodyParameterSpec)
                .build()
    }

    private fun includeFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(type.specParameterSpec)
                .build()
    }

    private fun includeFunctionWithContextAndBody(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .build()
    }

    private fun includeFunctionWithContextAndSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .build()
    }

    private fun includeForEachFunctionWithBody(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .build()
    }

    private fun includeForEachFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addModifiers(KModifier.ABSTRACT)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .build()
    }

}