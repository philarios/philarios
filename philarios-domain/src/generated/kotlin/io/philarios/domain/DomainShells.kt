package io.philarios.domain

import io.philarios.core.Scaffold
import io.philarios.util.registry.Registry
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class DomainShell(var entities: List<Scaffold<Entity>>? = null, var relationships: List<Scaffold<Relationship>>? = null) : Scaffold<Domain> {
    override suspend fun resolve(registry: Registry): Domain {
        coroutineScope {
            entities?.let{ it.forEach { launch { it.resolve(registry) } } }
            relationships?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Domain(
            entities.orEmpty().let{ it.map { it.resolve(registry) } },
            relationships.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class EntityShell(var name: Scaffold<String>? = null, var attributes: List<Scaffold<Attribute>>? = null) : Scaffold<Entity> {
    override suspend fun resolve(registry: Registry): Entity {
        checkNotNull(name) { "Entity is missing the name property" }
        coroutineScope {
            attributes?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Entity(
            name!!.let{ it.resolve(registry) },
            attributes.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Entity::class, value.name, value)
        return value
    }
}

internal data class RelationshipShell(
        var name: Scaffold<String>? = null,
        var from: Scaffold<Entity>? = null,
        var to: Scaffold<Entity>? = null,
        var attributes: List<Scaffold<Attribute>>? = null
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        checkNotNull(name) { "Relationship is missing the name property" }
        checkNotNull(from) { "Relationship is missing the from property" }
        checkNotNull(to) { "Relationship is missing the to property" }
        coroutineScope {
            from?.let{ launch { it.resolve(registry) } }
            to?.let{ launch { it.resolve(registry) } }
            attributes?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Relationship(
            name!!.let{ it.resolve(registry) },
            from!!.let{ it.resolve(registry) },
            to!!.let{ it.resolve(registry) },
            attributes.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        registry.put(Relationship::class, value.name, value)
        return value
    }
}

internal data class AttributeShell(var name: Scaffold<String>? = null, var type: Scaffold<Type>? = null) : Scaffold<Attribute> {
    override suspend fun resolve(registry: Registry): Attribute {
        checkNotNull(name) { "Attribute is missing the name property" }
        checkNotNull(type) { "Attribute is missing the type property" }
        coroutineScope {
            type?.let{ launch { it.resolve(registry) } }
        }
        val value = Attribute(
            name!!.let{ it.resolve(registry) },
            type!!.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class TypeShell(var type: Scaffold<RawType>? = null, var nullable: Scaffold<Boolean>? = null) : Scaffold<Type> {
    override suspend fun resolve(registry: Registry): Type {
        checkNotNull(type) { "Type is missing the type property" }
        val value = Type(
            type!!.let{ it.resolve(registry) },
            nullable?.let{ it.resolve(registry) }
        )
        return value
    }
}
