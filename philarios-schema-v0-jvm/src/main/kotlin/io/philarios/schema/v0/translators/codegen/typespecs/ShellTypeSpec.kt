package io.philarios.schema.v0.translators.codegen.typespecs

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Registry
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements

object ShellTypeSpec {

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> StructShellTypeSpec.build(type)
            is Union -> UnionShellTypeSpec.build(type)
            else -> emptyList()
        }
    }

}

private object StructShellTypeSpec {

    fun build(type: Struct, superclass: ClassName? = null): List<TypeSpec> {
        return listOf(buildOne(type, superclass))
    }

    private fun buildOne(type: Struct, superclass: ClassName? = null): TypeSpec {
        if (type.fields.isEmpty()) {
            return buildObject(type, superclass)
        }
        return buildDataClass(type, superclass)
    }

    private fun buildObject(type: Struct, superclass: ClassName? = null): TypeSpec {
        return TypeSpec.objectBuilder(type.shellClassName)
                .let { builder -> superclass?.let { builder.superclass(it) } ?: builder }
                .build()
    }

    private fun buildDataClass(type: Struct, superclass: ClassName? = null): TypeSpec {

        fun Field.scaffoldType() = when (this.type) {
            is Struct -> this.type.parameterizedScaffoldClassName.asNullable()
            is Union -> this.type.parameterizedScaffoldClassName.asNullable()
            is RefType -> this.type.parameterizedScaffoldClassName.asNullable()
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> this.type.className.asNonNullable().parameterizedScaffoldClassName.asNullable()
                    is Union -> this.type.className.asNonNullable().parameterizedScaffoldClassName.asNullable()
                    is RefType -> this.type.className.asNonNullable().parameterizedScaffoldClassName.asNullable()
                    else -> this.type.nullableTypeName
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> ParameterizedTypeName.get(ClassName.bestGuess("kotlin.collections.List"), listType.parameterizedScaffoldClassName)
                    is Union -> ParameterizedTypeName.get(ClassName.bestGuess("kotlin.collections.List"), listType.parameterizedScaffoldClassName)
                    is RefType -> ParameterizedTypeName.get(ClassName.bestGuess("kotlin.collections.List"), listType.parameterizedScaffoldClassName)
                    else -> this.type.typeName
                }
            }
            else -> this.type.nullableTypeName
        }

        return TypeSpec.classBuilder(type.shellClassName)
                .addSuperinterface(type.parameterizedScaffoldClassName)
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
                .addFunction(resolveFunction(type))
                .build()
    }

    private fun resolveFunction(type: Struct): FunSpec {
        val className = type.className
        val fields = type.fields
        val keyField = fields.find { it.key == true }

        fun Field.unwrappedPlaceholder() = when (this.type) {
            is OptionType -> "%L"
            is ListType -> "%L"
            else -> "%L!!"
        }

        fun Field.resolved() = when (this.type) {
            is Struct -> "${unwrappedPlaceholder()}.resolve(registry)"
            is Union -> "${unwrappedPlaceholder()}.resolve(registry)"
            is RefType -> "${unwrappedPlaceholder()}.resolve(registry)"
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> "%L?.resolve(registry)"
                    is Union -> "%L?.resolve(registry)"
                    is RefType -> "%L?.resolve(registry)"
                    else -> unwrappedPlaceholder()
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
                    is Union -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
                    is RefType -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
                    else -> unwrappedPlaceholder()
                }
            }
            else -> unwrappedPlaceholder()
        }

        fun Field.resolveJob() = when (this.type) {
            is Struct -> "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
            is Union -> "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
            is RefType -> "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
            is OptionType -> {
                val optionType = this.type.type
                when (optionType) {
                    is Struct -> "launch { %L?.resolve(registry) }"
                    is Union -> "launch { %L?.resolve(registry) }"
                    is RefType -> "launch { %L?.resolve(registry) }"
                    else -> null
                }
            }
            is ListType -> {
                val listType = this.type.type
                when (listType) {
                    is Struct -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
                    is Union -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
                    is RefType -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
                    else -> null
                }
            }
            else -> null
        }

        return FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec
                        .builder("registry", ClassName.bestGuess(Registry::class.qualifiedName!!))
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
                                            listOf(Statement("kotlinx.coroutines.experimental.coroutineScope {")) + it + Statement("}") // TODO function import
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

    fun build(type: Union): List<TypeSpec> {
        return listOf(buildSuperclass(type)) + buildShapes(type)
    }

    private fun buildSuperclass(type: Union): TypeSpec {
        return TypeSpec.classBuilder(type.shellClassName)
                .addModifiers(KModifier.SEALED)
                .build()
    }

    private fun buildShapes(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { StructShellTypeSpec.build(it, type.shellClassName) }
    }

}
