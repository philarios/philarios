package io.philarios.core.v0

@DslMarker
annotation class DslBuilder

interface Spec<in B : Builder<T>, out T : Any> { // TODO delete
    fun B.body()
}

interface Template<in C, out T : Any> {
    fun connect(context: C): Scaffold<T>
}

interface Builder<out T : Any> { // TODO rename
    fun scaffold(): Scaffold<T>
}

interface Translator<in C, out T> {
    suspend fun translate(context: C): T
}
