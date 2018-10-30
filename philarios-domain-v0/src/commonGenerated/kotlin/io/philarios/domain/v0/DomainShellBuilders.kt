package io.philarios.domain.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
internal class DomainShellBuilder<out C>(override val context: C, internal var shell: DomainShell = DomainShell()) : DomainBuilder<C> {
    override fun entity(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(entities = shell.entities.orEmpty() + EntitySpec<C>(body).connect(context))
    }

    override fun entity(spec: EntitySpec<C>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + spec.connect(context))
    }

    override fun entity(ref: EntityRef) {
        shell = shell.copy(entities = shell.entities.orEmpty() + ref)
    }

    override fun entity(entity: Entity) {
        shell = shell.copy(entities = shell.entities.orEmpty() + Wrapper(entity))
    }

    override fun entities(entities: List<Entity>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + entities.map { Wrapper(it) })
    }

    override fun relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipSpec<C>(body).connect(context))
    }

    override fun relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + spec.connect(context))
    }

    override fun relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    override fun relationship(relationship: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(relationship))
    }

    override fun relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    override fun include(body: DomainBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: DomainSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: DomainBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: DomainSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: DomainBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: DomainSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DomainShellBuilder<C2> = DomainShellBuilder(context, shell)

    private fun <C2> merge(other: DomainShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class EntityShellBuilder<out C>(override val context: C, internal var shell: EntityShell = EntityShell()) : EntityBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeSpec<C>(body).connect(context))
    }

    override fun attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + spec.connect(context))
    }

    override fun attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    override fun attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    override fun attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    override fun include(body: EntityBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: EntitySpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: EntityBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: EntitySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: EntityBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: EntitySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EntityShellBuilder<C2> = EntityShellBuilder(context, shell)

    private fun <C2> merge(other: EntityShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class RelationshipShellBuilder<out C>(override val context: C, internal var shell: RelationshipShell = RelationshipShell()) : RelationshipBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun from(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(from = EntitySpec<C>(body).connect(context))
    }

    override fun from(spec: EntitySpec<C>) {
        shell = shell.copy(from = spec.connect(context))
    }

    override fun from(ref: EntityRef) {
        shell = shell.copy(from = ref)
    }

    override fun from(from: Entity) {
        shell = shell.copy(from = Wrapper(from))
    }

    override fun to(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(to = EntitySpec<C>(body).connect(context))
    }

    override fun to(spec: EntitySpec<C>) {
        shell = shell.copy(to = spec.connect(context))
    }

    override fun to(ref: EntityRef) {
        shell = shell.copy(to = ref)
    }

    override fun to(to: Entity) {
        shell = shell.copy(to = Wrapper(to))
    }

    override fun attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeSpec<C>(body).connect(context))
    }

    override fun attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + spec.connect(context))
    }

    override fun attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    override fun attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    override fun attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    override fun include(body: RelationshipBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: RelationshipSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: RelationshipBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: RelationshipSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RelationshipShellBuilder<C2> = RelationshipShellBuilder(context, shell)

    private fun <C2> merge(other: RelationshipShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class AttributeShellBuilder<out C>(override val context: C, internal var shell: AttributeShell = AttributeShell()) : AttributeBuilder<C> {
    override fun name(name: String) {
        shell = shell.copy(name = name)
    }

    override fun type(body: TypeBuilder<C>.() -> Unit) {
        shell = shell.copy(type = TypeSpec<C>(body).connect(context))
    }

    override fun type(spec: TypeSpec<C>) {
        shell = shell.copy(type = spec.connect(context))
    }

    override fun type(ref: TypeRef) {
        shell = shell.copy(type = ref)
    }

    override fun type(type: Type) {
        shell = shell.copy(type = Wrapper(type))
    }

    override fun include(body: AttributeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: AttributeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: AttributeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: AttributeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: AttributeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: AttributeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttributeShellBuilder<C2> = AttributeShellBuilder(context, shell)

    private fun <C2> merge(other: AttributeShellBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
internal class TypeShellBuilder<out C>(override val context: C, internal var shell: TypeShell = TypeShell()) : TypeBuilder<C> {
    override fun type(type: RawType) {
        shell = shell.copy(type = type)
    }

    override fun nullable(nullable: Boolean) {
        shell = shell.copy(nullable = nullable)
    }

    override fun include(body: TypeBuilder<C>.() -> Unit) {
        apply(body)
    }

    override fun include(spec: TypeSpec<C>) {
        apply(spec.body)
    }

    override fun <C2> include(context: C2, body: TypeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    override fun <C2> include(context: C2, spec: TypeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    override fun <C2> includeForEach(context: Iterable<C2>, body: TypeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    override fun <C2> includeForEach(context: Iterable<C2>, spec: TypeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): TypeShellBuilder<C2> = TypeShellBuilder(context, shell)

    private fun <C2> merge(other: TypeShellBuilder<C2>) {
        this.shell = other.shell
    }
}
