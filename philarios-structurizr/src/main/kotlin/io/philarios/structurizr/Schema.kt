package io.philarios.structurizr

import io.philarios.schema.*

val structurizrSchema = SchemaSpec<Any?> {
    name("Structurizr")
    pkg("io.philarios.structurizr")

    struct("Workspace") {
        field("name", StringType)
        field("description", StringType)
        field("model", option(ref("Model")))
    }

    struct("Model") {
        field("people", list(ref("Person"))) {
            singularName("person")
        }
        field("softwareSystems", list(ref("SoftwareSystem")))
    }

    struct("Person") {
        field("id", StringType)
        field("name", StringType)
        field("description", StringType)
        field("location", option(ref("Location")))
        field("relationships", list(ref("Relationship")))
    }

    struct("SoftwareSystem") {
        field("id", StringType)
        field("name", StringType)
        field("description", StringType)
        field("location", option(ref("Location")))
        field("containers", list(ref("Container")))
        field("relationships", list(ref("Relationship")))
    }

    struct("Container") {
        field("id", StringType)
        field("name", StringType)
        field("description", StringType)
        field("technology", StringType)
        field("components", list(ref("Component")))
        field("relationships", list(ref("Relationship")))
    }

    struct("Component") {
        field("id", StringType)
        field("name", StringType)
        field("description", StringType)
        field("technology", StringType)
        field("relationships", list(ref("Relationship")))
    }

    struct("Relationship") {
        field("destinationId", StringType)
        field("description", StringType)
        field("technology", StringType)
        field("interactionStyle", ref("InteractionStyle"))
    }

    enum("Location") {
        value("Internal")
        value("External")
        value("Unspecified")
    }

    enum("InteractionStyle") {
        value("Synchronous")
        value("Asynchronous")
    }
}