package io.philarios.schema

val schemaSchema = SchemaSpec {
    name("Schema")
    pkg("io.philarios.schema")

    struct("Schema") {
        field("pkg", StringType)
        field("name", StringType) {
            key(true)
        }
        field("types", list(ref("Type")))
    }

    union("Type") {
        shape("Struct") {
            field("pkg", option(StringType))
            field("name", StringType) {
                key(true)
            }
            field("fields", list(ref("Field")))
        }
        shape("Union") {
            field("pkg", option(StringType))
            field("name", StringType) {
                key(true)
            }
            field("shapes", list(ref("Struct")))
        }
        shape("EnumType") {
            field("pkg", option(StringType))
            field("name", StringType) {
                key(true)
            }
            field("values", list(StringType))
        }
        shape("RefType") {
            field("pkg", option(StringType))
            field("name", StringType) {
                key(true)
            }
        }
        shape("OptionType") {
            field("type", ref("Type"))
        }
        shape("ListType") {
            field("type", ref("Type"))
        }
        shape("MapType") {
            field("keyType", ref("Type"))
            field("valueType", ref("Type"))
        }
        shape("BooleanType")
        shape("DoubleType")
        shape("FloatType")
        shape("LongType")
        shape("IntType")
        shape("ShortType")
        shape("ByteType")
        shape("CharacterType")
        shape("StringType")
        shape("AnyType")
    }

    struct("Field") {
        field("name", StringType)
        field("key", option(BooleanType))
        field("singularName", option(StringType))
        field("type", ref("Type"))
    }
}