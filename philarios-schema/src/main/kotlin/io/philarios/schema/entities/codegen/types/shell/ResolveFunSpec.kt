package io.philarios.schema.entities.codegen.types.shell

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import io.philarios.schema.*
import io.philarios.schema.entities.codegen.util.className
import io.philarios.schema.entities.codegen.util.escapedName
import io.philarios.util.kotlinpoet.Statement
import io.philarios.util.kotlinpoet.addStatements
import io.philarios.util.registry.Registry

internal fun Struct.resolveFun(typeRefs: Map<RefType, Type>, superclass: ClassName?): FunSpec =
        FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec
                        .builder("registry", Registry::class.className)
                        .build())
                .returns(className)
                .addStatements(resolveStatements(typeRefs, superclass))
                .addStatement("return value")
                .build()

private fun Struct.resolveStatements(typeRefs: Map<RefType, Type>, superclass: ClassName?): List<Statement> =
        listOf(
                checkChildrenStatements,
                resolveChildrenStatements(typeRefs),
                createValueStatements(typeRefs),
                getPutIntoRegistryStatements(superclass)
        ).flatten()

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

private fun Struct.createValueStatements(typeRefs: Map<RefType, Type>): List<Statement> =
        listOf(Statement("val value = %T(", listOf(className))) +
                fields
                        .map { it.resolveStatement(typeRefs) }
                        .mapIndexed { index, statement ->
                            // TODO simplify this
                            if (index == fields.size - 1) {
                                statement
                            } else {
                                statement.copy(format = "${statement.format},")
                            }
                        } +
                Statement(")")

private fun Struct.getPutIntoRegistryStatements(superclass: ClassName?): List<Statement> {
    return keyField?.let { keyField ->
        listOf(Statement(
                "registry.put(%T::class, value.%L, value)",
                listOf(className, keyField.escapedName)
        )) + superclass?.let {
            Statement(
                    "registry.put(%T::class, value.%L, value)",
                    listOf(it, keyField.escapedName)
            )
        }
    }.orEmpty().mapNotNull { it }
}

private fun Field.resolveJobStatement(typeRefs: Map<RefType, Type>): Statement? =
        copy(type = type.resolveRefs(typeRefs))
                .resolveJobStatement

private val Field.resolveJobStatement: Statement?
    get() = type.resolveJobStatement?.let { Statement("%>%L?.let{ $it }%<", listOf(escapedName)) }

internal val Type.resolveJobStatement: String?
    get() = when {
        this is OptionType -> type.resolveJobStatement
        this is ListType -> type.resolveJobStatement?.let { "it.forEach { $it }" }
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

private fun Field.resolveStatement(typeRefs: Map<RefType, Type>): Statement =
        copy(type = type.resolveRefs(typeRefs))
                .resolveStatement

private val Field.resolveStatement: Statement
    get() = Statement("%>$nullablePlaceholder.let{ ${type.resolveStatement} }%<", listOf(escapedName))

private val Type.resolveStatement: String
    get() = when {
        this is OptionType -> type.resolveStatement
        this is ListType -> type.resolveStatement.let { "it.map { $it }" }
        this is MapType -> Pair(keyType, valueType)
                .let { Pair(it.first.resolveStatement, it.second.resolveStatement) }
                .let { Pair(it.first.let { "it.key.let { $it }" }, it.second.let { "it.value.let { $it }" }) }
                .let { "Pair(${it.first}, ${it.second})" }
                .let { "it.map { $it }.toMap()" }
        else -> "it.resolve(registry)"
    }

private val Struct.keyField: Field? get() = fields.find { it.key == true }

private val Field.nullablePlaceholder: String
    get() = when (type) {
        is OptionType -> "%L?"
        is ListType -> "%L.orEmpty()"
        is MapType -> "%L.orEmpty()"
        else -> "%L!!"
    }

private fun Field.needsToBeCheckedForNull(): Boolean = when (type) {
    is OptionType -> false
    is ListType -> false
    is MapType -> false
    else -> true
}