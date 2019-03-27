package io.philarios.domain.v0

import io.philarios.core.DslBuilder
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

@DslBuilder
interface DomainBuilder<out C> {
    val context: C

    fun entity(body: EntityBuilder<C>.() -> Unit)

    fun entity(spec: EntitySpec<C>)

    fun entity(ref: EntityRef)

    fun entity(value: Entity)

    fun entities(entities: List<Entity>)

    fun relationship(body: RelationshipBuilder<C>.() -> Unit)

    fun relationship(spec: RelationshipSpec<C>)

    fun relationship(ref: RelationshipRef)

    fun relationship(value: Relationship)

    fun relationships(relationships: List<Relationship>)

    fun include(body: DomainBuilder<C>.() -> Unit)

    fun include(spec: DomainSpec<C>)

    fun <C2> include(context: C2, body: DomainBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: DomainSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: DomainBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: DomainSpec<C2>)
}

@DslBuilder
interface EntityBuilder<out C> {
    val context: C

    fun name(value: String)

    fun attribute(body: AttributeBuilder<C>.() -> Unit)

    fun attribute(spec: AttributeSpec<C>)

    fun attribute(ref: AttributeRef)

    fun attribute(value: Attribute)

    fun attributes(attributes: List<Attribute>)

    fun include(body: EntityBuilder<C>.() -> Unit)

    fun include(spec: EntitySpec<C>)

    fun <C2> include(context: C2, body: EntityBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: EntitySpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: EntityBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: EntitySpec<C2>)
}

@DslBuilder
interface RelationshipBuilder<out C> {
    val context: C

    fun name(value: String)

    fun from(body: EntityBuilder<C>.() -> Unit)

    fun from(spec: EntitySpec<C>)

    fun from(ref: EntityRef)

    fun from(value: Entity)

    fun to(body: EntityBuilder<C>.() -> Unit)

    fun to(spec: EntitySpec<C>)

    fun to(ref: EntityRef)

    fun to(value: Entity)

    fun attribute(body: AttributeBuilder<C>.() -> Unit)

    fun attribute(spec: AttributeSpec<C>)

    fun attribute(ref: AttributeRef)

    fun attribute(value: Attribute)

    fun attributes(attributes: List<Attribute>)

    fun include(body: RelationshipBuilder<C>.() -> Unit)

    fun include(spec: RelationshipSpec<C>)

    fun <C2> include(context: C2, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: RelationshipSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>)
}

@DslBuilder
interface AttributeBuilder<out C> {
    val context: C

    fun name(value: String)

    fun type(body: TypeBuilder<C>.() -> Unit)

    fun type(spec: TypeSpec<C>)

    fun type(ref: TypeRef)

    fun type(value: Type)

    fun include(body: AttributeBuilder<C>.() -> Unit)

    fun include(spec: AttributeSpec<C>)

    fun <C2> include(context: C2, body: AttributeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: AttributeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: AttributeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: AttributeSpec<C2>)
}

@DslBuilder
interface TypeBuilder<out C> {
    val context: C

    fun type(value: RawType)

    fun nullable(value: Boolean)

    fun include(body: TypeBuilder<C>.() -> Unit)

    fun include(spec: TypeSpec<C>)

    fun <C2> include(context: C2, body: TypeBuilder<C2>.() -> Unit)

    fun <C2> include(context: C2, spec: TypeSpec<C2>)

    fun <C2> includeForEach(context: Iterable<C2>, body: TypeBuilder<C2>.() -> Unit)

    fun <C2> includeForEach(context: Iterable<C2>, spec: TypeSpec<C2>)
}
