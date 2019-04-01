package io.philarios.structurizr

import io.philarios.schema.*

val structurizrSchema = SchemaSpec<Any?> {
    name("Structurizr")
    pkg("io.philarios.structurizr")

    struct("Workspace") {
        field("name", StringType)
        field("description", StringType)
        field("model", option(ref("Model")))
        field("viewSet", option(ref("ViewSet")))
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

    struct("ViewSet") {
        field("configuration", option(ref("Configuration")))
    }

    struct("Configuration") {
        field("branding", option(ref("Branding")))
        field("styles", option(ref("Styles")))
        field("terminology", option(ref("Terminology")))
    }

    struct("Branding") {
        field("logo", option(StringType))
        field("font", option(ref("Font")))
    }

    struct("Font") {
        field("name", StringType)
        field("url", option(StringType))
    }

    struct("Styles") {
        field("elements", option(list(ref("ElementStyle"))))
        field("relationships", option(list(ref("RelationshipStyle"))))
    }

    struct("ElementStyle") {
        field("tag", StringType)
        field("width", IntType)
        field("height", IntType)
        field("background", StringType)
        field("color", StringType)
        field("fontSize", IntType)
        field("shape", option(ref("Shape")))
        field("border", option(ref("Border")))
        field("opacity", option(IntType))
        field("metadata", option(BooleanType))
        field("description", option(BooleanType))
    }

    enum("Shape") {
        value("Box")
        value("RoundedBox")
        value("Circle")
        value("Ellipse")
        value("Hexagon")
        value("Cylinder")
        value("Pipe")
        value("Person")
        value("Robot")
        value("Folder")
        value("WebBrowser")
        value("MobileDevicePortrait")
        value("MobileDeviceLandscape")
    }

    enum("Border") {
        value("Solid")
        value("Dashed")
    }

    struct("RelationshipStyle") {
        field("tag", StringType)
        field("thickness", IntType)
        field("color", StringType)
        field("fontSize", IntType)
        field("width", IntType)
        field("dashed", BooleanType)
        field("routing", ref("Routing"))
        field("position", IntType)
        field("opacity", option(IntType))
    }

    enum("Routing") {
        value("Direct")
        value("Orthogonal")
    }

    struct("Terminology") {
        field("enterprise", option(StringType))
        field("person", option(StringType))
        field("softwareSystem", option(StringType))
        field("container", option(StringType))
        field("component", option(StringType))
        field("code", option(StringType))
        field("deploymentNode", option(StringType))
    }
}