package io.philarios.schema.sample

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.Map

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
