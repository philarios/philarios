package io.philarios.util.v0

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlin.coroutines.experimental.CoroutineContext

internal class ChannelTable<K, V> : Table<K, V>, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + receiveJob

    private val receiveJob: Job
    private val messageChannel = Channel<Message<K, V>>()

    sealed class Message<K, V>{
        data class Send<K, V>(val key: K, val value: V): Message<K, V>()
        data class Receive<K, V>(val key: K, val deferred: CompletableDeferred<V>): Message<K, V>()
    }

    init {
        receiveJob = launch {
            val valueMap = mutableMapOf<K, V>()
            val deferredMap = mutableMapOf<K, List<CompletableDeferred<V>>>()
            for (message in messageChannel) {
                when (message) {
                    is Message.Send<K, V> -> {
                        valueMap[message.key] = message.value
                        deferredMap[message.key]?.forEach { it.complete(message.value) }
                        deferredMap - message.key
                    }
                    is Message.Receive<K, V> -> {
                        if (valueMap.containsKey(message.key)) {
                            message.deferred.complete(valueMap[message.key]!!)
                        } else {
                            deferredMap[message.key] = deferredMap[message.key].orEmpty() + message.deferred
                        }
                    }
                }
            }
        }
    }

    override suspend fun send(key: K, value: V) {
        messageChannel.send(Message.Send(key, value))
    }

    override suspend fun receive(key: K): V {
        val deferred = CompletableDeferred<V>()
        messageChannel.send(Message.Receive(key, deferred))
        return deferred.await()
    }

    override fun close() {
        messageChannel.close()
    }

}