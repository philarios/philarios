package io.philarios.test.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class FooShell(var name: String? = null) : Scaffold<Foo> {
    override suspend fun resolve(registry: Registry): Foo {
        val value = Foo(name!!)
        registry.put(Foo::class, name!!, value)
        return value
    }
}

class FooRef(key: String) : Scaffold<Foo> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Foo::class, key)

open class FooSpec<in C>(internal val body: FooBuilder<C>.() -> Unit) : Spec<C, Foo> {
    override fun connect(context: C): Scaffold<Foo> {
        val builder = FooBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class BarShell(var name: String? = null, var foo: Scaffold<Foo>? = null) : Scaffold<Bar> {
    override suspend fun resolve(registry: Registry): Bar {
        coroutineScope {
        	launch { foo!!.resolve(registry) }
        }
        val value = Bar(name!!,foo!!.resolve(registry))
        registry.put(Bar::class, name!!, value)
        return value
    }
}

class BarRef(key: String) : Scaffold<Bar> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Bar::class, key)

open class BarSpec<in C>(internal val body: BarBuilder<C>.() -> Unit) : Spec<C, Bar> {
    override fun connect(context: C): Scaffold<Bar> {
        val builder = BarBuilder<C>(context)
        builder.apply(body)
        return builder.shell
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

class TestRef(key: String) : Scaffold<Test> by io.philarios.core.v0.RegistryRef(io.philarios.test.v0.Test::class, key)

open class TestSpec<in C>(internal val body: TestBuilder<C>.() -> Unit) : Spec<C, Test> {
    override fun connect(context: C): Scaffold<Test> {
        val builder = TestBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
