package io.philarios.ci.cmd

import io.philarios.ci.spec
import io.philarios.circleci.usecases.GenerateCircleCIConfig

suspend fun main() = GenerateCircleCIConfig(spec)