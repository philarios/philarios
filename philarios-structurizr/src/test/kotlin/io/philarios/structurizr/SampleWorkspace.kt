package io.philarios.structurizr

import io.philarios.structurizr.usecases.putWorkspaceSpec

enum class Id {
    TODO;

    enum class Person {
        ADMIN,
        USER
    }

    enum class Todo {
        APP,
        API,
        DATABASE;

        enum class Api {
            CONTROLLER,
            SERVICE,
            REPOSITORY
        }
    }
}

val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")

    model {
        person {
            id(Id.Person.ADMIN)
            name("Admin")
            description("An admin user of the system")
            location(Location.Internal)
        }
        person {
            id(Id.Person.USER)
            name("User")
            description("A normal user")
            location(Location.External)

            relationship {
                destinationId(Id.TODO)
                description("uses the app")
                technology("fingers")
                interactionStyle(InteractionStyle.Synchronous)
            }
        }
        softwareSystem {
            id(Id.TODO)
            name("Todo App")
            description("A simple todo app")

            container {
                id(Id.Todo.APP)
                name("app")
                description("displays the todo list")
                technology("Android")
            }
            container {
                id(Id.Todo.API)
                name("api")
                description("answers requests from the app")
                technology("Java")

                relationship {
                    destinationId(Id.Todo.DATABASE)
                    description("issues queries to the datbase")
                    technology("JDBC")
                    interactionStyle(InteractionStyle.Synchronous)
                }

                component {
                    id(Id.Todo.Api.CONTROLLER)
                    name("controller")
                    description("answers requests from the app")
                    technology("Spring MVC")
                }
                component {
                    id(Id.Todo.Api.SERVICE)
                    name("service")
                    description("contains the logic and use cases")
                    technology("Java")
                }
                component {
                    id(Id.Todo.Api.REPOSITORY)
                    name("repository")
                    description("queries the database")
                    technology("JPA")

                    relationship {
                        destinationId(Id.Todo.DATABASE)
                        description("issues queries to the datbase")
                        technology("JDBC")
                        interactionStyle(InteractionStyle.Synchronous)
                    }
                }
            }
            container {
                id(Id.Todo.DATABASE)
                name("database")
                description("the main database")
                technology("MySQL")
            }
        }
    }
}

private fun <C> PersonBuilder<C>.id(id: Any) {
    id(id.id())
}

private fun <C> SoftwareSystemBuilder<C>.id(id: Any) {
    id(id.id())
}

private fun <C> ContainerBuilder<C>.id(id: Any) {
    id(id.id())
}

private fun <C> ComponentBuilder<C>.id(id: Any) {
    id(id.id())
}

private fun <C> RelationshipBuilder<C>.destinationId(id: Any) {
    destinationId(id.id())
}

private fun Any.id(): String {
    val packageName = javaClass.`package`.name
    val className = javaClass.canonicalName
    val prefix = className.removePrefix("$packageName.")
    val name = this.toString().toLowerCase().capitalize()
    return "$prefix.$name"
}

suspend fun main() = putWorkspaceSpec(workspace)