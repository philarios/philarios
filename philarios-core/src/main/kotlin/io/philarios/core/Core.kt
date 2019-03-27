package io.philarios.core

@DslMarker
annotation class DslBuilder

interface Translator<in C, out T> {
    fun translate(context: C): T
}
