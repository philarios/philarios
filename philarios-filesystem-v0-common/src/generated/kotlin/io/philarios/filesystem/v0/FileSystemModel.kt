package io.philarios.filesystem.v0

import kotlin.String
import kotlin.collections.List
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch

sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry()

data class File(val name: String) : Entry()
