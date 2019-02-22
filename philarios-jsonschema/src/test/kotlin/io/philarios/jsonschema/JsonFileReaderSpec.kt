package io.philarios.jsonschema

import io.kotlintest.TestContext
import io.kotlintest.matchers.haveKey
import io.kotlintest.matchers.instanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.AbstractFreeSpec
import io.kotlintest.specs.FreeSpec

class JsonFileReaderSpec : FreeSpec({

    calling("read") {
        with("empty schema json") {
            val jsonSchema = executeRead("empty_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)
        }
        with("boolean schema json") {
            val jsonSchema = executeRead("boolean_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaBoolean::class)

            jsonSchema as JsonSchemaBoolean

            jsonSchema.value shouldBe true
        }
        with("properties schema json") {
            val jsonSchema = executeRead("properties_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! should haveKey("title")
        }
        with("properties with default schema json") {
            val jsonSchema = executeRead("properties_with_default_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! should haveKey("default")
            jsonSchema.properties!!["default"] shouldBe instanceOf(JsonSchemaBoolean::class)
        }
        with("complex properties schema json") {
            val jsonSchema = executeRead("complex_properties_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap<String, JsonSchema>()
        }
        with("properties and type schema json") {
            val jsonSchema = executeRead("properties_and_type_schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap<String, JsonSchema>()

            jsonSchema.type shouldNotBe null
            jsonSchema.type shouldBe instanceOf(TypeSimpleTypeArray::class)

            val type = jsonSchema.type as TypeSimpleTypeArray

            type.value.size shouldBe 2
        }
        with("schema json") {
            val jsonSchema = executeRead("schema.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap<String, JsonSchema>()
        }
        with("circleciconfig json") {
            val jsonSchema = executeRead("circleciconfig.json")
            jsonSchema shouldNotBe null
            jsonSchema shouldBe instanceOf(JsonSchemaObject::class)

            jsonSchema as JsonSchemaObject

            jsonSchema.properties shouldNotBe null
            jsonSchema.properties!! shouldNotBe emptyMap<String, JsonSchema>()
        }
    }

})

fun executeRead(filename: String): JsonSchema {
    val input = ClassLoader.getSystemResourceAsStream(filename)
    return read(input)
}

fun AbstractFreeSpec.calling(name: String, test: AbstractFreeSpec.FreeSpecScope.() -> Unit) {
    "calling $name" - test
}

fun AbstractFreeSpec.FreeSpecScope.with(name: String, test: TestContext.() -> Unit) {
    "with $name".invoke(test)
}