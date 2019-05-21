package io.philarios.structurizr

import io.philarios.structurizr.sugar.*
import io.philarios.structurizr.usecases.putWorkspaceSpec

val workspace = WorkspaceSpec {
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

    documentation {
        decision {
            id("test")
            elementId(TodoId.ROOT.hierarchicalId())
            date("2019-04-19")
            title("Test the decision log")
            status(DecisionStatus.Proposed)
            content("asdf")
            format(Format.Markdown)
        }
    }
}

private fun ModelBuilder.people() {
    person(PersonId.ADMIN) {
        name("Admin")
        description("An admin user of the system")
        internal()

        relationshipTo(IndexingId.ROOT) {
            description("manages the indexing logic")
            technology("brain")
        }
    }
    person(PersonId.USER) {
        name("User")
        description("A normal user")
        external()
    }
}

private fun ModelBuilder.indexingSoftwareSystem() {
    softwareSystem(IndexingId.ROOT) {
        name("Indexing")
        description("Indexes the todo list for machine learning magic")
        internal()

        relationshipTo(TodoId.ROOT) {
            description("reads todo list from")
            technology("Kafka")
            asynchronous()
        }
    }
}

private fun ModelBuilder.todoSoftwareSystem() {
    softwareSystem {
        id(TodoId.ROOT)
        name("Todo App")
        description("A simple todo app")
        internal()

        relationshipFrom(PersonId.USER) {
            description("uses the app")
            technology("fingers")
        }

        container(TodoId.APP) {
            name("app")
            description("displays the todo list")
            technology("Android")

            relationshipTo(TodoId.API) {
                description("sends requests to the API")
                technology("HTTP")
                technology("REST")
            }
        }
        container(TodoId.API) {
            name("api")
            description("answers requests from the app")
            technology("Java")

            relationshipTo(TodoId.DATABASE) {
                description("issues queries to the database")
                technology("JDBC")
            }

            component(TodoId.Api.CONTROLLER) {
                name("controller")
                description("answers requests from the app")
                technology("Spring MVC")
            }
            component(TodoId.Api.SERVICE) {
                name("service")
                description("contains the logic and use cases")
                technology("Java")
            }
            component(TodoId.Api.REPOSITORY) {
                name("repository")
                description("queries the database")
                technology("JPA")

                relationshipTo(TodoId.DATABASE) {
                    description("issues queries to the datbase")
                    technology("JDBC")
                    synchronous()
                }
            }
        }
        container(TodoId.DATABASE) {
            name("database")
            description("the main database")
            technology("MySQL")
            tag("database")
        }
    }
}

private fun ViewSetBuilder.landscapeView() {
    systemLandscapeView {
        id(SYSTEM)
        description("Shows entire software landscape of the todo system")

        softwareSystem(TodoId.ROOT)
        softwareSystem(IndexingId.ROOT)
        person(PersonId.ADMIN)
        person(PersonId.USER)
    }
}

private fun ViewSetBuilder.todoViews() {
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