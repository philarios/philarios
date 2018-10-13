package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Registry
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements

object ShellTypeSpec {

    fun build(type: Type, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return when (type) {
            is Struct -> StructShellTypeSpec.build(type, typeRefs)
            is Union -> UnionShellTypeSpec.build(type, typeRefs)
            else -> emptyList()
        }
    }

}

private object StructShellTypeSpec {

    fun build(type: Struct, typeRefs: Map<RefType, Type>, superclass: ClassName? = null): List<TypeSpec> {
        return listOf(buildOne(type, typeRefs, superclass)).mapNotNull { it }
    }

    private fun buildOne(type: Struct, typeRefs: Map<RefType, Type>, superclass: ClassName? = null): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildDataClass(type, typeRefs, superclass)
    }

    private fun buildDataClass(type: Struct, typeRefs: Map<RefType, Type>, superclass: ClassName? = null): TypeSpec {

        fun Field.scaffoldType(): TypeName = when (this.type) {
            is Struct -> if (this.type.fields.isEmpty()) {
                this.type.nullableTypeName
            } else {
                this.type.scaffoldClassName.asNullable()
            }
            is Union -> this.type.scaffoldClassName.asNullable()
            is RefType -> this.copy(type = typeRefs[this.type]!!).scaffoldType()
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> this.type.className.asNonNullable().scaffoldClassName.asNullable()
                    is Union -> this.type.className.asNonNullable().scaffoldClassName.asNullable()
                    is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).scaffoldType()
                    else -> this.type.nullableTypeName
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> ParameterizedTypeName.get(List::class.className, listType.scaffoldClassName)
                    is Union -> ParameterizedTypeName.get(List::class.className, listType.scaffoldClassName)
                    is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).scaffoldType()
                    else -> this.type.typeName
                }
            }
            else -> this.type.nullableTypeName
        }

        return TypeSpec.classBuilder(type.shellClassName)
                .addSuperinterface(type.scaffoldClassName)
                .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
                .addModifiers(KModifier.DATA)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameters(type.fields.map { field ->
                            ParameterSpec.builder(field.name, field.scaffoldType())
                                    .defaultValue(when (field.type) {
                                        is ListType -> "emptyList()"
                                        is MapType -> "emptyMap()"
                                        else -> "null"
                                    })
                                    .build()
                        })
                        .build()
                )
                .addProperties(type.fields.map { field ->
                    PropertySpec.builder(field.name, field.scaffoldType())
                            .mutable(true)
                            .initializer(field.name)
                            .build()
                })
                .addFunction(resolveFunction(type, typeRefs))
                .build()
    }

    private fun resolveFunction(type: Struct, typeRefs: Map<RefType, Type>): FunSpec {
        val className = type.className
        val fields = type.fields
        val keyField = fields.find { it.key == true }

        fun Field.unwrappedPlaceholder() = when (this.type) {
            is OptionType -> "%L"
            is ListType -> "%L"
            else -> "%L!!"
        }

        fun Field.resolved(): String = when (this.type) {
            is Struct -> if (this.type.fields.isEmpty()) {
                unwrappedPlaceholder()
            } else {
                "${unwrappedPlaceholder()}.resolve(registry)"
            }
            is Union -> "${unwrappedPlaceholder()}.resolve(registry)"
            is RefType -> this.copy(type = typeRefs[this.type]!!).resolved()
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> "%L?.resolve(registry)"
                    is Union -> "%L?.resolve(registry)"
                    is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).resolved()
                    else -> unwrappedPlaceholder()
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
                    is Union -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
                    is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).resolved()
                    else -> unwrappedPlaceholder()
                }
            }
            else -> unwrappedPlaceholder()
        }

        fun Field.resolveJob(): String? = when (this.type) {
            is Struct -> if (this.type.fields.isEmpty()) {
                null
            } else {
                "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
            }
            is Union -> "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
            is RefType -> this.copy(type = typeRefs[this.type]!!).resolveJob()
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> "launch { %L?.resolve(registry) }"
                    is Union -> "launch { %L?.resolve(registry) }"
                    is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).resolveJob()
                    else -> null
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
                    is Union -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
                    is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).resolveJob()
                    else -> null
                }
            }
            else -> null
        }

        return FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec
                        .builder("registry", Registry::class.className)
                        .build())
                .returns(className)
                .let { builder ->
                    fields
                            .map { Pair(it, it.resolveJob()) }
                            .filter { it.second != null }
                            .map { Statement("\t${it.second!!}", listOf(it.first.name)) }
                            .let {
                                if (it.isEmpty()) {
                                    builder
                                } else {
                                    builder.addStatements(
                                            listOf(Statement("coroutineScope {")) + it + Statement("}") // TODO function import
                                    )
                                }
                            }
                }
                .addStatement(
                        "val value = %T(${fields.map { it.resolved() }.joinToString(",")})",
                        *(listOf(className) + fields.map { it.name }).toTypedArray()
                )
                .let {
                    if (keyField == null) {
                        it
                    } else {
                        it.addStatement(
                                "registry.put(%T::class, ${keyField.unwrappedPlaceholder()}, value)",
                                className, keyField.name
                        )
                    }
                }
                .addStatement("return value")
                .build()
    }

}

private object UnionShellTypeSpec {

    fun build(type: Union, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return listOf(buildSuperclass(type)) + buildShapes(type, typeRefs)
    }

    private fun buildSuperclass(type: Union): TypeSpec {
        return TypeSpec.classBuilder(type.shellClassName)
                .addModifiers(KModifier.SEALED)
                .build()
    }

    private fun buildShapes(type: Union, typeRefs: Map<RefType, Type>): List<TypeSpec> {
        return type.shapes.flatMap { StructShellTypeSpec.build(it, typeRefs, type.shellClassName) }
    }

}
