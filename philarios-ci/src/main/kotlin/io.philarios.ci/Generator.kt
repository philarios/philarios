package io.philarios.ci

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.philarios.circleci.CircleCIScaffolder
import io.philarios.core.emptyContext
import io.philarios.core.map
import java.io.FileWriter

suspend fun main() {
    emptyContext()
            .map(CircleCIScaffolder(spec))
            .map {
                val objectMapper = ObjectMapper(YAMLFactory())
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                val writer = FileWriter(".circleci/config.yml")
                objectMapper.writeValue(writer, it)
            }
}