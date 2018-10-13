package io.philarios.core.v0

@DslMarker
annotation class DslBuilder

interface Spec<in C, out T : Any> {
    fun connect(context: C): Scaffold<T>
}

interface Translator<in C, out T> {
    fun translate(context: C): T
}
