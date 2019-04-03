package io.philarios.structurizr

import io.philarios.structurizr.sugar.*
import io.philarios.structurizr.usecases.putWorkspaceSpec

val workspace = WorkspaceSpec<Any?> {
    name("Test")
    description("This is a test workspace")

    model {
        people()
        todoSoftwareSystem()
        indexingSoftwareSystem()
    }

    viewSet {
        landscapeView()
        todoViews()
    }
}

private fun ModelBuilder<Any?>.people() {
    person {
        id(Id.Person.ADMIN)
        name("Admin")
        description("An admin user of the system")
        location(Location.Internal)

        relationship {
            destinationId(Id.INDEXING)
            description("manages the indexing logic")
            technology("brain")
            interactionStyle(InteractionStyle.Asynchronous)
        }
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
}

private fun ModelBuilder<Any?>.indexingSoftwareSystem() {
    softwareSystem {
        id(Id.INDEXING)
        name("Indexing")
        description("Indexes the todo list for machine learning magic")
        location(Location.Internal)

        relationship {
            destinationId(Id.TODO)
            description("reads todo list from")
            technology("Kafka")
            interactionStyle(InteractionStyle.Asynchronous)
        }
    }
}

private fun ModelBuilder<Any?>.todoSoftwareSystem() {
    softwareSystem {
        id(Id.TODO)
        name("Todo App")
        description("A simple todo app")
        location(Location.Internal)

        container {
            id(Id.Todo.APP)
            name("app")
            description("displays the todo list")
            technology("Android")

            relationship {
                destinationId(Id.Todo.API)
                description("sends requests to the API")
                technology("REST")
                interactionStyle(InteractionStyle.Synchronous)
            }
        }
        container {
            id(Id.Todo.API)
            name("api")
            description("answers requests from the app")
            technology("Java")

            relationship {
                destinationId(Id.Todo.DATABASE)
                description("issues queries to the database")
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

private fun ViewSetBuilder<Any?>.landscapeView() {
    systemLandscapeView {
        id(SYSTEM)
        description("Shows entire software landscape of the todo system")

        softwareSystem(Id.TODO)
        softwareSystem(Id.INDEXING)
        person(Id.Person.ADMIN)
        person(Id.Person.USER)
    }
}

private fun ViewSetBuilder<Any?>.todoViews() {
    systemContextView {
        id(Id.TODO)
        description("Shows the context of the todo system")

        person(Id.Person.USER)
    }
    containerView {
        id(Id.TODO)
        description("Shows the containers of the todo system")

        container(Id.Todo.API)
        container(Id.Todo.APP)
        container(Id.Todo.DATABASE)
    }
    componentView {
        id(Id.Todo.API)
        description("The internal's of the api container")

        component(Id.Todo.Api.CONTROLLER)
        component(Id.Todo.Api.SERVICE)
        component(Id.Todo.Api.REPOSITORY)
    }
}

suspend fun main() = putWorkspaceSpec(workspace)

const val SYSTEM = "System"

enum class Id {
    TODO,
    INDEXING;

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