package io.philarios.structurizr

import io.philarios.structurizr.usecases.putWorkspaceSpec

enum class Id(val value: String) {
    ADMIN("1"),
    USER("2"),
    TODO("3"),
    TODO_APP("31"),
    TODO_API("32"),
    TODO_API_CONTROLLER("321"),
    TODO_API_SERVICE("322"),
    TODO_API_REPOSITORY("323"),
    TODO_DATABASE("33"),
}

val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")

    model {
        people {
            id(Id.ADMIN)
            name("Admin")
            description("An admin user of the system")
            location(Location.Internal)
        }
        people {
            id(Id.USER)
            name("User")
            description("A normal user")
            location(Location.External)
        }
        softwareSystem {
            id(Id.TODO)
            name("Todo App")
            description("A simple todo app")

            container {
                id(Id.TODO_APP)
                name("app")
                description("displays the todo list")
                technology("Android")
            }
            container {
                id(Id.TODO_API)
                name("api")
                description("answers requests from the app")
                technology("Java")

                component {
                    id(Id.TODO_API_CONTROLLER)
                    name("controller")
                    description("answers requests from the app")
                    technology("Spring MVC")
                }
                component {
                    id(Id.TODO_API_SERVICE)
                    name("service")
                    description("contains the logic and use cases")
                    technology("Java")
                }
                component {
                    id(Id.TODO_API_REPOSITORY)
                    name("repository")
                    description("queries the database")
                    technology("JPA")

                    relationship {
                        destinationId(Id.TODO_DATABASE)
                        description("issues queries to the datbase")
                        technology("JDBC")
                        interactionStyle(InteractionStyle.Synchronous)
                    }
                }
            }
            container {
                id(Id.TODO_DATABASE)
                name("database")
                description("the main database")
                technology("MySQL")
            }
        }
    }
}

private fun <C> PersonBuilder<C>.id(id: Any) {
    id(id.toString())
}

private fun <C> SoftwareSystemBuilder<C>.id(id: Any) {
    id(id.toString())
}

private fun <C> ContainerBuilder<C>.id(id: Any) {
    id(id.toString())
}

private fun <C> ComponentBuilder<C>.id(id: Any) {
    id(id.toString())
}

private fun <C> RelationshipBuilder<C>.destinationId(id: Any) {
    destinationId(id.toString())
}

suspend fun main() = putWorkspaceSpec(workspace)