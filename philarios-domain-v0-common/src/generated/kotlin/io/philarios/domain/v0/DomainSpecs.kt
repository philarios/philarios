package io.philarios.domain.v0

import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec

open class DomainSpec<in C>(internal val body: DomainBuilder<C>.() -> Unit) : Spec<C, Domain> {
    override fun connect(context: C): Scaffold<Domain> {
        val builder = DomainBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class EntitySpec<in C>(internal val body: EntityBuilder<C>.() -> Unit) : Spec<C, Entity> {
    override fun connect(context: C): Scaffold<Entity> {
        val builder = EntityBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit) : Spec<C, Relationship> {
    override fun connect(context: C): Scaffold<Relationship> {
        val builder = RelationshipBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

open class AttributeSpec<in C>(internal val body: AttributeBuilder<C>.() -> Unit) : Spec<C, Attribute> {
    override fun connect(context: C): Scaffold<Attribute> {
        val builder = AttributeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
