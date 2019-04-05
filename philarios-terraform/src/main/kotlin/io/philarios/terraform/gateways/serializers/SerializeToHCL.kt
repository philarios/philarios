//package io.philarios.terraform.gateways.serializers
//
//import io.philarios.terraform.*
//
//fun Configuration.serializeToHCL(): String {
//    return listOf(
//            providers.map { it.serializeToHCL() },
//            dataSources.map { it.serializeToHCL() },
//            variables.map { it.serializeToHCL() },
//            resources.map { it.serializeToHCL() },
//            outputs.map { it.serializeToHCL() }
//    )
//            .flatten()
//            .joinToString("\n\n")
//}
//
//fun Resource.serializeToHCL(): String {
//    return """resource "$type" "$name" {
//${config.serializeToHCL(2)}
//}"""
//}
//
//fun DataSource.serializeToHCL(): String {
//    return """data "$type" "$name" {
//${config.serializeToHCL(2)}
//}"""
//}
//
//fun Provider.serializeToHCL(): String {
//    return """provider "$name" {
//${config.serializeToHCL(2)}
//}"""
//}
//
//fun Variable.serializeToHCL(): String {
//    return """variable "$name" {
//  type = "$type"
//}"""
//}
//
//fun Output.serializeToHCL(): String {
//    return """output "$name" {
//  value = "$value"
//}"""
//}
//
//fun Map<String, Any>.serializeToHCL(indent: Int): String {
//    return entries.map { "${indent.indent()}${it.key} = ${it.value.serializeToHCL()}" }.joinToString("\n")
//}
//
//fun Any.serializeToHCL(): String {
//    return when (this) {
//        is Map<*, *> -> (this as Map<String, Any>).serializeToHCL()
//        is List<*> -> (this as List<Any>).serializeToHCL()
//        is Boolean -> "$this"
//        is Int -> "$this"
//        is Long -> "$this"
//        is Float -> "$this"
//        is Double -> "$this"
//        else -> "\"$this\""
//    }
//}
//
//fun Map<String, Any>.serializeToHCL(): String {
//    return "{${entries.joinToString(",") { "${it.key} = ${it.value.serializeToHCL()}" }}}"
//}
//
//fun List<Any>.serializeToHCL(): String {
//    return "[${this.joinToString(","){ it.serializeToHCL() }}]"
//}
//
//fun Int.indent() = (1..this).joinToString("") { " " }
