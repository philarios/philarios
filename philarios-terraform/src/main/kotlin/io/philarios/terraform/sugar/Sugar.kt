package io.philarios.terraform.sugar

import io.philarios.terraform.*

fun TerraformBuilder.resource(type: String, name: String, body: ResourceBuilder.() -> Unit) {
    resource {
        type(type)
        name(name)
        apply(body)
    }
}

operator fun ResourceBuilder.set(key: String, value: Any) = config(key, value)

fun TerraformBuilder.data(type: String, name: String, body: DataSourceBuilder.() -> Unit) {
    dataSource {
        type(type)
        name(name)
        apply(body)
    }
}

operator fun DataSourceBuilder.set(key: String, value: Any) = config(key, value)

fun TerraformBuilder.provider(name: String, body: ProviderBuilder.() -> Unit) {
    provider {
        name(name)
        apply(body)
    }
}

operator fun ProviderBuilder.set(key: String, value: Any) = config(key, value)

fun TerraformBuilder.variable(name: String, body: VariableBuilder.() -> Unit) {
    variable {
        name(name)
        apply(body)
    }
}

fun TerraformBuilder.output(name: String, body: OutputBuilder.() -> Unit) {
    output {
        name(name)
        apply(body)
    }
}

