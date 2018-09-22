package io.philarios.schema.v0

object SchemaSchemaSpec : SchemaSpec<Any?>({
    name("Schema")
    pkg("io.philarios.schema.v0")

    type(Struct {
        name("Schema")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("pkg")
            type(StringType {})
        })
        field(Field {
            name("types")
            type(ListType {
                type(RefType {
                    name("Type")
                })
            })
        })
        field(Field {
            name("references")
            type(ListType {
                type(RefType{
                    name("Schema")
                })
            })
        })
    })

    type(Union {
        name("Type")
        shape(Struct {
            name("Struct")
            field(Field {
                name("pkg")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("name")
                type(StringType {})
            })
            field(Field {
                name("fields")
                type(ListType {
                    type(RefType {
                        name("Field")
                    })
                })
            })
        })
        shape(Struct {
            name("Union")
            field(Field {
                name("pkg")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("name")
                type(StringType {})
            })
            field(Field {
                name("shapes")
                type(ListType {
                    type(RefType {
                        name("Struct")
                    })
                })
            })
        })
        shape(Struct {
            name("EnumType")
            field(Field {
                name("pkg")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("name")
                type(StringType {})
            })
            field(Field {
                name("values")
                type(ListType {
                    type(StringType {})
                })
            })
        })
        shape(Struct {
            name("RefType")
            field(Field {
                name("pkg")
                type(OptionType {
                    type(StringType {})
                })
            })
            field(Field {
                name("name")
                type(StringType {})
            })
        })
        shape(Struct {
            name("OptionType")
            field(Field {
                name("type")
                type(RefType {
                    name("Type")
                })
            })
        })
        shape(Struct {
            name("ListType")
            field(Field {
                name("type")
                type(RefType {
                    name("Type")
                })
            })
        })
        shape(Struct {
            name("MapType")
            field(Field {
                name("keyType")
                type(RefType {
                    name("Type")
                })
            })
            field(Field {
                name("valueType")
                type(RefType {
                    name("Type")
                })
            })
        })
        shape(Struct {
            name("BooleanType")
        })
        shape(Struct {
            name("DoubleType")
        })
        shape(Struct {
            name("FloatType")
        })
        shape(Struct {
            name("LongType")
        })
        shape(Struct {
            name("IntType")
        })
        shape(Struct {
            name("ShortType")
        })
        shape(Struct {
            name("ByteType")
        })
        shape(Struct {
            name("CharacterType")
        })
        shape(Struct {
            name("StringType")
        })
        shape(Struct {
            name("AnyType")
        })
    })

    type(Struct {
        name("Field")
        field(Field {
            name("name")
            type(StringType {})
        })
        field(Field {
            name("type")
            type(RefType {
                name("Type")
            })
        })
    })
})