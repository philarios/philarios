package io.philarios.util.table

import java.lang.Exception

interface Table<K, V> {
    suspend fun send(key: K, value: V)
    suspend fun receive(key: K): Result<V>
    fun close()
}

sealed class Result<V> {
    data class Value<V>(val value: V): Result<V>()
    data class Error<V>(val exception: Exception): Result<V>()
}

fun <K, V> emptyTable(): Table<K, V> = ChannelTable()
