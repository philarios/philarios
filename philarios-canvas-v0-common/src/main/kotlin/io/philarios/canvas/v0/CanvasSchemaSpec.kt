package io.philarios.canvas.v0

import io.philarios.schema.v0.*

object CanvasSchemaSpec : SchemaSpec<Any?>({
    name("Canvas")
    pkg("io.philarios.canvas.v0")

    type(Struct {
        name("CanvasRoot")
        field(Field {
            name("tree")
            type(OptionType {
                type(RefType {
                    name("CanvasTree")
                })
            })
        })
    })

    type(Union {
        name("CanvasTree")
        shape(Struct {
            name("CanvasNode")
            field(Field {
                name("transform")
                type(OptionType {
                    type(RefType {
                        name("Transform")
                    })
                })
            })
            field(Field {
                name("children")
                type(ListType {
                    type(RefType {
                        name("CanvasTree")
                    })
                })
            })
        })
        shape(Struct {
            name("CanvasLeaf")
            field(Field {
                name("transform")
                type(OptionType {
                    type(RefType {
                        name("Transform")
                    })
                })
            })
            field(Field {
                name("canvas")
                type(RefType {
                    name("Canvas")
                })
            })
        })
    })

    type(Struct {
        name("Canvas")
        field(Field {
            name("paths")
            type(ListType {
                type(RefType {
                    name("TransformedPath")
                })
            })
        })
    })

    type(Struct {
        name("TransformedPath")
        field(Field {
            name("transform")
            type(OptionType {
                type(RefType {
                    name("Transform")
                })
            })
        })
        field(Field {
            name("path")
            type(RefType {
                name("Path")
            })
        })
    })

    type(Struct {
        name("Path")
        field(Field {
            name("color")
            type(RefType {
                name("Color")
            })
        })
        field(Field {
            name("verbs")
            type(ListType {
                type(RefType {
                    name("Verb")
                })
            })
        })
        field(Field {
            name("method")
            type(RefType {
                name("Method")
            })
        })
    })

    type(Struct {
        name("Color")
        field(Field {
            name("red")
            type(DoubleType {})
        })
        field(Field {
            name("green")
            type(DoubleType {})
        })
        field(Field {
            name("blue")
            type(DoubleType {})
        })
        field(Field {
            name("alpha")
            type(DoubleType {})
        })
    })

    type(Struct {
        name("Transform")
        field(Field {
            name("a")
            type(DoubleType {})
        })
        field(Field {
            name("b")
            type(DoubleType {})
        })
        field(Field {
            name("c")
            type(DoubleType {})
        })
        field(Field {
            name("d")
            type(DoubleType {})
        })
        field(Field {
            name("e")
            type(DoubleType {})
        })
        field(Field {
            name("f")
            type(DoubleType {})
        })
    })

    type(Union {
        name("Method")
        shape(Struct {
            name("Fill")
        })
        shape(Struct {
            name("Stroke")
            field(Field {
                name("lineWidth")
                type(DoubleType {})
            })
        })
    })

    type(Union {
        name("Verb")
        shape(Struct {
            name("MoveTo")
            field(Field {
                name("x")
                type(DoubleType {})
            })
            field(Field {
                name("y")
                type(DoubleType {})
            })
        })
        shape(Struct {
            name("LineTo")
            field(Field {
                name("x")
                type(DoubleType {})
            })
            field(Field {
                name("y")
                type(DoubleType {})
            })
        })
        shape(Struct {
            name("Arc")
            field(Field {
                name("x")
                type(DoubleType {})
            })
            field(Field {
                name("y")
                type(DoubleType {})
            })
            field(Field {
                name("radius")
                type(DoubleType {})
            })
            field(Field {
                name("startAngle")
                type(DoubleType {})
            })
            field(Field {
                name("endAngle")
                type(DoubleType {})
            })
            field(Field {
                name("anticlockwise")
                type(BooleanType {})
            })
        })
        shape(Struct {
            name("ArcTo")
            field(Field {
                name("x1")
                type(DoubleType {})
            })
            field(Field {
                name("y1")
                type(DoubleType {})
            })
            field(Field {
                name("x2")
                type(DoubleType {})
            })
            field(Field {
                name("y2")
                type(DoubleType {})
            })
            field(Field {
                name("radius")
                type(DoubleType {})
            })
        })
    })

})