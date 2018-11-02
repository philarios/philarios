package io.philarios.util.v0

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.Suite

interface TestBodyCoroutineScope : TestBody, CoroutineScope

class DelegatingTestBodyCoroutineScope(testBody: TestBody, coroutineScope: CoroutineScope)
    : TestBodyCoroutineScope, TestBody by testBody, CoroutineScope by coroutineScope

fun Suite.itSuspend(description: String, body: suspend TestBodyCoroutineScope.() -> Unit) {
    it(description) {
        runBlocking {
            withTimeout(10000) {
                with(DelegatingTestBodyCoroutineScope(this@it, this)) {
                    body()
                }
            }
        }
    }
}
