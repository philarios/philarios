---
layout: default
title: Getting started
nav_order: 1
---

# Getting started
Welcome! This here is just a small introduction on the library's purpose as well as some directions on where to go from
here. Please also make sure to take a look at the prerequisites before diving deeper. 

## Purpose
In the last couple of years I have been increasingly concerning myself with two sections of software development that
complement the pure writing of code. On the one hand you have the higher level architecture of a system and on the other
hand there are low-level infrastructure concerns.

The DevOps movement has brought forth a lot of good ideas such as infrastructure-as-code and the more specific
pipeline-as-code for build pipelines. Most of the currently available CI systems support pipeline configuration via
declarative YAML files. While I agree that this is definitely an improvement on a manually configured Jenkins instance,
there are still a number of shortcomings with this approach. First, it is really hard to reuse YAML snippets which
means that is equally easy to just start copy-pasting them all over the place. 

Secondly, due to YAML's static nature it is hard to parameterize the configuration files in case you want to apply them 
to multiple services with only some changes like the name and the source repository. Granted, a lot of systems have 
already recognized this and started to implement things like templating systems. However, I feel like this just leads to
myriad of different wrappers around pure YAML all of which are not the core business of the people implementing their
system. Which is why I think that using something like this library would be better suited.

Lastly, and most importantly for me, it is hard to connect the high-level architecture of a system with all of the
infrastructure configurations that we need to do. If we have to manage n different configuration systems all with a
different approach to templating it is hard to keep manually applying changing to the system's architecture to all of 
them.

What I am trying to achieve is to build a bridge between the high and low levels of software systems. No matter how much
DevOps is pushing forward immutability the infrastructure layer is exactly where the system's side-effects will occur.
So if we were to take inspiration from functional programming where it is common to push side-effects to the edges of
the system and keep the core purely functional, we would end up at a design having three concentric circles. The system's
core would be a high-level, project-specific architecture language. Here, the architects can define the interactions 
between sub-systems in a pure, infrastructure-independent way. The second circle would take this architecture language
and map it to the infrastructure systems. So every time we add a new service it would automatically create the source
repository, the build pipeline and the cloud infrastructure. The outputs of the second circle are pure (YAML) configuration
files which are then received by the outmost circle where they are applied to the infrastructure systems.   

Oh, and yeah, I am slightly [self-aware](https://xkcd.com/927/). 

## Prerequisites
Since this library is entirely written in Kotlin familiarity with the language is definitely a plus. The 
[Kotlin Koans](https://kotlinlang.org/docs/tutorials/koans.html) are an excellent way of getting more familiar with
Kotlin if you have not worked with it before. Especially the Koans covering the Kotlin-style `Builders` are useful to
read in combination with the reference page on [Type-Safe Builders](https://kotlinlang.org/docs/reference/type-safe-builders.html).

## Objective
Depending on what you want to achieve by using this library, i.e. what your objective is, you should be looking at the
documentation of different submodules.

### I want to define a schema for my own domain specific language
Please refer to the [Schema](philarios-schema) module to learn more about how to use the schema spec in order to create
new languages.

### I want to replace some configuration files (YAML, JSON, HCL) with type-safe builders
The library currently supports a number of different systems, all of which I have created because I used them in the
past or I am currently using them. 

* The [CircleCI](philarios-circleci) module contains a DSL for writing CircleCI configurations
* The [Concourse](philarios-concourse) module describes how to create Concourse pipeline configurations
* The [Terraform](philarios-terraform) module allows you to write arbitrary Terraform configurations in a type-safe way

If your favorite system's configuration is missing from the list, please raise an [Issue](https://github.com/philarios/philarios/issues)
requesting it or feel free to even create a [Pull Request](https://github.com/philarios/philarios/pulls) adding a new
module if you are feeling supportive.