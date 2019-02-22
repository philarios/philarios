package io.philarios.util.tests

import io.kotlintest.TestContext
import io.kotlintest.specs.AbstractFreeSpec

fun AbstractFreeSpec.calling(name: String, test: AbstractFreeSpec.FreeSpecScope.() -> Unit) {
    "calling $name" - test
}

fun AbstractFreeSpec.FreeSpecScope.with(name: String, test: TestContext.() -> Unit) {
    "with $name".invoke(test)
}