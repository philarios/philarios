package io.philarios.concourse.example.v0

import io.philarios.concourse.v0.*
import io.philarios.core.v0.translate

fun main(args: Array<String>) {
    "".translate(ConcourseTranslator(ConcourseSpec({
        team(TestTeam)
    })))
}

object TestTeam : TeamSpec<String>({
    name("test-team")

    pipeline(TestPipeline)
})

object TestPipeline: PipelineSpec<String>({

    name("test-pipeline")

    include(1, GitResource)

    job(Job {
        plan(Get{
            get("repo")
        })
    })

})

object GitResource : PipelineSpec<Int>({

    resource {
        source("repo" to context)
    }

})