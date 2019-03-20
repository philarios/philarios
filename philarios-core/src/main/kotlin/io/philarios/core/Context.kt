package io.philarios.core

interface Context<out C> {
    val value: C
    operator fun <T> get(clazz: Class<T>): T
}

suspend fun <C, T : Any> Context<C>.map(scaffolder: Scaffolder<C, T>, registry: Registry = emptyRegistry()): Context<T> {
    return contextOf(value.let { scaffolder.createScaffold(it).resolve(registry) })
}

fun <C, T> Context<C>.map(translator: Translator<C, T>): Context<T> {
    return contextOf(value.let { translator.translate(it) })
}

inline fun <C, T> Context<C>.map(transform: (C) -> T): Context<T> {
    return contextOf(value.let(transform))
}

fun <T> Context<T>.unwrap(): T {
    return value
}

fun emptyContext(): Context<Any?> = EmptyContext

internal object EmptyContext : Context<Nothing?> {
    override fun <T> get(clazz: Class<T>): T {
        TODO("not implemented")
    }

    override val value: Nothing? = null
}

fun <C> contextOf(value: C): Context<C> = ValueContext(value)

internal class ValueContext<out C>(override val value: C) : Context<C> {
    override fun <T> get(clazz: Class<T>): T {
        TODO("not implemented")
    }
}