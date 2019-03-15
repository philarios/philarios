package io.philarios.ci

import io.philarios.circleci.*

val spec = CircleCISpec<Any?> {
    version("2")

    jobs("build-snapshot") {
        gradleDocker()
        checkout()
        run("""
            gradle check
            gradle build
            """.trimIndent())
    }
    jobs("tag-release") {
        gradleDocker()
        checkout()
        run("git --no-pager tag --sort=-taggerdate | head -n 1")
    }

    workflows("default") {
        jobs("build-snapshot") {
            filters {
                branches {
                    only("master")
                }
            }
        }
        jobs("trigger-promotion") {
            require("build-snapshot")
            type(WorkflowJobType.approval)
        }
        jobs("tag-release") {
            require("trigger-promotion")
        }
    }
}

private fun JobBuilder<Any?>.gradleDocker() {
    docker {
        image("gradle:4.10.3")
    }
}

private fun JobBuilder<Any?>.checkout() {
    step(CheckoutStepSpec {
        checkout {
            path(".")
        }
    })
}

private fun JobBuilder<Any?>.run(command: String) {
    step(RunStepSpec {
        run {
            command(command)
        }
    })
}