package io.philarios.terraform.experimental

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.philarios.core.contextOf
import io.philarios.core.map
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
data class NamedSchemaInfo(
        val name: String,
        val schemaInfo: SchemaInfo
)

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
val providerSchema = SchemaSpec<ResourceProviderSchema> {
    name("ProviderAWS")
    pkg("io.philarios.terraform.sugar.provider.aws")

    struct(context.name.capitalize()) {
        field("provider", ref("Provider"))
        field("resources", list(ref("Resource")))
        field("dataSources", list(ref("DataSource")))
    }

    include(NamedSchemaInfo("Provider", context.provider)) {
        type(schemaInfoStruct)
    }

    union("DataSource") {
        includeForEach(context.dataSources.map {
            NamedSchemaInfo("Data${it.key.split("_").map { it.capitalize() }.joinToString("") { it }}", it.value)
        }) {
            shape(schemaInfoStruct)
        }
    }

    union("Resource") {
        includeForEach(context.resources.map {
            NamedSchemaInfo(it.key.split("_").map { it.capitalize() }.joinToString("") { it }, it.value)
        }) {
            shape(schemaInfoStruct)
        }
    }
}

@TerraformExperimental
val schemaInfoStruct = StructSpec<NamedSchemaInfo> {
    name(context.name)

    includeForEach(context.schemaInfo.entries) {
        field {
            name(context.key)
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

    val schema = contextOf(value)
            .map(SchemaScaffolder(providerSchema))
            .value

    println(schema)

    generateCode(schema)
}
