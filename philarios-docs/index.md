---
layout: default
title: Introduction
nav_order: 100
---

# Introduction
Welcome to Philarios! This short introduction page should give you a broad overview over what this library is intended
to do and why it was originally created. It gives some directions on where to go from here if you are interested
in using the library. In that case, please also make sure to take a look at the prerequisites before diving deeper.

## Context
This library is intended to provide a way to write type-safe specs in Kotlin instead of writing YAML or JSON-based
configuration files. These days almost every system can be configured using YAML which is nice on the one hand but also
prone to errors and redundant code on the other.

Which means that instead of writing this:
```yaml
version: 2
jobs:
  build:
    docker:
      - image: circleci/ruby:2.4.1
    steps:
      - checkout
      - run: echo "A first hello"
```

You can start writing this:
```kotlin
val circleCI = CircleCISpec {
    version("2")
    jobs("build") {
        docker {
            image("circleci/ruby:2.4.1")
        }
        checkout()
        run("echo 'A first hello'")
    }
}
```

This allows you to employ common software engineering practices in order to DRY your code. 

So you can go over:
```kotlin
val circleCI = CircleCISpec {
    jobs("build") {
        rubyImage()
        checkout()
        run("echo 'A first hello'")
    }
}
```  

All the way to:
```kotlin
data class ServiceContext(val name: String)

val circleCI = CircleCISpec<ServiceContext> {
    buildRubyJob(context.name)
}
```

## Design decisions 
If you do not particularly care about why this project exists please feel free to skip to the next section.

To sum everything up, I have created this project because I was writing a lot of YAML configuration files for 
DevOps-related systems at the same time as I was writing a lot of services in Kotlin. Which made me put one and one
together to create a library for writing configuration files in a type-safe way. In addition to that I was (at least
attempting to) designing a microservice-oriented architecture and I was missing some way of specifying the high-level 
dependencies between services. This lead me to generalize the type-safe configuration generator for arbitrary schemas.

Please feel free to read through [Design decisions](philarios-core/design-decisions) if you are interested in more details.

## Prerequisites
Since this library is entirely written in Kotlin familiarity with the language is definitely a plus. The 
[Kotlin Koans](https://kotlinlang.org/docs/tutorials/koans.html) are an excellent way of getting more familiar with
Kotlin if you have not worked with it before. Especially the Koans covering the Kotlin-style `Builders` are useful to
read in combination with the reference page on [Type-Safe Builders](https://kotlinlang.org/docs/reference/type-safe-builders.html).

## Getting started
Depending on what you want to achieve by using this library, i.e. what your objective is, you should be looking at the
documentation of different submodules.

### Getting started with replacing configuration files (YAML, JSON, HCL) with type-safe specs
The library currently supports a number of different systems, all of which I have created because I used them in the
past or I am currently using them. 

* The [CircleCI](philarios-circleci) module contains specs for writing CircleCI configurations
* The [Concourse](philarios-concourse) module describes how to create Concourse pipeline configurations
* The [Terraform](philarios-terraform) module allows you to write arbitrary Terraform configurations in a type-safe way

If your favorite system's configuration is missing from the list, please raise an [Issue](https://github.com/philarios/philarios/issues)
requesting it or feel free to even create a [Pull Request](https://github.com/philarios/philarios/pulls) adding a new
module if you are feeling supportive.

### Getting started with defining a schema for my own domain specific language
Please refer to the [Schema](philarios-schema) module to learn more about how to use the schema spec in order to create
new languages.