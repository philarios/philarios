package io.philarios.util.table

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.philarios.util.table.emptyTable
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class TableSpec : BehaviorSpec({

    Given("an empty table") {
        When("receiving after sending") {
            Then("it returns the value") {
                val table = emptyTable<String, String>()
                val bar = runBlocking {
                    table.send("foo", "bar")
                    table.receive("foo")
                }
                bar shouldBe "bar"
            }
        }
        When("sending after receiving") {
            Then("it returns the value") {
                val table = emptyTable<String, String>()
                val bar = runBlocking {
                    val bar = async { table.receive("foo") }
                    table.send("foo", "bar")
                    bar.await()
                }
                bar shouldBe "bar"
            }
        }
        When("receiving after sending two different key values") {
            Then("it returns the value put under the requested key") {
                val table = emptyTable<String, String>()
                val bar = runBlocking {
                    table.send("foo", "bar")
                    table.send("baz", "foo")
                    table.receive("foo")
                }
                bar shouldBe "bar"
            }
        }
        When("receiving before sending two diffrent key values") {
            Then("it returns the value put under the requested key") {
                val table = emptyTable<String, String>()
                val bar = runBlocking {
                    val bar = async { table.receive("foo") }
                    table.send("baz", "foo")
                    table.send("foo", "bar")
                    bar.await()
                }
                bar shouldBe "bar"
            }
        }
        When("receiving twice before sending") {
            Then("returns the value twice") {
                val table = emptyTable<String, String>()
                val bar = runBlocking {
                    val bar1 = async { table.receive("foo") }
                    val bar2 = async { table.receive("foo") }
                    table.send("foo", "bar")
                    Pair(bar1.await(), bar2.await())
                }
                bar.first shouldBe "bar"
                bar.second shouldBe "bar"
            }
        }
    }

})
