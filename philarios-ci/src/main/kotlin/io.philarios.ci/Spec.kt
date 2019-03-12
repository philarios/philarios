package io.philarios.ci

import io.philarios.circleci.CheckoutStepSpec
import io.philarios.circleci.CircleCISpec
import io.philarios.circleci.RunStepSpec

val spec = CircleCISpec<Any?> {
    version("2")

    jobs("build") {
        docker {
            image("circleci/ruby:2.4.1")
        }
        step(CheckoutStepSpec {
            checkout {
                path(".")
            }
        })
        step(RunStepSpec {
            run {
                command("echo \"A first hello\"")
            }
        })
    }
}