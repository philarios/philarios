package io.philarios.schema.sample

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.Any
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Pair
import kotlin.Short
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

@DslBuilder
internal class BooleanValueShellBuilder<out C>(override val context: C, internal var shell: BooleanValueShell = BooleanValueShell()) : BooleanValueBuilder<C> {
    override fun value(value: Boolean) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: BooleanValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: BooleanValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: BooleanValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: BooleanValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: BooleanValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: BooleanValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): BooleanValueShellBuilder<C2> = BooleanValueShellBuilder(context, shell)

    private fun <C2> merge(other: BooleanValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DoubleValueShellBuilder<out C>(override val context: C, internal var shell: DoubleValueShell = DoubleValueShell()) : DoubleValueBuilder<C> {
    override fun value(value: Double) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: DoubleValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DoubleValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DoubleValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DoubleValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DoubleValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DoubleValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DoubleValueShellBuilder<C2> = DoubleValueShellBuilder(context, shell)

    private fun <C2> merge(other: DoubleValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class FloatValueShellBuilder<out C>(override val context: C, internal var shell: FloatValueShell = FloatValueShell()) : FloatValueBuilder<C> {
    override fun value(value: Float) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: FloatValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: FloatValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: FloatValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: FloatValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: FloatValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: FloatValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): FloatValueShellBuilder<C2> = FloatValueShellBuilder(context, shell)

    private fun <C2> merge(other: FloatValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class LongValueShellBuilder<out C>(override val context: C, internal var shell: LongValueShell = LongValueShell()) : LongValueBuilder<C> {
    override fun value(value: Long) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: LongValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: LongValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: LongValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: LongValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: LongValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: LongValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): LongValueShellBuilder<C2> = LongValueShellBuilder(context, shell)

    private fun <C2> merge(other: LongValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class IntValueShellBuilder<out C>(override val context: C, internal var shell: IntValueShell = IntValueShell()) : IntValueBuilder<C> {
    override fun value(value: Int) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: IntValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: IntValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: IntValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: IntValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: IntValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: IntValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): IntValueShellBuilder<C2> = IntValueShellBuilder(context, shell)

    private fun <C2> merge(other: IntValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ShortValueShellBuilder<out C>(override val context: C, internal var shell: ShortValueShell = ShortValueShell()) : ShortValueBuilder<C> {
    override fun value(value: Short) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: ShortValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ShortValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ShortValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ShortValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ShortValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ShortValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ShortValueShellBuilder<C2> = ShortValueShellBuilder(context, shell)

    private fun <C2> merge(other: ShortValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ByteValueShellBuilder<out C>(override val context: C, internal var shell: ByteValueShell = ByteValueShell()) : ByteValueBuilder<C> {
    override fun value(value: Byte) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: ByteValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ByteValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ByteValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ByteValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ByteValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ByteValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ByteValueShellBuilder<C2> = ByteValueShellBuilder(context, shell)

    private fun <C2> merge(other: ByteValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class CharacterValueShellBuilder<out C>(override val context: C, internal var shell: CharacterValueShell = CharacterValueShell()) : CharacterValueBuilder<C> {
    override fun value(value: Char) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: CharacterValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: CharacterValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: CharacterValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: CharacterValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: CharacterValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: CharacterValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): CharacterValueShellBuilder<C2> = CharacterValueShellBuilder(context, shell)

    private fun <C2> merge(other: CharacterValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class StringValueShellBuilder<out C>(override val context: C, internal var shell: StringValueShell = StringValueShell()) : StringValueBuilder<C> {
    override fun value(value: String) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: StringValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: StringValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: StringValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: StringValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: StringValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: StringValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): StringValueShellBuilder<C2> = StringValueShellBuilder(context, shell)

    private fun <C2> merge(other: StringValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AnyValueShellBuilder<out C>(override val context: C, internal var shell: AnyValueShell = AnyValueShell()) : AnyValueBuilder<C> {
    override fun value(value: Any) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: AnyValueBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AnyValueSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AnyValueBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AnyValueSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AnyValueBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AnyValueSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AnyValueShellBuilder<C2> = AnyValueShellBuilder(context, shell)

    private fun <C2> merge(other: AnyValueShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class SimpleShellBuilder<out C>(override val context: C, internal var shell: SimpleShell = SimpleShell()) : SimpleBuilder<C> {
    override fun value(value: String) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun include(body: SimpleBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: SimpleSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: SimpleBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: SimpleSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: SimpleBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: SimpleSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): SimpleShellBuilder<C2> = SimpleShellBuilder(context, shell)

    private fun <C2> merge(other: SimpleShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ComplexShellBuilder<out C>(override val context: C, internal var shell: ComplexShell = ComplexShell()) : ComplexBuilder<C> {
    override fun value(value: String) {
        shell = shell.copy(value = Wrapper(value))
    }

    override fun age(value: Int) {
        shell = shell.copy(age = Wrapper(value))
    }

    override fun done(value: Boolean) {
        shell = shell.copy(done = Wrapper(value))
    }

    override fun children(body: SimpleBuilder<C>.() -> Unit) {
        shell = shell.copy(children = shell.children.orEmpty() + SimpleScaffolder<C>(SimpleSpec<C>(body)).createScaffold(context))
    }

    override fun children(spec: SimpleSpec<C>) {
        shell = shell.copy(children = shell.children.orEmpty() + SimpleScaffolder<C>(spec).createScaffold(context))
    }

    override fun children(ref: SimpleRef) {
        shell = shell.copy(children = shell.children.orEmpty() + ref)
    }

    override fun children(value: Simple) {
        shell = shell.copy(children = shell.children.orEmpty() + Wrapper(value))
    }

    override fun children(children: List<Simple>) {
        shell = shell.copy(children = shell.children.orEmpty() + children.map { Wrapper(it) })
    }

    override fun capitalCities(key: String, value: String) {
        shell = shell.copy(capitalCities = shell.capitalCities.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun capitalCities(pair: Pair<String, String>) {
        shell = shell.copy(capitalCities = shell.capitalCities.orEmpty() + Pair(Wrapper(pair.first), Wrapper(pair.second)))
    }

    override fun capitalCities(capitalCities: Map<String, String>) {
        shell = shell.copy(capitalCities = shell.capitalCities.orEmpty() + capitalCities.map { Pair(Wrapper(it.key), Wrapper(it.value)) })
    }

    override fun dataByKey(key: String, body: DataBuilder<C>.() -> Unit) {
        shell = shell.copy(dataByKey = shell.dataByKey.orEmpty() + Pair(Wrapper(key),DataScaffolder<C>(DataSpec<C>(body)).createScaffold(context)))
    }

    override fun dataByKey(key: String, spec: DataSpec<C>) {
        shell = shell.copy(dataByKey = shell.dataByKey.orEmpty() + Pair(Wrapper(key),DataScaffolder<C>(spec).createScaffold(context)))
    }

    override fun dataByKey(key: String, ref: DataRef) {
        shell = shell.copy(dataByKey = shell.dataByKey.orEmpty() + Pair(Wrapper(key),ref))
    }

    override fun dataByKey(key: String, value: Data) {
        shell = shell.copy(dataByKey = shell.dataByKey.orEmpty() + Pair(Wrapper(key),Wrapper(value)))
    }

    override fun include(body: ComplexBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ComplexSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ComplexBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ComplexSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ComplexBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ComplexSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ComplexShellBuilder<C2> = ComplexShellBuilder(context, shell)

    private fun <C2> merge(other: ComplexShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class DataShellBuilder<out C>(override val context: C, internal var shell: DataShell = DataShell()) : DataBuilder<C> {
    override fun x(value: Int) {
        shell = shell.copy(x = Wrapper(value))
    }

    override fun y(value: Int) {
        shell = shell.copy(y = Wrapper(value))
    }

    override fun include(body: DataBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DataSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DataBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DataSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DataBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DataSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DataShellBuilder<C2> = DataShellBuilder(context, shell)

    private fun <C2> merge(other: DataShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ResponseShellBuilder<out C>(override val context: C, internal var shell: ResponseShell = ResponseShell()) : ResponseBuilder<C> {
    override fun <T : Error> error(spec: ErrorSpec<C, T>) {
        shell = shell.copy(errors = shell.errors.orEmpty() + ErrorScaffolder<C, Error>(spec).createScaffold(context))
    }

    override fun <T : Error> error(ref: ErrorRef<T>) {
        shell = shell.copy(errors = shell.errors.orEmpty() + ref)
    }

    override fun <T : Error> error(value: T) {
        shell = shell.copy(errors = shell.errors.orEmpty() + Wrapper(value))
    }

    override fun include(body: ResponseBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ResponseSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ResponseBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ResponseSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ResponseBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ResponseSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ResponseShellBuilder<C2> = ResponseShellBuilder(context, shell)

    private fun <C2> merge(other: ResponseShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class InsufficientAmountErrorShellBuilder<out C>(override val context: C, internal var shell: InsufficientAmountErrorShell = InsufficientAmountErrorShell()) : InsufficientAmountErrorBuilder<C> {
    override fun amount(value: Double) {
        shell = shell.copy(amount = Wrapper(value))
    }

    override fun include(body: InsufficientAmountErrorBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: InsufficientAmountErrorSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: InsufficientAmountErrorBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: InsufficientAmountErrorSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: InsufficientAmountErrorBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: InsufficientAmountErrorSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): InsufficientAmountErrorShellBuilder<C2> = InsufficientAmountErrorShellBuilder(context, shell)

    private fun <C2> merge(other: InsufficientAmountErrorShellBuilder<C2>) {
        this.shell = other.shell
    }
}
