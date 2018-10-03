package io.philarios.schema.v0

import io.philarios.core.v0.TableRegistry
import io.philarios.core.v0.emptyContext
import io.philarios.core.v0.map
import io.philarios.core.v0.unwrap
import io.philarios.schema.v0.translators.codegen.SchemaCodegen
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<kotlin.String>) {
    runBlocking {
        emptyContext()
                .map(SchemaTranslator(SchemaSchemaSpec, TableRegistry()))
                .unwrap(SchemaCodegen("../philarios-schema-v0-common/src/generated/kotlin"))

        emptyContext()
                .map(SchemaTranslator(TestSchemaSpec))
                .unwrap(SchemaCodegen("../philarios-schema-v0-common/src/generated/kotlin"))
    }
}