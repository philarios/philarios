package io.philarios.domain

import io.philarios.core.Scaffold
import kotlin.String

class DomainRef(key: String) : Scaffold<Domain> by io.philarios.core.RegistryRef(io.philarios.domain.Domain::class, key)

class EntityRef(key: String) : Scaffold<Entity> by io.philarios.core.RegistryRef(io.philarios.domain.Entity::class, key)

class RelationshipRef(key: String) : Scaffold<Relationship> by io.philarios.core.RegistryRef(io.philarios.domain.Relationship::class, key)

class AttributeRef(key: String) : Scaffold<Attribute> by io.philarios.core.RegistryRef(io.philarios.domain.Attribute::class, key)

class TypeRef(key: String) : Scaffold<Type> by io.philarios.core.RegistryRef(io.philarios.domain.Type::class, key)
