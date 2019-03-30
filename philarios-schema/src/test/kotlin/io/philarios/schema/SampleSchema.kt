package io.philarios.schema

val sampleSpec = SchemaSpec<Any?> {
    pkg("io.philarios.schema")
    name("Sample")

    struct("Empty")

    include(primitivesSpecs)
    include(containerSpecs)
    include(mixedSpecs)
}

val primitivesSpecs = SchemaSpec<Any?> {
    struct("BooleanValue") {
        field("value") {
            type(BooleanType)
        }
    }
    struct("DoubleValue") {
        field("value") {
            type(DoubleType)
        }
    }
    struct("FloatValue") {
        field("value") {
            type(FloatType)
        }
    }
    struct("LongValue") {
        field("value") {
            type(LongType)
        }
    }
    struct("IntValue") {
        field("value") {
            type(IntType)
        }
    }
    struct("ShortValue") {
        field("value") {
            type(ShortType)
        }
    }
    struct("ByteValue") {
        field("value") {
            type(ByteType)
        }
    }
    struct("CharacterValue") {
        field("value") {
            type(CharacterType)
        }
    }
    struct("StringValue") {
        field("value") {
            type(StringType)
        }
    }
    struct("AnyValue") {
        field("value") {
            type(AnyType)
        }
    }
}

val containerSpecs = SchemaSpec<Any?> {
    struct("OptionValue") {
        field("value") {
            type(OptionTypeSpec {
                type(StringType)
            })
        }
    }
    struct("ListValue") {
        field("value") {
            type(ListTypeSpec {
                type(StringType)
            })
        }
    }
    struct("MapValue") {
        field("value") {
            type(MapTypeSpec {
                keyType(StringType)
                valueType(StringType)
            })
        }
    }
}

val mixedSpecs = SchemaSpec<Any?> {
    struct("Simple") {
        field("value") {
            type(StringType)
        }
    }

    struct("Complex") {
        field("value") {
            type(StringType)
        }
        field("age") {
            type(IntType)
        }
        field("done") {
            type(OptionTypeSpec {
                type(BooleanType)
            })
        }
        field("children") {
            type(ListTypeSpec {
                type(StructRef("Simple"))
            })
        }
        field("capitalCities") {
            type(MapTypeSpec {
                keyType(StringType)
                valueType(StringType)
            })
        }
        field("dataByKey") {
            type(MapTypeSpec {
                keyType(StringType)
                valueType(StructRef("Data"))
            })
        }
    }

    struct("Data") {
        field("x") {
            type(IntType)
        }
        field("y") {
            type(IntType)
        }
    }

    struct("Response") {
        field("errors") {
            type(ListTypeSpec {
                type(UnionRef("ServerError"))
            })
        }
    }

    union("ServerError") {
        shape("UnknownError")
        shape("InsufficientAmountError") {
            field("amount") {
                type(DoubleType)
            }
        }
    }
}
