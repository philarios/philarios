package io.philarios.jsonschema.gateways.reader

import io.philarios.jsonschema.entities.JsonSchema
import io.philarios.jsonschema.entities.JsonSchemaBoolean
import io.philarios.jsonschema.entities.JsonSchemaObject
import io.philarios.jsonschema.entities.TypeSimpleTypeArray
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldHaveKey
import org.amshove.kluent.shouldNotBe
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class JsonSchemaReaderSpec : Spek({

    describe("readJsonSchema") {
        it("empty schema json") {
            val jsonSchema = executeRead("empty_schema.json")
            
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class
        }
        it("boolean schema json") {
            val jsonSchema = executeRead("boolean_schema.json")
            
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaBoolean::class

            jsonSchema as JsonSchemaBoolean

            jsonSchema.value shouldBe true
        }
        it("properties schema json") {
            val jsonSchema = executeRead("properties_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldHaveKey "title"
        }
        it("properties with default schema json") {
            val jsonSchema = executeRead("properties_with_default_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldHaveKey "default"
            jsonSchema.properties!!["default"] shouldBeInstanceOf JsonSchemaBoolean::class
        }
        it("complex properties schema json") {
            val jsonSchema = executeRead("complex_properties_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap()
        }
        it("properties and type schema json") {
            val jsonSchema = executeRead("properties_and_type_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap()

            jsonSchema.type shouldNotBe null
            jsonSchema.type shouldBeInstanceOf TypeSimpleTypeArray::class

            val type = jsonSchema.type as TypeSimpleTypeArray

            type.value.size shouldBe 2
        }
        it("schema json") {
            val jsonSchema = executeRead("schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBeInstanceOf JsonSchemaObject::class

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap()
        }
    }

})

fun executeRead(filename: String): JsonSchema {
    val input = ClassLoader.getSystemResourceAsStream(filename)
    return readJsonSchema(input)
}
