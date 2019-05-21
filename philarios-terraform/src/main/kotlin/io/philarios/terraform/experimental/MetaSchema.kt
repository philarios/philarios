package io.philarios.terraform.experimental

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.philarios.core.emptyContext
import io.philarios.core.mapScaffolder
import io.philarios.schema.*
import io.philarios.schema.usecases.generateCode

@TerraformExperimental
data class SchemaElement(
        val type: String?,
        val value: String?,
        val elementsType: String?,
        val info: SchemaInfo?
)

@TerraformExperimental
data class SchemaDefinition(
        val type: String?,
        val optional: Boolean?,
        val required: Boolean?,
        val description: String?,
        val inputDefault: String?,
        val computed: Boolean?,
        val maxItems: Int?,
        val minItems: Int?,
        val promoteSingle: Boolean?,

        val computeWhen: List<String>?,
        val conflictsWith: List<String>?,

        val deprecated: String?,
        val removed: String?,

        val default: SchemaElement?,
        val elem: SchemaElement?
)

@UseExperimental(TerraformExperimental::class)
typealias SchemaInfo = Map<String, SchemaDefinition>

@TerraformExperimental
data class ResourceProviderSchema(
        val name: String,
        val type: String,
        val version: String,
        val provider: SchemaInfo,
        val resources: Map<String, SchemaInfo>,
        val dataSources: Map<String, SchemaInfo>
)

@TerraformExperimental
fun providerSchema(schema: ResourceProviderSchema) = schema {
    name("ProviderAWS")
    pkg("io.philarios.terraform.sugar.provider.aws")

    struct(schema.name.capitalize()) {
        field("provider", ref("Provider"))
        field("resources", list(ref("Resource")))
        field("dataSources", list(ref("DataSource")))
    }

    type(schemaInfoStruct("Provider", schema.provider))

    union("DataSource") {
        schema.dataSources.forEach {
            shape(schemaInfoStruct("Data${it.key.split("_").map { it.capitalize() }.joinToString("") { it }}", it.value))
        }
    }

    union("Resource") {
        schema.resources.forEach {
            shape(schemaInfoStruct(it.key.split("_").map { it.capitalize() }.joinToString("") { it }, it.value))
        }
    }
}

@TerraformExperimental
fun schemaInfoStruct(name: String, schemaInfo: SchemaInfo) = struct {
    name(name)

    schemaInfo.entries.forEach {
        field {
            name(it.key)
            type(StringType)
        }
    }
}

@TerraformExperimental
suspend fun main() {
    val resource = ResourceProviderSchema::class.java.classLoader.getResourceAsStream("aws.json")

    val objectMapper = ObjectMapper()
            .registerModule(KotlinModule())

    val value = objectMapper.readValue<ResourceProviderSchema>(resource)

    val schema = emptyContext()
            .mapScaffolder { SchemaScaffolder(providerSchema(value)) }
            .value

    println(schema)

    generateCode(schema)
}
