package io.philarios.filesystem.v0

import io.philarios.schema.v0.*

object FileSystemSchemaSpec : SchemaSpec<Any?>({
    name("FileSystem")
    pkg("io.philarios.filesystem.v0")

    type(Union {
        name("Entry")
        shape(Struct {
            name("Directory")
            field(Field {
                name("name")
                type(StringType {})
            })
            field(Field {
                name("entries")
                type(ListType {
                    type(RefType {
                        name("Entry")
                    })
                })
            })
        })
        shape(Struct {
            name("File")
            field(Field {
                name("name")
                type(StringType {})
            })
        })
    })

})