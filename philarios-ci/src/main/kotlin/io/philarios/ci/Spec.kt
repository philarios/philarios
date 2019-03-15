package io.philarios.ci

import io.philarios.circleci.CheckoutStepSpec
import io.philarios.circleci.CircleCISpec
import io.philarios.circleci.JobBuilder
import io.philarios.circleci.RunStepSpec

val spec = CircleCISpec<Any?> {
    version("2")

    jobs("snapshot-build") {
        gradleDocker()
        checkout()
        run("gradle check -PbintrayUser=${'$'}{BINTRAY_USER} -PbintrayKey=${'$'}{BINTRAY_KEY}")
    }
    jobs("release-build") {
        gradleDocker()
        checkout()
        run("""
            TAG=$(git --no-pager tag --sort=-taggerdate | head -n 1)
            gradle bintrayUpload -Pversion=${'$'}{TAG} -PbintrayUser=${'$'}{BINTRAY_USER} -PbintrayKey=${'$'}{BINTRAY_KEY}
        """.trimIndent())
    }

    workflows("snapshot") {
        jobs("snapshot-build") {
            filters {
                branches {
                    only("master")
                }
            }
        }
    }

    workflows("release") {
        jobs("release-build") {
            filters {
                branches {
                    ignore("/.*/")
                }
                tags {
                    only("/\\d+\\.\\d+\\.\\d+/")
                }
            }
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