package io.philarios.terraform.v0

import io.philarios.core.v0.emptyContext
import io.philarios.core.v0.map
import io.philarios.core.v0.unwrap
import io.philarios.schema.v0.translators.codegen.SchemaCodegen
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<kotlin.String>) {
    runBlocking {
        emptyContext()
                .map(TerraformSchemaSpec)
                .unwrap(SchemaCodegen("../philarios-terraform-v0-common/src/generated/kotlin"))
    }
}
