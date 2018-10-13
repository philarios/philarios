package io.philarios.test.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
import io.philarios.core.v0.Wrapper
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class Foo(val name: String)

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

@DslBuilder
class FooBuilder<out C>(val context: C, internal var shell: FooShell = FooShell()) {
    fun <C> FooBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> FooBuilder<C>.include(body: FooBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> FooBuilder<C>.include(spec: FooSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> FooBuilder<C>.include(context: C2, body: FooBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> FooBuilder<C>.include(context: C2, spec: FooSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> FooBuilder<C>.includeForEach(context: Iterable<C2>, body: FooBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> FooBuilder<C>.includeForEach(context: Iterable<C2>, spec: FooSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FooBuilder<C2> = FooBuilder(context, shell)

    private fun <C2> merge(other: FooBuilder<C2>) {
        this.shell = other.shell
    }
}

data class Bar(val name: String, val foo: Foo)

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

@DslBuilder
class BarBuilder<out C>(val context: C, internal var shell: BarShell = BarShell()) {
    fun <C> BarBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> BarBuilder<C>.foo(body: FooBuilder<C>.() -> Unit) {
        shell = shell.copy(foo = FooSpec<C>(body).connect(context))
    }

    fun <C> BarBuilder<C>.foo(spec: FooSpec<C>) {
        shell = shell.copy(foo = spec.connect(context))
    }

    fun <C> BarBuilder<C>.foo(ref: FooRef) {
        shell = shell.copy(foo = ref)
    }

    fun <C> BarBuilder<C>.foo(foo: Foo) {
        shell = shell.copy(foo = Wrapper(foo))
    }

    fun <C> BarBuilder<C>.include(body: BarBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> BarBuilder<C>.include(spec: BarSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> BarBuilder<C>.include(context: C2, body: BarBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> BarBuilder<C>.include(context: C2, spec: BarSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> BarBuilder<C>.includeForEach(context: Iterable<C2>, body: BarBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> BarBuilder<C>.includeForEach(context: Iterable<C2>, spec: BarSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): BarBuilder<C2> = BarBuilder(context, shell)

    private fun <C2> merge(other: BarBuilder<C2>) {
        this.shell = other.shell
    }
}

data class Test(val bars: List<Bar>, val foos: List<Foo>)

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

@DslBuilder
class TestBuilder<out C>(val context: C, internal var shell: TestShell = TestShell()) {
    fun <C> TestBuilder<C>.bar(body: BarBuilder<C>.() -> Unit) {
        shell = shell.copy(bars = shell.bars.orEmpty() + BarSpec<C>(body).connect(context))
    }

    fun <C> TestBuilder<C>.bar(spec: BarSpec<C>) {
        shell = shell.copy(bars = shell.bars.orEmpty() + spec.connect(context))
    }

    fun <C> TestBuilder<C>.bar(ref: BarRef) {
        shell = shell.copy(bars = shell.bars.orEmpty() + ref)
    }

    fun <C> TestBuilder<C>.bar(bar: Bar) {
        shell = shell.copy(bars = shell.bars.orEmpty() + Wrapper(bar))
    }

    fun <C> TestBuilder<C>.bars(bars: List<Bar>) {
        shell = shell.copy(bars = shell.bars.orEmpty() + bars.map { Wrapper(it) })
    }

    fun <C> TestBuilder<C>.foo(body: FooBuilder<C>.() -> Unit) {
        shell = shell.copy(foos = shell.foos.orEmpty() + FooSpec<C>(body).connect(context))
    }

    fun <C> TestBuilder<C>.foo(spec: FooSpec<C>) {
        shell = shell.copy(foos = shell.foos.orEmpty() + spec.connect(context))
    }

    fun <C> TestBuilder<C>.foo(ref: FooRef) {
        shell = shell.copy(foos = shell.foos.orEmpty() + ref)
    }

    fun <C> TestBuilder<C>.foo(foo: Foo) {
        shell = shell.copy(foos = shell.foos.orEmpty() + Wrapper(foo))
    }

    fun <C> TestBuilder<C>.foos(foos: List<Foo>) {
        shell = shell.copy(foos = shell.foos.orEmpty() + foos.map { Wrapper(it) })
    }

    fun <C> TestBuilder<C>.include(body: TestBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> TestBuilder<C>.include(spec: TestSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> TestBuilder<C>.include(context: C2, body: TestBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> TestBuilder<C>.include(context: C2, spec: TestSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> TestBuilder<C>.includeForEach(context: Iterable<C2>, body: TestBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> TestBuilder<C>.includeForEach(context: Iterable<C2>, spec: TestSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TestBuilder<C2> = TestBuilder(context, shell)

    private fun <C2> merge(other: TestBuilder<C2>) {
        this.shell = other.shell
    }
}
