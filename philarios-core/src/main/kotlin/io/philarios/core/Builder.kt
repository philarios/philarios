package io.philarios.core

@DslMarker
annotation class DslBuilder

interface Spec<B> {
    val body: B.() -> Unit
}

interface Builder<S : Spec<B>, B : Builder<S, B>> {
    val genericContext: GenericContext
        get() = NopGenericContext

    operator fun plus(spec: S) {
        plus(spec.body)
    }

    operator fun plus(body: B.() -> Unit) {
        this.let { it as? B }?.let(body)
    }
}
