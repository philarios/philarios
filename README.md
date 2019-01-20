# Philarios

Philarios is the library to satisfy all your type-safe builder and DSL needs. Its goal is to provide type-safe
alternatives to common configurable resources (as of right now we provide a DSL for [Concourse](https://concourse-ci.org/)
as well as [Terraform](https://www.terraform.io/)). In addition to this, we offer a DSL for writing your own DSL. This
allows you to define your high-level architecture in a declarative way and to connect it to more low-level infrastructure
configurations.

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

## Documentation

The documentation can be found under [https://philarios.gitbook.io](https://philarios.gitbook.io).

## Questions & Support

Feel free to open a new issue in case you have a question or need help. Contributions are also very welcome! Please keep
in mind that this is my side-project but I will do my best to answer as quickly as possible.