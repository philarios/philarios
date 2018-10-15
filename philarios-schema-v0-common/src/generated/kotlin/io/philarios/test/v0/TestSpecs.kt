package io.philarios.test.v0

import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec

open class FooSpec<in C>(internal val body: FooBuilder<C>.() -> Unit) : Spec<C, Foo> {
    override fun connect(context: C): Scaffold<Foo> {
        val builder = FooBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class BarSpec<in C>(internal val body: BarBuilder<C>.() -> Unit) : Spec<C, Bar> {
    override fun connect(context: C): Scaffold<Bar> {
        val builder = BarBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class TestSpec<in C>(internal val body: TestBuilder<C>.() -> Unit) : Spec<C, Test> {
    override fun connect(context: C): Scaffold<Test> {
        val builder = TestBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
