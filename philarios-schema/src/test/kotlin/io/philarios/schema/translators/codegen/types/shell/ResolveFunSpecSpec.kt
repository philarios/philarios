package io.philarios.schema.translators.codegen.types.shell

import io.philarios.schema.Field
import io.philarios.schema.StringType
import io.philarios.schema.Struct
import org.amshove.kluent.shouldBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ResolveFunSpecSpec : Spek({

    describe("resolveJobStatement") {
        it("works with structs without fields") {
            val type = Struct("pkg", "Name", emptyList())
            val statement = type.resolveJobStatement
            statement shouldBe null
        }
        it("works with structs with fields") {
            val type = Struct("pkg", "Name", listOf(Field("", false, StringType)))
            val statement = type.resolveJobStatement
            statement shouldBe "launch { it.resolve(registry) }"
        }
    }

})