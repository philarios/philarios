package io.philarios.schema.sample

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper

class EmptyScaffolder<in C>(internal val spec: EmptySpec<C>) : Scaffolder<C, Empty> {
    override fun createScaffold(context: C): Scaffold<Empty> = Wrapper(Empty)
}

class SimpleScaffolder<in C>(internal val spec: SimpleSpec<C>) : Scaffolder<C, Simple> {
    override fun createScaffold(context: C): Scaffold<Simple> {
        val builder = SimpleShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ComplexScaffolder<in C>(internal val spec: ComplexSpec<C>) : Scaffolder<C, Complex> {
    override fun createScaffold(context: C): Scaffold<Complex> {
        val builder = ComplexShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DataScaffolder<in C>(internal val spec: DataSpec<C>) : Scaffolder<C, Data> {
    override fun createScaffold(context: C): Scaffold<Data> {
        val builder = DataShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ResponseScaffolder<in C>(internal val spec: ResponseSpec<C>) : Scaffolder<C, Response> {
    override fun createScaffold(context: C): Scaffold<Response> {
        val builder = ResponseShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ErrorScaffolder<in C, out T : Error>(internal val spec: ErrorSpec<C, T>) : Scaffolder<C, T> {
    override fun createScaffold(context: C): Scaffold<T> {
        val result = when (spec) {
            is UnknownErrorSpec<C> -> UnknownErrorScaffolder(spec).createScaffold(context)
            is InsufficientAmountErrorSpec<C> -> InsufficientAmountErrorScaffolder(spec).createScaffold(context)
        }
        return result as Scaffold<T>
    }
}

class UnknownErrorScaffolder<in C>(internal val spec: UnknownErrorSpec<C>) : Scaffolder<C, UnknownError> {
    override fun createScaffold(context: C): Scaffold<UnknownError> = Wrapper(UnknownError)
}

class InsufficientAmountErrorScaffolder<in C>(internal val spec: InsufficientAmountErrorSpec<C>) : Scaffolder<C, InsufficientAmountError> {
    override fun createScaffold(context: C): Scaffold<InsufficientAmountError> {
        val builder = InsufficientAmountErrorShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
