// The model of your schema written as pure Kotlin classes.
//
// Because the model expresses the high-level domain, nothing in this file will depend on the generator or on any
// of the other files. This is done to ensure that you could potentially take this file and reuse the classes
// without having a dependency on the specs or materialization process.
//
// If you feel like something is preventing you from separating the model classes from the specific specs, builders,
// or materialization, please feel free to open an issue in the project's repository.
package io.philarios.filesystem

import kotlin.String
import kotlin.collections.List

sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry()

data class File(val name: String, val content: String) : Entry()
