package io.philarios.structurizr

import io.philarios.core.DslBuilder
import io.philarios.core.Wrapper
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
internal class WorkspaceShellBuilder<out C>(override val context: C, internal var shell: WorkspaceShell = WorkspaceShell()) : WorkspaceBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun model(body: ModelBuilder<C>.() -> Unit) {
        shell = shell.copy(model = ModelScaffolder<C>(ModelSpec<C>(body)).createScaffold(context))
    }

    override fun model(spec: ModelSpec<C>) {
        shell = shell.copy(model = ModelScaffolder<C>(spec).createScaffold(context))
    }

    override fun model(ref: ModelRef) {
        shell = shell.copy(model = ref)
    }

    override fun model(value: Model) {
        shell = shell.copy(model = Wrapper(value))
    }

    override fun include(body: WorkspaceBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: WorkspaceSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: WorkspaceBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: WorkspaceSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: WorkspaceBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: WorkspaceSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): WorkspaceShellBuilder<C2> = WorkspaceShellBuilder(context, shell)

    private fun <C2> merge(other: WorkspaceShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class ModelShellBuilder<out C>(override val context: C, internal var shell: ModelShell = ModelShell()) : ModelBuilder<C> {
    override fun people(body: PersonBuilder<C>.() -> Unit) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(PersonSpec<C>(body)).createScaffold(context))
    }

    override fun people(spec: PersonSpec<C>) {
        shell = shell.copy(people = shell.people.orEmpty() + PersonScaffolder<C>(spec).createScaffold(context))
    }

    override fun people(ref: PersonRef) {
        shell = shell.copy(people = shell.people.orEmpty() + ref)
    }

    override fun people(value: Person) {
        shell = shell.copy(people = shell.people.orEmpty() + Wrapper(value))
    }

    override fun people(people: List<Person>) {
        shell = shell.copy(people = shell.people.orEmpty() + people.map { Wrapper(it) })
    }

    override fun include(body: ModelBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: ModelSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: ModelBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: ModelSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: ModelBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: ModelSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): ModelShellBuilder<C2> = ModelShellBuilder(context, shell)

    private fun <C2> merge(other: ModelShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class PersonShellBuilder<out C>(override val context: C, internal var shell: PersonShell = PersonShell()) : PersonBuilder<C> {
    override fun name(value: String) {
        shell = shell.copy(name = Wrapper(value))
    }

    override fun description(value: String) {
        shell = shell.copy(description = Wrapper(value))
    }

    override fun location(value: Location) {
        shell = shell.copy(location = Wrapper(value))
    }

    override fun include(body: PersonBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: PersonSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: PersonBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: PersonSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: PersonBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: PersonSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): PersonShellBuilder<C2> = PersonShellBuilder(context, shell)

    private fun <C2> merge(other: PersonShellBuilder<C2>) {
        this.shell = other.shell
    }
}
