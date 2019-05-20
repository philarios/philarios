package io.philarios.structurizr.sugar

import io.philarios.structurizr.Role
import io.philarios.structurizr.WorkspaceConfigurationBuilder

fun WorkspaceConfigurationBuilder.readWriteUsers(vararg users: String) {
    readWriteUsers(users.toList())
}

fun WorkspaceConfigurationBuilder.readWriteUsers(users: List<String>) {
    users.forEach { user ->
        user {
            username(user)
            role(Role.ReadWrite)
        }
    }
}