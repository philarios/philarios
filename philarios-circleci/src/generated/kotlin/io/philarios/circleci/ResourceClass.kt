package io.philarios.circleci

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class ResourceClass {
    small,

    medium,

    `medium+`,

    large,

    xlarge
}
