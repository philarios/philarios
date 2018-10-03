package io.philarios.core.v0

import io.philarios.util.v0.Table
import io.philarios.util.v0.emptyTable
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch
import kotlin.reflect.KClass

@DslMarker
annotation class DslBuilder

interface Builder<out T : Any> { // TODO rename
    fun scaffold(): Scaffold<T>
}

interface Spec<in B : Builder<T>, out T : Any> { // TODO delete
    fun B.body()
}

interface Translator<in C, out T> {
    suspend fun translate(context: C): T
}

interface Scaffold<out T : Any> {
    suspend fun resolve(registry: Registry): T
}

interface Shell<out T : Any> : Scaffold<T> {
    fun children(): List<Scaffold<*>>
}

suspend fun resolveAll(scaffolds: List<Scaffold<*>>, registry: Registry) {
    coroutineScope {
        scaffolds.map { launch { it.resolve(registry) } }.forEach { it.join() }
    }
}

class ShellScaffold<out T : Any>(private val shell: Shell<T>) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T {
        resolveAll(shell.children(), registry)
        return shell.resolve(registry)
    }
}

interface Context<out C> {
    val value: C
}

suspend fun <T, R> Context<T>.map(translator: Translator<T, R>): Context<R> {
    return ValueContext(value.let { translator.translate(it) })
}

inline fun <T, R> Context<T>.map(transform: (T) -> R): Context<R> {
    return ValueContext(value.let(transform))
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

fun emptyContext() = EmptyContext

object EmptyContext : Context<Nothing?> {
    override val value: Nothing? = null
}

fun <C> contextOf(value: C) = ValueContext(value)

class ValueContext<out C>(override val value: C) : Context<C>

class BuilderSpecTranslator<in C, B : Builder<T>, out T : Any>(
        private val builder: B,
        private val spec: Spec<B, T>
) : Translator<C, T> {
    override suspend fun translate(context: C): T {
        val registry = DelegatingRegistry()
        return with(builder) {
            with(spec) {
                body()
                scaffold().resolve(registry)
            }
        }
    }
}

interface Registry {
    suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T)
    suspend fun <T : Any> get(clazz: KClass<T>, key: String): T?
}

class DelegatingRegistry : Registry {

    data class Key(val clazz: KClass<*>, val key: String)

    private var map: MutableMap<Key, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> get(clazz: KClass<T>, key: String): T? {
        return map[Key(clazz, key)] as? T
    }

    override suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T) {
        map[Key(clazz, key)] = value
    }

}

class TableRegistry : Registry {

    data class Key(val clazz: KClass<*>, val key: String)

    private var table: Table<Key, Any> = emptyTable()

    override suspend fun <T : Any> get(clazz: KClass<T>, key: String): T? {
        return table.receive(Key(clazz, key)) as? T
    }

    override suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T) {
        table.send(Key(clazz, key), value)
    }

}

fun emptyRegistry() = TableRegistry()

class RegistryRef<T : Any>(
        private val clazz: KClass<T>,
        private val key: String
) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T {
        return registry.get(clazz, key)!!
    }
}

inline fun <reified T : Any> ref(key: String): RegistryRef<T> {
    return RegistryRef(T::class, key)
}

class Wrapper<T : Any>(
        private val value: T
) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T = value
}