package io.philarios.core.v0

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

interface TranslatorFactory<in C, out T> {
    fun create(): Translator<C, T>
}

interface Translator<in C, out T> {
    fun translate(context: C): T
}

interface Context<out C> {
    val value: C

    fun <T> translate(translatorFactory: TranslatorFactory<C, T>): Context<T> {
        return translatorFactory.create()
                .let { translate(it) }
    }

    fun <T> translate(translator: Translator<C, T>): Context<T> {
        return translator.translate(value)
                .let { ValueContext(it) }
    }
}

fun emptyContext() = EmptyContext

object EmptyContext: Context<Nothing?> {
    override val value: Nothing? = null
}

fun <C> contextOf(value: C) = ValueContext(value)

class ValueContext<out C>(override val value: C) : Context<C>

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
