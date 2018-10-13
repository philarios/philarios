package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.*


object BuilderTypeSpec {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return when (type) {
            is Struct -> StructBuilderTypeSpec.build(type, typeRefs)
            is Union -> UnionBuilderTypeSpec.build(type, typeRefs)
            else -> emptyList()
        }
    }

}

private object StructBuilderTypeSpec {

    fun build(type: Struct, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return listOf(buildOne(type, typeRefs))
    }

    private fun buildOne(type: Struct, typeRefs: Map<RefType, Type>): TypeSpec {
        if (type.fields.isEmpty()) {
            return buildObject(type)
        }
        return buildDataClass(type, typeRefs)
    }

    private fun buildObject(type: Struct): TypeSpec {
        return TypeSpec.classBuilder(type.builderClassName.rawType)
                .addAnnotation(DslBuilder::class.className)
                .addTypeVariable(TypeVariableName("C", KModifier.OUT))
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .initializer("context")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(contextParameterSpec)
                        .build())
                .build()
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
                        .map { parameterFunction(type, it, typeRefs) }
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

    private fun parameterFunction(type: Struct, field: Field, typeRefs: Map<RefType, Type>): List<FunSpec> {
        val fieldType = field.type
        return when (fieldType) {
            is Struct -> listOf(
                    setParameterFunctionWithBody(type, field, fieldType),
                    setParameterFunctionWithSpec(type, field, fieldType),
                    setParameterFunctionWithRef(type, field, fieldType),
                    setParameterFunctionWithWrapper(type, field)
            )
            is Union -> fieldType.shapes.flatMap {
                listOf(
                        setParameterFunctionWithSpec(type, field, it),
                        setParameterFunctionWithRef(type, field, it)
                )
            }
            is ListType -> {
                val listType = fieldType.type
                when (listType) {
                    is Struct -> listOf(
                            addParameterFunctionWithBody(type, field, listType),
                            addParameterFunctionWithSpec(type, field, listType),
                            addParameterFunctionWithRef(type, field, listType),
                            addParameterFunctionWithWrapper(type, field, listType),
                            addAllParameterFunctionWithWrapper(type, field)
                    )
                    is Union -> listType.shapes.flatMap {
                        listOf(
                                addParameterFunctionWithSpec(type, field, it),
                                addParameterFunctionWithRef(type, field, it)
                        )
                    }
                    is ListType -> emptyList() // TODO no idea what to do here
                    is RefType -> parameterFunction(type, Field(field.name, field.key, ListType(typeRefs[listType]!!)), typeRefs)
                    else -> listOf(
                            addParameterFunction(type, field, listType),
                            addAllParameterFunction(type, field)
                    )
                }
            }
            is MapType -> {
                val keyType = fieldType.keyType
                val valueType = fieldType.valueType
                when {
                    keyType is RefType || valueType is RefType ->
                        parameterFunction(type, Field(field.name, field.key, MapType(
                                (keyType as? RefType)?.let { typeRefs[it]!! } ?: keyType,
                                (valueType as? RefType)?.let { typeRefs[it]!! } ?: valueType
                        )), typeRefs)
                    keyType is Struct || keyType is Union -> emptyList()
                    valueType is Struct -> listOf(
                            putKeyValueParameterFunctionWithBody(type, field, keyType, valueType),
                            putKeyValueParameterFunctionWithSpec(type, field, keyType, valueType),
                            putKeyValueParameterFunctionWithRef(type, field, keyType, valueType),
                            putKeyValueParameterFunction(type, field, keyType, valueType),
                            putPairParameterFunction(type, field, keyType, valueType)
                    )
                    valueType is Union -> valueType.shapes.flatMap {
                        listOf(
                                putKeyValueParameterFunctionWithSpec(type, field, keyType, it),
                                putKeyValueParameterFunctionWithRef(type, field, keyType, it)
                        )
                    }
                    else -> listOf(
                            putKeyValueParameterFunction(type, field, keyType, valueType),
                            putPairParameterFunction(type, field, keyType, valueType),
                            addAllParameterFunction(type, field)
                    )
                }
            }
            is RefType -> parameterFunction(type, Field(field.name, field.key, typeRefs[fieldType]!!), typeRefs)
            is OptionType -> parameterFunction(type, Field(field.name, field.key, fieldType.type), typeRefs)
            else -> listOf(setParameterFunction(type, field))
        }
    }

    private fun setParameterFunction(type: Struct, field: Field): FunSpec {
        val name = field.name
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = %L)", name, name)
                .build()
    }

    private fun setParameterFunctionWithWrapper(type: Struct, field: Field): FunSpec {
        val name = field.name
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = %T(%L))", name, Wrapper::class.className, name)
                .build()
    }

    private fun addParameterFunction(type: Struct, field: Field, listType: Type): FunSpec {
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

    private fun addParameterFunctionWithWrapper(type: Struct, field: Field, listType: Type): FunSpec {
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

    private fun putKeyValueParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
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

    private fun putKeyValueParameterFunctionWithBody(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
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

    private fun putKeyValueParameterFunctionWithSpec(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
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

    private fun putKeyValueParameterFunctionWithRef(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
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

    private fun putPairParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
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

    private fun addAllParameterFunction(type: Struct, field: Field): FunSpec {
        val name = field.name
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L)", name, name, name)
                .build()
    }

    private fun addAllParameterFunctionWithWrapper(type: Struct, field: Field): FunSpec {
        val name = field.name
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %L.map { %T(it) })", name, name, name, Wrapper::class.className)
                .build()
    }

    private fun setParameterFunctionWithBody(type: Struct, field: Field, fieldType: Type): FunSpec {
        val name = field.name

        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(fieldType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = %T(body).connect(context))", name, fieldType.specClassName)
                .build()
    }

    private fun setParameterFunctionWithSpec(type: Struct, field: Field, fieldType: Type): FunSpec {
        val name = field.name

        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(fieldType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = spec.connect(context))", name)
                .build()
    }

    private fun setParameterFunctionWithRef(type: Struct, field: Field, fieldType: Type): FunSpec {
        val name = field.name

        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(fieldType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = ref)", name)
                .build()
    }

    private fun addParameterFunctionWithBody(type: Struct, field: Field, listType: Type): FunSpec {
        val name = field.name
        val singularName = field.singularName

        return FunSpec.builder(singularName)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(listType.bodyParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + %T(body).connect(context))", name, name, listType.specClassName)
                .build()
    }

    private fun addParameterFunctionWithSpec(type: Struct, field: Field, listType: Type): FunSpec {
        val name = field.name
        val singularName = field.singularName

        return FunSpec.builder(singularName)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(listType.specParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + spec.connect(context))", name, name)
                .build()
    }

    private fun addParameterFunctionWithRef(type: Struct, field: Field, listType: Type): FunSpec {
        val name = field.name
        val singularName = field.singularName

        return FunSpec.builder(singularName)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(listType.refParameterSpec)
                .addStatement("shell = shell.copy(%L = shell.%L.orEmpty() + ref)", name, name)
                .build()
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

private object UnionBuilderTypeSpec {

    fun build(type: Union, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return type.shapes.flatMap { StructBuilderTypeSpec.build(it, typeRefs) }
    }

}