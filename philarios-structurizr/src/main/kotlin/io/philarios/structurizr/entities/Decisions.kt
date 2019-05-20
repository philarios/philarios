package io.philarios.structurizr.entities

import io.philarios.structurizr.DecisionBuilder
import io.philarios.structurizr.DecisionStatus
import io.philarios.structurizr.DocumentationBuilder
import io.philarios.structurizr.Format
import io.philarios.structurizr.sugar.hierarchicalId
import java.io.File

fun <T : Any> DocumentationBuilder.markdownDecisions(elementId: T, dir: File) {
    DecisionStatus.values().forEach { status ->
        File(dir, status.toString().toLowerCase())
                .takeIf { it.exists() }
                ?.listFiles()
                ?.forEach { file ->
                    val lines = file.readLines()

                    val id = file.name.substringBefore("-")
                    val date = lines
                            .firstOrNull { it.startsWith("Date: ") }
                            ?.substringAfter("Date: ")
                            ?: ""
                    val title = lines
                            .first()
                            .substringAfter(". ")
                    val content = lines
                            .dropWhile { !it.startsWith("##") }
                            .joinToString("\n")

                    decision {
                        id(id)
                        elementId(elementId)
                        date(date)
                        title(title)
                        status(status)
                        format(Format.Markdown)
                        content(content)
                    }
                }
    }
}

fun <T : Any> DecisionBuilder.elementId(id: T) {
    elementId(id.hierarchicalId())
}