package io.philarios.schema.sample

import io.philarios.util.registry.Registry
import io.philarios.core.Scaffold
import kotlin.Any
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class BooleanValueShell(var value: Scaffold<Boolean>? = null) : Scaffold<BooleanValue> {
    override suspend fun resolve(registry: Registry): BooleanValue {
        checkNotNull(value) { "BooleanValue is missing the value property" }
        val value = BooleanValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class DoubleValueShell(var value: Scaffold<Double>? = null) : Scaffold<DoubleValue> {
    override suspend fun resolve(registry: Registry): DoubleValue {
        checkNotNull(value) { "DoubleValue is missing the value property" }
        val value = DoubleValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class FloatValueShell(var value: Scaffold<Float>? = null) : Scaffold<FloatValue> {
    override suspend fun resolve(registry: Registry): FloatValue {
        checkNotNull(value) { "FloatValue is missing the value property" }
        val value = FloatValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class LongValueShell(var value: Scaffold<Long>? = null) : Scaffold<LongValue> {
    override suspend fun resolve(registry: Registry): LongValue {
        checkNotNull(value) { "LongValue is missing the value property" }
        val value = LongValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class IntValueShell(var value: Scaffold<Int>? = null) : Scaffold<IntValue> {
    override suspend fun resolve(registry: Registry): IntValue {
        checkNotNull(value) { "IntValue is missing the value property" }
        val value = IntValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ShortValueShell(var value: Scaffold<Short>? = null) : Scaffold<ShortValue> {
    override suspend fun resolve(registry: Registry): ShortValue {
        checkNotNull(value) { "ShortValue is missing the value property" }
        val value = ShortValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ByteValueShell(var value: Scaffold<Byte>? = null) : Scaffold<ByteValue> {
    override suspend fun resolve(registry: Registry): ByteValue {
        checkNotNull(value) { "ByteValue is missing the value property" }
        val value = ByteValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class CharacterValueShell(var value: Scaffold<Char>? = null) : Scaffold<CharacterValue> {
    override suspend fun resolve(registry: Registry): CharacterValue {
        checkNotNull(value) { "CharacterValue is missing the value property" }
        val value = CharacterValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class StringValueShell(var value: Scaffold<String>? = null) : Scaffold<StringValue> {
    override suspend fun resolve(registry: Registry): StringValue {
        checkNotNull(value) { "StringValue is missing the value property" }
        val value = StringValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class AnyValueShell(var value: Scaffold<Any>? = null) : Scaffold<AnyValue> {
    override suspend fun resolve(registry: Registry): AnyValue {
        checkNotNull(value) { "AnyValue is missing the value property" }
        val value = AnyValue(
            value!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class OptionValueShell(var value: Scaffold<String>? = null) : Scaffold<OptionValue> {
    override suspend fun resolve(registry: Registry): OptionValue {
        val value = OptionValue(
            value?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ListValueShell(var value: List<Scaffold<String>>? = null) : Scaffold<ListValue> {
    override suspend fun resolve(registry: Registry): ListValue {
        val value = ListValue(
            value.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class MapValueShell(var value: Map<Scaffold<String>, Scaffold<String>>? = null) : Scaffold<MapValue> {
    override suspend fun resolve(registry: Registry): MapValue {
        val value = MapValue(
            value.orEmpty().let{ it.map { Pair(it.key.let { it.resolve(registry) }, it.value.let { it.resolve(registry) }) }.toMap() }
        )
        return value
    }
}

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
