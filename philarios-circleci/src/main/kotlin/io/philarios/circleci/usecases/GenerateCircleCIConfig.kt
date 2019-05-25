package io.philarios.circleci.usecases

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import io.philarios.circleci.CircleCIScaffolder
import io.philarios.circleci.CircleCISpec
import io.philarios.core.resolve
import java.io.FileWriter

object GenerateCircleCIConfig {
    private val objectMapper = ObjectMapper(
            YAMLFactory()
                    .configure(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE, true)
    ).apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    }

    suspend operator fun invoke(spec: CircleCISpec) {
        val circleCI = CircleCIScaffolder(spec).resolve()

        // We have to manually inject the version into the workflows map
        val tree = objectMapper.valueToTree<ObjectNode>(circleCI)
        tree["workflows"]
                ?.let { it as? ObjectNode }
                ?.let { it.put("version", 2) }

        val writer = FileWriter("../.circleci/config.yml")

        objectMapper.writeValue(writer, tree)
    }
}