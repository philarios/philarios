---
layout: default
title: Glossary
parent: Core
nav_order: 2
---

# Glossary

A list of terms that are commonly used in Philarios accompanied by a short description.

## Domain
A domain is a distinct subject area that relates to a specific set of activities. Domains can be everything from the
world of soccer to the jobs and resource of a continuous integration pipeline.

## Model
Any domain can be expressed in form of many different models. Any one model is a simplified representation of some
aspects of the domain. This project focuses on models that represent the domain entities and their relationships.

## Schema
The model's structure is expressed in form of a schema. Creating a schema for a domain is a way of materializing it
as a model. Because the world of modelling entities and relationships is a domain itself a schema can be thought of as
the model of the model domain.

## Instantiation
An instantiation is the creation of some model elements. For example, if the model elements are implemented as classes
an instantiation would be the creation of the classes' objects.

## Spec
Essentially, a spec is a parameterized instantiation, i.e. it is a blueprint to create elements of a specific model.
While using specs is entirely optional (of course you can just instantiate your model classes like any other classes)
they provide you with a nice and modular interface to define your model elements more freely.

## Builder
A builder is what actually builds the model classes (hence, the name). Each spec gets injected with a builder interface
as the receiver so that the spec can be written using this interface. The actual materialization process can then be
changed by providing different builder implementations.

## Context
Each builder interface holds a reference to a generic context property. This context can be used to parameterized the
spec. You can define your context to hold any form of data that you want to have available within the spec's definition.
This also allows you to chain specs so that you can go from a high-level spec to lower-level specs.