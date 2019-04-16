package io.philarios.core

import kotlin.reflect.KClass

interface GenericContext {

    operator fun <T : Any> get(clazz: KClass<T>, name: String? = null): T?
}

inline operator fun <reified T : Any> GenericContext.get(name: String? = null): T? = get(T::class, name)

object NopGenericContext: GenericContext {
    override fun <T : Any> get(clazz: KClass<T>, name: String?): T? = null
}