package io.philarios.structurizr.sugar

internal fun <T : Any> T.hierarchicalId(): String {
    val packageName = javaClass.`package`.name
    val className = javaClass.canonicalName
    val prefix = className.removePrefix("$packageName.")
    val name = this.toString().toLowerCase().capitalize()
    return "$prefix.$name"
}
