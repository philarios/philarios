package io.philarios.schema.sample

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder
import io.philarios.core.Wrapper

class EmptyScaffolder<in C>(internal val spec: EmptySpec<C>) : Scaffolder<C, Empty> {
    override fun createScaffold(context: C): Scaffold<Empty> = Wrapper(Empty)
}

class BooleanValueScaffolder<in C>(internal val spec: BooleanValueSpec<C>) : Scaffolder<C, BooleanValue> {
    override fun createScaffold(context: C): Scaffold<BooleanValue> {
        val builder = BooleanValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class DoubleValueScaffolder<in C>(internal val spec: DoubleValueSpec<C>) : Scaffolder<C, DoubleValue> {
    override fun createScaffold(context: C): Scaffold<DoubleValue> {
        val builder = DoubleValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class FloatValueScaffolder<in C>(internal val spec: FloatValueSpec<C>) : Scaffolder<C, FloatValue> {
    override fun createScaffold(context: C): Scaffold<FloatValue> {
        val builder = FloatValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class LongValueScaffolder<in C>(internal val spec: LongValueSpec<C>) : Scaffolder<C, LongValue> {
    override fun createScaffold(context: C): Scaffold<LongValue> {
        val builder = LongValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class IntValueScaffolder<in C>(internal val spec: IntValueSpec<C>) : Scaffolder<C, IntValue> {
    override fun createScaffold(context: C): Scaffold<IntValue> {
        val builder = IntValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ShortValueScaffolder<in C>(internal val spec: ShortValueSpec<C>) : Scaffolder<C, ShortValue> {
    override fun createScaffold(context: C): Scaffold<ShortValue> {
        val builder = ShortValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class ByteValueScaffolder<in C>(internal val spec: ByteValueSpec<C>) : Scaffolder<C, ByteValue> {
    override fun createScaffold(context: C): Scaffold<ByteValue> {
        val builder = ByteValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class CharacterValueScaffolder<in C>(internal val spec: CharacterValueSpec<C>) : Scaffolder<C, CharacterValue> {
    override fun createScaffold(context: C): Scaffold<CharacterValue> {
        val builder = CharacterValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class StringValueScaffolder<in C>(internal val spec: StringValueSpec<C>) : Scaffolder<C, StringValue> {
    override fun createScaffold(context: C): Scaffold<StringValue> {
        val builder = StringValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AnyValueScaffolder<in C>(internal val spec: AnyValueSpec<C>) : Scaffolder<C, AnyValue> {
    override fun createScaffold(context: C): Scaffold<AnyValue> {
        val builder = AnyValueShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
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
