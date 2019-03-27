package io.philarios.domain.v0

class DomainSpec<in C>(internal val body: DomainBuilder<C>.() -> Unit)

class EntitySpec<in C>(internal val body: EntityBuilder<C>.() -> Unit)

class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit)

class AttributeSpec<in C>(internal val body: AttributeBuilder<C>.() -> Unit)

class TypeSpec<in C>(internal val body: TypeBuilder<C>.() -> Unit)
