package io.philarios.core

@DslMarker
annotation class DslBuilder

interface Spec<B> {
    val body: B.() -> Unit
}
typealias Body<B> = B.() -> Unit

@DslBuilder
interface Builder<S : Spec<B>, B : Builder<S, B>> {
    val genericContext: GenericContext
        get() = NopGenericContext

    operator fun S.unaryPlus() = plus(this)

    operator fun Body<B>.unaryPlus() = plus(this)

    operator fun plus(spec: S) {
        plus(spec.body)
    }

    operator fun plus(body: Body<B>) {
        this.let { it as? B }?.let(body)
    }
}
