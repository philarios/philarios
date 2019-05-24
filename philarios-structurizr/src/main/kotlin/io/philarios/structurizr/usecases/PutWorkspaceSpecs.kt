package io.philarios.structurizr.usecases

import io.philarios.core.resolve
import io.philarios.structurizr.*
import io.philarios.structurizr.entities.convert
import io.philarios.structurizr.gateways.upload.StructurizrUploader

suspend fun putWorkspaceSpecs(vararg workspaceSpecs: WorkspaceSpec) {
    val workspace = workspaceSpecs
            .map { workspaceSpec ->
                WorkspaceScaffolder(workspaceSpec)
                        .createScaffold()
                        .resolve()
            }
            .reduce(Workspace::plus)
            .let(Workspace::convert)
    val uploader = StructurizrUploader()
    uploader.upload(workspace)
}

operator fun Workspace.plus(other: Workspace): Workspace {
    return Workspace(
            name = name,
            description = description ?: other.description,
            model = model + other.model,
            viewSet = viewSet + other.viewSet,
            configuration = configuration,
            documentation = documentation + other.documentation
    )
}

operator fun Model?.plus(other: Model?): Model? {
    if (this == null) return other
    if (other == null) return this
    return Model(
            people = (people + other.people)
                    .groupBy { it.id }
                    .map { (_, people) ->
                        people.reduce { acc, person ->
                            acc.copy(
                                    relationships = acc.relationships + person.relationships,
                                    tags = acc.tags + person.tags
                            )
                        }
                    },
            softwareSystems = softwareSystems + other.softwareSystems
    )
}

private operator fun ViewSet?.plus(other: ViewSet?): ViewSet? {
    if (this == null) return other
    if (other == null) return this
    return ViewSet(
            systemLandscapeViews = systemLandscapeViews.orEmpty() + other.systemLandscapeViews.orEmpty(),
            systemContextViews = systemContextViews.orEmpty() + other.systemContextViews.orEmpty(),
            containerViews = containerViews.orEmpty() + other.containerViews.orEmpty(),
            componentViews = componentViews.orEmpty() + other.componentViews.orEmpty(),
            dynamicViews = dynamicViews.orEmpty() + other.dynamicViews.orEmpty(),
            configuration = configuration
    )
}

private operator fun Documentation?.plus(other: Documentation?): Documentation? {
    if (this == null) return other
    if (other == null) return this
    return Documentation(
            decisions = decisions + other.decisions
    )
}