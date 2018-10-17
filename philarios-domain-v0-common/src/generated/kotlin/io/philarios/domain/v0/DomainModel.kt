package io.philarios.domain.v0

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

data class Domain(val entities: List<Entity>, val relationships: List<Relationship>)

data class Entity(val name: String, val attributes: List<Attribute>)

data class Relationship(
        val name: String,
        val from: Entity,
        val to: Entity,
        val attributes: List<Attribute>
)

data class Attribute(val name: String, val type: Type)

data class Type(val type: RawType, val nullable: Boolean?)

enum class RawType {
    Boolean,

    Int,

    Long,

    Float,

    Double,

    String
}
