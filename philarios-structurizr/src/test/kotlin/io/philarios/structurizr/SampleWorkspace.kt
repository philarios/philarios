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

        configuration {
            styles {
                element {
                    tag("Person")
                    shape(Shape.Person)
                }
                element {
                    tag("database")
                    shape(Shape.Cylinder)
                }
            }
        }
    }
}

private fun ModelBuilder<Any?>.people() {
    person {
        id(PersonId.ADMIN)
        name("Admin")
        description("An admin user of the system")
        location(Location.Internal)

        relationship {
            destinationId(IndexingId.ROOT)
            description("manages the indexing logic")
            technology("brain")
        }
    }
    person {
        id(PersonId.USER)
        name("User")
        description("A normal user")
        location(Location.External)

        relationship {
            destinationId(TodoId.ROOT)
            description("uses the app")
            technology("fingers")
        }
    }
}

private fun ModelBuilder<Any?>.indexingSoftwareSystem() {
    softwareSystem {
        id(IndexingId.ROOT)
        name("Indexing")
        description("Indexes the todo list for machine learning magic")
        location(Location.Internal)

        relationship {
            destinationId(TodoId.ROOT)
            description("reads todo list from")
            technology("Kafka")
        }
    }
}

private fun ModelBuilder<Any?>.todoSoftwareSystem() {
    softwareSystem {
        id(TodoId.ROOT)
        name("Todo App")
        description("A simple todo app")
        location(Location.Internal)

        container {
            id(TodoId.APP)
            name("app")
            description("displays the todo list")
            technology("Android")

            relationship {
                destinationId(TodoId.API)
                description("sends requests to the API")
                technology("HTTP")
                technology("REST")
            }
        }
        container {
            id(TodoId.API)
            name("api")
            description("answers requests from the app")
            technology("Java")

            relationship {
                destinationId(TodoId.DATABASE)
                description("issues queries to the database")
                technology("JDBC")
            }

            component {
                id(TodoId.Api.CONTROLLER)
                name("controller")
                description("answers requests from the app")
                technology("Spring MVC")
            }
            component {
                id(TodoId.Api.SERVICE)
                name("service")
                description("contains the logic and use cases")
                technology("Java")
            }
            component {
                id(TodoId.Api.REPOSITORY)
                name("repository")
                description("queries the database")
                technology("JPA")

                relationship {
                    destinationId(TodoId.DATABASE)
                    description("issues queries to the datbase")
                    technology("JDBC")
                }
            }
        }
        container {
            id(TodoId.DATABASE)
            name("database")
            description("the main database")
            technology("MySQL")
            tag("database")
        }
    }
}

private fun ViewSetBuilder<Any?>.landscapeView() {
    systemLandscapeView {
        id(SYSTEM)
        description("Shows entire software landscape of the todo system")

        softwareSystem(TodoId.ROOT)
        softwareSystem(IndexingId.ROOT)
        person(PersonId.ADMIN)
        person(PersonId.USER)
    }
}

private fun ViewSetBuilder<Any?>.todoViews() {
    systemContextView {
        id(TodoId.ROOT)
        description("Shows the context of the todo system")

        person(PersonId.USER)
    }
    containerView {
        id(TodoId.ROOT)
        description("Shows the containers of the todo system")

        containers(TodoId.values())
    }
    componentView {
        id(TodoId.API)
        description("The internal's of the api container")

        components(TodoId.Api.values())
    }
}

suspend fun main() = putWorkspaceSpec(workspace)

const val SYSTEM = "System"

enum class PersonId {
    ADMIN, USER
}

enum class TodoId {
    ROOT, APP, API, DATABASE;

    enum class Api {
        CONTROLLER, SERVICE, REPOSITORY
    }
}

enum class IndexingId {
    ROOT
}