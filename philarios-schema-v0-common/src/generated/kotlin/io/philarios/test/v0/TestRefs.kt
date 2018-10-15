package io.philarios.test.v0

import io.philarios.core.v0.Scaffold
import kotlin.String

class FooRef(key: String) : Scaffold<Foo> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Foo::class, key)

class BarRef(key: String) : Scaffold<Bar> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Bar::class, key)

class TestRef(key: String) : Scaffold<Test> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Test::class, key)
