package io.philarios.schema.v0.translators.codegen.builders.builder

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.Field
import io.philarios.schema.v0.Struct
import io.philarios.schema.v0.Type
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.PutParameterFunSpecs.putPairParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.builder.ShellStructWithParametersBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithWrapper

object ShellStructWithParametersBuilderTypeBuilder : StructWithParametersBuilderTypeBuilder {
    override fun buildOneWithParameterFunctions(type: Struct, parameterFunctions: List<ParameterFunction>): TypeSpec {
        return TypeSpec.classBuilder(type.shellBuilderClassName.rawType)
                .addModifiers(KModifier.INTERNAL)
                .addSuperinterface(type.builderClassName)
                .addAnnotation(DslBuilder::class.className)
                .addTypeVariable(TypeVariableName("C", KModifier.OUT))
                .primaryConstructor(constructor(type))
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .initializer("context")
                        .build())
                .addProperty(PropertySpec.builder("shell", type.shellClassName, KModifier.INTERNAL)
                        .initializer("shell")
                        .mutable(true)
                        .build())
                .addFunctions(parameterFunctions.map { parameterFunction(it) })
                .addFunctions(includeFunctions(type))
                .addFunction(splitFunction(type))
                .addFunction(mergeFunction(type))
                .build()
    }

    private fun constructor(type: Struct): FunSpec {
        return FunSpec.constructorBuilder()
                .addParameter(contextParameterSpec)
                .addParameter(ParameterSpec.builder("shell", type.shellClassName)
                        .defaultValue("%T()", type.shellClassName)
                        .build())
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
            val name = field.name
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = %L)", name, name)
                    .build()
        }

        fun setParameterFunctionWithWrapper(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = %T(%L))", name, Wrapper::class.className, name)
                    .build()
        }

        fun setParameterFunctionWithBody(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(fieldType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = %T(body).connect(context))", name, fieldType.specClassName)
                    .build()
        }

        fun setParameterFunctionWithSpec(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(fieldType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = spec.connect(context))", name)
                    .build()
        }

        fun setParameterFunctionWithRef(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(fieldType.refParameterSpec)
                    .addStatement("shell = shell.copy(%L = ref)", name)
                    .build()
        }
    }

    private object AddParameterFunSpecs {
        fun addParameterFunction(type: Struct, field: Field, listType: Type): FunSpec {
            val listTypeName = listType.typeName
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L)", name, name, singularName)
                    .build()
        }

        fun addParameterFunctionWithWrapper(type: Struct, field: Field, listType: Type): FunSpec {
            val listTypeName = listType.typeName
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(%L))", name, name, Wrapper::class.className, singularName)
                    .build()
        }

        fun addParameterFunctionWithBody(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(listType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(body).connect(context))", name, name, listType.specClassName)
                    .build()
        }

        fun addParameterFunctionWithSpec(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(listType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + spec.connect(context))", name, name)
                    .build()
        }

        fun addParameterFunctionWithRef(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(listType.refParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + ref)", name, name)
                    .build()
        }
    }

    private object PutParameterFunSpecs {
        fun putKeyValueParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val valueClassName = valueType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(ParameterSpec.builder("value", valueClassName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,%L))", name, name, "key", "value")
                    .build()
        }

        fun putKeyValueParameterFunctionWithBody(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,%T(body).connect(context)))", name, name, "key", valueType.specClassName)
                    .build()
        }

        fun putKeyValueParameterFunctionWithSpec(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = this.%L.orEmpty() + Pair(%L,spec.connect(context)))", name, name, "key")
                    .build()
        }

        fun putKeyValueParameterFunctionWithRef(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.refParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,ref))", name, name, "key")
                    .build()
        }

        fun putPairParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val valueClassName = valueType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(Pair::class.className, keyClassName, valueClassName)).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,%L))", name, name, "pair.first", "pair.second")
                    .build()
        }
    }

    private object AddAllParameterFunSpecs {
        fun addAllParameterFunction(type: Struct, field: Field): FunSpec {
            val name = field.name
            val typeName = field.type.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L)", name, name, name)
                    .build()
        }

        fun addAllParameterFunctionWithWrapper(type: Struct, field: Field): FunSpec {
            val name = field.name
            val typeName = field.type.typeName
            return FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L.map { %T(it) })", name, name, name, Wrapper::class.className)
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
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(type.bodyParameterSpec)
                .addStatement("apply(body)")
                .build()
    }

    private fun includeFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(type.specParameterSpec)
                .addStatement("apply(spec.body)")
                .build()
    }

    private fun includeFunctionWithContextAndBody(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.OVERRIDE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .addStatement("val builder = split(context)")
                .addStatement("builder.apply(body)")
                .addStatement("merge(builder)")
                .build()
    }

    private fun includeFunctionWithContextAndSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addModifiers(KModifier.OVERRIDE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .addStatement("val builder = split(context)")
                .addStatement("builder.apply(spec.body)")
                .addStatement("merge(builder)")
                .build()
    }

    private fun includeForEachFunctionWithBody(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addModifiers(KModifier.OVERRIDE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .addStatement("context.forEach { include(it, body) }")
                .build()
    }

    private fun includeForEachFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addModifiers(KModifier.OVERRIDE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .addStatement("context.forEach { include(it, spec) }")
                .build()
    }

    private fun splitFunction(type: Struct): FunSpec {
        return FunSpec.builder("split")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .returns(type.otherShellBuilderClassName)
                .addStatement("return %T(context, shell)", type.otherShellBuilderClassName.rawType)
                .build()
    }

    private fun mergeFunction(type: Struct): FunSpec {
        return FunSpec.builder("merge")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter("other", type.otherShellBuilderClassName)
                .addStatement("this.shell = other.shell")
                .build()
    }

}