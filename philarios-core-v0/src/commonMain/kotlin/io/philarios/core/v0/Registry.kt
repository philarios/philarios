package io.philarios.core.v0

import kotlin.reflect.KClass

interface Registry {
    suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T)
    suspend fun <T : Any> get(clazz: KClass<T>, key: String): T?
}

fun emptyRegistry(): Registry = TableRegistry()