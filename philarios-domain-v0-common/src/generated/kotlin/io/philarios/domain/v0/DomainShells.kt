package io.philarios.domain.v0

import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
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

data class EntityShell(var name: String? = null, var attributes: List<Scaffold<Attribute>> = emptyList()) : Scaffold<Entity> {
    override suspend fun resolve(registry: Registry): Entity {
        checkNotNull(name) { "Entity is missing the name property" }
        coroutineScope {
        	attributes.forEach { launch { it.resolve(registry) } }
        }
        val value = Entity(name!!,attributes.map { it.resolve(registry) })
        registry.put(Entity::class, name!!, value)
        return value
    }
}

data class RelationshipShell(
        var name: String? = null,
        var from: Scaffold<Entity>? = null,
        var to: Scaffold<Entity>? = null,
        var attributes: List<Scaffold<Attribute>> = emptyList()
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        checkNotNull(name) { "Relationship is missing the name property" }
        checkNotNull(from) { "Relationship is missing the from property" }
        checkNotNull(to) { "Relationship is missing the to property" }
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

data class AttributeShell(var name: String? = null, var type: Type? = null) : Scaffold<Attribute> {
    override suspend fun resolve(registry: Registry): Attribute {
        checkNotNull(name) { "Attribute is missing the name property" }
        checkNotNull(type) { "Attribute is missing the type property" }
        val value = Attribute(name!!,type!!)
        return value
    }
}
