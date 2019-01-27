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

private fun Struct.resolveChildrenStatements(typeRefs: Map<RefType, Type>) =
        fields
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
                    "registry.put(%T::class, ${keyField.unwrappedPlaceholder}, value)",
                    listOf(className, keyField.escapedName)
            )
        }

private fun Field.resolveJobStatement(typeRefs: Map<RefType, Type>): String? =
        copy(type = type.resolveRefs(typeRefs))
                .resolveJobStatement

private val Field.resolveJobStatement
    get(): String? = when (type) {
        is OptionType -> type.type.resolveJobStatement?.let { "%L?.let { $it }" }
        is ListType -> type.type.resolveJobStatement?.let { "%L.forEach { $it }" }
        is MapType -> Pair(type.keyType, type.valueType)
                .let { Pair(it.first.resolveJobStatement, it.second.resolveJobStatement) }
                .let {
                    Pair(
                            it.first?.let { "it.key.let { $it }" },
                            it.second?.let { "it.value.let { $it }" }
                    )
                }
                .let { listOf(it.first, it.second) }
                .filterNotNull()
                .joinToString("; ")
                .takeIf { it.isNotBlank() }
                ?.let { "%L.forEach { $it }" }
        else -> type.resolveJobStatement?.let { "$unwrappedPlaceholder.let { $it }" }
    }

private fun Field.resolveStatement(typeRefs: Map<RefType, Type>): String =
        copy(type = type.resolveRefs(typeRefs))
                .resolveStatement

private val Field.resolveStatement
    get(): String = when (type) {
        is OptionType -> type.type.resolveStatement?.let { "%L?.let { $it }" }
        is ListType -> type.type.resolveStatement?.let { "%L.map { $it }" }
        is MapType -> Pair(type.keyType, type.valueType)
                .let { Pair(it.first.resolveStatement, it.second.resolveStatement) }
                .let {
                    Pair(
                            it.first?.let { "it.key.let { $it }" } ?: "it.key",
                            it.second?.let { "it.value.let { $it }" } ?: "it.key"
                    )
                }
                .let { "Pair(${it.first}, ${it.second})" }
                .let { "%L.map { $it }.toMap()" }
        else -> type.resolveStatement?.let { "$unwrappedPlaceholder.let { $it }" }
    } ?: unwrappedPlaceholder

private val Type.resolveJobStatement
    get() = resolveStatement?.let { "launch { $it }" }

private val Type.resolveStatement
    get() = when {
        this is Struct && fields.isEmpty() -> null
        this is Struct -> "it.resolve(registry)"
        this is Union -> "it.resolve(registry)"
        else -> null
    }

private val Struct.keyField get() = fields.find { it.key == true }

private val Field.unwrappedPlaceholder
    get() = when (type) {
        is OptionType -> "%L"
        is ListType -> "%L"
        is MapType -> "%L"
        else -> "%L!!"
    }

private fun Field.needsToBeCheckedForNull() = when (type) {
    is OptionType -> false
    is ListType -> false
    is MapType -> false
    else -> true
}