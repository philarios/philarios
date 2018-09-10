package io.philarios.layout.v0

import io.philarios.canvas.v0.CanvasSchemaSpec
import io.philarios.schema.v0.*

object LayoutSchemaSpec : SchemaSpec<Any?>({
    name("Layout")
    pkg("io.philarios.layout.v0")

    reference(CanvasSchemaSpec)

    type(Struct {
        name("Box")
        field(Field {
            name("key")
            type(StringType {})
        })
        field(Field {
            name("children")
            type(ListType {
                type(RefType {
                    name("Box")
                })
            })
        })
        field(Field {
            name("constraints")
            type(MapType {
                keyType(RefType {
                    name("ConstraintType")
                })
                valueType(RefType {
                    name("ConstraintValue")
                })
            })
        })
        field(Field {
            name("background")
            type(OptionType {
                type(RefType {
                    name("BoxBackground")
                })
            })
        })
        field(Field {
            name("text")
            type(OptionType {
                type(RefType {
                    name("BoxText")
                })
            })
        })
    })

    type(Struct {
        name("BoxBackground")
        field(Field {
            name("color")
            type(RefType {
                pkg("io.philarios.canvas.v0")
                name("Color")
            })
        })
    })

    type(Struct {
        name("BoxText")
        field(Field {
            name("color")
            type(RefType {
                pkg("io.philarios.canvas.v0")
                name("Color")
            })
        })
    })

    type(EnumType {
        name("ConstraintType")
        value("LEFT")
        value("CENTER")
        value("RIGHT")
        value("WIDTH")
        value("TOP")
        value("MIDDLE")
        value("BOTTOM")
        value("HEIGHT")

        value("TRANSLATE_X")
        value("TRANSLATE_Y")
        value("SCALE_X")
        value("SCALE_Y")
    })

    type(Union {
        name("ConstraintValue")
        shape(Struct {
            name("Scalar")
            field(Field {
                name("value")
                type(DoubleType {})
            })
        })
        shape(Struct {
            name("Linear")
            field(Field {
                name("partner")
                type(StringType {})
            })
            field(Field {
                name("type")
                type(RefType {
                    name("ConstraintType")
                })
            })
            field(Field {
                name("offset")
                type(OptionType {
                    type(DoubleType {})
                })
            })
            field(Field {
                name("multiplier")
                type(OptionType {
                    type(DoubleType {})
                })
            })
        })
    })

})