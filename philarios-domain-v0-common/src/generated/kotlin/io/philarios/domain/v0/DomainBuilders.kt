package io.philarios.domain.v0

import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Wrapper
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
class DomainBuilder<out C>(val context: C, internal var shell: DomainShell = DomainShell()) {
    fun <C> DomainBuilder<C>.entity(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(entities = shell.entities.orEmpty() + EntitySpec<C>(body).connect(context))
    }

    fun <C> DomainBuilder<C>.entity(spec: EntitySpec<C>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + spec.connect(context))
    }

    fun <C> DomainBuilder<C>.entity(ref: EntityRef) {
        shell = shell.copy(entities = shell.entities.orEmpty() + ref)
    }

    fun <C> DomainBuilder<C>.entity(entity: Entity) {
        shell = shell.copy(entities = shell.entities.orEmpty() + Wrapper(entity))
    }

    fun <C> DomainBuilder<C>.entities(entities: List<Entity>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + entities.map { Wrapper(it) })
    }

    fun <C> DomainBuilder<C>.relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipSpec<C>(body).connect(context))
    }

    fun <C> DomainBuilder<C>.relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + spec.connect(context))
    }

    fun <C> DomainBuilder<C>.relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    fun <C> DomainBuilder<C>.relationship(relationship: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(relationship))
    }

    fun <C> DomainBuilder<C>.relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    fun <C> DomainBuilder<C>.include(body: DomainBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DomainBuilder<C>.include(spec: DomainSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DomainBuilder<C>.include(context: C2, body: DomainBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> DomainBuilder<C>.include(context: C2, spec: DomainSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> DomainBuilder<C>.includeForEach(context: Iterable<C2>, body: DomainBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DomainBuilder<C>.includeForEach(context: Iterable<C2>, spec: DomainSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DomainBuilder<C2> = DomainBuilder(context, shell)

    private fun <C2> merge(other: DomainBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class EntityBuilder<out C>(val context: C, internal var shell: EntityShell = EntityShell()) {
    fun <C> EntityBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> EntityBuilder<C>.attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeSpec<C>(body).connect(context))
    }

    fun <C> EntityBuilder<C>.attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + spec.connect(context))
    }

    fun <C> EntityBuilder<C>.attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    fun <C> EntityBuilder<C>.attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    fun <C> EntityBuilder<C>.attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    fun <C> EntityBuilder<C>.include(body: EntityBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> EntityBuilder<C>.include(spec: EntitySpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> EntityBuilder<C>.include(context: C2, body: EntityBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> EntityBuilder<C>.include(context: C2, spec: EntitySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> EntityBuilder<C>.includeForEach(context: Iterable<C2>, body: EntityBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> EntityBuilder<C>.includeForEach(context: Iterable<C2>, spec: EntitySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EntityBuilder<C2> = EntityBuilder(context, shell)

    private fun <C2> merge(other: EntityBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class RelationshipBuilder<out C>(val context: C, internal var shell: RelationshipShell = RelationshipShell()) {
    fun <C> RelationshipBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> RelationshipBuilder<C>.from(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(from = EntitySpec<C>(body).connect(context))
    }

    fun <C> RelationshipBuilder<C>.from(spec: EntitySpec<C>) {
        shell = shell.copy(from = spec.connect(context))
    }

    fun <C> RelationshipBuilder<C>.from(ref: EntityRef) {
        shell = shell.copy(from = ref)
    }

    fun <C> RelationshipBuilder<C>.from(from: Entity) {
        shell = shell.copy(from = Wrapper(from))
    }

    fun <C> RelationshipBuilder<C>.to(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(to = EntitySpec<C>(body).connect(context))
    }

    fun <C> RelationshipBuilder<C>.to(spec: EntitySpec<C>) {
        shell = shell.copy(to = spec.connect(context))
    }

    fun <C> RelationshipBuilder<C>.to(ref: EntityRef) {
        shell = shell.copy(to = ref)
    }

    fun <C> RelationshipBuilder<C>.to(to: Entity) {
        shell = shell.copy(to = Wrapper(to))
    }

    fun <C> RelationshipBuilder<C>.attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeSpec<C>(body).connect(context))
    }

    fun <C> RelationshipBuilder<C>.attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + spec.connect(context))
    }

    fun <C> RelationshipBuilder<C>.attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    fun <C> RelationshipBuilder<C>.attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    fun <C> RelationshipBuilder<C>.attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    fun <C> RelationshipBuilder<C>.include(body: RelationshipBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> RelationshipBuilder<C>.include(spec: RelationshipSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> RelationshipBuilder<C>.include(context: C2, body: RelationshipBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> RelationshipBuilder<C>.include(context: C2, spec: RelationshipSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> RelationshipBuilder<C>.includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> RelationshipBuilder<C>.includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RelationshipBuilder<C2> = RelationshipBuilder(context, shell)

    private fun <C2> merge(other: RelationshipBuilder<C2>) {
        this.shell = other.shell
    }
}

@DslBuilder
class AttributeBuilder<out C>(val context: C, internal var shell: AttributeShell = AttributeShell()) {
    fun <C> AttributeBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> AttributeBuilder<C>.type(type: Type) {
        shell = shell.copy(type = type)
    }

    fun <C> AttributeBuilder<C>.include(body: AttributeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> AttributeBuilder<C>.include(spec: AttributeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> AttributeBuilder<C>.include(context: C2, body: AttributeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> AttributeBuilder<C>.include(context: C2, spec: AttributeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> AttributeBuilder<C>.includeForEach(context: Iterable<C2>, body: AttributeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> AttributeBuilder<C>.includeForEach(context: Iterable<C2>, spec: AttributeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttributeBuilder<C2> = AttributeBuilder(context, shell)

    private fun <C2> merge(other: AttributeBuilder<C2>) {
        this.shell = other.shell
    }
}
