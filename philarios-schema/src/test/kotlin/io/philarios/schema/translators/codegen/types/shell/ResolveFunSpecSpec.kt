package io.philarios.schema.translators.codegen.types.shell

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.philarios.schema.Field
import io.philarios.schema.StringType
import io.philarios.schema.Struct
import io.philarios.util.tests.calling
import io.philarios.util.tests.with

class ResolveFunSpecSpec : FreeSpec({

    calling("resolveJobStatement") {
        with("struct without fields") {
            val type = Struct("pkg", "Name", emptyList())
            val statement = type.resolveJobStatement
            statement shouldBe null
        }
        with("struct with fields") {
            val type = Struct("pkg", "Name", listOf(Field("", false, StringType)))
            val statement = type.resolveJobStatement
            statement shouldBe "launch { it.resolve(registry) }"
        }
    }

})