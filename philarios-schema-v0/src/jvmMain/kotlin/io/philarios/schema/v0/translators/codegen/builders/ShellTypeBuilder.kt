package io.philarios.schema.v0.translators.codegen.builders

import com.squareup.kotlinpoet.*
import io.philarios.core.v0.Registry
import io.philarios.schema.v0.*
import io.philarios.schema.v0.translators.codegen.*
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.Statement
import io.philarios.schema.v0.translators.codegen.util.kotlinpoet.addStatements

class ShellTypeBuilder(typeRefs: Map<RefType, Type>) {

    private val structBuilder = StructShellTypeBuilder(typeRefs)
    private val unionBuilder = UnionShellTypeBuilder(typeRefs)

    fun build(type: Type): List<TypeSpec> {
        return when (type) {
            is Struct -> structBuilder.build(type)
            is Union -> unionBuilder.build(type)
            else -> emptyList()
        }
    }

}

private class StructShellTypeBuilder(private val typeRefs: Map<RefType, Type>) {

    private val resolveBuilder = ResolveFunSpecBuilder(typeRefs)

    fun build(type: Struct, superclass: ClassName? = null): List<TypeSpec> {
        return listOf(buildOne(type, superclass)).mapNotNull { it }
    }

    private fun buildOne(type: Struct, superclass: ClassName? = null): TypeSpec? {
        if (type.fields.isEmpty()) {
            return null
        }
        return buildDataClass(type, superclass)
    }

    private fun buildDataClass(type: Struct, superclass: ClassName? = null): TypeSpec {

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
                .addModifiers(KModifier.INTERNAL)
                .addModifiers(KModifier.DATA)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameters(type.fields.map { field ->
                            ParameterSpec.builder(field.escapedName, field.scaffoldType())
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
                    PropertySpec.builder(field.escapedName, field.scaffoldType())
                            .mutable(true)
                            .initializer(field.escapedName)
                            .build()
                })
                .addFunction(resolveBuilder.build(type))
                .build()
    }

    private class ResolveFunSpecBuilder(private val typeRefs: Map<RefType, Type>) {
        fun build(type: Struct): FunSpec {
            val className = type.className

            return FunSpec.builder("resolve")
                    .addModifiers(KModifier.OVERRIDE)
                    .addModifiers(KModifier.SUSPEND)
                    .addParameter(ParameterSpec
                            .builder("registry", Registry::class.className)
                            .build())
                    .returns(className)
                    .checkChildren(type)
                    .resolveChildren(type)
                    .createValue(type)
                    .putIntoRegistry(type)
                    .addStatement("return value")
                    .build()
        }

        private fun FunSpec.Builder.checkChildren(type: Struct): FunSpec.Builder {
            val className = type.className
            return type.fields
                    .filter { it.needsToBeCheckedForNull() }
                    .map { Statement("checkNotNull(%L) { \"%T is missing the %L property\" }", listOf(it.escapedName, className, it.name)) }
                    .let {
                        if (it.isEmpty()) {
                            this
                        } else {
                            this.addStatements(it)
                        }
                    }
        }

        private fun FunSpec.Builder.resolveChildren(type: Struct): FunSpec.Builder {
            return type.fields
                    .map { Pair(it, it.resolveJob()) }
                    .filter { it.second != null }
                    .map { Statement("\t${it.second!!}", listOf(it.first.escapedName)) }
                    .let {
                        if (it.isEmpty()) {
                            this
                        } else {
                            this.addStatements(
                                    listOf(Statement("coroutineScope {")) + it + Statement("}")
                            )
                        }
                    }
        }

        private fun FunSpec.Builder.createValue(type: Struct): FunSpec.Builder {
            val className = type.className
            val fields = type.fields
            return addStatement(
                    "val value = %T(${fields.map { it.resolved() }.joinToString(",")})",
                    *(listOf(className) + fields.map { it.escapedName }).toTypedArray()
            )
        }

        private fun FunSpec.Builder.putIntoRegistry(type: Struct): FunSpec.Builder {
            val className = type.className
            val fields = type.fields
            val keyField = fields.find { it.key == true }

            return if (keyField == null) {
                this
            } else {
                addStatement(
                        "registry.put(%T::class, ${keyField.unwrappedPlaceholder()}, value)",
                        className, keyField.escapedName
                )
            }
        }

        private fun Field.resolveJob(): String? = when (this.type) {
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

        private fun Field.resolved(): String = when (this.type) {
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

        // TODO MapType should be included here
        private fun Field.unwrappedPlaceholder() = when (this.type) {
            is OptionType -> "%L"
            is ListType -> "%L"
            else -> "%L!!"
        }

        private fun Field.needsToBeCheckedForNull() = when (this.type) {
            is OptionType -> false
            is ListType -> false
            is MapType -> false
            else -> true
        }
    }

}

private class UnionShellTypeBuilder(typeRefs: Map<RefType, Type>) {

    private val structBuilder = StructShellTypeBuilder(typeRefs)

    fun build(type: Union): List<TypeSpec> {
        return listOf(buildSuperclass(type)) + buildShapes(type)
    }

    private fun buildSuperclass(type: Union): TypeSpec {
        return TypeSpec.classBuilder(type.shellClassName)
                .addModifiers(KModifier.INTERNAL)
                .addModifiers(KModifier.SEALED)
                .build()
    }

    private fun buildShapes(type: Union): List<TypeSpec> {
        return type.shapes.flatMap { structBuilder.build(it, type.shellClassName) }
    }

}
