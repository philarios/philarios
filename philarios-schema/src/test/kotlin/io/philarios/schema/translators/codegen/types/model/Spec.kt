package io.philarios.schema.translators.codegen.types.model

import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.assertSoftly
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.philarios.schema.*
import io.philarios.util.tests.calling
import io.philarios.util.tests.with

class Spec : FreeSpec({

    calling("modelTypeSpec") {
        with("enum type") {
            val type = EnumType("pkg", "name", listOf("banana", "apple", "orange"))
            val typeSpec = type.modelTypeSpec

            assertSoftly {
                typeSpec.name shouldBe "name"
                typeSpec.enumConstants.keys shouldContainAll listOf("banana", "apple", "orange")
            }
        }

        with("struct type with no fields") {
            val struct = Struct("pkg", "name", emptyList())
            val typeSpec = struct.modelTypeSpec()

            assertSoftly {
                typeSpec.name shouldBe "name"
                typeSpec.kind shouldBe TypeSpec.Kind.OBJECT
            }
        }

        with("struct type with fields") {
            val struct = Struct("pkg", "name", listOf(Field("", false, StringType)))
            val typeSpec = struct.modelTypeSpec()

            assertSoftly {
                typeSpec.name shouldBe "name"
                typeSpec.kind shouldBe TypeSpec.Kind.CLASS
            }
        }

        with("union type") {
            val union = Union("pkg", "name", emptyList())
            val typeSpecs = union.modelTypeSpecs

            assertSoftly {
                typeSpecs.forOne {
                    it.kind shouldBe TypeSpec.Kind.CLASS
                }
            }
        }
    }

})
