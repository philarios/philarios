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

    fun apply(spec: S) {
        val body = spec.body as? Builder<S, B>.() -> Unit ?: return
        apply(body)
    }

}
