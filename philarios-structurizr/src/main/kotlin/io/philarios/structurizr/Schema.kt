package io.philarios.structurizr

import io.philarios.schema.*

val structurizrSchema = SchemaSpec<Any?> {
    name("Structurizr")
    pkg("io.philarios.structurizr")

    struct("Workspace") {
        field("name", StringType)
        field("description", option(StringType))
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
        field("description", option(StringType))
        field("location", option(ref("Location")))
        field("relationships", list(ref("Relationship")))
        field("tags", list(StringType))
    }

    struct("SoftwareSystem") {
        field("id", StringType)
        field("name", StringType)
        field("description", option(StringType))
        field("location", option(ref("Location")))
        field("containers", list(ref("Container")))
        field("relationships", list(ref("Relationship")))
        field("tags", list(StringType))
    }

    struct("Container") {
        field("id", StringType)
        field("name", StringType)
        field("description", option(StringType))
        field("technologies", list(StringType))
        field("components", list(ref("Component")))
        field("relationships", list(ref("Relationship")))
        field("tags", list(StringType))
    }

    struct("Component") {
        field("id", StringType)
        field("name", StringType)
        field("description", option(StringType))
        field("technologies", list(StringType))
        field("relationships", list(ref("Relationship")))
        field("tags", list(StringType))
    }

    struct("Relationship") {
        field("destinationId", StringType)
        field("description", option(StringType))
        field("technologies", list(StringType))
        field("interactionStyle", option(ref("InteractionStyle")))
        field("tags", list(StringType))
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
        field("systemLandscapeViews", option(list(ref("SystemLandscapeView"))))
        field("systemContextViews", option(list(ref("SystemContextView"))))
        field("containerViews", option(list(ref("ContainerView"))))
        field("componentViews", option(list(ref("ComponentView"))))
        field("dynamicViews", option(list(ref("DynamicView"))))
        field("configuration", option(ref("Configuration")))
    }

    struct("SystemLandscapeView") {
        field("key", StringType)
        field("description", option(StringType))
        field("title", option(StringType))
        field("paperSize", option(ref("PaperSize")))
        field("softwareSystems", option(list(StringType)))
        field("people", option(list(StringType))) {
            singularName("person")
        }
    }
    struct("SystemContextView") {
        field("softwareSystemId", StringType)
        field("key", StringType)
        field("description", option(StringType))
        field("title", option(StringType))
        field("paperSize", option(ref("PaperSize")))
        field("softwareSystems", option(list(StringType)))
        field("people", option(list(StringType))) {
            singularName("person")
        }
    }
    struct("ContainerView") {
        field("softwareSystemId", StringType)
        field("key", StringType)
        field("description", option(StringType))
        field("title", option(StringType))
        field("paperSize", option(ref("PaperSize")))
        field("softwareSystems", option(list(StringType)))
        field("people", option(list(StringType))) {
            singularName("person")
        }
        field("containers", option(list(StringType)))
    }
    struct("ComponentView") {
        field("containerId", StringType)
        field("key", StringType)
        field("description", option(StringType))
        field("title", option(StringType))
        field("paperSize", option(ref("PaperSize")))
        field("softwareSystems", option(list(StringType)))
        field("people", option(list(StringType))) {
            singularName("person")
        }
        field("containers", option(list(StringType)))
        field("components", option(list(StringType)))
    }
    struct("DynamicView") {
        field("key", StringType)
        field("description", option(StringType))
        field("title", option(StringType))
        field("paperSize", option(ref("PaperSize")))
    }

    // TODO implement deployment and filtered views
//    field("deploymentViews", option(list(ref("DeploymentView"))))
//    field("filteredViews", option(list(ref("FilteredView"))))
//    struct("DeploymentView") {
//        field("key", StringType)
//        field("description", option(StringType))
//        field("title", option(StringType))
//        field("paperSize", option(ref("PaperSize")))
//    }
//    struct("FilteredView") {
//        field("key", StringType)
//        field("description", option(StringType))
//        field("title", option(StringType))
//        field("paperSize", option(ref("PaperSize")))
//    }

    enum("PaperSize") {
        value("A6_Portrait")
        value("A6_Landscape")
        value("A5_Portrait")
        value("A5_Landscape")
        value("A4_Portrait")
        value("A4_Landscape")
        value("A3_Portrait")
        value("A3_Landscape")
        value("A2_Portrait")
        value("A2_Landscape")
        value("Letter_Portrait")
        value("Letter_Landscape")
        value("Legal_Portrait")
        value("Legal_Landscape")
        value("Slide_4_3")
        value("Slide_16_9")
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
        field("width", option(IntType))
        field("height", option(IntType))
        field("background", option(StringType))
        field("color", option(StringType))
        field("fontSize", option(IntType))
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
        field("thickness", option(IntType))
        field("color", option(StringType))
        field("fontSize", option(IntType))
        field("width", option(IntType))
        field("dashed", option(BooleanType))
        field("routing", option(ref("Routing")))
        field("position", option(IntType))
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