package io.philarios.util.v0

interface Table<K, V> {
    suspend fun send(key: K, value: V)
    suspend fun receive(key: K): V
    fun close()
}

fun <K, V> emptyTable(): Table<K, V> = ChannelTable()
