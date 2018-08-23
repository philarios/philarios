package io.philarios.schema.v0.translators.codegen

import com.squareup.kotlinpoet.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements
import io.philarios.schema.v0.*


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
                .addSuperinterface(type.builderSuperinterface)
                .addAnnotation(dslBuilderClassName)
                .addTypeVariable(TypeVariableName("C", KModifier.OUT))
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .initializer("context")
                        .build())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(contextParameterSpec)
                        .build())
                .addFunction(FunSpec.builder("build")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(type.className)
                        .addStatement("return %T", type.className)
                        .build())
                .build()
    }

    private fun buildDataClass(type: Struct, typeRefs: Map<RefType, Type>): TypeSpec {
        val fields = type.fields
        return TypeSpec.classBuilder(type.builderClassName.rawType)
                .addSuperinterface(type.builderSuperinterface)
                .addAnnotation(dslBuilderClassName)
                .addTypeVariable(TypeVariableName("C", KModifier.OUT))
                .primaryConstructor(constructor(type))
                .addProperty(PropertySpec.builder("context", TypeVariableName("C"))
                        .initializer("context")
                        .build())
                .addProperties(fields.map { field ->
                    PropertySpec.builder(field.name, field.type.nullableTypeName, KModifier.PRIVATE)
                            .mutable(true)
                            .initializer(field.name)
                            .build()
                })
                .addFunctions(fields
                        .map { parameterFunction(type, it, typeRefs) }
                        .flatMap { it }
                )
                .addFunctions(includeFunctions(type))
                .addFunction(splitFunction(type))
                .addFunction(mergeFunction(type))
                .addFunction(buildFunction(type))
                .build()
    }

    private fun constructor(type: Struct): FunSpec {
        val fields = type.fields
        return FunSpec.constructorBuilder()
                .addParameter(contextParameterSpec)
                .addParameters(fields.map { field ->
                    ParameterSpec.builder(field.name, field.type.nullableTypeName, KModifier.PRIVATE)
                            .defaultValue(when (field.type) {
                                is ListType -> "emptyList()"
                                is MapType -> "emptyMap()"
                                else -> "null"
                            })
                            .build()
                })
                .build()
    }

    private fun parameterFunction(type: Struct, field: Field, typeRefs: Map<RefType, Type>): List<FunSpec> {
        val fieldType = field.type
        return when (fieldType) {
            is Struct -> listOf(
                    setParameterFunctionWithBody(type, field, fieldType),
                    setParameterFunctionWithSpec(type, field, fieldType)
            )
            is Union -> fieldType.shapes.map {
                setParameterFunctionWithSpec(type, field, it)
            }
            is ListType -> {
                val listType = fieldType.type
                when (listType) {
                    is Struct -> listOf(
                            addParameterFunctionWithBody(type, field, listType),
                            addParameterFunctionWithSpec(type, field, listType)
                    )
                    is Union -> listType.shapes.map {
                        addParameterFunctionWithSpec(type, field, it)
                    }
                    is ListType -> emptyList() // TODO no idea what to do here
                    is RefType -> parameterFunction(type, Field(field.name, ListType(typeRefs[listType]!!)), typeRefs)
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
                        parameterFunction(type, Field(field.name, MapType(
                                (keyType as? RefType)?.let { typeRefs[it]!! } ?: keyType,
                                (valueType as? RefType)?.let { typeRefs[it]!! } ?: valueType
                        )), typeRefs)
                    keyType is Struct || keyType is Union -> emptyList()
                    valueType is Struct -> listOf(
                            putKeyValueParameterFunctionWithBody(type, field, keyType, valueType),
                            putKeyValueParameterFunctionWithSpec(type, field, keyType, valueType)
                    )
                    valueType is Union -> valueType.shapes.map {
                        putKeyValueParameterFunctionWithSpec(type, field, keyType, it)
                    }
                    else -> listOf(
                            putKeyValueParameterFunction(type, field, keyType, valueType),
                            putPairParameterFunction(type, field, keyType, valueType)
                    )
                }
            }
            is RefType -> parameterFunction(type, Field(field.name, typeRefs[fieldType]!!), typeRefs)
            is OptionType -> parameterFunction(type, Field(field.name, fieldType.type), typeRefs)
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
                .addStatement("this.%L = %L", name, name)
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
                .addStatement("this.%L = this.%L.orEmpty() + %L", name, name, singularName)
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
                .addStatement("this.%L = this.%L.orEmpty() + Pair(%L,%L)", name, name, "key", "value")
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
                .addStatement("this.%L = this.%L.orEmpty() + Pair(%L,%T(body).translate(context))", name, name, "key", valueType.translatorClassName)
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
                .addStatement("this.%L = this.%L.orEmpty() + Pair(%L,%T(spec).translate(context))", name, name, "key", valueType.translatorClassName)
                .build()
    }

    private fun putPairParameterFunction(type: Struct, field: Field, keyType: Type, valueType: Type): FunSpec {
        val keyClassName = keyType.className
        val valueClassName = valueType.className
        val name = field.name
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder("pair", ParameterizedTypeName.get(ClassName.bestGuess(Pair::class.qualifiedName!!), keyClassName, valueClassName)).build())
                .addStatement("this.%L = this.%L.orEmpty() + Pair(%L,%L)", name, name, "pair.first", "pair.second")
                .build()
    }

    private fun addAllParameterFunction(type: Struct, field: Field): FunSpec {
        val name = field.name
        val typeName = field.type.typeName
        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(ParameterSpec.builder(name, typeName).build())
                .addStatement("this.%L = this.%L.orEmpty() + %L", name, name, name)
                .build()
    }

    private fun setParameterFunctionWithBody(type: Struct, field: Field, fieldType: Type): FunSpec {
        val name = field.name

        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(fieldType.bodyParameterSpec)
                .addStatement("this.%L = %T(body).translate(context)", name, fieldType.translatorClassName)
                .build()
    }

    private fun setParameterFunctionWithSpec(type: Struct, field: Field, fieldType: Type): FunSpec {
        val name = field.name

        return FunSpec.builder(name)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(fieldType.specParameterSpec)
                .addStatement("this.%L = %T(spec).translate(context)", name, fieldType.translatorClassName)
                .build()
    }

    private fun addParameterFunctionWithBody(type: Struct, field: Field, listType: Type): FunSpec {
        val name = field.name
        val singularName = field.singularName

        return FunSpec.builder(singularName)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(listType.bodyParameterSpec)
                .addStatement("this.%L = this.%L.orEmpty() + %T(body).translate(context)", name, name, listType.translatorClassName)
                .build()
    }

    private fun addParameterFunctionWithSpec(type: Struct, field: Field, listType: Type): FunSpec {
        val name = field.name
        val singularName = field.singularName

        return FunSpec.builder(singularName)
                .addTypeVariable(TypeVariableName("C"))
                .receiver(type.builderClassName)
                .addParameter(listType.specParameterSpec)
                .addStatement("this.%L = this.%L.orEmpty() + %T(spec).translate(context)", name, name, listType.translatorClassName)
                .build()
    }

    private fun includeFunctions(type: Struct): List<FunSpec> {
        return listOf(
                includeFunctionWithContextAndBody(type),
                includeFunctionWithContextAndSpec(type),
                includeFunctionWithBody(type),
                includeFunctionWithSpec(type),
                includeForEachFunctionWithBody(type),
                includeForEachFunctionWithSpec(type)
        )
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
        val fields = type.fields
        return FunSpec.builder("split")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter(otherContextParameterSpec)
                .returns(builderClassNameAlternate)
                .addStatement(
                        "return %T(${(listOf("context") + fields).map { "%L" }.joinToString(",")})",
                        *(listOf(builderClassNameAlternate.rawType, "context") + fields.map { it.name }).toTypedArray()
                )
                .build()
    }

    private fun mergeFunction(type: Struct): FunSpec {
        val fields = type.fields
        return FunSpec.builder("merge")
                .addModifiers(KModifier.PRIVATE)
                .addTypeVariable(TypeVariableName("C2"))
                .addParameter("other", type.otherBuilderClassName)
                .addStatements(
                        fields.map { Statement("this.%L = other.%L", listOf(it.name, it.name)) }
                )
                .build()
    }

    private fun buildFunction(type: Struct): FunSpec {
        val className = type.className
        val fields = type.fields
        return FunSpec.builder("build")
                .addModifiers(KModifier.OVERRIDE)
                .returns(className)
                .addStatement(
                        "return %T(${fields.map {
                            when (it.type) {
                                is OptionType -> "%L"
                                else -> "%L!!"
                            }
                        }.joinToString(",")})",
                        *(listOf(className) + fields.map { it.name }).toTypedArray()
                )
                .build()
    }

}

private object UnionBuilderTypeSpec {

    fun build(type: Union, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return type.shapes.flatMap { StructBuilderTypeSpec.build(it, typeRefs) }
    }

}