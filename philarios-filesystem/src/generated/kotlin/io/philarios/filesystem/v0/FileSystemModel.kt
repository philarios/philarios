package io.philarios.filesystem.v0

import kotlin.String
import kotlin.collections.List

sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry()

data class File(val name: String, val content: String) : Entry()
