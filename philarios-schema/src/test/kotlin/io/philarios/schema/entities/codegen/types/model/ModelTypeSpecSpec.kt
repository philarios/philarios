package io.philarios.schema.entities.codegen.types.model

import com.squareup.kotlinpoet.TypeSpec
import io.philarios.schema.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ModelTypeSpecSpec : Spek({

    describe("modelTypeSpec") {
        it("works for enum types") {
            val type = EnumType("pkg", "name", listOf("banana", "apple", "orange"))
            val typeSpec = type.modelTypeSpec

            typeSpec.name shouldBe "name"
            typeSpec.enumConstants.keys shouldContainAll listOf("banana", "apple", "orange")
        }
        it("works for a struct type with no fields") {
            val struct = Struct("pkg", "name", emptyList())
            val typeSpec = struct.modelTypeSpec()

            typeSpec.name shouldBe "name"
            typeSpec.kind shouldBe TypeSpec.Kind.OBJECT
        }
        it("works for struct types with fields") {
            val struct = Struct("pkg", "name", listOf(Field("", false, StringType)))
            val typeSpec = struct.modelTypeSpec()

            typeSpec.name shouldBe "name"
            typeSpec.kind shouldBe TypeSpec.Kind.CLASS
        }
        it("works for union types") {
            val union = Union("pkg", "name", emptyList())
            val typeSpecs = union.modelTypeSpecs

            typeSpecs.map { it.kind } shouldContain TypeSpec.Kind.CLASS
        }
    }

})
