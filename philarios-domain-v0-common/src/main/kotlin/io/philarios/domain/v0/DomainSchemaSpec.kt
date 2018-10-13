package io.philarios.domain.v0

import io.philarios.schema.v0.*

object DomainSchemaSpec : SchemaSpec<Any?>({
    name("Domain")
    pkg("io.philarios.domain.v0")

    type(StructSpec {
        name("Domain")

        field {
            name("entities")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Entity")
                })
            })
        }

        field {
            name("relationships")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Relationship")
                })
            })
        }
    })

    type(StructSpec {
        name("Entity")

        field {
            name("name")
            key(true)
            type(StringType)
        }

        field {
            name("attributes")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Attribute")
                })
            })
        }
    })

    type(StructSpec {
        name("Relationship")

        field {
            name("name")
            key(true)
            type(StringType)
        }

        field {
            name("from")
            type(RefTypeSpec {
                name("Entity")
            })
        }

        field {
            name("to")
            type(RefTypeSpec {
                name("Entity")
            })
        }

        field {
            name("attributes")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Attribute")
                })
            })
        }
    })

    type(StructSpec {
        name("Attribute")

        field {
            name("name")
            type(StringType)
        }

        field {
            name("type")
            type(RefTypeSpec {
                name("Type")
            })
        }
    })

    type(EnumTypeSpec {
        name("Type")
        value("Boolean")
        value("Int")
        value("Long")
        value("Float")
        value("Double")
        value("String")
    })

})