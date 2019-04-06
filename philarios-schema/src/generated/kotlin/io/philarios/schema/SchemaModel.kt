// The model of your schema written as pure Kotlin classes.
//
// Because the model expresses the high-level domain, nothing in this file will depend on the generator or on any
// of the other files. This is done to ensure that you could potentially take this file and reuse the classes
// without having a dependency on the specs or materialization process.
//
// If you feel like something is preventing you from separating the model classes from the specific specs, builders,
// or materialization, please feel free to open an issue in the project's repository.
package io.philarios.schema

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

data class Schema(
        val pkg: String,
        val name: String,
        val types: List<Type>
)

sealed class Type

data class Struct(
        val pkg: String?,
        val name: String,
        val fields: List<Field>
) : Type()

data class Union(
        val pkg: String?,
        val name: String,
        val shapes: List<Struct>
) : Type()

data class EnumType(
        val pkg: String?,
        val name: String,
        val values: List<String>
) : Type()

data class RefType(val pkg: String?, val name: String) : Type()

data class OptionType(val type: Type) : Type()

data class ListType(val type: Type) : Type()

data class MapType(val keyType: Type, val valueType: Type) : Type()

object BooleanType : Type()

object DoubleType : Type()

object FloatType : Type()

object LongType : Type()

object IntType : Type()

object ShortType : Type()

object ByteType : Type()

object CharacterType : Type()

object StringType : Type()

object AnyType : Type()

data class Field(
        val name: String,
        val key: Boolean?,
        val singularName: String?,
        val type: Type
)
