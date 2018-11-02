package io.philarios.util.v0

import kotlinx.coroutines.async
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object TableSpek : Spek({

    describe("empty table") {
        itSuspend("when receiving after sending it returns the value") {
            val table = emptyTable<String, String>()
            table.send("foo", "bar")
            val bar = table.receive("foo")
            assertEquals("bar", bar)
        }
        itSuspend("when sending after receiving it returns the value") {
            val table = emptyTable<String, String>()
            val bar = async { table.receive("foo") }
            table.send("foo", "bar")
            assertEquals("bar", bar.await())
        }
        itSuspend("when receiving after sending two different key values it returns the value") {
            val table = emptyTable<String, String>()
            table.send("foo", "bar")
            table.send("baz", "foo")
            val bar = table.receive("foo")
            assertEquals("bar", bar)
        }
        itSuspend("when receiving before sending two different key values it returns the value") {
            val table = emptyTable<String, String>()
            val bar = async { table.receive("foo") }
            table.send("baz", "foo")
            table.send("foo", "bar")
            assertEquals("bar", bar.await())
        }
        itSuspend("when receiving twice before sending it returns the value twice") {
            val table = emptyTable<String, String>()
            val bar1 = async { table.receive("foo") }
            val bar2 = async { table.receive("foo") }
            table.send("foo", "bar")
            assertEquals("bar", bar1.await())
            assertEquals("bar", bar2.await())
        }
    }

})