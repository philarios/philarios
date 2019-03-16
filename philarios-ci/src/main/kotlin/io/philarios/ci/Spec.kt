package io.philarios.ci

import io.philarios.circleci.*

val spec = CircleCISpec<Any?> {
    version("2")

    jobs("snapshot-build") {
        gradleDocker()
        checkout()
        run("check", """
            gradle check -PbintrayUser=${'$'}{BINTRAY_USER} -PbintrayKey=${'$'}{BINTRAY_KEY}
        """.trimIndent())
        run("report", """
            mkdir -p ~/test-results/junit/
            find . -type f -regex ".*/build/test-results/.*xml" -exec cp {} ~/test-results/junit/ \;
        """.trimIndent())
        store_test_results {
            path("test-results")
        }
    }
    jobs("release-build") {
        gradleDocker()
        checkout()
        run("publish", """
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

private fun JobBuilder<Any?>.checkout(body: CheckoutBuilder<Any?>.() -> Unit) {
    step(CheckoutStepSpec {
        checkout(body)
    })
}

private fun JobBuilder<Any?>.store_test_results(body: StoreTestResultsBuilder<Any?>.() -> Unit) {
    step(StoreTestResultsStepSpec {
        store_test_results(body)
    })
}

private fun JobBuilder<Any?>.run(body: RunBuilder<Any?>.() -> Unit) {
    step(RunStepSpec {
        run(body)
    })
}

private fun JobBuilder<Any?>.checkout() {
    checkout {
        path(".")
    }
}

private fun JobBuilder<Any?>.run(name: String, command: String) {
    run {
        name(name)
        command(command)
    }
}

private fun JobBuilder<Any?>.gradleDocker() {
    docker {
        image("gradle:4.10.3")
    }
}
