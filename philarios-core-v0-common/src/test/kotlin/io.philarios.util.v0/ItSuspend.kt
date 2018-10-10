package io.philarios.util.v0

import kotlinx.coroutines.experimental.CoroutineScope
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.Suite

interface TestBodyCoroutineScope : TestBody, CoroutineScope

class DelegatingTestBodyCoroutineScope(testBody: TestBody, coroutineScope: CoroutineScope)
    : TestBodyCoroutineScope, TestBody by testBody, CoroutineScope by coroutineScope

expect fun Suite.itSuspend(description: String, skip: Skip = Skip.No, body: suspend TestBodyCoroutineScope.() -> Unit)