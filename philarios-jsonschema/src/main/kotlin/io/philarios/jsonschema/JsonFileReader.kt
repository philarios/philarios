package io.philarios.jsonschema

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream

fun read(inputStream: InputStream): JsonSchema {


    val deserializerModule = SimpleModule()
            .addDeserializer(JsonSchema::class.java, object : JsonDeserializer<JsonSchema>() {
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonSchema {
                    val node = p.codec.readTree<JsonNode>(p)
                    return when (node) {
                        is BooleanNode -> JsonSchemaBoolean(node.booleanValue())
                        is ObjectNode -> p.codec.treeToValue(node, JsonSchemaObject::class.java)
                        else -> throw RuntimeException()
                    }
                }
            })
            .addDeserializer(Items::class.java, object : JsonDeserializer<Items>() {
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Items {
                    val node = p.codec.readTree<JsonNode>(p)
                    return when (node) {
                        is ObjectNode -> ItemsJsonSchema(p.codec.treeToValue(node, JsonSchema::class.java))
                        is BooleanNode -> ItemsJsonSchema(p.codec.treeToValue(node, JsonSchema::class.java))
                        is ArrayNode -> ItemsJsonSchemaArray(p.codec.treeToValue(node, Array<JsonSchema>::class.java).toList())
                        else -> throw RuntimeException()
                    }
                }
            })
            .addDeserializer(Dependencies::class.java, object : JsonDeserializer<Dependencies>() {
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Dependencies {
                    val node = p.codec.readTree<JsonNode>(p)
                    return when (node) {
                        is ObjectNode -> DependenciesJsonSchema(p.codec.treeToValue(node, JsonSchema::class.java))
                        is ArrayNode -> DependenciesStringArray(p.codec.treeToValue(node, Array<String>::class.java).toList())
                        else -> throw RuntimeException()
                    }
                }
            })
            .addDeserializer(Type::class.java, object : JsonDeserializer<Type>() {
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Type {
                    val node = p.codec.readTree<JsonNode>(p)
                    return when (node) {
                        is TextNode -> TypeSimpleType(p.codec.treeToValue(node, SimpleType::class.java))
                        is ArrayNode -> TypeSimpleTypeArray(p.codec.treeToValue(node, Array<SimpleType>::class.java).toList())
                        else -> throw RuntimeException()
                    }
                }
            })

    val objectMapper = ObjectMapper()
//            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(KotlinModule())
            .registerModule(deserializerModule)

    val value = objectMapper.readValue(inputStream, JsonSchema::class.java)

    return value
}