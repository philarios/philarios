package io.philarios.filesystem

import io.philarios.schema.*

val fileSystemSchema = SchemaSpec {
    name("FileSystem")
    pkg("io.philarios.filesystem")

    union("Entry") {
        shape("Directory") {
            field("name", StringType)
            field("entries", list(ref("Entry")))
        }
        shape("File") {
            field("name", StringType)
            field("content", StringType)
        }
    }
}