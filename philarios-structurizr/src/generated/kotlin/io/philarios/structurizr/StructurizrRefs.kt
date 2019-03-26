package io.philarios.structurizr

import io.philarios.core.Scaffold
import kotlin.String

class WorkspaceRef(key: String) : Scaffold<Workspace> by io.philarios.core.RegistryRef(io.philarios.structurizr.Workspace::class, key)
