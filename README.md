# Philarios

This project aims to provide an alternative to JSON- or YAML-based configuration files.

The ease of use of declarative configuration files has lead to a significant increase in adoption in the last couple of
years. However, their simplicity is also one of their drawbacks once the code base starts to grow. More often than not
they fail to support standard software engineering practices such as modularization, DRY or static type checking.

Philarios provides a set of domain specific languages (DSLs) for common software systems. Other than their official
configuration syntax, these DSL are pure Kotlin code. This means that every piece of configuration will automatically
be checked for syntax, typing or other errors. You can also freely parameterize or modularize parts of your system in
order to reuse them in the same or other projects.

In addition to DSLs for common systems, we also offer a DSL for defining your own DSls. Imagine that you are writing a
*spec* for defining a CI pipeline for an arbitrary Java micro service - you can declare the service's name or a link
to its git repository as parameters the spec's *context*. Next you define a new higher-level DSL for your project
architecture where you can declaratively add or remove services. Philarios then allows you to translate the high-level
DSL into one (or more) low-level DSLs so that you can generate CI pipelines for new services with minimal effort.

## Quick setup

```groovy
buildscript {
    ext {
        philarios_version = '0.10.0'
    }
}

repositories {
	maven {
		url  "https://dl.bintray.com/philarios/philarios"
	}
}

dependencies {
    // The core module is required for the core functionality
    compile "io.philarios:philarios-core-v0-jvm:${philarios_version}"

    // Use this module if you want to define your own DSL schema
    compile "io.philarios:philarios-schema-v0-jvm:${philarios_version}"

    // The concourse module contains a DSL for configuring concourse pipelines
    compile "io.philarios:philarios-concourse-v0-jvm:${philarios_version}"

    // The terraform module contains a DSL for configuring terraform resources
    compile "io.philarios:philarios-terraform-v0-jvm:${philarios_version}"
}
```

## Example spec

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

## Documentation

The documentation can be found under [https://philarios.gitbook.io](https://philarios.gitbook.io).

## Questions & Support

Feel free to open a new issue in case you have a question or need help. Contributions are also very welcome! Please keep
in mind that this is my side-project but I will do my best to answer as quickly as possible.