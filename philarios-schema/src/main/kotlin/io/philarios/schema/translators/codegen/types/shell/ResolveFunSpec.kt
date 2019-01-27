package io.philarios.schema.translators.codegen.types.shell

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import io.philarios.core.Registry
import io.philarios.schema.*
import io.philarios.schema.translators.codegen.util.className
import io.philarios.schema.translators.codegen.util.escapedName
import io.philarios.schema.util.kotlinpoet.Statement
import io.philarios.schema.util.kotlinpoet.addStatements

internal fun Struct.resolveFun(typeRefs: Map<RefType, Type>) =
        FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec
                        .builder("registry", Registry::class.className)
                        .build())
                .returns(className)
                .addStatements(resolveStatements(typeRefs))
                .addStatement("return value")
                .build()

private fun Struct.resolveStatements(typeRefs: Map<RefType, Type>) =
        listOf(
                checkChildrenStatements,
                resolveChildrenStatements(typeRefs),
                listOf(createValueStatement(typeRefs)),
                listOf(putIntoRegistryStatement)
        )
                .flatten()
                .mapNotNull { it }

private val Struct.checkChildrenStatements
    get() = fields
            .filter { it.needsToBeCheckedForNull() }
            .map {
                Statement(
                        "checkNotNull(%L) { \"%T is missing the %L property\" }",
                        listOf(it.escapedName, className, it.name)
                )
            }

private fun Struct.resolveChildrenStatements(typeRefs: Map<RefType, Type>) = fields
        .map { Pair(it, it.resolveJobStatement(typeRefs)) }
        .filter { it.second != null }
        .map { Statement("\t${it.second!!}", listOf(it.first.escapedName)) }
        .takeIf { it.isNotEmpty() }
        ?.let { listOf(Statement("coroutineScope {")) + it + Statement("}") }
        ?: emptyList()

private fun Struct.createValueStatement(typeRefs: Map<RefType, Type>) =
        Statement(
                "val value = %T(${fields.map { it.resolveStatement(typeRefs) }.joinToString(",")})",
                listOf(className) + fields.map { it.escapedName }
        )

private val Struct.putIntoRegistryStatement
    get() =
        keyField?.let { keyField ->
            Statement(
                    "registry.put(%T::class, ${keyField.unwrappedPlaceholder()}, value)",
                    listOf(className, keyField.escapedName)
            )
        }

private fun Field.resolveJobStatement(typeRefs: Map<RefType, Type>): String? = when (this.type) {
    is Struct -> if (this.type.fields.isEmpty()) {
        null
    } else {
        "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
    }
    is Union -> "launch { ${unwrappedPlaceholder()}.resolve(registry) }"
    is RefType -> this.copy(type = typeRefs[this.type]!!).resolveJobStatement(typeRefs)
    is OptionType -> {
        val optionType = this.type.type
        when (optionType) {
            is Struct -> "launch { %L?.resolve(registry) }"
            is Union -> "launch { %L?.resolve(registry) }"
            is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).resolveJobStatement(typeRefs)
            else -> null
        }
    }
    is ListType -> {
        val listType = this.type.type
        when (listType) {
            is Struct -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
            is Union -> "${unwrappedPlaceholder()}.forEach { launch { it.resolve(registry) } }"
            is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).resolveJobStatement(typeRefs)
            else -> null
        }
    }
    else -> null
}

private fun Field.resolveStatement(typeRefs: Map<RefType, Type>): String = when (this.type) {
    is Struct -> if (this.type.fields.isEmpty()) {
        unwrappedPlaceholder()
    } else {
        "${unwrappedPlaceholder()}.resolve(registry)"
    }
    is Union -> "${unwrappedPlaceholder()}.resolve(registry)"
    is RefType -> this.copy(type = typeRefs[this.type]!!).resolveStatement(typeRefs)
    is OptionType -> {
        val optionType = this.type.type
        when (optionType) {
            is Struct -> "%L?.resolve(registry)"
            is Union -> "%L?.resolve(registry)"
            is RefType -> this.copy(type = OptionType(typeRefs[optionType]!!)).resolveStatement(typeRefs)
            else -> unwrappedPlaceholder()
        }
    }
    is ListType -> {
        val listType = this.type.type
        when (listType) {
            is Struct -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
            is Union -> "${unwrappedPlaceholder()}.map { it.resolve(registry) }"
            is RefType -> this.copy(type = ListType(typeRefs[listType]!!)).resolveStatement(typeRefs)
            else -> unwrappedPlaceholder()
        }
    }
    else -> unwrappedPlaceholder()
}

private val Struct.keyField get() = fields.find { it.key == true }

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