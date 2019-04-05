package io.philarios.circleci

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class When {
    always,

    on_success,

    on_fail
}
