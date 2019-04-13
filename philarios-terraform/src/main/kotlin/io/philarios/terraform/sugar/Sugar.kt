package io.philarios.terraform.sugar

import io.philarios.terraform.*

fun <C> TerraformBuilder<C>.resource(type: String, name: String, body: ResourceBuilder<C>.() -> Unit) {
    resource {
        type(type)
        name(name)
        include(body)
    }
}

operator fun <C> ResourceBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> TerraformBuilder<C>.data(type: String, name: String, body: DataSourceBuilder<C>.() -> Unit) {
    dataSource {
        type(type)
        name(name)
        include(body)
    }
}

operator fun <C> DataSourceBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> TerraformBuilder<C>.provider(name: String, body: ProviderBuilder<C>.() -> Unit) {
    provider {
        name(name)
        include(body)
    }
}

operator fun <C> ProviderBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> TerraformBuilder<C>.variable(name: String, body: VariableBuilder<C>.() -> Unit) {
    variable {
        name(name)
        include(body)
    }
}

fun <C> TerraformBuilder<C>.output(name: String, body: OutputBuilder<C>.() -> Unit) {
    output {
        name(name)
        include(body)
    }
}

