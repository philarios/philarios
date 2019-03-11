package io.philarios.schema.sample

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class SimpleShell(var value: Scaffold<String>? = null) : Scaffold<Simple> {
    override suspend fun resolve(registry: Registry): Simple {
        checkNotNull(value) { "Simple is missing the value property" }
        val value = Simple(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ComplexShell(
        var value: Scaffold<String>? = null,
        var age: Scaffold<Int>? = null,
        var done: Scaffold<Boolean>? = null,
        var children: List<Scaffold<Simple>>? = null,
        var capitalCities: Map<Scaffold<String>, Scaffold<String>>? = null,
        var dataByKey: Map<Scaffold<String>, Scaffold<Data>>? = null
) : Scaffold<Complex> {
    override suspend fun resolve(registry: Registry): Complex {
        checkNotNull(value) { "Complex is missing the value property" }
        checkNotNull(age) { "Complex is missing the age property" }
        coroutineScope {
            children?.let{ it.forEach { launch { it.resolve(registry) } } }
            dataByKey?.let{ it.forEach { it.value.let { launch { it.resolve(registry) } } } }
        }
        val value = Complex(
            value!!.let{ it.resolve(registry) },
            age!!.let{ it.resolve(registry) },
            done?.let{ it.resolve(registry) },
            children.orEmpty().let{ it.map { it.resolve(registry) } },
            capitalCities.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() },
            dataByKey.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

internal data class DataShell(var x: Scaffold<Int>? = null, var y: Scaffold<Int>? = null) : Scaffold<Data> {
    override suspend fun resolve(registry: Registry): Data {
        checkNotNull(x) { "Data is missing the x property" }
        checkNotNull(y) { "Data is missing the y property" }
        val value = Data(
            x!!.let{ it.resolve(registry) },
            y!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ResponseShell(var errors: List<Scaffold<Error>>? = null) : Scaffold<Response> {
    override suspend fun resolve(registry: Registry): Response {
        coroutineScope {
            errors?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Response(
            errors.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal sealed class ErrorShell

internal data class InsufficientAmountErrorShell(var amount: Scaffold<Double>? = null) : ErrorShell(),
        Scaffold<InsufficientAmountError> {
    override suspend fun resolve(registry: Registry): InsufficientAmountError {
        checkNotNull(amount) { "InsufficientAmountError is missing the amount property" }
        val value = InsufficientAmountError(
            amount!!.let{ it.resolve(registry) }
        )
        return value
    }
}
