package io.philarios.concourse.v0.translators

import io.philarios.concourse.v0.Concourse
import io.philarios.core.v0.Translator

object DistinctByName : Translator<Concourse, Concourse> {
    override fun translate(context: Concourse): Concourse {
        return context.let {
            it.copy(teams = it.teams.map {
                it.copy(pipelines = it.pipelines.map {
                    it.copy(
                            jobs = it.jobs.distinctBy { it.name },
                            resources = it.resources.distinctBy { it.name },
                            resource_types = it.resource_types.distinctBy { it.name },
                            groups = it.groups.distinctBy { it.name }
                    )
                })
            })
        }
    }
}