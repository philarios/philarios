package io.philarios.test.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class FooShell(var name: String? = null) : Scaffold<Foo> {
    override suspend fun resolve(registry: Registry): Foo {
        checkNotNull(name) { "Foo is missing the name property" }
        val value = Foo(name!!)
        registry.put(Foo::class, name!!, value)
        return value
    }
}

data class BarShell(var name: String? = null, var foo: Scaffold<Foo>? = null) : Scaffold<Bar> {
    override suspend fun resolve(registry: Registry): Bar {
        checkNotNull(name) { "Bar is missing the name property" }
        checkNotNull(foo) { "Bar is missing the foo property" }
        coroutineScope {
        	launch { foo!!.resolve(registry) }
        }
        val value = Bar(name!!,foo!!.resolve(registry))
        registry.put(Bar::class, name!!, value)
        return value
    }
}

data class TestShell(var bars: List<Scaffold<Bar>> = emptyList(), var foos: List<Scaffold<Foo>> = emptyList()) : Scaffold<Test> {
    override suspend fun resolve(registry: Registry): Test {
        coroutineScope {
        	bars.forEach { launch { it.resolve(registry) } }
        	foos.forEach { launch { it.resolve(registry) } }
        }
        val value = Test(bars.map { it.resolve(registry) },foos.map { it.resolve(registry) })
        return value
    }
}
