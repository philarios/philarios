package io.philarios.terraform

import io.philarios.schema.*

val terraformSchema = SchemaSpec {
    name("Terraform")
    pkg("io.philarios.terraform")

    type(StructSpec {
        name("Terraform")

        field {
            name("resources")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Resource")
                })
            })
        }
        field {
            name("dataSources")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("DataSource")
                })
            })
        }
        field {
            name("providers")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Provider")
                })
            })
        }
        field {
            name("variables")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Variable")
                })
            })
        }
        field {
            name("outputs")
            type(ListTypeSpec {
                type(RefTypeSpec {
                    name("Output")
                })
            })
        }
    })

    type(StructSpec {
        name("Resource")
        field {
            name("type")
            type(StringType)
        }
        field {
            name("name")
            type(StringType)
        }
        field {
            name("config")
            type(MapTypeSpec {
                keyType(StringType)
                valueType(AnyType)
            })
        }
    })

    type(StructSpec {
        name("DataSource")
        field {
            name("type")
            type(StringType)
        }
        field {
            name("name")
            type(StringType)
        }
        field {
            name("config")
            type(MapTypeSpec {
                keyType(StringType)
                valueType(AnyType)
            })
        }
    })

    type(StructSpec {
        name("Provider")
        field {
            name("name")
            type(StringType)
        }
        field {
            name("config")
            type(MapTypeSpec {
                keyType(StringType)
                valueType(AnyType)
            })
        }
    })

    type(StructSpec {
        name("Variable")
        field {
            name("name")
            type(StringType)
        }
        field {
            name("type")
            type(StringType)
        }
        field {
            name("default")
            type(AnyType)
        }
    })

    type(StructSpec {
        name("Output")
        field {
            name("name")
            type(StringType)
        }
        field {
            name("value")
            type(AnyType)
        }
    })
}