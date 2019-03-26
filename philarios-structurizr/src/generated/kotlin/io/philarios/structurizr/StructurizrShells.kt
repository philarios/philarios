package io.philarios.structurizr

import io.philarios.core.Registry
import io.philarios.core.Scaffold
import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal data class WorkspaceShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var model: Scaffold<Model>? = null
) : Scaffold<Workspace> {
    override suspend fun resolve(registry: Registry): Workspace {
        checkNotNull(name) { "Workspace is missing the name property" }
        checkNotNull(description) { "Workspace is missing the description property" }
        coroutineScope {
            model?.let{ launch { it.resolve(registry) } }
        }
        val value = Workspace(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            model?.let{ it.resolve(registry) }
        )
        return value
    }
}

internal data class ModelShell(var people: List<Scaffold<Person>>? = null) : Scaffold<Model> {
    override suspend fun resolve(registry: Registry): Model {
        coroutineScope {
            people?.let{ it.forEach { launch { it.resolve(registry) } } }
        }
        val value = Model(
            people.orEmpty().let{ it.map { it.resolve(registry) } }
        )
        return value
    }
}

internal data class PersonShell(
        var name: Scaffold<String>? = null,
        var description: Scaffold<String>? = null,
        var location: Scaffold<Location>? = null
) : Scaffold<Person> {
    override suspend fun resolve(registry: Registry): Person {
        checkNotNull(name) { "Person is missing the name property" }
        checkNotNull(description) { "Person is missing the description property" }
        val value = Person(
            name!!.let{ it.resolve(registry) },
            description!!.let{ it.resolve(registry) },
            location?.let{ it.resolve(registry) }
        )
        return value
    }
}
