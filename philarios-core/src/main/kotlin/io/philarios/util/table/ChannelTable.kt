package io.philarios.util.table

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

internal class ChannelTable<K, V> : Table<K, V>, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    private val receiveJob: Job
    private val messageChannel = Channel<Message<K, V>>()

    sealed class Message<K, V> {
        data class Send<K, V>(val key: K, val value: V) : Message<K, V>()
        data class Receive<K, V>(val key: K, val deferred: CompletableDeferred<Result<V>>) : Message<K, V>()
    }

    init {
        receiveJob = launch {
            val valueMap = mutableMapOf<K, V>()
            val deferredMap = mutableMapOf<K, List<CompletableDeferred<Result<V>>>>()
            try {
                for (message in messageChannel) {
                    when (message) {
                        is Message.Send<K, V> -> {
                            valueMap[message.key] = message.value
                            deferredMap[message.key]?.forEach { it.complete(Result.Value(message.value)) }
                            deferredMap - message.key
                        }
                        is Message.Receive<K, V> -> {
                            if (valueMap.containsKey(message.key)) {
                                message.deferred.complete(Result.Value(valueMap[message.key]!!))
                            } else {
                                deferredMap[message.key] = deferredMap[message.key].orEmpty() + message.deferred
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                deferredMap.values
                        .flatten()
                        .forEach { it.complete(Result.Error(e)) }
            }
        }
    }

    override suspend fun send(key: K, value: V) {
        messageChannel.send(Message.Send(key, value))
    }

    override suspend fun receive(key: K): Result<V> {
        val deferred = CompletableDeferred<Result<V>>()
        messageChannel.send(Message.Receive(key, deferred))
        return deferred.await()
    }

    override fun close() {
        receiveJob.cancel()
        messageChannel.close()
    }

}