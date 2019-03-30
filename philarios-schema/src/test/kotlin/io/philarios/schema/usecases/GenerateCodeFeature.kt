package io.philarios.schema.usecases

import io.philarios.schema.gateways.writers.DirectoryFileSpecWriter
import io.philarios.schema.sampleSpec
import io.philarios.util.compiler.JvmCompiler
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.File

object GenerateCodeFeature : Spek({

    Feature("GenerateCode") {
        lateinit var generateCode: GenerateCode

        Scenario("generating code for a complex schmema") {
            val directory = File("./build/generated/kotlin")
            val fileSpecWriter = DirectoryFileSpecWriter(directory)
            generateCode = GenerateCode(fileSpecWriter)

            When("generting code for complex schmea with a lot of different types") {
                runBlocking {
                    generateCode(sampleSpec)
                }
            }

            Then("the generated code can compile") {
                directory.shouldContainCompilableKotlinCode()
            }

        }

    }

})

fun File.shouldContainCompilableKotlinCode() {
    val output = createTempDir().apply { deleteOnExit() }
    val compilable = JvmCompiler.compile(this, emptyList(), output)
    compilable.shouldBeTrue()
}

