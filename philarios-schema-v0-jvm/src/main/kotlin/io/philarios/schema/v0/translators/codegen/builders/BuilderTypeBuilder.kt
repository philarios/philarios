package io.philarios.schema.v0.translators.codegen.builders

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddAllParameterFunSpecs.addAllParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.AddParameterFunSpecs.addParameterFunctionWithWrapper
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.PutParameterFunSpecs.putKeyValueParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.PutParameterFunSpecs.putPairParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunction
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithBody
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithRef
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithSpec
import io.philarios.schema.v0.translators.codegen.builders.StructBuilderTypeBuilder.SetParameterFunSpecs.setParameterFunctionWithWrapper

object BuilderTypeBuilder {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return when (type) {
            is Struct -> StructBuilderTypeBuilder.build(type, typeRefs)
            is Union -> UnionBuilderTypeBuilder.build(type, typeRefs)
            else -> emptyList()
        }
    }

}

private object StructBuilderTypeBuilder {

    fun build(type: Struct, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return listOf(buildOne(type, typeRefs)).mapNotNull { it }
    }

    private fun buildOne(type: Struct, typeRefs: Map<RefType, Type>): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildDataClass(type, typeRefs)
    }

    private fun buildDataClass(type: Struct, typeRefs: Map<RefType, Type>): TypeSpec {
        val fields = type.fields
        return TypeSpec.classBuilder(type.builderClassName.rawType)
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
                .addFunctions(fields
                        .map { parameterFunctions(type, it, typeRefs) }
                        .flatMap { it }
                )
                .addFunctions(includeFunctions(type))
                .addFunction(splitFunction(type))
                .addFunction(mergeFunction(type))
                .build()
    }

    private fun constructor(type: Struct): FunSpec {
        return FunSpec.constructorBuilder()
                .addParameter(contextParameterSpec)
                .addParameter(ParameterSpec
                        .builder("shell", type.shellClassName)
                        .defaultValue("%T()", type.shellClassName)
                        .build())
                .build()
    }

    private fun parameterFunctions(type: Struct, field: Field, typeRefs: Map<RefType, Type>): List<FunSpec> {
        val fieldType = field.type
        return when (fieldType) {
            is Struct -> structParameterFunctions(type, field, fieldType)
            is Union -> unionParameterFunctions(type, field, fieldType)
            is ListType -> listParameterFunctions(type, field, fieldType, typeRefs)
            is MapType -> mapParameterFunctions(type, field, fieldType, typeRefs)
            is RefType -> refTypeParameterFunctions(type, field, fieldType, typeRefs)
            is OptionType -> optionTypeParameterFunctions(type, field, fieldType, typeRefs)
            else -> primitiveParameterFunctions(type, field)
        }
    }

    private fun structParameterFunctions(type: Struct, field: Field, fieldType: Struct): List<FunSpec> {
        return if (fieldType.fields.isEmpty()) {
            listOf(
                    setParameterFunction(type, field, fieldType)
            )
        } else {
            listOf(
                    setParameterFunctionWithBody(type, field, fieldType),
                    setParameterFunctionWithSpec(type, field, fieldType),
                    setParameterFunctionWithRef(type, field, fieldType),
                    setParameterFunctionWithWrapper(type, field, fieldType)
            )
        }
    }

    private fun unionParameterFunctions(type: Struct, field: Field, fieldType: Union): List<FunSpec> {
        return fieldType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        setParameterFunctionWithWrapper(type, field, it)
                )
            } else {
                listOf(
                        setParameterFunctionWithSpec(type, field, it),
                        setParameterFunctionWithRef(type, field, it)
                )
            }
        }
    }

    private fun listParameterFunctions(type: Struct, field: Field, fieldType: ListType, typeRefs: Map<RefType, Type>): List<FunSpec> {
        val listType = fieldType.type
        return when (listType) {
            is Struct -> structListParameterFunctions(type, field, listType)
            is Union -> unionListParameterFunctions(type, field, listType)
            is ListType -> throw UnsupportedOperationException("Nested lists are not (yet) supported")
            is MapType -> throw UnsupportedOperationException("Nested lists are not (yet) supported")
            is RefType -> refListParameterFunctions(type, field, listType, typeRefs)
            else -> primitiveListParameterFunction(type, field, listType)
        }
    }

    private fun structListParameterFunctions(type: Struct, field: Field, listType: Struct): List<FunSpec> {
        return if (listType.fields.isEmpty()) {
            listOf(
                    addParameterFunction(type, field, listType),
                    addAllParameterFunction(type, field)
            )
        } else {
            listOf(
                    addParameterFunctionWithBody(type, field, listType),
                    addParameterFunctionWithSpec(type, field, listType),
                    addParameterFunctionWithRef(type, field, listType),
                    addParameterFunctionWithWrapper(type, field, listType),
                    addAllParameterFunctionWithWrapper(type, field)
            )
        }
    }

    private fun unionListParameterFunctions(type: Struct, field: Field, listType: Union): List<FunSpec> {
        return listType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        addParameterFunctionWithWrapper(type, field, it)
                )
            } else {
                listOf(
                        addParameterFunctionWithSpec(type, field, it),
                        addParameterFunctionWithRef(type, field, it)
                )
            }
        }
    }

    private fun refListParameterFunctions(type: Struct, field: Field, listType: RefType, typeRefs: Map<RefType, Type>): List<FunSpec> {
        return parameterFunctions(type, field.copy(type = ListType(typeRefs[listType]!!)), typeRefs)
    }

    private fun primitiveListParameterFunction(type: Struct, field: Field, listType: Type): List<FunSpec> {
        return listOf(
                addParameterFunction(type, field, listType),
                addAllParameterFunction(type, field)
        )
    }

    private fun mapParameterFunctions(type: Struct, field: Field, fieldType: MapType, typeRefs: Map<RefType, Type>): List<FunSpec> {
        val keyType = fieldType.keyType
        val valueType = fieldType.valueType
        return when {
            keyType is RefType || valueType is RefType -> refMapParameterFunctions(type, field, keyType, valueType, typeRefs)
            keyType is ListType || valueType is ListType -> throw UnsupportedOperationException("Nested maps are not (yet) supported")
            keyType is MapType || valueType is MapType -> throw UnsupportedOperationException("Nested maps are not (yet) supported")
            keyType is Struct -> throw UnsupportedOperationException("Structs as map keys are not (yet) supported")
            keyType is Union -> throw UnsupportedOperationException("Unions as map keys are not (yet) supported")
            valueType is Struct -> structValueMapParameterFunctions(type, field, keyType, valueType)
            valueType is Union -> unionValueMapParameterFunctions(type, field, keyType, valueType)
            else -> primitiveMapParameterFunctions(type, field, keyType, valueType)
        }
    }

    fun structValueMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Struct): List<FunSpec> {
        return if (valueType.fields.isEmpty()) {
            listOf(
                    putKeyValueParameterFunction(type, field, keyType, valueType),
                    putPairParameterFunction(type, field, keyType, valueType)
            )
        } else {
            listOf(
                    putKeyValueParameterFunctionWithBody(type, field, keyType, valueType),
                    putKeyValueParameterFunctionWithSpec(type, field, keyType, valueType),
                    putKeyValueParameterFunctionWithRef(type, field, keyType, valueType),
                    putKeyValueParameterFunction(type, field, keyType, valueType),
                    putPairParameterFunction(type, field, keyType, valueType)
            )
        }
    }

    fun unionValueMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Union): List<FunSpec> {
        return valueType.shapes.flatMap {
            if (it.fields.isEmpty()) {
                listOf(
                        putKeyValueParameterFunction(type, field, keyType, it),
                        putPairParameterFunction(type, field, keyType, it)
                )
            } else {
                listOf(
                        putKeyValueParameterFunctionWithSpec(type, field, keyType, it),
                        putKeyValueParameterFunctionWithRef(type, field, keyType, it)
                )
            }
        }
    }

    fun refMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Type, typeRefs: Map<RefType, Type>): List<FunSpec> {
        return parameterFunctions(type, Field(field.name, field.key, MapType(
                (keyType as? RefType)?.let { typeRefs[it]!! } ?: keyType,
                (valueType as? RefType)?.let { typeRefs[it]!! } ?: valueType
        )), typeRefs)
    }

    fun primitiveMapParameterFunctions(type: Struct, field: Field, keyType: Type, valueType: Type): List<FunSpec> {
        return listOf(
                putKeyValueParameterFunction(type, field, keyType, valueType),
                putPairParameterFunction(type, field, keyType, valueType),
                addAllParameterFunction(type, field)
        )
    }

    private fun refTypeParameterFunctions(type: Struct, field: Field, fieldType: RefType, typeRefs: Map<RefType, Type>): List<FunSpec> {
        return parameterFunctions(type, field.copy(type = typeRefs[fieldType]!!), typeRefs)
    }

    private fun optionTypeParameterFunctions(type: Struct, field: Field, fieldType: OptionType, typeRefs: Map<RefType, Type>): List<FunSpec> {
        return parameterFunctions(type, field.copy(type = fieldType.type), typeRefs)
    }

    private fun primitiveParameterFunctions(type: Struct, field: Field): List<FunSpec> {
        return listOf(
                setParameterFunction(type, field, field.type)
        )
    }

    private object SetParameterFunSpecs {
        fun setParameterFunction(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = %L)", name, name)
                    .build()
        }

        fun setParameterFunctionWithWrapper(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            val typeName = fieldType.typeName
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = %T(%L))", name, Wrapper::class.className, name)
                    .build()
        }

        fun setParameterFunctionWithBody(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(fieldType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = %T(body).connect(context))", name, fieldType.specClassName)
                    .build()
        }

        fun setParameterFunctionWithSpec(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(fieldType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = spec.connect(context))", name)
                    .build()
        }

        fun setParameterFunctionWithRef(type: Struct, field: Field, fieldType: Type): FunSpec {
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
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
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L)", name, name, singularName)
                    .build()
        }

        fun addParameterFunctionWithWrapper(type: Struct, field: Field, listType: Type): FunSpec {
            val listTypeName = listType.typeName
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder(singularName, listTypeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(%L))", name, name, Wrapper::class.className, singularName)
                    .build()
        }

        fun addParameterFunctionWithBody(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(listType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(body).connect(context))", name, name, listType.specClassName)
                    .build()
        }

        fun addParameterFunctionWithSpec(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(listType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + spec.connect(context))", name, name)
                    .build()
        }

        fun addParameterFunctionWithRef(type: Struct, field: Field, listType: Type): FunSpec {
            val name = field.name
            val singularName = field.singularName
            return FunSpec.builder(singularName)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
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
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(ParameterSpec.builder("value", valueClassName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,%L))", name, name, "key", "value")
                    .build()
        }

        fun putKeyValueParameterFunctionWithBody(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.bodyParameterSpec)
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + Pair(%L,%T(body).connect(context)))", name, name, "key", valueType.specClassName)
                    .build()
        }

        fun putKeyValueParameterFunctionWithSpec(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder("key", keyClassName).build())
                    .addParameter(valueType.specParameterSpec)
                    .addStatement("shell = shell.copy(%L = this.%L.orEmpty() + Pair(%L,spec.connect(context)))", name, name, "key")
                    .build()
        }

        fun putKeyValueParameterFunctionWithRef(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
            val keyClassName = keyType.className
            val name = field.name
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
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
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
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
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
                    .addParameter(ParameterSpec.builder(name, typeName).build())
                    .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L)", name, name, name)
                    .build()
        }

        fun addAllParameterFunctionWithWrapper(type: Struct, field: Field): FunSpec {
            val name = field.name
            val typeName = field.type.typeName
            return FunSpec.builder(name)
                    .addTypeVariable(TypeVariableName("C"))
                    .receiver(type.builderClassName)
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
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(type.bodyParameterSpec)
                .addStatement("apply(body)")
                .build()
    }

    private fun includeFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(type.specParameterSpec)
                .addStatement("apply(spec.body)")
                .build()
    }

    private fun includeFunctionWithContextAndBody(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addTypeVariable(TypeVariableName("C"))
                .addTypeVariable(TypeVariableName("C2"))
                .receiver(type.builderClassName)
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .addStatement("val builder = split(context)")
                .addStatement("builder.apply(body)")
                .addStatement("merge(builder)")
                .build()
    }

    private fun includeFunctionWithContextAndSpec(type: Struct): FunSpec {
        return FunSpec.builder("include")
                .addTypeVariable(TypeVariableName("C"))
                .addTypeVariable(TypeVariableName("C2"))
                .receiver(type.builderClassName)
                .addParameter(otherContextParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .addStatement("val builder = split(context)")
                .addStatement("builder.apply(spec.body)")
                .addStatement("merge(builder)")
                .build()
    }

    private fun includeForEachFunctionWithBody(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addTypeVariable(TypeVariableName("C"))
                .addTypeVariable(TypeVariableName("C2"))
                .receiver(type.builderClassName)
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherBodyParameterSpec)
                .addStatement("context.forEach { include(it, body) }")
                .build()
    }

    private fun includeForEachFunctionWithSpec(type: Struct): FunSpec {
        return FunSpec.builder("includeForEach")
                .addTypeVariable(TypeVariableName("C"))
                .addTypeVariable(TypeVariableName("C2"))
                .receiver(type.builderClassName)
                .addParameter(otherContextIterableParameterSpec)
                .addParameter(type.otherSpecParameterSpec)
                .addStatement("context.forEach { include(it, spec) }")
                .build()
    }

    private fun splitFunction(type: Struct): FunSpec {
        val builderClassNameAlternate = type.otherBuilderClassName
        return FunSpec.builder("split")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .returns(builderClassNameAlternate)
                .addStatement("return %T(context, shell)", builderClassNameAlternate.rawType)
                .build()
    }

    private fun mergeFunction(type: Struct): FunSpec {
        return FunSpec.builder("merge")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter("other", type.otherBuilderClassName)
                .addStatement("this.shell = other.shell")
                .build()
    }

}

private object UnionBuilderTypeBuilder {

    fun build(type: Union, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return type.shapes.flatMap { StructBuilderTypeBuilder.build(it, typeRefs) }
    }

}