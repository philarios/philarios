package io.philarios.schema.translators.codegen.types.spec

import io.philarios.schema.Union

internal val Union.specTypeSpecs get() = shapes.map { it.specTypeSpec }
