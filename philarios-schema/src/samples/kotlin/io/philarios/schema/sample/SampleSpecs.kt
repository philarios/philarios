package io.philarios.schema.sample

class EmptySpec<in C>

class SimpleSpec<in C>(internal val body: SimpleBuilder<C>.() -> Unit)

class ComplexSpec<in C>(internal val body: ComplexBuilder<C>.() -> Unit)

class DataSpec<in C>(internal val body: DataBuilder<C>.() -> Unit)

class ResponseSpec<in C>(internal val body: ResponseBuilder<C>.() -> Unit)

sealed class ErrorSpec<in C, out T : Error>

class UnknownErrorSpec<in C> : ErrorSpec<C, UnknownError>()

class InsufficientAmountErrorSpec<in C>(internal val body: InsufficientAmountErrorBuilder<C>.() -> Unit) : ErrorSpec<C, InsufficientAmountError>()
