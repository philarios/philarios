package io.philarios.util.v0

import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.Suite

actual fun Suite.itSuspend(description: String, skip: Skip, body: suspend TestBodyCoroutineScope.() -> Unit) {
    it(description, skip) {
        runBlocking {
            withTimeout(10000) {
                with(DelegatingTestBodyCoroutineScope(this@it, this)) {
                    body()
                }
            }
        }
    }
}
