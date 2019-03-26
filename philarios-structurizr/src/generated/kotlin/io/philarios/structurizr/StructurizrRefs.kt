package io.philarios.structurizr

import io.philarios.core.Scaffold
import kotlin.String

class WorkspaceRef(key: String) : Scaffold<Workspace> by io.philarios.core.RegistryRef(io.philarios.structurizr.Workspace::class, key)

class ModelRef(key: String) : Scaffold<Model> by io.philarios.core.RegistryRef(io.philarios.structurizr.Model::class, key)

class PersonRef(key: String) : Scaffold<Person> by io.philarios.core.RegistryRef(io.philarios.structurizr.Person::class, key)

class SoftwareSystemRef(key: String) : Scaffold<SoftwareSystem> by io.philarios.core.RegistryRef(io.philarios.structurizr.SoftwareSystem::class, key)

class ContainerRef(key: String) : Scaffold<Container> by io.philarios.core.RegistryRef(io.philarios.structurizr.Container::class, key)
