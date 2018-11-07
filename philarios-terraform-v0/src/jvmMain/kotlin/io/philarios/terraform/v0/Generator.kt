package io.philarios.terraform.v0

import io.philarios.core.v0.emptyContext
import io.philarios.core.v0.map
import io.philarios.schema.v0.translators.codegen.SchemaCodegen

suspend fun main() {
    emptyContext()
            .map(TerraformSchemaSpec)
            .map(SchemaCodegen("./src/commonGenerated/kotlin"))
}
