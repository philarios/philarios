package io.philarios.diagram.v0

import io.philarios.schema.v0.*

object DiagramSchemaSpec : SchemaSpec<Any?>({
    name("Diagram")
    pkg("io.philarios.diagram.v0")

    type(Struct {
        name("Diagram")
        field(Field {
            name("size")
            type(RefType {
                name("Bounds")
            })
        })
        field(Field {
            name("shapes")
            type(ListType {
                type(RefType {
                    name("Shape")
                })
            })
        })
    })

    type(Union {
        name("Shape")
        shape(Struct {
            name("Rectangle")
            field(Field {
                name("origin")
                type(RefType {
                    name("Point")
                })
            })
            field(Field {
                name("size")
                type(RefType {
                    name("Bounds")
                })
            })
        })
    })

    type(Struct {
        name("Point")
        field(Field {
            name("x")
            type(DoubleType {})
        })
        field(Field {
            name("y")
            type(DoubleType {})
        })
    })

    type(Struct {
        name("Bounds")
        field(Field {
            name("width")
            type(DoubleType {})
        })
        field(Field {
            name("height")
            type(DoubleType {})
        })
    })

})