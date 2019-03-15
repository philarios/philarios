package io.philarios.ci

import io.philarios.circleci.CheckoutStepSpec
import io.philarios.circleci.CircleCISpec
import io.philarios.circleci.JobBuilder
import io.philarios.circleci.RunStepSpec

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
    jobs("trigger-promotion") {
        gradleDocker()

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