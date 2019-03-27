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
        field("people", list(ref("Person")))
        field("softwareSystems", list(ref("SoftwareSystem")))
    }

    struct("Person") {
        field("name", StringType)
        field("description", StringType)
        field("location", option(ref("Location")))
    }

    struct("SoftwareSystem") {
        field("name", StringType)
        field("description", StringType)
        field("location", option(ref("Location")))
        field("containers", list(ref("Container")))
    }

    struct("Container") {
        field("name", StringType)
        field("description", StringType)
        field("technology", StringType)
        field("components", list(ref("Component")))
    }

    struct("Component") {
        field("name", StringType)
        field("description", StringType)
        field("technology", StringType)
    }

    enum("Location") {
        value("Internal")
        value("External")
        value("Unspecified")
    }
}