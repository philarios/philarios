package io.philarios.filesystem.v0

import io.philarios.schema.v0.*

val fileSystemSchema = SchemaSpec<Any?> {
    name("FileSystem")
    pkg("io.philarios.filesystem.v0")

    type(UnionSpec {
        name("Entry")
        shape(StructSpec {
            name("Directory")
            field(FieldSpec {
                name("name")
                type(StringType)
            })
            field(FieldSpec {
                name("entries")
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Entry")
                    })
                })
            })
        })
        shape(StructSpec {
            name("File")
            field(FieldSpec {
                name("name")
                type(StringType)
            })
        })
    })

}