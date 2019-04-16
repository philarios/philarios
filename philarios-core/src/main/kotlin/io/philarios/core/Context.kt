package io.philarios.core

import io.philarios.util.registry.Registry
import io.philarios.util.registry.emptyRegistry

interface Context<out C> {
    val value: C
}

fun emptyContext(): Context<Any?> = ValueContext(null)

fun <C> contextOf(value: C): Context<C> = ValueContext(value)

internal class ValueContext<out C>(override val value: C) : Context<C>

// TODO this is not staying like this (probably)
suspend fun <C, T : Any> Context<C>.mapScaffolder(
        registry: Registry = emptyRegistry(),
        scaffolder: (C) -> Scaffolder<T>
): Context<T> {
    return contextOf(value.let {
        scaffolder(it)
                .createScaffold()
                .resolve(registry)
    })
}

inline fun <C, T> Context<C>.map(transform: (C) -> T): Context<T> {
    return contextOf(value.let(transform))
}

fun <T> Context<T>.unwrap(): T {
    return value
}
