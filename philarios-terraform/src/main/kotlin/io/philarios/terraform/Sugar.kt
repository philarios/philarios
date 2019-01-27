package io.philarios.terraform

fun <C> ConfigurationBuilder<C>.resource(type: String, name: String, body: ResourceBuilder<C>.() -> Unit) {
    resource {
        type(type)
        name(name)
        include(body)
    }
}

operator fun <C> ResourceBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> ConfigurationBuilder<C>.data(type: String, name: String, body: DataSourceBuilder<C>.() -> Unit) {
    dataSource {
        type(type)
        name(name)
        include(body)
    }
}

operator fun <C> DataSourceBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> ConfigurationBuilder<C>.provider(name: String, body: ProviderBuilder<C>.() -> Unit) {
    provider {
        name(name)
        include(body)
    }
}

operator fun <C> ProviderBuilder<C>.set(key: String, value: Any) = config(key, value)

fun <C> ConfigurationBuilder<C>.variable(name: String, body: VariableBuilder<C>.() -> Unit) {
    variable {
        name(name)
        include(body)
    }
}

fun <C> ConfigurationBuilder<C>.output(name: String, body: OutputBuilder<C>.() -> Unit) {
    output {
        name(name)
        include(body)
    }
}

