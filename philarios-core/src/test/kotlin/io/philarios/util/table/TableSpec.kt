package io.philarios.util.table

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TableSpec : Spek({

    describe("Table") {
        it("returns the value that was sent when receiving after sending") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                table.send("foo", "bar")
                table.receive("foo")
            }
            bar shouldEqual Result.Value("bar")
        }
        it("returns the value that was sent when sending after receiving") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                val bar = async { table.receive("foo") }
                table.send("foo", "bar")
                bar.await()
            }
            bar shouldEqual Result.Value("bar")
        }
        it("returns the value put under the requested key when receiving after sending two different key values") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                table.send("foo", "bar")
                table.send("baz", "foo")
                table.receive("foo")
            }
            bar shouldEqual Result.Value("bar")
        }
        it("returns the value put under the requested key when receiving before sending two different key values") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                val bar = async { table.receive("foo") }
                table.send("baz", "foo")
                table.send("foo", "bar")
                bar.await()
            }
            bar shouldEqual Result.Value("bar")
        }
        it("returns the value twice when receiving twice before sending") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                val bar1 = async { table.receive("foo") }
                val bar2 = async { table.receive("foo") }
                table.send("foo", "bar")
                Pair(bar1.await(), bar2.await())
            }
            bar.first shouldEqual Result.Value("bar")
            bar.second shouldEqual Result.Value("bar")
        }
        it("returns an exception when closing the table while receiving") {
            val table = emptyTable<String, String>()
            val bar = runBlocking {
                val bar = async { table.receive("foo") }
                delay(1)
                table.close()
                bar.await()
            }
            bar.javaClass shouldEqual Result.Error::class.java
        }
    }

})
