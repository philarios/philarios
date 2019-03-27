package io.philarios.domain

import io.philarios.core.Scaffold
import io.philarios.core.Scaffolder

class DomainScaffolder<in C>(internal val spec: DomainSpec<C>) : Scaffolder<C, Domain> {
    override fun createScaffold(context: C): Scaffold<Domain> {
        val builder = DomainShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class EntityScaffolder<in C>(internal val spec: EntitySpec<C>) : Scaffolder<C, Entity> {
    override fun createScaffold(context: C): Scaffold<Entity> {
        val builder = EntityShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class RelationshipScaffolder<in C>(internal val spec: RelationshipSpec<C>) : Scaffolder<C, Relationship> {
    override fun createScaffold(context: C): Scaffold<Relationship> {
        val builder = RelationshipShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class AttributeScaffolder<in C>(internal val spec: AttributeSpec<C>) : Scaffolder<C, Attribute> {
    override fun createScaffold(context: C): Scaffold<Attribute> {
        val builder = AttributeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

class TypeScaffolder<in C>(internal val spec: TypeSpec<C>) : Scaffolder<C, Type> {
    override fun createScaffold(context: C): Scaffold<Type> {
        val builder = TypeShellBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}
