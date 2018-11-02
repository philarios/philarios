package io.philarios.terraform.v0.translators

import io.philarios.terraform.v0.*

fun Configuration.serializeToHCL(): String {
    return ""
}

fun Resource.serializeToHCL(): String {
    return """resource "$type" "$name" {
${config.serializeToHCL(2)}
}"""
}

fun DataSource.serializeToHCL(): String {
    return """data "$type" "$name" {
${config.serializeToHCL(2)}
}"""
}

fun Provider.serializeToHCL(): String {
    return """provider "$name" {
${config.serializeToHCL(2)}
}"""
}

fun Variable.serializeToHCL(): String {
    return """variable "$name" {
  type = "$type"
}"""
}

fun Output.serializeToHCL(): String {
    return """output "$name" {
  value = "$value"
}"""
}

fun Map<String, Any>.serializeToHCL(indent: Int): String {
    return entries.map { "${indent.indent()}${it.key} = \"${it.value}\"" }.joinToString("\n")
}

fun Int.indent() = (1..this).joinToString("") { " " }
