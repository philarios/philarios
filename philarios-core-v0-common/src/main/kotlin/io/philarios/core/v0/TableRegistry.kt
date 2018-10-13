package io.philarios.core.v0

import io.philarios.util.v0.Table
import io.philarios.util.v0.emptyTable
import kotlin.reflect.KClass

internal class TableRegistry : Registry {

    data class Key(val clazz: KClass<*>, val key: String)

    private var table: Table<Key, Any> = emptyTable()

    override suspend fun <T : Any> get(clazz: KClass<T>, key: String): T? {
        return table.receive(Key(clazz, key)) as? T
    }

    override suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T) {
        table.send(Key(clazz, key), value)
    }

}