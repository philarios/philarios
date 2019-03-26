package io.philarios.structurizr

class WorkspaceSpec<in C>(internal val body: WorkspaceBuilder<C>.() -> Unit)

class ModelSpec<in C>(internal val body: ModelBuilder<C>.() -> Unit)

class PersonSpec<in C>(internal val body: PersonBuilder<C>.() -> Unit)
