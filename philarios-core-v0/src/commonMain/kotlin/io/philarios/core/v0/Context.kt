package io.philarios.core.v0

interface Context<out C> {
    val value: C
}

suspend fun <T, R : Any> Context<T>.map(spec: Spec<T, R>, registry: Registry = io.philarios.core.v0.emptyRegistry()): Context<R> {
    return contextOf(value.let { spec.connect(it).resolve(registry) })
}

fun <T, R> Context<T>.map(translator: Translator<T, R>): Context<R> {
    return contextOf(value.let { translator.translate(it) })
}

inline fun <T, R> Context<T>.map(transform: (T) -> R): Context<R> {
    return contextOf(value.let(transform))
}

fun <T> Context<T>.unwrap(): T {
    return value
}

fun emptyContext(): Context<Any?> = EmptyContext

internal object EmptyContext : Context<Nothing?> {
    override val value: Nothing? = null
}

fun <C> contextOf(value: C): Context<C> = ValueContext(value)

internal class ValueContext<out C>(override val value: C) : Context<C>