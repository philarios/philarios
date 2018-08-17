package io.philarios.concourse.example.v0

import io.philarios.concourse.v0.*
import io.philarios.core.v0.contextOf
import io.philarios.schema.v0.SchemaSpec
import io.philarios.schema.v0.SchemaTranslator

fun main(args: Array<String>) {
    contextOf("")
            .translate(ConcourseTranslator(TestConcourse))
            .translate(SchemaTranslator(Test))
}

object TestConcourse : ConcourseSpec<String>({
    team(TestTeam)
})

object TestTeam : TeamSpec<String>({
    name("test-team")

    pipeline(TestPipeline)
})

object TestPipeline : PipelineSpec<String>({

    name("test-pipeline")

    include(1, GitResource)

    job(Job {
        plan(Get {
            get("repo")
        })
    })

})

object GitResource : PipelineSpec<Int>({

    resource {
        source("repo" to context)
    }

})

object Test : SchemaSpec<Concourse>({

})