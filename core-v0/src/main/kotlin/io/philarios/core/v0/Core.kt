package io.philarios.core.v0

annotation class DslSpec

@DslMarker
annotation class DslBuilder

interface BuilderFactory<in C, out B : Builder<T>, out T> {
    fun create(context: C): B
}

interface Builder<out T> {
    fun build(): T
}

interface Spec<in B : Builder<T>, out T> {
    fun B.body()
}

interface Translator<in C, out T> {
    fun translate(context: C): T
}

class BuilderFactorySpecTranslator<in C, B : Builder<T>, out T>(
        private val builderFactory: BuilderFactory<C, B, T>,
        private val spec: Spec<B, T>
) : Translator<C, T> {
    override fun translate(context: C): T {
        val builder = builderFactory.create(context)
        val translator = BuilderSpecTranslator<C, B, T>(builder, spec)
        return translator.translate(context)
    }
}

class BuilderSpecTranslator<in C, B : Builder<T>, out T>(
        private val builder: B,
        private val spec: Spec<B, T>
) : Translator<C, T> {
    override fun translate(context: C): T {
        return with(builder) {
            kotlin.with(spec) {
                body()
                build()
            }
        }
    }
}

inline fun <C, T> C.translate(translate: (C) -> T): T {
    return translate(this)
}

fun <C, T> C.translate(translator: Translator<C, T>): T {
    return translator.translate(this)
}

fun <C, B : Builder<T>, T> C.translate(builderFactory: BuilderFactory<C, B, T>, spec: Spec<B, T>): T {
    return BuilderFactorySpecTranslator(builderFactory, spec).translate(this)
}

fun <T> translate(translate: () -> T): T {
    return translate()
}

fun <T> translate(translator: Translator<Any?, T>): T {
    return translator.translate(null)
}

fun <B : Builder<T>, T> translate(builderFactory: BuilderFactory<Any?, B, T>, spec: Spec<B, T>): T {
    return BuilderFactorySpecTranslator(builderFactory, spec).translate(null)
}