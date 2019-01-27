package io.philarios.schema

val schemaSchema = SchemaSpec<Any?> {
    name("Schema")
    pkg("io.philarios.schema")

    type(StructSpec {
        name("Schema")
        field(FieldSpec {
            name("pkg")
            type(StringType)
        })
        field(FieldSpec {
            name("name")
            key(true)
            type(StringType)
        })
        field(FieldSpec {
            name("types")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Type")
                })
            })
        })
//        field(FieldSpec {
//            name("references")
//            type(ListTypeSpec {
//                type(RefTypeSpec {
//                    name("Schema")
//                })
//            })
//        })
    })

    type(UnionSpec {
        name("Type")
        shape(StructSpec {
            name("Struct")
            field(FieldSpec {
                name("pkg")
                type(OptionTypeSpec {
                    type(StringType)
                })
            })
            field(FieldSpec {
                name("name")
                key(true)
                type(StringType)
            })
            field(FieldSpec {
                name("fields")
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Field")
                    })
                })
            })
        })
        shape(StructSpec {
            name("Union")
            field(FieldSpec {
                name("pkg")
                type(OptionTypeSpec {
                    type(StringType)
                })
            })
            field(FieldSpec {
                name("name")
                key(true)
                type(StringType)
            })
            field(FieldSpec {
                name("shapes")
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Struct")
                    })
                })
            })
        })
        shape(StructSpec {
            name("EnumType")
            field(FieldSpec {
                name("pkg")
                type(OptionTypeSpec {
                    type(StringType)
                })
            })
            field(FieldSpec {
                name("name")
                key(true)
                type(StringType)
            })
            field(FieldSpec {
                name("values")
                type(ListTypeSpec {
                    type(StringType)
                })
            })
        })
        shape(StructSpec {
            name("RefType")
            field(FieldSpec {
                name("pkg")
                type(OptionTypeSpec {
                    type(StringType)
                })
            })
            field(FieldSpec {
                name("name")
                key(true)
                type(StringType)
            })
        })
        shape(StructSpec {
            name("OptionType")
            field(FieldSpec {
                name("type")
                type(RefTypeSpec {
                    name("Type")
                })
            })
        })
        shape(StructSpec {
            name("ListType")
            field(FieldSpec {
                name("type")
                type(RefTypeSpec {
                    name("Type")
                })
            })
        })
        shape(StructSpec {
            name("MapType")
            field(FieldSpec {
                name("keyType")
                type(RefTypeSpec {
                    name("Type")
                })
            })
            field(FieldSpec {
                name("valueType")
                type(RefTypeSpec {
                    name("Type")
                })
            })
        })
        shape(StructSpec {
            name("BooleanType")
        })
        shape(StructSpec {
            name("DoubleType")
        })
        shape(StructSpec {
            name("FloatType")
        })
        shape(StructSpec {
            name("LongType")
        })
        shape(StructSpec {
            name("IntType")
        })
        shape(StructSpec {
            name("ShortType")
        })
        shape(StructSpec {
            name("ByteType")
        })
        shape(StructSpec {
            name("CharacterType")
        })
        shape(StructSpec {
            name("StringType")
        })
        shape(StructSpec {
            name("AnyType")
        })
    })

    type(StructSpec {
        name("Field")
        field(FieldSpec {
            name("name")
            type(StringType)
        })
        field(FieldSpec {
            name("key")
            type(OptionTypeSpec {
                type(BooleanType)
            })
        })
        field(FieldSpec {
            name("type")
            type(RefTypeSpec {
                name("Type")
            })
        })
    })
}