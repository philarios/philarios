package io.philarios.concourse.v0

import io.philarios.core.v0.emptyContext
import io.philarios.core.v0.map
import io.philarios.core.v0.unwrap
import io.philarios.schema.v0.SchemaTranslator
import io.philarios.schema.v0.translators.codegen.SchemaCodegen
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<kotlin.String>) {
    runBlocking {
        emptyContext()
                .map(SchemaTranslator(ConcourseSchemaSpec))
                .unwrap(SchemaCodegen("../philarios-concourse-v0-common/src/generated/kotlin"))
    }
}