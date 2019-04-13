---
layout: default
title: Schema
nav_order: 300
has_children: true
has_toc: false
permalink: philarios-schema
---

# Getting started
This small tutorial is aimed at teaching you the basics of Philarios schemas.

## Prerequisites
Since this module does not depend on any external systems, it is enough to have a solid grasp on Kotlin [Type-Safe Builders](https://kotlinlang.org/docs/reference/type-safe-builders.html)
in order to proceed. Since the meta-schema is structured using types similar to [algebraic data types](https://en.wikipedia.org/wiki/Algebraic_data_type),
it would also be great if you had at least some exposure to those.

## Goal
Getting started with the Philarios Schema module means to write your own first schema and then using it to define some
parameterized instances of your model.

## Walkthrough
Without any further ado let us get started!

### Setup
As usual when trying out some new software I would recommend to just create a new Kotlin project in Intellij using
Gradle. Because the project is primarily hosted on bintray, for now, please add the repository to your `build.gradle`
file in addition to the two dependencies for the `core` module and the `schema` module.

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