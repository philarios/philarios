package io.philarios.schema.translators.codegen.util

internal inline fun <T, O> T.runIfNotNull(optional: O?, block: T.(O) -> T): T {
    return if (optional != null) {
        block.invoke(this, optional)
    } else {
        this
    }
}

internal inline fun <T> T.runIfTrue(predicate: Boolean, block: T.() -> T): T {
    return if (predicate) {
        block.invoke(this)
    } else {
        this
    }
}
