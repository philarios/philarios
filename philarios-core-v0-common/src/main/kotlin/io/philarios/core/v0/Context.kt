package io.philarios.core.v0

interface Context<out C> {
    val value: C
}

suspend fun <T, R> Context<T>.map(translator: Translator<T, R>): Context<R> {
    return contextOf(value.let { translator.translate(it) })
}

suspend fun <T, R : Any> Context<T>.map(template: Template<T, R>, registry: Registry = io.philarios.core.v0.emptyRegistry()): Context<R> {
    return contextOf(value.let { template.connect(it).resolve(registry) })
}

inline fun <T, R> Context<T>.map(transform: (T) -> R): Context<R> {
    return contextOf(value.let(transform))
}

suspend fun <T, R> Context<T>.unwrap(translator: Translator<T, R>): R {
    return value.let { translator.translate(it) }
}

inline fun <T, R> Context<T>.unwrap(block: (T) -> R): R {
    return value.let(block)
}

fun <T> Context<T>.unwrap(): T {
    return value
}

fun emptyContext(): Context<Nothing?> = EmptyContext

internal object EmptyContext : Context<Nothing?> {
    override val value: Nothing? = null
}

fun <C> contextOf(value: C): Context<C> = ValueContext(value)

internal class ValueContext<out C>(override val value: C) : Context<C>