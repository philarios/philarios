package io.philarios.structurizr.sugar

import io.philarios.structurizr.ModelBuilder
import io.philarios.structurizr.PersonBuilder
import io.philarios.structurizr.SoftwareSystemBuilder

fun <C, T : Any> ModelBuilder<C>.person(id: T, body: PersonBuilder<C>.() -> Unit = {}) {
    person {
        id(id)
        apply(body)
    }
}

fun <C, T : Any> ModelBuilder<C>.softwareSystem(id: T, body: SoftwareSystemBuilder<C>.() -> Unit = {}) {
    softwareSystem {
        id(id)
        apply(body)
    }
}

