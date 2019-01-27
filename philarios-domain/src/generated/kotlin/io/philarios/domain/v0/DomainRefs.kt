package io.philarios.domain.v0

import io.philarios.core.Scaffold
import kotlin.String

class DomainRef(key: String) : Scaffold<Domain> by io.philarios.core.RegistryRef(io.philarios.domain.v0.Domain::class, key)

class EntityRef(key: String) : Scaffold<Entity> by io.philarios.core.RegistryRef(io.philarios.domain.v0.Entity::class, key)

class RelationshipRef(key: String) : Scaffold<Relationship> by io.philarios.core.RegistryRef(io.philarios.domain.v0.Relationship::class, key)

class AttributeRef(key: String) : Scaffold<Attribute> by io.philarios.core.RegistryRef(io.philarios.domain.v0.Attribute::class, key)

class TypeRef(key: String) : Scaffold<Type> by io.philarios.core.RegistryRef(io.philarios.domain.v0.Type::class, key)
