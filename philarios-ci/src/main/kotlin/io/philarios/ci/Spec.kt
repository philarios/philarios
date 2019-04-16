package io.philarios.ci

import io.philarios.circleci.*

val spec = CircleCISpec {
    version("2")

    jobs("snapshot-build") {
        gradleDocker()
        checkout()
        runFromResource("check", "snapshot-build-check.sh")
        runFromResource("report", "snapshot-build-report.sh")
        store_test_results {
            path("~/test-results")
        }
        store_artifacts {
            path("~/reports")
        }
    }
    jobs("release-build") {
        gradleDocker()
        checkout()
        runFromResource("publish", "release-build-publish.sh")
    }

    workflows("snapshot") {
        jobs("snapshot-build") {
            snapshotFilters()
        }
    }

    workflows("release") {
        jobs("release-build") {
            releaseFilters()
        }
    }
}

private fun JobBuilder.gradleDocker() {
    docker {
        image("gradle:4.10.3")
    }
}

private fun WorkflowJobBuilder.snapshotFilters() {
    filters {
        branches {
            only("master")
        }
    }
}

private fun WorkflowJobBuilder.releaseFilters() {
    filters {
        branches {
            ignore("/.*/")
        }
        tags {
            only("/\\d+\\.\\d+\\.\\d+/")
        }
    }
}
