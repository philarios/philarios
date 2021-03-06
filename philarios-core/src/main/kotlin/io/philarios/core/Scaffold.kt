package io.philarios.core

import io.philarios.util.registry.Registry
import io.philarios.util.registry.emptyRegistry
import kotlin.reflect.KClass

interface Scaffolder<out T : Any> {
    fun createScaffold(): Scaffold<T>
}

suspend fun <T : Any> Scaffolder<T>.resolve(): T = resolve(emptyRegistry())

suspend fun <T : Any> Scaffolder<T>.resolve(registry: Registry): T = createScaffold().resolve(registry)

interface Scaffold<out T : Any> {
    suspend fun resolve(registry: Registry): T
}

suspend fun <T : Any> Scaffold<T>.resolve(): T = resolve(emptyRegistry())

class ValueScaffold<T : Any>(private val value: T) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T = value
}

inline fun <reified T : Any> RefScaffold(key: String) = RefScaffold(T::class, key)

class RefScaffold<T : Any>(private val clazz: KClass<T>, private val key: String) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T {
        return registry.get(clazz, key)!!
    }
}
