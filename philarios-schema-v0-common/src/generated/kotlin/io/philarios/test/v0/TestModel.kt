package io.philarios.test.v0

import kotlin.String
import kotlin.collections.List

data class Foo(val name: String)

data class Bar(val name: String, val foo: Foo)

data class Test(val bars: List<Bar>, val foos: List<Foo>)
