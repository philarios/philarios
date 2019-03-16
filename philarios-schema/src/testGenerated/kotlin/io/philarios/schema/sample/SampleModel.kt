package io.philarios.schema.sample

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

object Empty

data class BooleanValue(val value: Boolean)

data class DoubleValue(val value: Double)

data class FloatValue(val value: Float)

data class LongValue(val value: Long)

data class IntValue(val value: Int)

data class ShortValue(val value: Short)

data class ByteValue(val value: Byte)

data class CharacterValue(val value: Char)

data class StringValue(val value: String)

data class AnyValue(val value: Any)

data class OptionValue(val value: String?)

data class ListValue(val value: List<String>)

data class MapValue(val value: Map<String, String>)

data class Simple(val value: String)

data class Complex(
        val value: String,
        val age: Int,
        val done: Boolean?,
        val children: List<Simple>,
        val capitalCities: Map<String, String>,
        val dataByKey: Map<String, Data>
)

data class Data(val x: Int, val y: Int)

data class Response(val errors: List<Error>)

sealed class Error

object UnknownError : Error()

data class InsufficientAmountError(val amount: Double) : Error()
