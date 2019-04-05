package io.philarios.core

import io.philarios.util.registry.Registry
import kotlin.reflect.KClass

interface Scaffolder<in C, out T : Any> {
    fun createScaffold(context: C): Scaffold<T>
}

interface Scaffold<out T : Any> {
    suspend fun resolve(registry: Registry): T
}

class Wrapper<T : Any>(private val value: T) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T = value
}

inline fun <reified T : Any> Deferred(key: String) = Deferred(T::class, key)

class Deferred<T : Any>(private val clazz: KClass<T>, private val key: String) : Scaffold<T> {
    override suspend fun resolve(registry: Registry): T {
        return registry.get(clazz, key)!!
    }
}
