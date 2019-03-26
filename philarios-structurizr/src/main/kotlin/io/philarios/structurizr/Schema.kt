package io.philarios.structurizr

import io.philarios.schema.*
import io.philarios.schema.sugar.field
import io.philarios.schema.sugar.struct

val structurizrSchema = SchemaSpec<Any?> {
    name("Structurizr")
    pkg("io.philarios.structurizr")

    struct("Workspace") {
        field("name", StringType)
        field("description", StringType)
    }
}