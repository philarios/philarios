# Philarios

[![CircleCI](https://circleci.com/gh/philarios/philarios/tree/master.svg?style=svg)](https://circleci.com/gh/philarios/philarios/tree/master)

This project aims to provide an alternative to JSON- or YAML-based configuration files.

The project documentation can be found under [https://philarios.github.io](https://philarios.github.io).

## Example spec
Philarios allows you to define new schemas for specs that could look something like this:

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
    compile "io.philarios:philarios-core:${philarios_version}"

    // Use this module if you want to define your own schema
    compile "io.philarios:philarios-schema:${philarios_version}"

    // The concourse module for configuring CircleCI pipelines
    compile "io.philarios:philarios-circleci:${philarios_version}"

    // The concourse module for configuring concourse pipelines
    compile "io.philarios:philarios-concourse:${philarios_version}"

    // The terraform module for configuring terraform resources
    compile "io.philarios:philarios-terraform:${philarios_version}"
}
```

## Questions & Support

Feel free to open a new issue in case you have a question or need help. Contributions are also very welcome! Please keep
in mind that this is my side-project but I will do my best to answer as quickly as possible.
