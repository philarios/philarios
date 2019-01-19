# Tutorial

In this section we will walk through setting up a project using Philarios and creating our fist DSL specs.

## Prerequisites
- Familiarity with [Kotlin](https://kotlinlang.org/)
- Having read about [Type-Safe Builders](https://kotlinlang.org/docs/reference/type-safe-builders.html)
- For the more advanced sections knowledge of [Terraform](https://www.terraform.io/)
## Project setup

The easiest way to setup a project is to use Gradle. For this, we just add this following snippet to out `build.gradle`
file:

```groovy
buildscript {
    ext {
        philarios_version = '0.9.3'
    }
}

repositories {
	maven {
		url  "https://dl.bintray.com/philarios/philarios"
	}
}

dependencies {
    compile "io.philarios:philarios-core-v0-jvm:${philarios_version}"
    compile "io.philarios:philarios-filesystem-v0-jvm:${philarios_version}"
}
```

This include two dependencies to your project: the Philarios core module as well as the file system module. The core
module contains all fundamental code that is necessary for running all Philarios projects. The file system model, on the
other hand, is a simple example DSL that we will be using throughout the tutorial. 

## Your first spec

The file system spec allows us to write type-safe builders that allows us to create objects of the following domain
model:

```kotlin
sealed class Entry

data class Directory(val name: String, val entries: List<Entry>) : Entry()

data class File(val name: String) : Entry()
```

This model defines entries in a file system. An entry can either be a directory which has a name and a list of children
or a file which just has a name. In other words: it's a file tree.

Now, after taking a look at the model, let's write our first `FileSpec`:

```kotlin
val file = FileSpec<Any?> {
    this.name("My file")
}
```

This code creates a new `FileSpec` variable using a special constructor. This constructor's only parameter is a function
that takes a `FileBuilder` as its `receiver`. Basically, this means that inside the curly braces the `this` keyword 
refers to a `FileBuilder` instance. Using this builder, we can declaratively set the file's name to "My file". You also
might be wondering about the generic parameter (here set to `Any?`) but please ignore it for now as we will cover it 
soon.

The next thing we will do is define a directory containing our file:

```kotlin
val directory = DirectorySpec<Any?> {
    name("My directory")
    entry(file)
}
```

This looks very similar to the the `FileSpec` with the difference that we are actually adding the previously defined
`file` variable as an entry to our `dictionary`. Because the `Dictionary` class is defined to have a list of entries
the `DictionaryBuilder` contains functions to add them.

Of course, instead of defining the `file` in a separate variable, we can also inline the `FileSpec` directly into the
`entry` function:  

```kotlin
val directory = DirectorySpec<Any?> {
    name("My directory")
    entry(FileSpec { 
        name("My File")
    })
}
```

Finally, we can evaluate our spec with just a few lines of code:

```kotlin
suspend fun main() {
    val output = emptyContext()
        .map(directory)
        .value
    println(output)
}
```

How this exactly works will be explained in the next section. But for now, we can take a look at the output and see that
it is in fact a instance of the `Directory` class with on `File` entry:

```
Directory(name=My directory, entries=[File(name=My file)])
```

How useful is this, though? Well, with only this much code, not very useful. But we could implement a simple algorithm 
that traverses the output file tree and actually creates the directories and files in the system. Then this would allow 
us to write a declarative scaffolding tool. Which could be nice to have. 

## Working with context

Now let us take a closer look at that generic parameter. What this allows us to do is to define the type of `context`
that is used to write the spec, i.e. we can write a parameterized spec, similar to a template engine. In fact, each
builder receiver, in addition to its DSL functions, also has a `context` variable the type of which is the same as the
spec's generic parameter.

The following snippet shows how use the `context` variable in a spec. The spec's generic parameter is `Int`, which means
that the builder's `context` variable is also of the same type. 

```kotlin
val file = FileSpec<Int> {
    name("My file #$context")
}
```

Each builder also provides the `include` and `includeForEach` helper functions for including specs with a different
context. `context..context+4` creates a list of five integers starting from the value of the outer context while
`includeForEach` takes an iterable and includes an inner spec for each of its values.

```kotlin
val directory = DirectorySpec<Int> {
    name("My directory")

    includeForEach(context..context+4) {
        entry(file)
    }
}
```

Next we can make a slight change to the `main` function: instead of using the `emptyContext` - which would set the 
builder's `context` variable to `null` - we will use `contextOf(0)`. This sets the `context` variable of the outer
directory spec to `0`.

```kotlin
suspend fun main() {
    val output = contextOf(0)
        .map(directory)
        .value
    println(output)
}
```

And just like we expected (hopefully me and you, both), the new directory now has five entries (up from only one in the
first example):

```
Directory(
    name=My directory, 
    entries=[
        File(name=My file #0), 
        File(name=My file #1), 
        File(name=My file #2), 
        File(name=My file #3), 
        File(name=My file #4)
    ]
)
```

Basically, each spec is a templated DSL invocation which is then realized in the `main` method when connecting it with
an actual context.

## Creating a schema

So far we have only used the file system DSL. But fret not, creating your own Philarios DSL is easy! Because with 
Philarios there is a DSL for defining DSLs (how wonderful). The only thing you need to add to your project is the
schema module, which contains the DSL for defining new schemas:

```groovy
compile "io.philarios:philarios-schema-v0-jvm:${philarios_version}"
```

With the new module in hand, we can create a new schema that declares a project DSL:

```kotlin
val projectSchema = SchemaSpec<Any?> {
    name("Project")
    pkg("io.philarios.example.tutorial.creatingaschema.project")

    type(StructSpec {
        name("Project")
        field {
            name("name")
            type(StringType)
        }
        field {
            name("modules")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Module")
                })
            })
        }
    })

    type(UnionSpec {
        name("Module")
        shape {
            name("JavaModule")
            field {
                name("name")
                type(StringType)
            }
            field {
                name("version")
                type(IntType)
            }
        }
        shape {
            name("DockerModule")
            field {
                name("name")
                type(StringType)
            }
        }
    })
}
```

Now this snippet is somewhat longer but you do not need to understand everything right away. The most important parts
are that we create a new `SchemaSpec`, give it a name and specify in which package it is. Then we define a couple of
types. A `Project` is a struct that has a name as well as a couple of modules while a `Module` is a union of the
`JavaModule` struct and the `DockerModule` struct. In short, a project has a number of modules which can either be
Java- or Docker-focused.

Similar to the directory specs in the previous examples we can create a `main` function to generate us some code that
makes the `ProjectSchema` usable:   

```kotlin
suspend fun main() {
    emptyContext()
        .map(projectSchema)
        .map(SchemaCodegen("./src/main/kotlin"))
}
```

The `SchemaCodegen` class is a `Translator` which translates a `Schema` object into Kotlin code containing all the model,
spec and builder classes (among others). In fact, both the file system DSL and the schema DSL itself are created this
way.

Just to double check, here is the resulting model in plain Kotlin classes:

```kotlin
data class Project(val name: String, val modules: List<Module>)

sealed class Module

data class JavaModule(val name: String, val version: Int) : Module()

data class DockerModule(val name: String) : Module()
```

As you can see `Struct` types are translated into data classes while `Union` types are translated into sealed classes.

## Using the schema

Once the necessary classes have been generated we can use them right away to declare ourselves a cool new project:

```kotlin
val myProject = ProjectSpec<Any?> {
    name("My cool project")

    module(JavaModuleSpec {
        name("server")
        version(8)
    })

    module(JavaModuleSpec {
        name("client")
        version(6)
    })

    module(DockerModuleSpec {
        name("lb")
    })
}
```

This should not surprise you all that much, considering that it is very similar to the directory spec we have created
earlier. Our cool project has three modules: a server running Java 8, a client running Java 6 and a load balancer that
we package as a Docker image.

But what can we do with this project spec? Well, we can take the its output and define us some module and project
directories. This could work as the basis of an actual project scaffolding tool.

```kotlin
val javaModuleDir = DirectorySpec<JavaModule> {
    name(context.name)

    if (context.version < 7) {
        entry(FileSpec {
            name("pom.xml")
        })
    } else {
        entry(FileSpec {
            name("build.gradle")
        })
    }

}

val dockerModuleDir = DirectorySpec<DockerModule> {
    name(context.name)

    entry(FileSpec {
        name("Dockerfile")
    })
}
```

Here we have defined two directories: a java module directory as well as a docker module directory. And because we are
setting the context type to `JavaModule` and `DockerModule`, respectively, we can access instances of these classes
using the builder's `context` variable. For example, we can create a Maven-based directory if the Java version is less
than seven but a Gradle-based directory otherwise (I must admit this is just a little bit contrived). 

Next we can create an encompassing project directory:

```kotlin
val projectDir = DirectorySpec<Project> {

    name(context.name)

    includeForEach(context.modules) {
        entry(context.directory)
    }

}

val Module.directory: DirectorySpec<Module>
    get() = when (this) {
        is JavaModule -> javaModuleDir as DirectorySpec<Module>
        is DockerModule -> dockerModuleDir as DirectorySpec<Module>
    }
```

For each of the project's modules we include the corresponding module directory. The `Module.directory` property uses
one of Kotlin's features: if a `when` block is looking at an instance of a sealed class (in this case `Module`), there
has to be a branch for each of its subclasses - otherwise it will not compile (Honestly, I don't know why I need to cast 
the return values, please let me know why this is). This is nice to have in case you want to add a new module type and 
forget to add a directory for it.

All in all, defining a `DirectorySpec` with a `Project` as its `context` allows us to chain the specs:

```kotlin
suspend fun main() {
    val output = emptyContext()
        .map(myProject)
        .map(projectDir)
        .value
    println(output)
}
```

First, we create our project using a `null` context; next, we create our project directory using our project as the
context. Just as we have hoped, this creates a single project directory with three child directories for its three
modules:

```
Directory(
    name=My cool project, 
    entries=[
        Directory(name=server, entries=[File(name=build.gradle)]), 
        Directory(name=client, entries=[File(name=pom.xml)]), 
        Directory(name=lb, entries=[File(name=Dockerfile)])
    ]
)
```

## Translating to Terraform

While it is certainly cool to be able to create a project scaffolding tool with Philarios, its true potential shines
through when connecting our project spec with an external tool, like [Terraform](https://www.terraform.io/). Assume
that we now want to create some repositories on Github for our project, one for each module. We can do so using the
[Github provider](https://www.terraform.io/docs/providers/github/index.html) for terraform.

We start off by including the Terraform DSL in our project:

```groovy
compile "io.philarios:philarios-terraform-v0-jvm:${philarios_version}"
```

This allows us to write some configuration specs for terraform, starting with the Github provider definition:

```kotlin
val projectProvider = ConfigurationSpec<Project> {
    provider {
        name("github")
        config("organization" to context.name)
        config("token" to "123456")
    }
}
```

Now, this only vaguely resembles HCL and could definitely use some improvements, but I hope you see the parallels
nevertheless. We can already improve it somewhat using the syntax-sugar extension functions that are coming with the
terraform module:

```kotlin
val projectProvider = ConfigurationSpec<Project> {
    provider("github") {
        config("organization" to context.name)
        config("token" to "123456")
    }
}
```

This comes closer to the original HCL where the provider name directly follows the `provider` keyword on the first line.
We can also define two `github_repository` resources for the Java and Docker modules:

```kotlin
val javaModuleRepo = ConfigurationSpec<JavaModule> {
    resource("github_repository", context.name) {
        name(context.name)
    }
}

val dockerModuleRepo = ConfigurationSpec<DockerModule> {
    resource("github_repository", context.name) {
        name(context.name)
    }
}
```

We can wrap it all up by defining a `projectGithub` spec as well a `moduleRepo` spec:

```kotlin
val projectGithub = ConfigurationSpec<Project> {
    include(projectProvider)
    includeForEach(context.modules, moduleRepo)
}

val moduleRepo = ConfigurationSpec<Module> {
    include(when (context) {
        is JavaModule -> javaModuleRepo as ConfigurationSpec<Module>
        is DockerModule -> dockerModuleRepo as ConfigurationSpec<Module>
    })
}
```

Finally, we can create a `main` function where we chain the empty context into our project, into the project Github
spec and into actual HCL using the `serializeToHCL` function:

```kotlin
suspend fun main() {
    val output = emptyContext()
        .map(myProject)
        .map(projectGithub)
        .map(Configuration::serializeToHCL)
        .value
    println(output)
}
```

The `output` variable contains pure HCL terraform configuration code:

```
provider "github" {
  organization = "My cool project"
  token = "123456"
}

resource "github_repository" "server" {
  name = "server"
}

resource "github_repository" "client" {
  name = "client"
}

resource "github_repository" "lb" {
  name = "lb"
}
```

We can hand this code off to terraform and create the module repositories on Github.

## Conclusion

In this tutorial we went from defining a simple directory spec, to creating our own custom DSL for declaring a project
module structure. Using these newly created projects as a context we then constructed project directory as well as a 
terraform configuration for actually creating module repositories on Github.

Going forward we could use additional low-level DSLs for creating all kinds of resources, ranging from CI pipelines to
Kubernetes deployments. We can also define new custom-made DSLs that model our more abstract high-level concepts that
are specific to the domain we are currently working in. Then, we can use the `context` variables to translate the 
high-level languages down to the low-level languages. This opens up a lot of new possibilities for declarative, type-safe
ways to define our software architecture as a whole.

I hope this tutorial was helpful for you and you are excited to use Philarios in the future. If you have any questions,
comments or concerns please feel free to open a new issue on the [Github repository](https://github.com/philarios/philarios).