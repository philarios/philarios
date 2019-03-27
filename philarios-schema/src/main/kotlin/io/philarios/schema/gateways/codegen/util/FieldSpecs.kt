package io.philarios.schema.gateways.codegen.util

import io.philarios.schema.Field

val Field.singularName
    get() = when {
        name.endsWith("ies") -> name.removeSuffix("ies").plus("y")
        else -> name.removeSuffix("s")
    }

val Field.escapedName
    get() = name.escaped
