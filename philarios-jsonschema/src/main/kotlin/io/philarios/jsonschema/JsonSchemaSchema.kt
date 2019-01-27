package io.philarios.jsonschema

import io.philarios.schema.*

val jsonSchemaSchema = SchemaSpec<Any?> {
    name("JsonSchema")
    pkg("io.philarios.jsonschema")

    type(StructSpec {
        name("JsonSchema")
        stringOptionField("id")
        stringOptionField("schema") // TODO fix keys with invalid variable names in them
        stringOptionField("title")
        stringOptionField("description")
        field {
            name("definitions")
            type(MapTypeSpec {
                keyType(StringType)
                valueType(RefTypeSpec {
                    name("JsonSchema")
                })
            })
        }

    })

}

private fun StructBuilder<Any?>.stringOptionField(name: String) {
    field {
        name(name)
        type(OptionTypeSpec {
            type(StringType)
        })
    }
}

fun StructBuilder<Any?>.stringField(name: String) {
    field {
        name(name)
        type(StringType)
    }
}
