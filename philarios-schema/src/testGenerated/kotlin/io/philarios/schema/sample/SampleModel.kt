package io.philarios.schema.sample

import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

object Empty

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
