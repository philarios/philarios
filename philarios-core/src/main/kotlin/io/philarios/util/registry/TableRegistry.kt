package io.philarios.util.registry

import io.philarios.util.table.Table
import io.philarios.util.table.Result
import io.philarios.util.table.emptyTable
import kotlin.reflect.KClass

internal class TableRegistry : Registry {

    data class Key(val clazz: KClass<*>, val key: String)

    private var table: Table<Key, Any> = emptyTable()

    override suspend fun <T : Any> get(clazz: KClass<T>, key: String): T? {
        val value = table.receive(Key(clazz, key)) as? Result.Value<T>
        return value?.value
    }

    override suspend fun <T : Any> put(clazz: KClass<T>, key: String, value: T) {
        table.send(Key(clazz, key), value)
    }

    override fun close() {
        table.close()
    }

}