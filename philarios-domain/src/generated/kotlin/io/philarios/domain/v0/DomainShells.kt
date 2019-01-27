package io.philarios.domain.v0

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class DomainShell(var entities: List<Scaffold<Entity>> = emptyList(), var relationships: List<Scaffold<Relationship>> = emptyList()) : Scaffold<Domain> {
    override suspend fun resolve(registry: Registry): Domain {
        coroutineScope {
        	entities.forEach { launch { it.resolve(registry) } }
        	relationships.forEach { launch { it.resolve(registry) } }
        }
        val value = Domain(entities.map { it.resolve(registry) },relationships.map { it.resolve(registry) })
        return value
    }
}

internal data class EntityShell(var name: String? = null, var attributes: List<Scaffold<Attribute>> = emptyList()) : Scaffold<Entity> {
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

internal data class RelationshipShell(
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

internal data class AttributeShell(var name: String? = null, var type: Scaffold<Type>? = null) : Scaffold<Attribute> {
    override suspend fun resolve(registry: Registry): Attribute {
        checkNotNull(name) { "Attribute is missing the name property" }
        checkNotNull(type) { "Attribute is missing the type property" }
        coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = Attribute(name!!,type!!.resolve(registry))
        return value
    }
}

internal data class TypeShell(var type: RawType? = null, var nullable: Boolean? = null) : Scaffold<Type> {
    override suspend fun resolve(registry: Registry): Type {
        checkNotNull(type) { "Type is missing the type property" }
        val value = Type(type!!,nullable)
        return value
    }
}
