package io.philarios.jsonschema

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import io.philarios.core.contextOf
import io.philarios.core.map
import io.philarios.core.unwrap
import io.philarios.schema.BooleanType
import io.philarios.schema.Schema
import io.philarios.schema.SchemaScaffolder
import io.philarios.util.tests.calling
import io.philarios.util.tests.with
import kotlinx.coroutines.runBlocking

class JsonSchemaSchemaSpec : FreeSpec({

    calling("jsonSchemaSchema") {
        with("empty JsonSchemaObject") {
            val exception = shouldThrow<UnsupportedJsonSchemaException> {
                runJsonSchemaSchema(JsonSchemaObject())
            }
            exception shouldBe unsupportedFields
        }
        with("JsonSchemaObject with null type") {
            val exception = shouldThrow<UnsupportedJsonSchemaException> {
                runJsonSchemaSchema(JsonSchemaObject(type = TypeSimpleType(SimpleType.`null`)))
            }
            exception shouldBe unsupportedNullType
        }
        with("JsonSchemaObject with boolean type") {
            val schema = runJsonSchemaSchema(JsonSchemaObject(type = TypeSimpleType(SimpleType.boolean)))
            val expected = Schema("", "", listOf(BooleanType))
            schema shouldBe expected
        }
    }

})

private fun runJsonSchemaSchema(jsonSchemaObject: JsonSchemaObject): Schema {
    return runBlocking {
        contextOf(jsonSchemaObject)
                .map(SchemaScaffolder(jsonSchemaSchema("", "")))
                .unwrap()
    }
}
