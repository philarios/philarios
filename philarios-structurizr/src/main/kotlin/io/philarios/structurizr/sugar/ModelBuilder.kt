package io.philarios.structurizr.sugar

import io.philarios.structurizr.ModelBuilder
import io.philarios.structurizr.PersonBuilder
import io.philarios.structurizr.SoftwareSystemBuilder

fun <T : Any> ModelBuilder.person(id: T, body: PersonBuilder.() -> Unit = {}) {
    person {
        id(id)
        apply(body)
    }
}

fun <T : Any> ModelBuilder.softwareSystem(id: T, body: SoftwareSystemBuilder.() -> Unit = {}) {
    softwareSystem {
        id(id)
        apply(body)
    }
}

