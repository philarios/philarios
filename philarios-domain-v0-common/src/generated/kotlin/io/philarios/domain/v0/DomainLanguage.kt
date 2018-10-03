package io.philarios.domain.v0

import io.philarios.core.v0.Builder
import io.philarios.core.v0.DslBuilder
import io.philarios.core.v0.Registry
import io.philarios.core.v0.Scaffold
import io.philarios.core.v0.Translator
import io.philarios.core.v0.Wrapper
import kotlinx.coroutines.experimental.launch
import kotlin.String
import kotlin.collections.Iterable
import kotlin.collections.List

data class Domain(val entities: List<Entity>, val relationships: List<Relationship>)

data class DomainShell(var entities: List<Scaffold<Entity>> = emptyList(), var relationships: List<Scaffold<Relationship>> = emptyList()) : Scaffold<Domain> {
    override suspend fun resolve(registry: Registry): Domain {
        kotlinx.coroutines.experimental.coroutineScope {
        	entities.forEach { launch { it.resolve(registry) } }
        	relationships.forEach { launch { it.resolve(registry) } }
        }
        val value = Domain(entities.map { it.resolve(registry) },relationships.map { it.resolve(registry) })
        return value
    }
}

class DomainRef(key: String) : Scaffold<Domain> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Domain::class, key)

class DomainTemplate<in C>(private val spec: DomainSpec<C>, private val context: C) : Builder<Domain> {
    constructor(body: DomainBuilder<C>.() -> Unit, context: C) : this(io.philarios.domain.v0.DomainSpec<C>(body), context)

    override fun scaffold(): Scaffold<Domain> {
        val builder = DomainBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class DomainSpec<in C>(internal val body: DomainBuilder<C>.() -> Unit)

@DslBuilder
class DomainBuilder<out C>(val context: C, internal var shell: DomainShell = DomainShell()) {
    fun <C> DomainBuilder<C>.entity(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(entities = shell.entities.orEmpty() + EntityTemplate<C>(body, context).scaffold())
    }

    fun <C> DomainBuilder<C>.entity(spec: EntitySpec<C>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + EntityTemplate<C>(spec, context).scaffold())
    }

    fun <C> DomainBuilder<C>.entity(ref: EntityRef) {
        shell = shell.copy(entities = shell.entities.orEmpty() + ref)
    }

    fun <C> DomainBuilder<C>.entity(entity: Entity) {
        shell = shell.copy(entities = shell.entities.orEmpty() + Wrapper(entity))
    }

    fun <C> DomainBuilder<C>.entities(entities: List<Entity>) {
        shell = shell.copy(entities = shell.entities.orEmpty() + entities.map { Wrapper(it) })
    }

    fun <C> DomainBuilder<C>.relationship(body: RelationshipBuilder<C>.() -> Unit) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipTemplate<C>(body, context).scaffold())
    }

    fun <C> DomainBuilder<C>.relationship(spec: RelationshipSpec<C>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + RelationshipTemplate<C>(spec, context).scaffold())
    }

    fun <C> DomainBuilder<C>.relationship(ref: RelationshipRef) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + ref)
    }

    fun <C> DomainBuilder<C>.relationship(relationship: Relationship) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + Wrapper(relationship))
    }

    fun <C> DomainBuilder<C>.relationships(relationships: List<Relationship>) {
        shell = shell.copy(relationships = shell.relationships.orEmpty() + relationships.map { Wrapper(it) })
    }

    fun <C> DomainBuilder<C>.include(body: DomainBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> DomainBuilder<C>.include(spec: DomainSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> DomainBuilder<C>.include(context: C2, body: DomainBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> DomainBuilder<C>.include(context: C2, spec: DomainSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> DomainBuilder<C>.includeForEach(context: Iterable<C2>, body: DomainBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> DomainBuilder<C>.includeForEach(context: Iterable<C2>, spec: DomainSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): DomainBuilder<C2> = DomainBuilder(context, shell)

    private fun <C2> merge(other: DomainBuilder<C2>) {
        this.shell = other.shell
    }
}

open class DomainTranslator<in C>(private val spec: DomainSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Domain> {
    constructor(body: DomainBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.domain.v0.DomainSpec<C>(body), registry)

    override suspend fun translate(context: C): Domain {
        val builder = DomainTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Entity(val name: String, val attributes: List<Attribute>)

data class EntityShell(var name: String? = null, var attributes: List<Scaffold<Attribute>> = emptyList()) : Scaffold<Entity> {
    override suspend fun resolve(registry: Registry): Entity {
        kotlinx.coroutines.experimental.coroutineScope {
        	attributes.forEach { launch { it.resolve(registry) } }
        }
        val value = Entity(name!!,attributes.map { it.resolve(registry) })
        registry.put(Entity::class, name!!, value)
        return value
    }
}

class EntityRef(key: String) : Scaffold<Entity> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Entity::class, key)

class EntityTemplate<in C>(private val spec: EntitySpec<C>, private val context: C) : Builder<Entity> {
    constructor(body: EntityBuilder<C>.() -> Unit, context: C) : this(io.philarios.domain.v0.EntitySpec<C>(body), context)

    override fun scaffold(): Scaffold<Entity> {
        val builder = EntityBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class EntitySpec<in C>(internal val body: EntityBuilder<C>.() -> Unit)

@DslBuilder
class EntityBuilder<out C>(val context: C, internal var shell: EntityShell = EntityShell()) {
    fun <C> EntityBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> EntityBuilder<C>.attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeTemplate<C>(body, context).scaffold())
    }

    fun <C> EntityBuilder<C>.attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeTemplate<C>(spec, context).scaffold())
    }

    fun <C> EntityBuilder<C>.attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    fun <C> EntityBuilder<C>.attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    fun <C> EntityBuilder<C>.attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    fun <C> EntityBuilder<C>.include(body: EntityBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> EntityBuilder<C>.include(spec: EntitySpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> EntityBuilder<C>.include(context: C2, body: EntityBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> EntityBuilder<C>.include(context: C2, spec: EntitySpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> EntityBuilder<C>.includeForEach(context: Iterable<C2>, body: EntityBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> EntityBuilder<C>.includeForEach(context: Iterable<C2>, spec: EntitySpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): EntityBuilder<C2> = EntityBuilder(context, shell)

    private fun <C2> merge(other: EntityBuilder<C2>) {
        this.shell = other.shell
    }
}

open class EntityTranslator<in C>(private val spec: EntitySpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Entity> {
    constructor(body: EntityBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.domain.v0.EntitySpec<C>(body), registry)

    override suspend fun translate(context: C): Entity {
        val builder = EntityTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Relationship(
        val name: String,
        val from: Entity,
        val to: Entity,
        val attributes: List<Attribute>
)

data class RelationshipShell(
        var name: String? = null,
        var from: Scaffold<Entity>? = null,
        var to: Scaffold<Entity>? = null,
        var attributes: List<Scaffold<Attribute>> = emptyList()
) : Scaffold<Relationship> {
    override suspend fun resolve(registry: Registry): Relationship {
        kotlinx.coroutines.experimental.coroutineScope {
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

class RelationshipTemplate<in C>(private val spec: RelationshipSpec<C>, private val context: C) : Builder<Relationship> {
    constructor(body: RelationshipBuilder<C>.() -> Unit, context: C) : this(io.philarios.domain.v0.RelationshipSpec<C>(body), context)

    override fun scaffold(): Scaffold<Relationship> {
        val builder = RelationshipBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class RelationshipSpec<in C>(internal val body: RelationshipBuilder<C>.() -> Unit)

@DslBuilder
class RelationshipBuilder<out C>(val context: C, internal var shell: RelationshipShell = RelationshipShell()) {
    fun <C> RelationshipBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> RelationshipBuilder<C>.from(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(from = EntityTemplate<C>(body, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.from(spec: EntitySpec<C>) {
        shell = shell.copy(from = EntityTemplate<C>(spec, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.from(ref: EntityRef) {
        shell = shell.copy(from = ref)
    }

    fun <C> RelationshipBuilder<C>.from(from: Entity) {
        shell = shell.copy(from = Wrapper(from))
    }

    fun <C> RelationshipBuilder<C>.to(body: EntityBuilder<C>.() -> Unit) {
        shell = shell.copy(to = EntityTemplate<C>(body, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.to(spec: EntitySpec<C>) {
        shell = shell.copy(to = EntityTemplate<C>(spec, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.to(ref: EntityRef) {
        shell = shell.copy(to = ref)
    }

    fun <C> RelationshipBuilder<C>.to(to: Entity) {
        shell = shell.copy(to = Wrapper(to))
    }

    fun <C> RelationshipBuilder<C>.attribute(body: AttributeBuilder<C>.() -> Unit) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeTemplate<C>(body, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.attribute(spec: AttributeSpec<C>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + AttributeTemplate<C>(spec, context).scaffold())
    }

    fun <C> RelationshipBuilder<C>.attribute(ref: AttributeRef) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + ref)
    }

    fun <C> RelationshipBuilder<C>.attribute(attribute: Attribute) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + Wrapper(attribute))
    }

    fun <C> RelationshipBuilder<C>.attributes(attributes: List<Attribute>) {
        shell = shell.copy(attributes = shell.attributes.orEmpty() + attributes.map { Wrapper(it) })
    }

    fun <C> RelationshipBuilder<C>.include(body: RelationshipBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> RelationshipBuilder<C>.include(spec: RelationshipSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> RelationshipBuilder<C>.include(context: C2, body: RelationshipBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> RelationshipBuilder<C>.include(context: C2, spec: RelationshipSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> RelationshipBuilder<C>.includeForEach(context: Iterable<C2>, body: RelationshipBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> RelationshipBuilder<C>.includeForEach(context: Iterable<C2>, spec: RelationshipSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): RelationshipBuilder<C2> = RelationshipBuilder(context, shell)

    private fun <C2> merge(other: RelationshipBuilder<C2>) {
        this.shell = other.shell
    }
}

open class RelationshipTranslator<in C>(private val spec: RelationshipSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Relationship> {
    constructor(body: RelationshipBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.domain.v0.RelationshipSpec<C>(body), registry)

    override suspend fun translate(context: C): Relationship {
        val builder = RelationshipTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

data class Attribute(val name: String, val type: Type)

data class AttributeShell(var name: String? = null, var type: Scaffold<Type>? = null) : Scaffold<Attribute> {
    override suspend fun resolve(registry: Registry): Attribute {
        kotlinx.coroutines.experimental.coroutineScope {
        	launch { type!!.resolve(registry) }
        }
        val value = Attribute(name!!,type!!.resolve(registry))
        return value
    }
}

class AttributeRef(key: String) : Scaffold<Attribute> by io.philarios.core.v0.RegistryRef(io.philarios.domain.v0.Attribute::class, key)

class AttributeTemplate<in C>(private val spec: AttributeSpec<C>, private val context: C) : Builder<Attribute> {
    constructor(body: AttributeBuilder<C>.() -> Unit, context: C) : this(io.philarios.domain.v0.AttributeSpec<C>(body), context)

    override fun scaffold(): Scaffold<Attribute> {
        val builder = AttributeBuilder<C>(context)
        builder.apply(spec.body)
        return builder.shell
    }
}

open class AttributeSpec<in C>(internal val body: AttributeBuilder<C>.() -> Unit)

@DslBuilder
class AttributeBuilder<out C>(val context: C, internal var shell: AttributeShell = AttributeShell()) {
    fun <C> AttributeBuilder<C>.name(name: String) {
        shell = shell.copy(name = name)
    }

    fun <C> AttributeBuilder<C>.type(type: Type) {
        shell = shell.copy(type = Wrapper(type))
    }

    fun <C> AttributeBuilder<C>.include(body: AttributeBuilder<C>.() -> Unit) {
        apply(body)
    }

    fun <C> AttributeBuilder<C>.include(spec: AttributeSpec<C>) {
        apply(spec.body)
    }

    fun <C, C2> AttributeBuilder<C>.include(context: C2, body: AttributeBuilder<C2>.() -> Unit) {
        val builder = split(context)
        builder.apply(body)
        merge(builder)
    }

    fun <C, C2> AttributeBuilder<C>.include(context: C2, spec: AttributeSpec<C2>) {
        val builder = split(context)
        builder.apply(spec.body)
        merge(builder)
    }

    fun <C, C2> AttributeBuilder<C>.includeForEach(context: Iterable<C2>, body: AttributeBuilder<C2>.() -> Unit) {
        context.forEach { include(it, body) }
    }

    fun <C, C2> AttributeBuilder<C>.includeForEach(context: Iterable<C2>, spec: AttributeSpec<C2>) {
        context.forEach { include(it, spec) }
    }

    private fun <C2> split(context: C2): AttributeBuilder<C2> = AttributeBuilder(context, shell)

    private fun <C2> merge(other: AttributeBuilder<C2>) {
        this.shell = other.shell
    }
}

open class AttributeTranslator<in C>(private val spec: AttributeSpec<C>, private val registry: Registry = io.philarios.core.v0.emptyRegistry()) : Translator<C, Attribute> {
    constructor(body: AttributeBuilder<C>.() -> Unit, registry: Registry = io.philarios.core.v0.emptyRegistry()) : this(io.philarios.domain.v0.AttributeSpec<C>(body), registry)

    override suspend fun translate(context: C): Attribute {
        val builder = AttributeTemplate<C>(spec, context)
        val scaffold = builder.scaffold()
        return scaffold.resolve(registry)
    }
}

enum class Type {
    Boolean,

    Int,

    Long,

    Float,

    Double,

    String
}
