package io.philarios.schema.sample

import io.philarios.core.Scaffold
import kotlin.String

class BooleanValueRef(key: String) : Scaffold<BooleanValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.BooleanValue::class, key)

class DoubleValueRef(key: String) : Scaffold<DoubleValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.DoubleValue::class, key)

class FloatValueRef(key: String) : Scaffold<FloatValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.FloatValue::class, key)

class LongValueRef(key: String) : Scaffold<LongValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.LongValue::class, key)

class IntValueRef(key: String) : Scaffold<IntValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.IntValue::class, key)

class ShortValueRef(key: String) : Scaffold<ShortValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.ShortValue::class, key)

class ByteValueRef(key: String) : Scaffold<ByteValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.ByteValue::class, key)

class CharacterValueRef(key: String) : Scaffold<CharacterValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.CharacterValue::class, key)

class StringValueRef(key: String) : Scaffold<StringValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.StringValue::class, key)

class AnyValueRef(key: String) : Scaffold<AnyValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.AnyValue::class, key)

class OptionValueRef(key: String) : Scaffold<OptionValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.OptionValue::class, key)

class ListValueRef(key: String) : Scaffold<ListValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.ListValue::class, key)

class MapValueRef(key: String) : Scaffold<MapValue> by io.philarios.core.RegistryRef(io.philarios.schema.sample.MapValue::class, key)

class SimpleRef(key: String) : Scaffold<Simple> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Simple::class, key)

class ComplexRef(key: String) : Scaffold<Complex> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Complex::class, key)

class DataRef(key: String) : Scaffold<Data> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Data::class, key)

class ResponseRef(key: String) : Scaffold<Response> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Response::class, key)

sealed class ErrorRef<T : Error> : Scaffold<T>

class InsufficientAmountErrorRef(key: String) : ErrorRef<InsufficientAmountError>(),
        Scaffold<InsufficientAmountError> by io.philarios.core.RegistryRef(io.philarios.schema.sample.InsufficientAmountError::class, key)
