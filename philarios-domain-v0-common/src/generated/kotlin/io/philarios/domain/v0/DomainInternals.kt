package io.philarios.domain.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Spec
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class DomainShell(var entities: List<Scaffold<Entity>> = emptyList(), var relationships: List<Scaffold<Relationship>> = emptyList()) : Scaffold<Domain> {
    override suspend fun resolve(registry: Registry): Domain {
        coroutineScope {
        	entities.forEach { launch { it.resolve(registry) } }
        	relationships.forEach { launch { it.resolve(registry) } }
        }
        val value = Domain(entities.map { it.resolve(registry) },relationships.map { it.resolve(registry) })
        return value
    }
}

class DomainRef(key: String) : Scaffold<Domain> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Domain::class, key)

open class DomainSpec<in C>(internal val body: DomainBuilder<C>.() -> Unit) : Spec<C, Domain> {
    override fun connect(context: C): Scaffold<Domain> {
        val builder = DomainBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class EntityShell(var name: String? = null, var attributes: List<Scaffold<Attribute>> = emptyList()) : Scaffold<Entity> {
    override suspend fun resolve(registry: Registry): Entity {
        coroutineScope {
        	attributes.forEach { launch { it.resolve(registry) } }
        }
        val value = Entity(name!!,attributes.map { it.resolve(registry) })
        registry.put(Entity::class, name!!, value)
        return value
    }
}

class EntityRef(key: String) : Scaffold<Entity> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Entity::class, key)

open class EntitySpec<in C>(internal val body: EntityBuilder<C>.() -> Unit) : Spec<C, Entity> {
    override fun connect(context: C): Scaffold<Entity> {
        val builder = EntityBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class RelationshipShell(
        var name: String? = null,
        var from: Scaffold<Entity>? = null,
        var to: Scaffold<Entity>? = null,
        var attributes: List<Scaffold<Attribute>> = emptyList()
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        coroutineScope {
        	launch { from!!.resolve(registry) }
        	launch { to!!.resolve(registry) }
        	attributes.forEach { launch { it.resolve(registry) } }
        }
        val value = Relationship(name!!,from!!.resolve(registry),to!!.resolve(registry),attributes.map { it.resolve(registry) })
        registry.put(Relationship::class, name!!, value)
        return value
    }
}

class RelationshipRef(key: String) : Scaffold<Relationship> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Relationship::class, key)

open class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit) : Spec<C, Relationship> {
    override fun connect(context: C): Scaffold<Relationship> {
        val builder = RelationshipBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}

data class AttributeShell(var name: String? = null, var type: Type? = null) : Scaffold<Attribute> {
    override suspend fun resolve(registry: Registry): Attribute {
        val value = Attribute(name!!,type!!)
        return value
    }
}

class AttributeRef(key: String) : Scaffold<Attribute> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Attribute::class, key)

open class AttributeSpec<in C>(internal val body: AttributeBuilder<C>.() -> Unit) : Spec<C, Attribute> {
    override fun connect(context: C): Scaffold<Attribute> {
        val builder = AttributeBuilder<C>(context)
        builder.apply(body)
        return builder.shell
    }
}
