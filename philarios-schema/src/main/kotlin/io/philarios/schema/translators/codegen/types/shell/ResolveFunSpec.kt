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

internal fun Struct.resolveFun(typeRefs: Map<RefType, Type>): FunSpec =
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

private fun Struct.resolveStatements(typeRefs: Map<RefType, Type>): List<Statement> =
        listOf(
                checkChildrenStatements,
                resolveChildrenStatements(typeRefs),
                listOf(createValueStatement(typeRefs)),
                listOf(putIntoRegistryStatement)
        )
                .flatten()
                .mapNotNull { it }

private val Struct.checkChildrenStatements: List<Statement>
    get() = fields
            .filter { it.needsToBeCheckedForNull() }
            .map {
                Statement(
                        "checkNotNull(%L) { \"%T is missing the %L property\" }",
                        listOf(it.escapedName, className, it.name)
                )
            }

private fun Struct.resolveChildrenStatements(typeRefs: Map<RefType, Type>): List<Statement> =
        fields
                .mapNotNull { it.resolveJobStatement(typeRefs) }
                .takeIf { it.isNotEmpty() }
                ?.let { listOf(Statement("coroutineScope {")) + it + Statement("}") }
                ?: emptyList()

private fun Struct.createValueStatement(typeRefs: Map<RefType, Type>): Statement =
        Statement(
                "val value = %T(${fields.map { it.resolveStatement(typeRefs) }.joinToString(",")})",
                listOf(className) + fields.map { it.escapedName }
        )

private val Struct.putIntoRegistryStatement: Statement?
    get() =
        keyField?.let { keyField ->
            Statement(
                    "registry.put(%T::class, ${keyField.unwrappedPlaceholder}, value)",
                    listOf(className, keyField.escapedName)
            )
        }

private fun Field.resolveJobStatement(typeRefs: Map<RefType, Type>): Statement? =
        copy(type = type.resolveRefs(typeRefs))
                .resolveJobStatement

private val Field.resolveJobStatement: Statement?
    get() = type.resolveJobStatement?.let { Statement("%L.let{ $it }", listOf(escapedName)) }

internal val Type.resolveJobStatement: String?
    get() = when {
        this is OptionType -> type.resolveJobStatement?.let { "it?.let { $it }" }
        this is ListType -> type.resolveJobStatement?.let { "it?.forEach { $it }" }
        this is MapType -> Pair(keyType, valueType)
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
                ?.let { "it.forEach { $it }" }
        this is Struct && fields.isEmpty() -> null
        this is Struct -> "launch { it.resolve(registry) }"
        this is Union -> "launch { it.resolve(registry) }"
        else -> null
    }

private fun Field.resolveStatement(typeRefs: Map<RefType, Type>): String =
        copy(type = type.resolveRefs(typeRefs))
                .resolveStatement

private val Field.resolveStatement: String
    get() = "%L.let{ ${type.resolveStatement} }"

private val Type.resolveStatement: String
    get() = when {
        this is OptionType -> type.resolveStatement.let { "it?.let { $it }" }
        this is ListType -> type.resolveStatement.let { "it?.map { $it }" }
        this is MapType -> Pair(keyType, valueType)
                .let { Pair(it.first.resolveStatement, it.second.resolveStatement) }
                .let { Pair(it.first.let { "it.key.let { $it }" }, it.second.let { "it.value.let { $it }" }) }
                .let { "Pair(${it.first}, ${it.second})" }
                .let { "it.map { $it }.toMap()" }
        this is Struct && fields.isEmpty() -> "it"
        this is Struct -> "it.resolve(registry)"
        this is Union -> "it.resolve(registry)"
        else -> "it"
    }

private val Struct.keyField: Field? get() = fields.find { it.key == true }

private val Field.unwrappedPlaceholder: String
    get() = when (type) {
        is OptionType -> "%L"
        is ListType -> "%L"
        is MapType -> "%L"
        else -> "%L!!"
    }

private fun Field.needsToBeCheckedForNull(): Boolean = when (type) {
    is OptionType -> false
    is ListType -> false
    is MapType -> false
    else -> true
}