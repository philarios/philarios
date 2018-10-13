package io.philarios.domain.v0

import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

data class Domain(val entities: List<Entity>, val relationships: List<Relationship>)

data class Entity(val name: String, val attributes: List<Attribute>)

data class Relationship(
        val name: String,
        val from: Entity,
        val to: Entity,
        val attributes: List<Attribute>
)

data class Attribute(val name: String, val type: Type)

enum class Type {
    Boolean,

    Int,

    Long,

    Float,

    Double,

    String
}
