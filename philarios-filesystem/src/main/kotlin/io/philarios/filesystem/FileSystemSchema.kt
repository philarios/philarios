package io.philarios.filesystem

import io.philarios.schema.*

val fileSystemSchema = SchemaSpec<Any?> {
    name("FileSystem")
    pkg("io.philarios.filesystem.v0")

    type(UnionSpec {
        name("Entry")
        shape {
            name("Directory")
            field {
                name("name")
                type(StringType)
            }
            field {
                name("entries")
                type(ListTypeSpec {
                    type(RefTypeSpec {
                        name("Entry")
                    })
                })
            }
        }
        shape {
            name("File")
            field {
                name("name")
                type(StringType)
            }
            field {
                name("content")
                type(StringType)
            }
        }
    })

}