package io.philarios.util.compiler

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File

// Note: This is taken from https://stackoverflow.com/a/46266592 and modified slightly
object JvmCompiler {

    fun compile(input: File, cp: List<String>, output: File): Boolean {
        val args = K2JVMCompilerArguments().apply {
            freeArgs = input.listFilesRecursively().map { it.absolutePath }
            loadBuiltInsFromDependencies = true
            destination = output.absolutePath
            classpath = System.getProperty("java.class.path")
                    .split(System.getProperty("path.separator"))
                    .let { it + cp }
                    .filter { it.asFile().exists() && it.asFile().canRead() }
                    .joinToString(":")
            noStdlib = true
            noReflect = true
            skipRuntimeVersionCheck = true
            reportPerf = true
            jvmTarget = "1.8"
        }

        val jvmCompiler = K2JVMCompiler()
        val exitCode = jvmCompiler.execImpl(
                PrintingMessageCollector(System.out, MessageRenderer.WITHOUT_PATHS, true),
                Services.EMPTY,
                args
        )
        return exitCode.code == 0
    }

}

fun File.listFilesRecursively(): List<File> =
        if (isFile) {
            listOf(this)
        } else {
            listFiles().flatMap(File::listFilesRecursively)
        }

fun String.asFile() = File(this)