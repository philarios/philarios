package io.philarios.util.registry

import kotlin.reflect.KClass

internal class MapRegistry : Registry {

    data class Key(val clazz: KClass<*>, val key: String)

    private var map: MutableMap<Key, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> get(clazz: KClass<T>, key: String): T? {
        return map[Key(clazz, key)] as? T
    }

    override suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T) {
        map[Key(clazz, key)] = value
    }

    override fun close() {
        // TODO implement this method or just delete this class
    }

}