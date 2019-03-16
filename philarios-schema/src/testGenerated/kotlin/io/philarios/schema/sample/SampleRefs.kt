package io.philarios.schema.sample

import io.philarios.core.Scaffold
import kotlin.String

class SimpleRef(key: String) : Scaffold<Simple> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Simple::class, key)

class ComplexRef(key: String) : Scaffold<Complex> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Complex::class, key)

class DataRef(key: String) : Scaffold<Data> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Data::class, key)

class ResponseRef(key: String) : Scaffold<Response> by io.philarios.core.RegistryRef(io.philarios.schema.sample.Response::class, key)

sealed class ErrorRef<T : Error> : Scaffold<T>

class InsufficientAmountErrorRef(key: String) : ErrorRef<InsufficientAmountError>(),
        Scaffold<InsufficientAmountError> by io.philarios.core.RegistryRef(io.philarios.schema.sample.InsufficientAmountError::class, key)
