package io.philarios.domain

import io.philarios.schema.*

val domainSchema = SchemaSpec<Any?> {
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

    type(StructSpec {
        name("Type")

        field {
            name("type")
            type(RefTypeSpec {
                name("RawType")
            })
        }
        field {
            name("nullable")
            type(OptionTypeSpec {
                type(BooleanType)
            })
        }
    })

    type(EnumTypeSpec {
        name("RawType")
        value("Boolean")
        value("Int")
        value("Long")
        value("Float")
        value("Double")
        value("String")
    })

}