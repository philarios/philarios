package io.philarios.schema

import io.philarios.schema.translators.generateInto

val sampleSpec = SchemaSpec<Any?> {
    pkg("io.philarios.schema.sample")
    name("Sample")

    struct("Empty")

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
                type(UnionRef("Error"))
            })
        }
    }

    union("Error") {
        shape("UnknownError")
        shape("InsufficientAmountError") {
            field("amount") {
                type(DoubleType)
            }
        }
    }

}

private fun <C> SchemaBuilder<C>.struct(name: String, body: StructBuilder<C>.() -> Unit = {}) {
    type(StructSpec {
        name(name)
        apply(body)
    })
}

private fun <C> SchemaBuilder<C>.union(name: String, body: UnionBuilder<C>.() -> Unit = {}) {
    type(UnionSpec {
        name(name)
        apply(body)
    })
}

private fun <C> UnionBuilder<C>.shape(name: String, body: StructBuilder<C>.() -> Unit = {}) {
    shape {
        name(name)
        apply(body)
    }
}

private fun <C> StructBuilder<C>.field(name: String, body: FieldBuilder<C>.() -> Unit = {}) {
    field {
        name(name)
        apply(body)
    }
}

suspend fun main() = sampleSpec.generateInto("./src/samples/kotlin")