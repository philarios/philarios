package io.philarios.schema.sample

class EmptySpec<in C>

class BooleanValueSpec<in C>(internal val body: BooleanValueBuilder<C>.() -> Unit)

class DoubleValueSpec<in C>(internal val body: DoubleValueBuilder<C>.() -> Unit)

class FloatValueSpec<in C>(internal val body: FloatValueBuilder<C>.() -> Unit)

class LongValueSpec<in C>(internal val body: LongValueBuilder<C>.() -> Unit)

class IntValueSpec<in C>(internal val body: IntValueBuilder<C>.() -> Unit)

class ShortValueSpec<in C>(internal val body: ShortValueBuilder<C>.() -> Unit)

class ByteValueSpec<in C>(internal val body: ByteValueBuilder<C>.() -> Unit)

class CharacterValueSpec<in C>(internal val body: CharacterValueBuilder<C>.() -> Unit)

class StringValueSpec<in C>(internal val body: StringValueBuilder<C>.() -> Unit)

class AnyValueSpec<in C>(internal val body: AnyValueBuilder<C>.() -> Unit)

class SimpleSpec<in C>(internal val body: SimpleBuilder<C>.() -> Unit)

class ComplexSpec<in C>(internal val body: ComplexBuilder<C>.() -> Unit)

class DataSpec<in C>(internal val body: DataBuilder<C>.() -> Unit)

class ResponseSpec<in C>(internal val body: ResponseBuilder<C>.() -> Unit)

sealed class ErrorSpec<in C, out T : Error>

class UnknownErrorSpec<in C> : ErrorSpec<C, UnknownError>()

class InsufficientAmountErrorSpec<in C>(internal val body: InsufficientAmountErrorBuilder<C>.() -> Unit) : ErrorSpec<C, InsufficientAmountError>()
