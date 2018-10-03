package io.philarios.schema.v0

object TestSchemaSpec : SchemaSpec<Any?>({
    name("Test")
    pkg("io.philarios.test.v0")

    type(StructSpec {
        name("Foo")
        field(FieldSpec {
            name("name")
            key(true)
            type(StringTypeSpec {})
        })
    })

    type(StructSpec {
        name("Bar")
        field(FieldSpec {
            name("name")
            key(true)
            type(StringTypeSpec {})
        })
        field {
            name("foo")
            type(StructRef("Foo"))
        }
    })

    type(StructSpec {
        name("Test")
        field {
            name("bars")
            type(ListTypeSpec {
                type(StructRef("Bar"))
            })
        }
        field {
            name("foos")
            type(ListTypeSpec {
                type(StructRef("Foo"))
            })
        }
    })

})