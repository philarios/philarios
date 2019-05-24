package io.philarios.jsonschema.entities

import io.philarios.core.resolve
import io.philarios.schema.BooleanType
import io.philarios.schema.Schema
import io.philarios.schema.SchemaScaffolder
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class JsonSchemaSchemaSpec : Spek({

    describe("jsonSchemaSchema") {
        it("throws an exception for an empty JsonSchemaObject") {
            invoking {
                runJsonSchemaSchema(JsonSchemaObject())
            } shouldThrow unsupportedFields
        }
        it("throws an exception for a JsonSchemaObject with null type") {
            invoking {
                runJsonSchemaSchema(JsonSchemaObject(type = TypeSimpleType(SimpleType.`null`)))
            } shouldThrow unsupportedNullType
        }
        it("works for a JsonSchemaObject with boolean type") {
            val schema = runJsonSchemaSchema(JsonSchemaObject(type = TypeSimpleType(SimpleType.boolean)))
            val expected = Schema("", "", listOf(BooleanType))
            schema shouldEqual expected
        }
    }

})

private fun runJsonSchemaSchema(jsonSchemaObject: JsonSchemaObject): Schema {
    return runBlocking {
        SchemaScaffolder(jsonSchemaSchema("", "", jsonSchemaObject))
                .createScaffold()
                .resolve()
    }
}
