---
layout: default
title: Schema
nav_order: 300
has_children: true
has_toc: false
permalink: philarios-schema
---

# Getting started

## Prerequisites

## Goal

## Walkthrough

### Setup

```
repositories {
    maven {
        url "https://dl.bintray.com/philarios/philarios"
    }
}

dependencies {
    compile "io.philarios:philarios-core:${philarios_version}"
    compile "io.philarios:philarios-schema:${philarios_version}"
}

```

### Creating your first schema spec

```
val filesystemSchema = SchemaSpec<Any?> {
    name("FileSystem")
    pkg("io.philarios.filesystem")

    struct("File") {
        field("name", StringType)
    }
}
```

```
suspend fun main() = generateCode(filesystemSchema)
```

### Using the filesystem spec

```
val indexFile = FileSpec<Any?> {
    name("index.html")
}
```

```
suspend fun main() {
    val file = emptyContext()
            .map(FileScaffolder(indexFile))
            .value
    println(file)
}
```

```
File(name=index.html)
```

## Improving the schema

```
val filesystemSchema = SchemaSpec<Any?> {
    name("FileSystem")
    pkg("io.philarios.filesystem")

    union("Entry") {
        shape("Directory") {
            field("name", StringType)
            field("entries", list(ref("Entry")))
        }
        shape("File") {
            field("name", StringType)
        }
    }
}
```

```
val projectDir = DirectorySpec<Any?> {
    name("website")
    entry(indexFile)
    entry(DirectorySpec {
        name("assets")
        entry(FileSpec {
            name("favicon.ico")
        })
    })
}

val indexFile = FileSpec<Any?> {
    name("index.html")
}
```

```
Directory(name=website, entries=[File(name=index.html), Directory(name=assets, entries=[File(name=favicon.ico)])])
```

### Using the context

```
data class ProjectContext(val name: String)

val projectDir = DirectorySpec<ProjectContext> {
    name(context.name)
    entry(indexFile)
    entry(DirectorySpec {
        name("assets")
        entry(FileSpec {
            name("favicon.ico")
        })
    })
}
```

```
val rootDir = DirectorySpec<List<ProjectContext>> {
    name("root")

    includeForEach(context) {
        entry(projectDir)
    }
}
```

```
suspend fun main() {
    val projects = listOf(
            ProjectContext("Hello World"),
            ProjectContext("Todo"),
            ProjectContext("Tetris")
    )
    val file = contextOf(projects)
            .map(DirectoryScaffolder(rootDir))
            .value
    println(file)
}
```

```
Directory(name=root, entries=[
    Directory(name=Hello World, entries=[File(name=index.html), Directory(name=assets, entries=[File(name=favicon.ico)])]),
    Directory(name=Todo, entries=[File(name=index.html), Directory(name=assets, entries=[File(name=favicon.ico)])]),
    Directory(name=Tetris, entries=[File(name=index.html), Directory(name=assets, entries=[File(name=favicon.ico)])])
])
```